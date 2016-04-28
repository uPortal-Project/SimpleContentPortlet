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

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

/**
 * Class to encapsulate the AWS S3 bucket properties for content.  This class should be injected as a dependency into 
 * classes that need Content AWS S3 bucket properties values.
 */
@Component
@PropertySource("classpath:/properties/content-aws-s3-bucket.properties")
public class ContentAwsS3BucketConfig {

    @Value("${simplecontentportlet.content.aws.s3.bucket.name}")
    private String bucketName;

    @Value("${simplecontentportlet.content.aws.s3.bucket.object-key-format}")
    private String objectKeyFormat;

    @Value("${simplecontentportlet.content.aws.s3.bucket.object-key-format.tokens.portlet-pref-keys}")
    private String objectKeyFormatTokensPortletPrefKeys;

    @Value("${simplecontentportlet.content.aws.s3.bucket.object-cache-control}")
    private String objectCacheControl;

    public String getBucketName() {
        return this.bucketName;
    }

    public String getObjectKeyFormat() {
        return this.objectKeyFormat;
    }

    public String getObjectKeyFormatTokensPortletPrefKeys() {
        return this.objectKeyFormatTokensPortletPrefKeys;
    }

    public List<String> getObjectKeyFormatTokensPortletPrefKeysList() {
        return Arrays.asList(this.objectKeyFormatTokensPortletPrefKeys.split("\\s*,\\s*"));
    }

    public String getObjectCacheControl() {
        return this.objectCacheControl;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
