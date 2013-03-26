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
package org.jasig.portlet.attachment.dao.jpa;

import org.apache.commons.codec.binary.Base64;
import org.jasig.portlet.attachment.dao.IAttachmentDao;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.model.Folder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;


/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Repository
public class JpaAttachmentDao extends BaseJpaDao implements IAttachmentDao {
    private final Base64 base64 = new Base64();

    public Attachment getAttachmentById(final long attachmentId) {
        final Attachment attachment = this.getEntityManager().find(Attachment.class, attachmentId);
        return attachment;
    }

    public Attachment getThinAttachmentById(final long attachmentId) {
        final EntityManager em = this.getEntityManager();
        final TypedQuery<Attachment> query = em.createNamedQuery(Queries.GET_THIN_ATTACHMENT, Attachment.class);
        query.setParameter("id", attachmentId);
        return query.getSingleResult();
    }

    public String getAttachmentContent(final long attachmentId) {
        final EntityManager em = this.getEntityManager();
        final TypedQuery<String> query = em.createNamedQuery(Queries.GET_ATTACHMENT_CONTENT, String.class);
        query.setParameter("id",attachmentId);
        String encoded = query.getSingleResult();
        String decoded =new String(base64.decode(encoded));
        return decoded;
    }

    @Transactional
    public List<Attachment> getAttachments() {
        final EntityManager em = this.getEntityManager();
        final TypedQuery<Attachment> query = em.createNamedQuery(Queries.GET_ATTACHMENTS, Attachment.class);
        List<Attachment> results = query.getResultList();
        return results;
    }

    @Transactional
    public List<Attachment> getAttachmentsByFolder(Folder folder) {
        return this.getAttachmentsByFolder(folder.getId());
    }

    @Transactional
    public List<Attachment> getAttachmentsByFolder(final long folderId) {
        final EntityManager em = this.getEntityManager();
        final TypedQuery<Attachment> query = em.createNamedQuery(Queries.GET_ATTACHMENTS_BY_FOLDER, Attachment.class);
        query.setParameter("folder",folderId);
        List<Attachment> results = query.getResultList();
        return results;
    }

    public long attachmentExists(Attachment attachment)
    {
        final EntityManager em = this.getEntityManager();
        final TypedQuery<Long> query = em.createNamedQuery(Queries.ATTACHMENT_EXISTS, Long.class);
        //query.setParameter("folder", attachment.getFolder());
        query.setParameter("filename", attachment.getFilename());
        try {
            long id = query.getSingleResult();
            return id;
        } catch(NoResultException noResultException) {
            return 0;
        }
    }

    @Transactional
    public Attachment saveAttachment(Attachment attachment) {
        return this.getEntityManager().merge(attachment);
    }

    @Transactional
    public void deleteAttachment(Attachment attachment) {
        this.getEntityManager().remove(attachment);
    }

    @Transactional
    public void deleteAttachment(final long attachmentId) {
        Attachment attachment = this.getAttachmentById(attachmentId);
        if(attachment != null)
        {
            this.deleteAttachment(attachment);
        }
    }

    @Transactional
    public void updateLastAccessedAt(long attachmentId) {
        final EntityManager em = this.getEntityManager();
        final Date now = new Date();
        Query query = em.createNamedQuery(Queries.UPDATE_ATTACHMENT_LAST_ACCESSED_AT);
        query.setParameter("id",attachmentId);
        query.setParameter("date",now);
        query.executeUpdate();
    }
}
