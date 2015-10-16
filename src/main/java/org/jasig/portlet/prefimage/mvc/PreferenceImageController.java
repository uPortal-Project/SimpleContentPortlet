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
package org.jasig.portlet.prefimage.mvc;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jasig.portlet.attachment.controller.AttachmentsController;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for handling image files that are to be used as portlet preferences (where a portlet preference specifies a URL to an
 * image).  Can be used to upload a "candidate" image file to be used for a portlet preference.  After being uploaded, the candidate 
 * image should then be either canceled (determined to not be needed, and therefore deleted) or confirmed (determined to be 
 * used with the preference, thereby replacing any existing image that may currently be used with the preference).  This supports a 
 * UI where an image can be uploaded, but the preference value changes are subsequently canceled or saved (confirmed) as a separate 
 * step.  The idea is not to continue storing image files that are not really being used.
 */
@Controller
public final class PreferenceImageController {

    private static final String RELATIVE_ROOT = "/content";
    private static final String CANDIDATE_POSTFIX = "-CANDIDATE";
    private static final MessageFormat PATH_FORMAT = new MessageFormat(RELATIVE_ROOT + "/{0}/{1}");

    @Autowired
    private IAttachmentService attachmentService = null;

    @RequestMapping(value = "/content/imagepref/candidate/{prefId}/upload", method = RequestMethod.POST)
    public ModelAndView uploadCandidate(
            @PathVariable String prefId, 
            @RequestParam(value = "qqfile") MultipartFile file, 
            HttpServletRequest servletRequest) throws IOException {
        final Map<String, String> model = new HashMap<String, String>();
        final String user = this.getUserFromRequest(servletRequest);
        final HttpServletRequest request = new PreferenceImageServletRequestWrapper(servletRequest, user);
        this.deleteAnyExistingPreferenceImageFileCandidates(request, prefId);
        if (file != null) {
            final PreferenceImageFile newCandidate = this.savePreferenceImageFileCandidate(request, file, prefId);
            this.addPreferenceImageFileInfoToModel(model, newCandidate);
        }
        return new ModelAndView("jsonView", model);
    }

    @RequestMapping(value = "/content/imagepref/candidate/{prefId}/cancel", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void cancelCandidate(@PathVariable String prefId, HttpServletRequest servletRequest) throws IOException {
        final String user = this.getUserFromRequest(servletRequest);
        final HttpServletRequest request = new PreferenceImageServletRequestWrapper(servletRequest, user);
        this.deleteAnyExistingPreferenceImageFileCandidates(request, prefId);
    }

    @RequestMapping(value = "/content/imagepref/candidate/{prefId}/confirm", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void confirmCandidate(@PathVariable String prefId, HttpServletRequest servletRequest) throws IOException {
        final String user = this.getUserFromRequest(servletRequest);
        final HttpServletRequest request = new PreferenceImageServletRequestWrapper(servletRequest, user);
        this.confirmPreferenceImageFileCandidate(request, prefId);
    }

    @RequestMapping(value = "/content/imagepref/{prefId}/delete", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.OK)
    public void deletePreferenceImage(@PathVariable String prefId, HttpServletRequest servletRequest) throws IOException {
        final String user = this.getUserFromRequest(servletRequest);
        final HttpServletRequest request = new PreferenceImageServletRequestWrapper(servletRequest, user);
        this.deleteImages(request, prefId);
    }

    private String getUserFromRequest(final HttpServletRequest request) {
        return (String)request.getSession().getAttribute(AttachmentsController.REMOTE_USER_ATTR);
    }

    private void deleteAnyExistingPreferenceImageFileCandidates(final HttpServletRequest request, final String prefId) {
        this.deleteImages(request, prefId + CANDIDATE_POSTFIX);
    }

    private void deleteImages(final HttpServletRequest request, final String filename) {
        final List<Attachment> attachments = this.attachmentService.find(request.getRemoteUser(), filename);
        for (Attachment attachment : attachments) {
            this.attachmentService.delete(attachment.getId());
        }
    }

    private PreferenceImageFile savePreferenceImageFileCandidate(
            final HttpServletRequest request, final MultipartFile file, final String prefId) throws IOException {
        Attachment attachment = generateAttachment(file, request, prefId + CANDIDATE_POSTFIX);
        attachment = this.saveAttachment(request, attachment);
        return new PreferenceImageFile(prefId, attachment);
    }

    private Attachment saveAttachment(final HttpServletRequest request, final Attachment attachment) {
        return this.attachmentService.save(attachment, request.getRemoteUser());
    }

    private void addPreferenceImageFileInfoToModel(final Map<String,String> model, final PreferenceImageFile file) {
        model.put("prefId",  file.getPrefId());
        model.put("id", Long.toString(file.getFileId()));
        model.put("guid", file.getGuid());
        model.put("path", file.getPath());
        model.put("pathOnceConfirmed", file.getPath().replace(file.getFilename(), file.getPrefId()));
        model.put("filename", file.getFilename());
        model.put("filenameOnceConfirmed", file.getPrefId());
        model.put("success","true");
    }

    private void confirmPreferenceImageFileCandidate(final HttpServletRequest request, final String prefId) {
        final List<Attachment> results = this.attachmentService.find(request.getRemoteUser(), prefId + CANDIDATE_POSTFIX);
        if (results.size() == 0) {
            throw new PreferenceImageNotFoundException(prefId);
        }
        this.deleteImages(request, prefId); // delete current pref image file
        for (Attachment attachment : results) {
            attachment.setFilename(prefId);
            this.attachmentService.save(attachment, request.getRemoteUser());
        }
    }

    private static Attachment generateAttachment(
            final MultipartFile file, final HttpServletRequest req, final String filename) throws IOException {
        final Attachment attachment = new Attachment();
        final String context = req.getContextPath();
        final String path = context + PATH_FORMAT.format(new Object[]{ attachment.getGuid(),filename });
        attachment.setFilename(filename);
        attachment.setPath(path);
        attachment.setData(file.getBytes());
        return attachment;
    }

    class PreferenceImageFile {
        private String prefId;
        private Attachment attachment;
        public PreferenceImageFile(final String prefId, final Attachment attachment) {
            this.prefId = prefId;
            this.attachment = attachment;
        }
        public String getPrefId() {
            return this.prefId;
        }
        public long getFileId() {
            return this.attachment.getId();
        }
        public String getFilename() {
            return this.attachment.getFilename();
        }
        public String getGuid() {
            return this.attachment.getGuid();
        }
        public String getPath() {
            return this.attachment.getPath();
        }
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public class PreferenceImageNotFoundException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public PreferenceImageNotFoundException(final String prefId) {
            super("Preference image not found for: " + prefId);
        }
    }

}
