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

import java.io.IOException;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;
import javax.portlet.ReadOnlyException;
import javax.portlet.ValidatorException;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.mvc.exception.ContentPersistenceException;
import org.springframework.stereotype.Component;

/**
 * PortletPreferencesContentDaoImpl is an implementation of the IContentDao
 * interface that uses simple portlet preferences as a store for HTML content.
 * This implementation relies on the portal for appropriate caching of
 * portlet preferences and does not on its own provide any validation or
 * stripping of HTML content.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
@Component
public class PortletPreferencesContentDaoImpl implements IContentDao {
    
    protected static final String CONTENT_KEY = "content";
    
    protected final Log log = LogFactory.getLog(getClass());

    /*
     * (non-Javadoc)
     * @see org.jasig.portlet.cms.service.dao.IContentDao#getContent(javax.portlet.PortletRequest, java.lang.String)
     */
    public String getContent(PortletRequest request, String localeKey) {
        PortletPreferences preferences = request.getPreferences();

        String content = null;
        if (StringUtils.isNotBlank(localeKey)) {
            content = preferences.getValue(getLocaleSpecificKey(localeKey), null);
        }
        if (content == null) {
            content = preferences.getValue(CONTENT_KEY, "");
        }
        return content;
    }

    /*
     * (non-Javadoc)
     * @see org.jasig.portlet.cms.service.dao.IContentDao#saveContent(javax.portlet.ActionRequest, java.lang.String, java.lang.String)
     */
    public void saveContent(ActionRequest request, String content, String localeKey) {
        try {

            PortletPreferences preferences = request.getPreferences();
            if (StringUtils.isNotBlank(localeKey)) {
                preferences.setValue(getLocaleSpecificKey(localeKey), content);
            } else {
                preferences.setValue(CONTENT_KEY, content);
            }
            preferences.store();
        
        } catch (ReadOnlyException e) {
            throw new ContentPersistenceException("Failed to save read-only preference", e);
        } catch (ValidatorException e) {
            throw new ContentPersistenceException("Portlet preferences validation error while attempting to persist portlet content", e);
        } catch (IOException e) {
            throw new ContentPersistenceException("IO error while attempting to persist portlet content", e);
        }
    }
    
    /**
     * Return a locale-specific content preference key.
     * 
     * @param localeKey
     * @return
     */
    protected String getLocaleSpecificKey(String localeKey) {
        return CONTENT_KEY.concat("-").concat(localeKey);
    }

    
}
