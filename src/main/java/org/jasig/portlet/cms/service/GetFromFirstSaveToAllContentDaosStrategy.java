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

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import org.jasig.portlet.cms.service.dao.IContentDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link IContentDaosDelegatingStrategy} implementation that:
 *  -- returns content from the first DAO in the provided list of DAOs
 *  -- saves content to all DAOs provided
 *  -- by default, aborts when an exception occurs, allowing the exception to propagate
 *     (this can be configured to instead continue iterating through list of DAOs regardless of any exceptions thrown)
 */
public class GetFromFirstSaveToAllContentDaosStrategy implements IContentDaosDelegatingStrategy {

    private static final Logger log = LoggerFactory.getLogger(GetFromFirstSaveToAllContentDaosStrategy.class);
    private static final String SWALLOWED_EXCEPTION_LOG_MSG =
            "Exception caught (will not be propagated) calling 'saveContent' on DAO {}. Exception: {}";

    private boolean abortOnException = true;

    /**
     * Returns the content from the first DAO in the list.
     */
    @Override
    public String getContent(List<IContentDao> daos, PortletRequest request, String localeKey) {
        return daos.get(0).getContent(request, localeKey);
    }

    /**
     * Saves the content to all the DAOs provided.
     */
    @Override
    public void saveContent(List<IContentDao> daos, ActionRequest request, String content, String localeKey) {
        for (IContentDao dao : daos) {
            try {
                dao.saveContent(request, content, localeKey);
            } catch (Exception e) {
                if (this.abortOnException()) {
                    throw e;
                } else {
                    log.info(SWALLOWED_EXCEPTION_LOG_MSG, dao, e);
                }
            }
        }
    }

    public void setAbortOnException(boolean b) {
        this.abortOnException = b;
    }

    /**
     * Method indicating whether or not remaining delegate calls to the list of DAOs will be aborted when an exception 
     * occurs.  If "true", the exception will be thrown.  If "false", the exception will be logged and the remaining 
     * delegate calls will be made. 
     * @return 'true' if processing should abort upon exception; 'false' otherwise
     */
    public boolean abortOnException() {
        return this.abortOnException;
    }

}
