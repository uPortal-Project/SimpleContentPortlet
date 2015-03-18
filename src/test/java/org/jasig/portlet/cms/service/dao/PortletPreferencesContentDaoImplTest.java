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

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.jasig.portlet.cms.mvc.exception.ContentPersistenceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class PortletPreferencesContentDaoImplTest {

    @Mock ActionRequest request;
    @Mock PortletPreferences preferences;
    IContentDao contentDao = new PortletPreferencesContentDaoImpl();
    
    String content = "<h1>Title</h1><p>content</p>";
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        when(request.getPreferences()).thenReturn(preferences);
        when(preferences.getValue(PortletPreferencesContentDaoImpl.CONTENT_KEY, "")).thenReturn(content);
    }
    
    @Test
    public void testGetContent() {
        String result = contentDao.getContent(request, null);
        assert content.equals(result);
    }
    
    @Test
    public void testSaveContent() throws ReadOnlyException {
        contentDao.saveContent(request, content, null);
        verify(preferences).setValue(PortletPreferencesContentDaoImpl.CONTENT_KEY, content);
    }

    @Test
    public void testReadOnlyError() {
        
        try {
            doThrow(new ReadOnlyException("")).when(preferences).setValue(PortletPreferencesContentDaoImpl.CONTENT_KEY, content);
            contentDao.saveContent(request, content, null);
            
            Assert.fail("Should have thrown an exception");
        } catch (ReadOnlyException e) {
            Assert.fail("Read-only exception should have been converted to a ContentPersistenceException");
        } catch (ContentPersistenceException e) {
        }
        
    }

    @Test
    public void testValidatorError() throws IOException {
        
        try {
            doThrow(new ValidatorException("", null)).when(preferences).store();
            contentDao.saveContent(request, content, null);
            
            Assert.fail("Should have thrown an exception");
        } catch (ValidatorException e) {
            Assert.fail("Validator exception should have been converted to a ContentPersistenceException");
        } catch (ContentPersistenceException e) {
        }

    }


    @Test
    public void testIOError() throws ValidatorException {
        
        try {
            doThrow(new IOException("")).when(preferences).store();
            contentDao.saveContent(request, content, null);
            
            Assert.fail("Should have thrown an exception");
        } catch (IOException e) {
            Assert.fail("Validator exception should have been converted to a ContentPersistenceException");
        } catch (ContentPersistenceException e) {
        }

    }

}
