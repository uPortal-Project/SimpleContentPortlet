/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.cms.service.dao;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.jasig.portlet.cms.service.dao.IContentDao;
import org.jasig.portlet.cms.service.dao.PortletPreferencesContentDaoImpl;
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
        String result = contentDao.getContent(request);
        assert content.equals(result);
    }
    
    @Test
    public void testSaveContent() throws ReadOnlyException {
        contentDao.saveContent(request, content);
        verify(preferences).setValue(PortletPreferencesContentDaoImpl.CONTENT_KEY, content);
    }


}
