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

/**
 * {@link ServiceException} indicating that a content service could not be found.  Message should provide further 
 * details on how/why the service could not be found.
 */
public class ContentServiceNotFoundException extends ServiceException {

    private static final long serialVersionUID = 880945238494890710L;

    public ContentServiceNotFoundException() {
        super();
    }

    public ContentServiceNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public ContentServiceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ContentServiceNotFoundException(String message) {
        super(message);
    }

    public ContentServiceNotFoundException(Throwable cause) {
        super(cause);
    }

}
