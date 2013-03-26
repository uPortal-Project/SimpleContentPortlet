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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.attachment.controller.AttachmentsController;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.jasig.portlet.util.FileHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Waymire (chris@waymire)
 */
@Controller
public class UploadController {

    private static final Log log = LogFactory.getLog(UploadController.class);

    @Autowired
    private IAttachmentService attachmentService = null;

    @Autowired
    private ServletContext servletContext;

    @Autowired
    private TaskPool taskPool;

    @RequestMapping(value = "/upload.json", method = RequestMethod.POST)
    public ModelAndView uploadForm(@RequestParam(value = "qqfile") MultipartFile file,
                                  HttpServletRequest servletRequest)
            throws IOException
    {
        final Map<String, String> model = new HashMap<String, String>();
        final String user = (String)servletRequest.getSession().getAttribute(AttachmentsController.REMOTE_USER_ATTR);
        final HttpServletRequest request = new AttachmentServletRequestWrapper(servletRequest,user);

        if(file != null)
        {
            Attachment attachment = AttachmentHelper.createAttachmentFromFile(file,request);
            attachment = attachmentService.saveAttachment(request,attachment);
            if(attachment.getId() > 0)
            {
                String path = AttachmentHelper.getAttachmentFilesystemPath(attachment,request);
                Task task = new FileWriteTask(path,file.getBytes());
                taskPool.add(task);
                populateModel(model,attachment);
                model.put("success","true");
            }
        } else {
            model.put("success","false");
        }

        return new ModelAndView("jsonView",model);
    }

    @RequestMapping(value = "/download/{id}.json")
    public void download(@PathVariable Long id,
                         HttpServletRequest request,HttpServletResponse response)
    {
        try
        {
            Attachment attachment = attachmentService.getThinAttachmentById(request,id);
            attachmentService.updateLastAccessedAt(attachment.getId());

            String path = AttachmentHelper.getAttachmentFilesystemPath(attachment,request);
            byte[] bytes = FileHelper.read(path);
            if((bytes == null) || (bytes.length == 0))
            {
                String content = attachmentService.getAttachmentContent(request,id);
                String file = AttachmentHelper.getAttachmentFilesystemPath(attachment,request);
                Task task = new FileWriteTask(file,content);
                taskPool.add(task);
                bytes = content.getBytes();
                response.getOutputStream().write(content.getBytes());
                response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getFilename());
                response.flushBuffer();
            }

            response.getOutputStream().write(bytes);
            response.setHeader("Content-Disposition", "attachment; filename=" + attachment.getFilename());
            response.flushBuffer();
        } catch (Exception exception) {
            log.info("Error writing attachment to output stream. Attachment ID was " + id);
        }
    }

    @RequestMapping(value = "/download/{uuid}/{name}.json")
    public void download(@PathVariable String uuid,@PathVariable String name,
                         HttpServletRequest request,HttpServletResponse response)
    {
        String path = uuid + "/" + name;
        try
        {
            File file = new File(AttachmentHelper.getAttachmentFilesystemPath(path,request));
            byte[] bytes = FileHelper.read(file);

            response.getOutputStream().write(bytes);
            response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
            response.flushBuffer();

        } catch (Exception exception) {
            log.info("Error writing attachment to output stream. Attachment filename was " + path);
        }
    }

    @RequestMapping(value = "/details/{id}.json")
    public ModelAndView attachmentInfo(@PathVariable Long id,
                                       HttpServletRequest request)
    {
        final Map<String, String> model = new HashMap<String, String>();
        if(id != null)
        {
            Attachment attachment = attachmentService.getAttachmentById(request,id);
            populateModel(model,attachment);
        }
        return new ModelAndView("jsonView",model);
    }

    @RequestMapping(value = "/exists/{name}.json")
    public ModelAndView attachmentExists(@PathVariable String name,
                                         HttpServletRequest request)
    {
        final Map<String,Object> model = new HashMap<String,Object>();
        Attachment attachment = new Attachment();
        attachment.setFilename(name);
        long existing = attachmentService.attachmentExists(request,attachment);
        model.put("exists",(existing > 0));
        return new ModelAndView("jsonView",model);
    }

    private void populateModel(Map<String,String> model,Attachment attachment)
    {
        model.put("attachmentId",Long.toString(attachment.getId()));
        model.put("attachmentPath", AttachmentHelper.getAttachmentRelativePath(attachment));
        model.put("attachmentFilename",attachment.getFilename());
    }
}
