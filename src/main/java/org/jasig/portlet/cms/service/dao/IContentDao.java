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

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

/**
 * IContentDao is responsible for persisting and retrieving configured
 * HTML content.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
public interface IContentDao {
    
    /**
     * Get the HTML content to be displayed for this portlet.
     * 
     * @param request
     * @return
     */
    public String getContent(PortletRequest request);

    /**
     * Save the HTML content for this portlet.
     * 
     * @param request Action request associated with this operation.
     * @param content HTML content to be persisted.
     */
    public void saveContent(ActionRequest request, String content);

}
