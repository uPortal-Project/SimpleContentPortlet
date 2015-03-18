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
package org.jasig.portlet.cms.mvc.portlet;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;

import org.jasig.portlet.cms.mvc.form.ContentForm;
import org.jasig.portlet.cms.service.IStringCleaningService;
import org.jasig.portlet.cms.service.dao.IContentDao;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ConfigureContentControllerTest {

    @Mock ActionRequest request;
    @Mock ActionResponse response;
    @Mock IContentDao contentDao;
    @Mock IStringCleaningService cleaningService;
    @Mock PortletPreferences preferences;
    
    String content = "<h1>Title</h1><p>content</p>";
    String cleanContent = "Title<p>content</p>";
    ConfigureContentController controller = new ConfigureContentController();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        when(request.getPreferences()).thenReturn(preferences);
        when(preferences.getValue("cleanContent", "true")).thenReturn("true");
        
        controller.setContentDao(contentDao);
        when(contentDao.getContent(request, null)).thenReturn(cleanContent);
        
        controller.setStringCleaningService(cleaningService);
        when(cleaningService.getSafeContent(content)).thenReturn(cleanContent);
    }
    
    @Test
    public void testShowContentForm() {
        assert "configureContent".equals(controller.showContentForm());
    }
    
    @Test
    public void testGetForm() {
        ContentForm form = controller.getForm(request);
        assert cleanContent.endsWith(form.getContent());
    }
    
    @Test
    public void testCancelUpdate() throws PortletModeException {
        controller.cancelUpdate(request, response);
        verify(response).setPortletMode(PortletMode.VIEW);
    }

    @Test
    public void testUpdateConfiguration() throws ReadOnlyException, PortletModeException {
        
        ContentForm form = new ContentForm();
        form.setContent(content);
        
        controller.updateConfiguration(request, response, form);
        verify(contentDao).saveContent(request, cleanContent, null);
        
        verify(response).setPortletMode(PortletMode.VIEW);
        
    }
    
    @Test
    public void testUpdateConfigurationWithoutCleaning() throws PortletModeException {
        
        when(preferences.getValue("cleanContent", "true")).thenReturn("false");
        
        ContentForm form = new ContentForm();
        form.setContent(content);
        controller.updateConfiguration(request, response, form);
        
        verify(contentDao).saveContent(request, content, null);

    }

}
