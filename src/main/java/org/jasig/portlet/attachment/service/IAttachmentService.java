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
package org.jasig.portlet.attachment.service;

import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.model.Folder;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
public interface IAttachmentService {
    Attachment getAttachmentById(HttpServletRequest request,long attachmentId);
    Attachment getThinAttachmentById(HttpServletRequest request,long attachmentId);
    String getAttachmentContent(HttpServletRequest request,long attachmentId);
    List<Attachment> getAttachments(HttpServletRequest request);
    List<Attachment> getAttachmentsByFolder(HttpServletRequest request,Folder folder);
    List<Attachment> getAttachmentsByFolder(HttpServletRequest request,long folderId);
    long attachmentExists(HttpServletRequest request,Attachment attachment);
    Attachment saveAttachment(HttpServletRequest request,Attachment attachment);
    void deleteAttachment(HttpServletRequest request,Attachment attachment);
    void deleteAttachment(HttpServletRequest request,long attachmentId);
}
