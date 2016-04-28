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
package org.jasig.portlet.attachment.service.impl;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.attachment.dao.IAttachmentDao;
import org.jasig.portlet.attachment.service.IDocumentPersistenceStrategy;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Component
public class AttachmentService implements IAttachmentService {
    @Autowired
    private IAttachmentDao attachmentDao;

    @Autowired
    @Qualifier(value = "documentPersistenceStrategy")
    private IDocumentPersistenceStrategy documentPersistenceStrategy;

    @Override
    public Attachment get(final long attachmentId) {
        return attachmentDao.get(attachmentId);
    }

    @Override
    public Attachment get(final String guid) {
        return attachmentDao.get(guid);
    }

    @Override
    public List<Attachment> find(final String creator) {
        return attachmentDao.find(creator);
    }

    @Override
    public List<Attachment> find(final String creator,final String filename) {
        return attachmentDao.find(creator,filename);
    }

    @Override
    public List<Attachment> findAll(int offset, int maxresults) {
        return attachmentDao.findAll(offset, maxresults);
    }

    @Override
    public Attachment save(Attachment attachment, String username, HttpServletRequest request) {
        // The username must be present
        if (StringUtils.isBlank(username)) {
            throw new IllegalArgumentException("Value for username cannot be blank;  " +
                    "is Tomcat's session path configured for shared sessions?");
        }
        String urlPath = documentPersistenceStrategy.persistAttachmentBinary(request, attachment);
        // If document was persisted (might not be with data import operation), update the attachment's path.
        if (urlPath != null) {
            attachment.setPath(urlPath);
        }

        // If the documentPersistenceStrategy indicates document does not need to be stored in the database,
        // null it out before persisting it so we don't waste space.  This can happen if the document is
        // persisted to an URL-accessible common external store, such as S3.
        if (!documentPersistenceStrategy.isPersistenceIntoDatabaseRequired()) {
            attachment.setData(null);
        }

        Attachment existing = attachmentDao.get(attachment.getGuid());
        if(existing != null)
        {
            existing.setFilename(attachment.getFilename());
            existing.setPath(attachment.getPath());
            if (documentPersistenceStrategy.isPersistenceIntoDatabaseRequired()) {
                existing.setData(attachment.getData());
            }
            existing.setChecksum(attachment.getChecksum());
            existing.setSource(attachment.getSource());
            existing.setContentType(attachment.getContentType());
            attachment = existing;
        }

        updateTimestamps(attachment, username);
        Attachment saved = attachmentDao.save(attachment);
        return saved;
    }

    @Override
    public void delete(Attachment attachment) {
        attachmentDao.delete(attachment);
    }

    @Override
    public void delete(long attachmentId) {
        attachmentDao.delete(attachmentId);
    }

    protected void updateTimestamps(final Attachment attachment,final String user)
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

    public IAttachmentDao getAttachmentDao() {
        return attachmentDao;
    }

    public void setAttachmentDao(IAttachmentDao attachmentDao) {
        this.attachmentDao = attachmentDao;
    }

    public IDocumentPersistenceStrategy getDocumentPersistenceStrategy() {
        return documentPersistenceStrategy;
    }

    public void setDocumentPersistenceStrategy(IDocumentPersistenceStrategy documentPersistenceStrategy) {
        this.documentPersistenceStrategy = documentPersistenceStrategy;
    }

    @Override
    public boolean isPersistenceIntoDatabaseRequired() {
        return documentPersistenceStrategy.isPersistenceIntoDatabaseRequired();
    }
}
