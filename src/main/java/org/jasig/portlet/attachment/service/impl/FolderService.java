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

import org.jasig.portlet.attachment.dao.IFolderDao;
import org.jasig.portlet.attachment.model.Folder;
import org.jasig.portlet.attachment.service.IFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Component
public class FolderService implements IFolderService {
    @Autowired
    private IFolderDao folderDao = null;

    public Folder getFolderById(final HttpServletRequest request,final long folderId) {
        return folderDao.getFolderById(folderId);
    }

    public List<Folder> getFolders(final HttpServletRequest request) {
        return folderDao.getFolders();
    }

    public List<Folder> getFoldersByParent(final HttpServletRequest request,final Folder parent) {
        return folderDao.getFoldersByParent(parent);
    }

    public List<Folder> getFoldersByParent(final HttpServletRequest request,final long parentId) {
        return folderDao.getFoldersByParent(parentId);
    }

    public Folder saveFolder(final HttpServletRequest request,Folder folder) {
        String user = request.getRemoteUser();
        updateCreatedModified(folder,user);
        Folder saved = folderDao.saveFolder(folder);
        return saved;
    }

    public void deleteFolder(final HttpServletRequest request,Folder folder) {
        folderDao.deleteFolder(folder);
    }

    public void deleteFolder(final HttpServletRequest request,final long folderId) {
        folderDao.deleteFolder(folderId);
    }

    private void updateCreatedModified(Folder folder,String user) {
        if(folder.getId() == -1)
        {
            folder.setCreatedBy(user);
        }
        folder.setModifiedBy(user);
    }
}
