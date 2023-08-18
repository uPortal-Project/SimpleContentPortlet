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
package org.jasig.portlet.cms.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import org.jasig.portlet.cms.mvc.exception.StringCleaningException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class AntiSamyStringCleaningServiceTest {

    private AntiSamyStringCleaningService cleaningService;
    @Mock AntiSamy antiSamy;
    String content = "<h1>Title</h1><p>Content<script type=\"text/javascript\">alert('uhoh!');</script></p>";

    AutoCloseable mockitoAnnotationsMocksCloseable;

    @Before
    public void setUp() {
        this.mockitoAnnotationsMocksCloseable = MockitoAnnotations.openMocks(this);
    }

    @After
    public void tearDown() throws Exception {
        this.mockitoAnnotationsMocksCloseable.close();
    }

    @Autowired(required = true)
    public void setCleaningService(AntiSamyStringCleaningService cleaningService) {
        this.cleaningService = cleaningService;
    }

    @Test
    public void testGetSafeContent() {
        String clean = cleaningService.getSafeContent(content);
        assert ("Title<p>Content</p>").equals(clean);
    }

    @Test
    public void testScanException() throws PolicyException {

        cleaningService = spy(cleaningService);
        when(cleaningService.getAntiSamyInstance()).thenReturn(antiSamy);
        
        try {
            doThrow(new ScanException("")).when(antiSamy).scan(anyString(), any(Policy.class));
            cleaningService.getSafeContent(content);
            
            Assert.fail("Should have thrown an exception");
        } catch (ScanException e) {
            Assert.fail("Scan exception should have been converted to a ContentPersistenceException");
        } catch (StringCleaningException e) {
        }

    }

    @Test
    public void testPolicyException() throws ScanException {

        cleaningService = spy(cleaningService);
        when(cleaningService.getAntiSamyInstance()).thenReturn(antiSamy);
        
        try {
            
            doThrow(new PolicyException("")).when(antiSamy).scan(anyString(), any(Policy.class));
            cleaningService.getSafeContent(content);
            
            Assert.fail("Should have thrown an exception");
        } catch (PolicyException e) {
            Assert.fail("Policy exception should have been converted to a ContentPersistenceException");
        } catch (StringCleaningException e) {
        }

    }
    
}
