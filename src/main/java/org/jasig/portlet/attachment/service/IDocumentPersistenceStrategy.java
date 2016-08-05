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
package org.jasig.portlet.attachment.service;

import javax.servlet.http.HttpServletRequest;

import org.jasig.portlet.attachment.model.Attachment;

/**
 * Defines persistence strategies for Attachment binary data.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

public interface IDocumentPersistenceStrategy {

    /**
     * Accepts an attachment to save and persists the attachment binary data using the implemented persistence strategy.
     *
     * @param request HTTPServletRequest.  May be null to indicate a data import operation.
     * @param attachment attachment containing data to save
     * @return URL to access the attachment with.  Null means did not persist the attachment.
     * @throws PersistenceException Exception persisting the document to the persistence store
     */
    String persistAttachmentBinary(HttpServletRequest request, Attachment attachment) throws PersistenceException;

    /**
     * Boolean indicating whether or not attachment data is stored into the database.  Some persistence stores,
     * such as the local filesystem, would want the data stored into the database to allow for hydrating the
     * data from the database in case it is not on the local file system.  This is used when you have a cluster
     * of portal servers and a server may not have the content hydrated locally.  Other persistence stores, such
     * as a network share or S3, store their data external to the portal server so there is no need to have the
     * file data stored into a database.
     *
     * @return True if the file data is stored in the database, else false.
     */
    boolean isPersistenceIntoDatabaseRequired();
}
