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

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.NullArgumentException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IContentService} implementation that contains a Map of {@link IContentService} delegates and uses a particular
 * portlet preference value as the key for mapping to the proper delegate to use.  If no key is found, then the configured  
 * default {@link IContentService} will be used.
 */
public class PrefValueBasedDelegatingContentService implements IContentService {

    private static final Logger log = LoggerFactory.getLogger(PrefValueBasedDelegatingContentService.class);

    public static final String DELEGATE_KEY_FOR_DEFAULT = "default";
    public static final String DELEGATE_KEY_PREF_NAME_DEFAULT = "contentServiceDelegateKey";

    private Map<String, IContentService> delegatesMap;
    private String delegateKeyPreferenceName = DELEGATE_KEY_PREF_NAME_DEFAULT;

    public PrefValueBasedDelegatingContentService(final Map<String, IContentService> delegates) {
        if (delegates == null) {
           throw new NullArgumentException("delegates must be provided");
        }
        if (delegates.get(DELEGATE_KEY_FOR_DEFAULT) == null) {
           throw new IllegalArgumentException("default delegate must be provided");
        }
        this.delegatesMap = new HashMap<String, IContentService>(delegates);
    }

    /**
     * @see org.jasig.portlet.cms.service.IContentService#getContent(javax.portlet.PortletRequest, java.lang.String)
     */
    public String getContent(PortletRequest request, String localeKey) {
        return this.getDelegateToUse(request).getContent(request, localeKey);
    }

    /**
     * @see org.jasig.portlet.cms.service.IContentService#saveContent(javax.portlet.ActionRequest, java.lang.String, java.lang.String)
     */
    public void saveContent(ActionRequest request, String content, String localeKey) {
        this.getDelegateToUse(request).saveContent(request, content, localeKey);
    }

    private IContentService getDelegateToUse(PortletRequest request) {
        final PortletPreferences preferences = request.getPreferences();
        String delegateKey = preferences.getValue(this.delegateKeyPreferenceName, null);
        if (delegateKey == null) {
            log.debug(
                    "No portlet preference value found for key '{}'.  Will use default '{}'.",
                    this.delegateKeyPreferenceName,
                    DELEGATE_KEY_FOR_DEFAULT);
            delegateKey = DELEGATE_KEY_FOR_DEFAULT;
        }
        final IContentService delegate = this.delegatesMap.get(delegateKey);
        log.debug("Delegate key '{}' maps to: {}", delegateKey, delegate);
        if (delegate == null) {
            throw new ContentServiceNotFoundException(
                "No delegate found for key '" + delegateKey + "' in class " + this.getClass().getName());
        }
        return delegate;
    }

    public void setDelegateKeyPreferenceName(final String name) {
        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("delegateKeyPreferenceName cannot be blank");
        }
        this.delegateKeyPreferenceName = name;
    }

}
