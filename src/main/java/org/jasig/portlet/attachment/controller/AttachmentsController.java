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
package org.jasig.portlet.attachment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import com.liferay.portletmvc4spring.bind.annotation.RenderMapping;

import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import java.util.HashMap;
import java.util.Map;
/**
 * @author Chris Waymire (chris@waymire)
 */
@Controller
@RequestMapping("VIEW")
public class AttachmentsController {
    public static final String REMOTE_USER_ATTR = AttachmentsController.class.getName() + "_REMOTE_USER";
    private static final String USE_PPORTAL_JS_LIBS_PREFERENCE = "Attachments.usePortalJsLibs";
    private static final String PPORTAL_JS_NAMESPACE_PREFERENCE = "Attachments.portalJsNamespace";
    private static final String VIEW_MAIN = "main";

    @RenderMapping
    public ModelAndView main(final PortletRequest request) {
        request.getPortletSession().setAttribute(REMOTE_USER_ATTR,request.getRemoteUser(),PortletSession.APPLICATION_SCOPE);
        ModelAndView view = createModelAndView(request, VIEW_MAIN);
        return view;
    }

    private ModelAndView createModelAndView(final PortletRequest request, final String view)
    {
        final Map<String,Object> model = new HashMap<String,Object>();
        final PortletPreferences prefs = request.getPreferences();

        final boolean usePortalJsLibs = Boolean.valueOf(prefs.getValue(USE_PPORTAL_JS_LIBS_PREFERENCE, "true"));  // default is true
        model.put("usePortalJsLibs", usePortalJsLibs);

        final String portalJsNamespace = prefs.getValue(PPORTAL_JS_NAMESPACE_PREFERENCE, "up");  // Matches the current convention in uPortal
        model.put("portalJsNamespace", portalJsNamespace);

        return new ModelAndView(view,model);
    }
}
