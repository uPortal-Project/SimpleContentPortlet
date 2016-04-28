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

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.NullArgumentException;
import org.jasig.portlet.cms.service.dao.IContentDao;

/**
 * {@link IContentService} implementation that simply delegates to a {@link IContentDao}.
 */
public class PassThroughToDaoContentService implements IContentService {

    private IContentDao contentDao;

    public PassThroughToDaoContentService(final IContentDao dao) {
        if (dao == null) {
            throw new NullArgumentException("dao");
        }
        this.contentDao = dao;
    }

    @Override
    public String getContent(PortletRequest request, String localeKey) {
        return this.contentDao.getContent(request, localeKey);
    }

    @Override
    public void saveContent(ActionRequest request, String content, String localeKey) {
        this.contentDao.saveContent(request, content, localeKey);
    }

}
