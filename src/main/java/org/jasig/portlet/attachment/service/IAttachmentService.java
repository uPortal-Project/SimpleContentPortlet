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

import org.jasig.portlet.attachment.model.Attachment;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
public interface IAttachmentService {
    Attachment get(long attachmentId, HttpServletRequest request);
    Attachment get(String guid, HttpServletRequest request);
    List<Attachment> find(String creator, HttpServletRequest request);
    List<Attachment> find(String creator, String filename, HttpServletRequest request);
    Attachment save(Attachment attachment, HttpServletRequest request);
    void delete(Attachment attachment, HttpServletRequest request);
    void delete(long attachmentId, HttpServletRequest request);
}
