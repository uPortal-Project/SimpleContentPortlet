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
package org.jasig.portlet.attachment.mvc;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.util.FileHelper;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Chris Waymire (chris@waymire)
 */
public class AttachmentHelper {
    public static String ATTACHMENTS_REL_ROOT = "/attachments";
    public static String ATTACHMENTS_ABS_ROOT = "/WEB-INF" + ATTACHMENTS_REL_ROOT;

    public static String getAttachmentRelativePath(Attachment attachment)
    {
        return ATTACHMENTS_REL_ROOT + "/" + attachment.getGuid() + "/" + attachment.getFilename();
    }

    public static String getAttachmentAbsolutePath(Attachment attachment)
    {
        return ATTACHMENTS_ABS_ROOT + "/" + attachment.getGuid() + "/" + attachment.getFilename();
    }

    public static String getAttachmentFilesystemPath(Attachment attachment,HttpServletRequest request) throws IOException
    {
        String root = request.getSession().getServletContext().getRealPath(ATTACHMENTS_ABS_ROOT);
        return root + File.separator + attachment.getGuid() + File.separator + attachment.getFilename();
    }

    public static String getAttachmentFilesystemPath(String filename,HttpServletRequest request) throws IOException
    {
        String root = request.getSession().getServletContext().getRealPath(ATTACHMENTS_ABS_ROOT);
        return root + File.separator + filename;
    }

    public static void writeAttachmentToDisk(Attachment attachment,HttpServletRequest request) throws IOException
    {
        String dest = getAttachmentFilesystemPath(attachment,request);
        FileHelper.write(dest,attachment.getContent());
    }

    public static void writeAttachmentToDisk(Attachment attachment,String content,HttpServletRequest request) throws IOException
    {
        String dest = getAttachmentFilesystemPath(attachment,request);
        FileHelper.write(dest,content);
    }

    public static Attachment createAttachmentFromFile(MultipartFile file,HttpServletRequest request) throws IOException
    {
        String fileNameParam = request.getParameter("filename");
        String filename = StringUtils.isEmpty(fileNameParam) ? file.getOriginalFilename() : fileNameParam;
        Attachment attachment = new Attachment();
        attachment.setFilename(filename);
        attachment.setContent(new String(file.getBytes()));
        return attachment;
    }

    private AttachmentHelper() { }
}
