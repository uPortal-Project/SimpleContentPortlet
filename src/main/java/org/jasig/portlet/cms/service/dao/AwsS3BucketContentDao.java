/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.cms.service.dao;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NullArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.AmazonWebServiceRequest;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

/**
 * {@link IContentDao} implementation that uses an AWS S3 as a store for HTML content.  This implementation does not 
 * on its own provide any validation or stripping of HTML content.
 */
public class AwsS3BucketContentDao implements IContentDao {

    private static final Logger log = LoggerFactory.getLogger(AwsS3BucketContentDao.class);

    private static final String ATTEMPTING_TO_GET_FILE_FROM_AWS_S3_LOG_MSG = "Attempting to get file from AWS S3: bucket[{}]; key[{}]";
    private static final String FILE_RETRIEVED_FROM_AWS_S3_LOG_MSG = "File retrieved from AWS S3 with no reported errors: bucket[{}]; key[{}]";

    private static final String ATTEMPTING_TO_SAVE_FILE_TO_AWS_S3_LOG_MSG = "Attempting to save file to AWS S3: bucket[{}]; key[{}]";
    private static final String FILE_SAVED_TO_AWS_S3_LOG_MSG = "File saved to AWS S3 with no reported errors: bucket[{}]; key[{}]";

    public static final String CONTENT_CACHE_CONTROL_PORTLET_PREF_NAME = "contentCacheControl";
    public static final String OBJECT_KEY_FORMAT_PORTLET_PREF_TOKEN_FORMAT = "pref{%s}";

    private AmazonS3 amazonS3Client;
    private ContentAwsS3BucketConfig awsS3BucketConfig;
    private Map<String,String> awsObjectUserMetadata;

    public AwsS3BucketContentDao(final AmazonS3 client, final ContentAwsS3BucketConfig config) {
        if (client == null) {
            throw new NullArgumentException("client cannot be null");
        }
        if (config == null) {
            throw new NullArgumentException("config cannot be null");
        }
        this.amazonS3Client = client;
        this.awsS3BucketConfig = config;
        log.info("ContentAwsS3BucketConfig provided: {}", config);
    }

    /**
     * @see org.jasig.portlet.cms.service.IContentService#getContent(javax.portlet.PortletRequest, java.lang.String)
     */
    public String getContent(PortletRequest request, String localeKey) {
        return this.getContentFromAwsS3Bucket(request);
    }

    /**
     * @see org.jasig.portlet.cms.service.IContentService#saveContent(javax.portlet.ActionRequest, java.lang.String, java.lang.String)
     */
    public void saveContent(ActionRequest request, String content, String localeKey) {
        this.saveContentToAwsS3Bucket(request, content);
    }

    private String getContentFromAwsS3Bucket(final PortletRequest request) {
        try {
            final PortletPreferences portletPreferences = request.getPreferences();
            final String objectKey = this.createObjectKey(portletPreferences);
            return this.getContentFromAwsS3Bucket(objectKey);
        } catch (TokenValueNotFoundException e) {
            throw e;
        }
    }

    private void saveContentToAwsS3Bucket(final ActionRequest request, final String content) {
        try {
            final PortletPreferences portletPreferences = request.getPreferences();
            final String objectKey = this.createObjectKey(portletPreferences);
            this.saveContentToAwsS3Bucket(objectKey, content, portletPreferences);
        } catch (TokenValueNotFoundException e) {
            throw e;
        }
    }

    private String getContentFromAwsS3Bucket(final String objectKey) {
        log.info(ATTEMPTING_TO_GET_FILE_FROM_AWS_S3_LOG_MSG, this.awsS3BucketConfig.getBucketName(), objectKey);
        final GetObjectRequest getObjectRequest = this.createGetObjectRequest(objectKey);
        final S3Object contentS3Object = this.getContentFromAwsS3Bucket(getObjectRequest);
        log.info(FILE_RETRIEVED_FROM_AWS_S3_LOG_MSG, this.awsS3BucketConfig.getBucketName(), objectKey);
        final S3ObjectInputStream inputStream = contentS3Object.getObjectContent();
        final String result = this.readContentFromInputStream(inputStream);
        return result;
    }

    private String readContentFromInputStream(final S3ObjectInputStream inputStream) {
        try {
            return IOUtils.toString(inputStream, "UTF-8");
        } catch(IOException ioe) {
            throw new ContentPersistenceException("Problem reading AWS S3 input stream.", ioe);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

    private void saveContentToAwsS3Bucket(
            final String objectKey, final String content, final PortletPreferences portletPreferences) {
        log.info(ATTEMPTING_TO_SAVE_FILE_TO_AWS_S3_LOG_MSG, this.awsS3BucketConfig.getBucketName(), objectKey);
        final InputStream inputStream = IOUtils.toInputStream(content);
        final ObjectMetadata objectMetadata = this.createObjectMetadata(content, portletPreferences);
        final PutObjectRequest putObjectRequest = this.createPutObjectRequest(objectKey, inputStream, objectMetadata);
        this.saveContentToAwsS3Bucket(putObjectRequest);
        log.info(FILE_SAVED_TO_AWS_S3_LOG_MSG, this.awsS3BucketConfig.getBucketName(), objectKey);
    }

    // exception only used internal to this class
    class TokenValueNotFoundException extends ContentPersistenceException {
        private static final long serialVersionUID = 1L;
        public TokenValueNotFoundException(final String msg) {
            super(msg);
        }
    }

    private String createObjectKey(final PortletPreferences portletPreferences) {
        String result = this.awsS3BucketConfig.getObjectKeyFormat();
        for (String portletPrefName : this.awsS3BucketConfig.getObjectKeyFormatTokensPortletPrefKeysList()) {
            final String portletPrefValue = portletPreferences.getValue(portletPrefName, null);
            if (portletPrefValue == null) {
                throw new TokenValueNotFoundException(
                    "Portlet pref value not found for token: " + portletPrefName 
                    + "; Could not resolve AWS S3 object key: " + this.awsS3BucketConfig.getObjectKeyFormat());
            } else {
                result = result.replace(String.format(OBJECT_KEY_FORMAT_PORTLET_PREF_TOKEN_FORMAT, portletPrefName), portletPrefValue);
            }
        }
        return result;
    }

    private ObjectMetadata createObjectMetadata(final String content, final PortletPreferences portletPreferences) {
        final ObjectMetadata metadata = new ObjectMetadata();
        this.addContentMetadata(metadata, content);
        this.addUserMetatadata(metadata);
        this.addPortletPreferenceMetadata(metadata, portletPreferences);
        return metadata;
    }

    private GetObjectRequest createGetObjectRequest(final String objectKey) {
        return new GetObjectRequest(this.awsS3BucketConfig.getBucketName(), objectKey);
    }

    private PutObjectRequest createPutObjectRequest(
            final String objectKey, final InputStream inputStream, final ObjectMetadata objectMetadata) {
        return new PutObjectRequest(this.awsS3BucketConfig.getBucketName(), objectKey, inputStream, objectMetadata);
    }

    private S3Object getContentFromAwsS3Bucket(final GetObjectRequest getObjectRequest) {
        try {
            return this.amazonS3Client.getObject(getObjectRequest);
        } catch (AmazonServiceException ase) {
            this.logAmazonServiceException(ase, getObjectRequest);
            throw new ContentPersistenceException("AWS S3 'get object' failure for: " + getObjectRequest, ase);
        } catch (AmazonClientException ace) {
            this.logAmazonClientException(ace, getObjectRequest);
            throw new ContentPersistenceException("AWS S3 'get object' failure for: " + getObjectRequest, ace);
        }
    }

    private void saveContentToAwsS3Bucket(final PutObjectRequest putObjectRequest) {
        try {
            this.amazonS3Client.putObject(putObjectRequest);
        } catch (AmazonServiceException ase) {
            this.logAmazonServiceException(ase, putObjectRequest);
            throw new ContentPersistenceException("AWS S3 'put object' failure for: " + putObjectRequest, ase);
        } catch (AmazonClientException ace) {
            this.logAmazonClientException(ace, putObjectRequest);
            throw new ContentPersistenceException("AWS S3 'put object' failure for: " + putObjectRequest, ace);
        }
    }

    private void addContentMetadata(final ObjectMetadata metadata, final String content) {
        metadata.setContentMD5(this.calculateBase64EncodedMd5Digest(content));
        metadata.setContentLength(content.length());
        metadata.setContentType("text/html");
        metadata.setCacheControl(this.awsS3BucketConfig.getObjectCacheControl());
    }

    private String calculateBase64EncodedMd5Digest(final String content) {
        final byte[] md5DigestAs16ElementByteArray = DigestUtils.md5(content);
        return new String(Base64.encodeBase64(md5DigestAs16ElementByteArray));
    }

    private void addUserMetatadata(final ObjectMetadata metadata) {
        if (this.awsObjectUserMetadata != null) {
            for (Entry<String, String> entry : this.awsObjectUserMetadata.entrySet()) {
                metadata.addUserMetadata(entry.getKey(), entry.getValue());
            }
        }
    }

    private void addPortletPreferenceMetadata(
            final ObjectMetadata metadata, final PortletPreferences portletPreferences) {
        final String contentCacheControl = portletPreferences.getValue(CONTENT_CACHE_CONTROL_PORTLET_PREF_NAME, null);
        if (contentCacheControl != null) {
            metadata.setCacheControl(contentCacheControl);
        }
    }

    private void logAmazonClientException(final AmazonClientException exception, final AmazonWebServiceRequest request) {
        log.info("Caught an AmazonClientException, which means the client encountered a serious internal problem "
                + "while trying to communicate with S3, such as not being able to access the network.");
        log.info("Error Message: {}", exception.getMessage());
    }

    private void logAmazonServiceException(final AmazonServiceException exception, final AmazonWebServiceRequest request) {
        log.info("Caught an AmazonServiceException, which means your request made it "
                + "to Amazon S3, but was rejected with an error response for some reason.");
        log.info("Error Message:    {}", exception.getMessage());
        log.info("HTTP Status Code: {}", exception.getStatusCode());
        log.info("AWS Error Code:   {}", exception.getErrorCode());
        log.info("Error Type:       {}", exception.getErrorType());
        log.info("Request ID:       {}", exception.getRequestId());
    }

    public void setAwsObjectUserMetadata(final Map<String, String> metadata) {
        this.awsObjectUserMetadata  = new HashMap<String, String>(metadata.size());
        this.awsObjectUserMetadata.putAll(metadata);
    }

}
