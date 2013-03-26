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

import org.jasig.portlet.attachment.dao.IFolderDao;
import org.jasig.portlet.attachment.model.Folder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Repository
public class JpaFolderDao extends BaseJpaDao implements IFolderDao
{
    public Folder getFolderById(final long folderId) {
        return this.getEntityManager().find(Folder.class,folderId);
    }

    @Transactional
    public List<Folder> getFoldersByParent(final Folder parent) {
        return this.getFoldersByParent(parent.getId());
    }

    @Transactional
    public List<Folder> getFoldersByParent(final long parentId) {
        final EntityManager em = this.getEntityManager();
        final TypedQuery<Folder> query = em.createNamedQuery(Queries.GET_FOLDERS_BY_PARENT, Folder.class);
        query.setParameter("parent",parentId);
        List<Folder> results = query.getResultList();
        return results;
    }

    @Transactional
    public List<Folder> getFolders() {
        final EntityManager em = this.getEntityManager();
        final TypedQuery<Folder> query = em.createNamedQuery(Queries.GET_FOLDERS, Folder.class);
        List<Folder> results = query.getResultList();
        return results;
    }

    @Transactional
    public Folder saveFolder(Folder folder) {
        return this.getEntityManager().merge(folder);
    }

    @Transactional
    public void deleteFolder(final long folderId) {
        final Folder folder = this.getFolderById(folderId);
        if(folder != null)
        {
            this.getEntityManager().remove(folder);
        }
    }

    @Transactional
    public void deleteFolder(Folder folder) {
        this.deleteFolder(folder.getId());
    }
}
