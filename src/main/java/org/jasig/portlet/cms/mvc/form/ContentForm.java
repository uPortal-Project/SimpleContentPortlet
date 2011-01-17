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

package org.jasig.portlet.cms.mvc.form;

/**
 * Simple form for representing CMS content entry.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
public class ContentForm {

    private String content;
    private String locale;

    /**
     * Get the HTML content to be displayed by this portlet.
     * 
     * @return
     */
    public String getContent() {
        return content;
    }

    /**
     * Set the HTML content to be displayed by this portlet.
     * 
     * @param content
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Get the locale string associated with the content.
     * 
     * @return
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Set the locale string associated with the content.
     * 
     * @param locale
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }
}
