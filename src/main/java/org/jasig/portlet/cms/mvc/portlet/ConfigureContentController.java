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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletModeException;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.mvc.form.ContentForm;
import org.jasig.portlet.cms.service.IStringCleaningService;
import org.jasig.portlet.cms.service.dao.IContentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * ConfigureContentController allows administrative users to set the content 
 * to be displayed by the portlet.  This controller is responsible for 
 * performing any required content validation, removing any unwanted or 
 * dangerous tags and attributes from the configured HTML, and persisting it
 * to the configured content store.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
@Controller
@RequestMapping("CONFIG")
public class ConfigureContentController {

    protected final Log log = LogFactory.getLog(getClass());
    
    private IContentDao contentDao;
    
    @Autowired
    public void setContentDao(IContentDao contentDao) {
        this.contentDao = contentDao;
    }
    
    private IStringCleaningService cleaningService;
    
    @Autowired
    public void setStringCleaningService(IStringCleaningService cleaningService) {
        this.cleaningService = cleaningService;
    }

    /**
     * Show the main configuration view.
     * 
     * @return main configuration view
     */
    @RequestMapping
    public String showContentForm() {
        return "configureContent";
    }
    
    /**
     * Update the portlet's configuration according to the submitted form
     * object.
     * 
     * @param request ActionRequest
     * @param response ActionResponse
     * @param form configuration form
     * @throws PortletModeException exception
     */
    @RequestMapping(params="action=updateConfiguration")
    public void updateConfiguration(ActionRequest request, ActionResponse response, 
            @ModelAttribute("form") ContentForm form) throws PortletModeException {
        
        String content = form.getContent();
        String locale = form.getLocale();
        
        // if configured to do so, validate the content and strip out any
        // potentially dangerous HTML
        PortletPreferences preferences = request.getPreferences();
        if (Boolean.valueOf(preferences.getValue("cleanContent", "true"))) {
            content = cleaningService.getSafeContent(form.getContent());
        }
        
        // save the new content to the portlet preferences
        this.contentDao.saveContent(request, content, locale);
        
        // exit the portlet's configuration mode
        response.setPortletMode(PortletMode.VIEW);
    }
    
    /**
     * Cancel any pending portlet configuration edits and exit configuration mode.
     * 
     * @param request ActionRequest
     * @param response ActionResponse
     * @throws PortletModeException exception
     */
    @RequestMapping(params="action=cancelUpdate")
    public void cancelUpdate(ActionRequest request, ActionResponse response) 
            throws PortletModeException {
        // exit the portlet's configuration mode
        response.setPortletMode(PortletMode.VIEW);
    }
    
    @ResourceMapping("preview")
    public ModelAndView getPreview(@RequestParam("content") String content) {
        
        Map<String, String> model = new HashMap<String, String>();
        String cleanContent = cleaningService.getSafeContent(content);
        model.put("content", cleanContent);
        
        return new ModelAndView("jsonView", model);
    }
    
    
    /**
     * Get the form object for the portlet configuration.  If this portlet has
     * already been configured with content, the current HTML will be 
     * pre-populated into the form object.  If this is a new portlet, the 
     * initial content will be an empty string.
     * 
     * @param request PortletRequest
     * @return form object for the portlet configuration
     */
    @ModelAttribute("form")
    public ContentForm getForm(PortletRequest request) {
        
        // TODO: Get the locale specified in the drop-down list
        String content = this.contentDao.getContent(request, null);
        
        ContentForm form = new ContentForm();
        form.setContent(content);
        return form;
    }

    /**
     * Adds clean content attribute to the model.
     * @param request PortletRequest
     * @return true if the clean content portlet preference is set
     */
    @ModelAttribute("cleanContent")
    public boolean isCleanContent(PortletRequest request) {
        PortletPreferences preferences = request.getPreferences();
        return Boolean.valueOf(preferences.getValue("cleanContent", "true"));
    }
    
    /**
     * Get the list of supported locales to populate the drop-down list with.
     * 
     * @param request PortletRequest
     * @return list of supported locals
     */
//    @ModelAttribute("supportedLocales")
    public HashMap<String, String> getLocales(PortletRequest request) {

        PortletPreferences preferences = request.getPreferences();
        String[] supportedLocales = preferences.getValues("supportedLocales", new String[]{});
        HashMap<String, String> locales = new HashMap<String, String>(); 

        for (String localeString : supportedLocales) {
            localeString = localeString.trim();
            Locale locale = StringUtils.parseLocaleString(localeString.trim());
            locales.put(localeString.trim(), localeString.trim() + ": " + locale.getDisplayName());
        }

        return locales;
    }
}
