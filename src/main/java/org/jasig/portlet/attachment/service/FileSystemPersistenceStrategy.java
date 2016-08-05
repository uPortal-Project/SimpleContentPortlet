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

import java.io.IOException;
import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;

import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.util.FileUtil;
import org.springframework.stereotype.Service;

/**
 * Persistence strategy which persists the attachment's file to the local filesystem.
 *
 * TODO Enhance to allow specifying a network share location.  Currently file storage is always
 * on the local file system relative to webapp directory.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

@Service
public class FileSystemPersistenceStrategy implements IDocumentPersistenceStrategy {

    private static final String RELATIVE_ROOT = "/content";
    private static final MessageFormat PATH_FORMAT = new MessageFormat(RELATIVE_ROOT + "/{0}/{1}");

    @Override
    public String persistAttachmentBinary(HttpServletRequest request, Attachment attachment)
            throws PersistenceException {

        // Data import operation. Don't save file to local file system.
        if (request == null) {
            return null;
        }

        String path = getAttachmentAbsolutePath(attachment, request);
        try {
            FileUtil.write(path, attachment.getData());
        } catch (IOException e) {
            throw new PersistenceException("Unable to persist file " + attachment.getFilename()
                + "to path " + path, e);
        }

        final String context = request.getContextPath();
        final String urlPath = context + PATH_FORMAT.format(new Object[]{ attachment.getGuid(),attachment.getFilename()});
        return urlPath;
    }

    private String getAttachmentAbsolutePath(Attachment attachment, HttpServletRequest req) {
        String relative = PATH_FORMAT.format(new Object[]{attachment.getGuid(), attachment.getFilename()});
        String path = req.getSession().getServletContext().getRealPath(relative);
        return path;
    }

    @Override
    public boolean isPersistenceIntoDatabaseRequired() {
        return true;
    }
}
