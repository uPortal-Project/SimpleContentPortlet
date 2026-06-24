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
package org.jasig.portlet.attachment.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import org.jasig.portlet.attachment.model.Attachment;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class AmazonS3PersistenceStrategyTest {

    @Test
    public void persistsAttachmentWithExpectedKeyAndMetadata() {
        final S3Client mockClient = mock(S3Client.class);
        AmazonS3PersistenceStrategy strategy =
                new AmazonS3PersistenceStrategy() {
                    @Override
                    protected S3Client buildS3Client() {
                        return mockClient;
                    }
                };
        strategy.setS3BucketName("my-bucket");
        strategy.setS3BucketBaseUrl("https://cdn.example.edu");
        strategy.setS3BucketPath("portlets/attachments");
        strategy.setS3CacheControlString("private, max-age=2592000");

        Attachment attachment = new Attachment();
        attachment.setGuid("abc-123");
        attachment.setFilename("file.txt");
        attachment.setContentType("text/plain");
        attachment.setData("hello".getBytes(StandardCharsets.UTF_8));
        attachment.setChecksum("5d41402abc4b2a76b9719d911017c592"); // md5("hello")

        String url = strategy.persistAttachmentBinary(null, attachment);

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(mockClient).putObject(captor.capture(), any(RequestBody.class));

        PutObjectRequest request = captor.getValue();
        assertEquals("my-bucket", request.bucket());
        assertEquals("portlets/attachments/abc-123/file.txt", request.key());
        assertEquals("text/plain", request.contentType());
        assertEquals("private, max-age=2592000", request.cacheControl());
        assertEquals(Long.valueOf(5L), request.contentLength());

        assertEquals(
                "https://cdn.example.edu/portlets/attachments/abc-123/file.txt", url);

        // The v2 client is AutoCloseable; try-with-resources must close it.
        verify(mockClient).close();
    }
}
