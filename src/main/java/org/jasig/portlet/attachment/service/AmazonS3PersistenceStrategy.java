/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.attachment.service;

import java.io.ByteArrayInputStream;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.apache.commons.codec.binary.Base64;
import org.jasig.portlet.attachment.model.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Persistence strategy which persists the attachment's file to an S3 bucket
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */
@Service
public class AmazonS3PersistenceStrategy implements IDocumentPersistenceStrategy {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final MessageFormat PATH_FORMAT = new MessageFormat("{0}/{1}/{2}");

    @Value("${attachments.s3.bucket.name}")
    String s3BucketName;

    @Value("${attachments.s3.bucket.base.url}")
    String s3BucketBaseUrl;

    @Value("${attachments.s3.bucket.path:portlets/attachments}")
    String s3BucketPath;

    @Value("${attachments.s3.cache.control:private, max-age=2592000}")
    String s3CacheControlString;

    // By default, don't store file data into the database since S3 is an external location independent
    // of the portal server.
    boolean persistenceIntoDatabaseRequired = false;

    @Override
    public String persistAttachmentBinary(HttpServletRequest request, Attachment attachment)
            throws PersistenceException {

        // If doing a data import operation and there is no data in the attachment, there is nothing to save to S3.
        // The data import is only going to update the attachment metadata in the database.
        if (request == null && attachment.getData() == null ) {
            return null;
        }

        AmazonS3 s3 = new AmazonS3Client();

        String key = PATH_FORMAT.format(new Object[]{s3BucketPath, attachment.getGuid(), attachment.getFilename()});

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(attachment.getContentType());
        metadata.setContentLength(attachment.getData().length);
        metadata.setCacheControl(s3CacheControlString);

        // S3 chose base64-encoded hash not the typical 32-character hex string so convert accordingly.
        // Hex.decodeHex(attachment.getChecksum().toCharArray())
        metadata.setContentMD5(Base64.encodeBase64String(DatatypeConverter.parseHexBinary(attachment.getChecksum())));

        try {
            s3.putObject(new PutObjectRequest(s3BucketName, key,
                    new ByteArrayInputStream(attachment.getData()), metadata));
            log.debug("Successfully sent {} to S3 bucket {} under key {}", attachment.getFilename(),
                    s3BucketName, key);
        } catch (AmazonClientException e) {
            String message = String.format("Unable to persist attachment %1s to S3 bucket %2s, key %3s",
                    attachment.getFilename(), s3BucketName, key);
            throw new PersistenceException(message, e);
        }

        return (s3BucketBaseUrl.endsWith("/")? s3BucketBaseUrl : s3BucketBaseUrl + "/") + key;
    }



    /**
     * Boolean indicating whether or not to store the file data into the database.  Storage into S3
     * has the data external to the portal server in a stable persistent location so there is no need to have the
     * file data stored into a database.
     *
     * @return True to store the file data in the database, else false.
     */
    public void setPersistenceIntoDatabaseRequired(boolean persistenceIntoDatabaseRequired) {
        this.persistenceIntoDatabaseRequired = persistenceIntoDatabaseRequired;
    }

    @Override
    public boolean isPersistenceIntoDatabaseRequired() {
        return persistenceIntoDatabaseRequired;
    }

    public String getS3BucketName() {
        return s3BucketName;
    }

    public void setS3BucketName(String s3BucketName) {
        this.s3BucketName = s3BucketName;
    }

    public String getS3BucketBaseUrl() {
        return s3BucketBaseUrl;
    }

    public void setS3BucketBaseUrl(String s3BucketBaseUrl) {
        this.s3BucketBaseUrl = s3BucketBaseUrl;
    }

    public String getS3BucketPath() {
        return s3BucketPath;
    }

    public void setS3BucketPath(String s3BucketPath) {
        this.s3BucketPath = s3BucketPath;
    }

    public String getS3CacheControlString() {
        return s3CacheControlString;
    }

    public void setS3CacheControlString(String s3CacheControlString) {
        this.s3CacheControlString = s3CacheControlString;
    }
}
