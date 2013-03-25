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
package org.jasig.portlet.attachment.service.impl;

import org.apache.commons.codec.binary.Hex;
import org.jasig.portlet.attachment.dao.IAttachmentDao;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.model.Folder;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Component
public class AttachmentService implements IAttachmentService {
    @Autowired
    private IAttachmentDao attachmentDao;

    public Attachment getAttachmentById(final HttpServletRequest request, final long attachmentId) {
        return attachmentDao.getAttachmentById(attachmentId);
    }

    public Attachment getThinAttachmentById(final HttpServletRequest request, final long attachmentId) {
        return attachmentDao.getThinAttachmentById(attachmentId);
    }

    public String getAttachmentContent(HttpServletRequest request,long attachmentId) {
        return attachmentDao.getAttachmentContent(attachmentId);
    }

    public List<Attachment> getAttachments(final HttpServletRequest request) {
        return attachmentDao.getAttachments();
    }

    public List<Attachment> getAttachmentsByFolder(final HttpServletRequest request, final Folder folder) {
        return attachmentDao.getAttachmentsByFolder(folder);
    }

    public List<Attachment> getAttachmentsByFolder(final HttpServletRequest request, final long folderId) {
        return attachmentDao.getAttachmentsByFolder(folderId);
    }

    public long attachmentExists(HttpServletRequest request,Attachment attachment)
    {
        return attachmentDao.attachmentExists(attachment);
    }

    public Attachment saveAttachment(final HttpServletRequest request, Attachment attachment) {
        long existing = attachmentExists(request,attachment);
        if(existing > 0)
        {
            Attachment a2 = getAttachmentById(request,existing);
            a2.setContent(attachment.getContent());
            a2.setVersion(attachment.getVersion()+1);
            attachment = a2;
        }
        String user = request.getRemoteUser();
        updateCreatedModified(attachment, user);
        Attachment saved = attachmentDao.saveAttachment(attachment);
        return saved;
    }

    public void deleteAttachment(final HttpServletRequest request, Attachment attachment) {
        attachmentDao.deleteAttachment(attachment);
    }

    public void deleteAttachment(final HttpServletRequest request, final long attachmentId) {
        attachmentDao.deleteAttachment(attachmentId);
    }

    protected void updateCreatedModified(Attachment attachment,String user)
    {
        Date now = new Date();
        if(attachment.getCreatedAt() == null)
        {
            attachment.setCreatedBy(user);
            attachment.setCreatedAt(now);
        }
        attachment.setModifiedBy(user);
        attachment.setModifiedAt(now);
    }

    private String generateChecksum(String content)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(content.getBytes(), 0, content.getBytes().length);
            return new String(Hex.encodeHex(digest.digest()));
        } catch(Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
