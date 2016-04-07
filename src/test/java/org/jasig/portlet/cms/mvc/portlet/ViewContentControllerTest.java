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

import static org.mockito.Mockito.when;

import java.util.Locale;

import javax.portlet.PortletRequest;

import org.jasig.portlet.cms.service.IContentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ViewContentControllerTest {

    @Mock PortletRequest request;
    @Mock IContentService contentService;
    
    String content = "<h1>Title</h1><p>content</p>";
    ViewContentController controller = new ViewContentController();
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        
        controller.setContentService(contentService);
        when(contentService.getContent(request, Locale.US.toString())).thenReturn(content);
        when(request.getLocale()).thenReturn(Locale.US);
    }
    
    @Test
    public void testViewContent() {
        assert "viewContent".equals(controller.viewContent());
    }
    
    @Test
    public void testGetContent() {
        String result = controller.getContent(request);
        assert content.equals(result);
    }

}
