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

import java.text.MessageFormat;
import org.apache.commons.lang.StringUtils;
import org.jasig.portlet.attachment.controller.AttachmentsController;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.jasig.portlet.util.DataUtil;
import org.jasig.portlet.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Chris Waymire (chris@waymire)
 */
@Controller
public final class LocalAttachmentController {

    private static final String RELATIVE_ROOT = "/content";

    private static final MessageFormat PATH_FORMAT = new MessageFormat(RELATIVE_ROOT + "/{0}/{1}");

    @Autowired
    private IAttachmentService attachmentService = null;

    @RequestMapping(value = "/content/attach/local.json", method = RequestMethod.POST)
    public ModelAndView uploadForm(@RequestParam(value = "qqfile") MultipartFile file,
                                  HttpServletRequest servletRequest)
            throws IOException
    {
        final Map<String, String> model = new HashMap<String, String>();
        final String user = (String)servletRequest.getSession().getAttribute(AttachmentsController.REMOTE_USER_ATTR);
        final HttpServletRequest request = new AttachmentServletRequestWrapper(servletRequest,user);

        if(file != null)
        {
            Attachment attachment = generateAttachment(file, request);
            attachment = attachmentService.save(attachment, request);
            if(attachment.getId() > 0)
            {
                String path = getAttachmentAbsolutePath(attachment, request);
                FileUtil.write(path,file.getBytes());
                model.put("id",Long.toString(attachment.getId()));
                model.put("guid",attachment.getGuid());
                model.put("path", attachment.getPath());
                model.put("filename",attachment.getFilename());
                model.put("success","true");
            }
        }

        return new ModelAndView("jsonView",model);
    }

    private String getAttachmentAbsolutePath(Attachment attachment,HttpServletRequest request)
    {
        String relative = PATH_FORMAT.format(new Object[]{attachment.getGuid(), attachment.getFilename()});
        String path = request.getSession().getServletContext().getRealPath(relative);
        return path;
    }

    private static Attachment generateAttachment(MultipartFile file,HttpServletRequest request) throws IOException {
        final Attachment attachment = new Attachment();
        final String fileNameParam = request.getParameter("filename");
        final String filename = StringUtils.isEmpty(fileNameParam) ? file.getOriginalFilename() : fileNameParam;
        final String content = DataUtil.encodeAsString(file.getBytes());
        final String context = request.getContextPath();
        final String path = context + PATH_FORMAT.format(new Object[]{ attachment.getGuid(),filename });

        attachment.setFilename(filename);
        attachment.setPath(path);
        attachment.setData(content);
        return attachment;
    }
}
