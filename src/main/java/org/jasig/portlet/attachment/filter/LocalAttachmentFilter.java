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
package org.jasig.portlet.attachment.filter;

import java.io.File;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.jasig.portlet.attachment.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
public final class LocalAttachmentFilter implements Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private IAttachmentService attachmentService;

    public void init(FilterConfig filterConfig) throws ServletException {}

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String relative = httpServletRequest.getServletPath();
        String path = httpServletRequest.getSession().getServletContext().getRealPath(relative);
        log.debug("Looking up file {}", path);
        File file = new File(path);

        // If file is present on the hard drive, let the Tomcat default servlet serve it up.  Otherwise, hydrate
        // it from the database onto the file system.
        if(!file.exists()) {
            String[] parts = path.split("/");
            int guidIndex = parts.length - 2;
            String guid = parts[guidIndex];

            Attachment attachment = attachmentService.findByGuid(guid);
            if (attachment != null) {
                log.debug("Restoring the following  attachment to the server file system:  {}", path);
                byte[] content = attachment.getData();
                // If the content is not null, save it to the file system.  If the content is null, the
                // attachment may have been saved with v1.2.x or prior which used a Base64-encoded text field
                // to hold the content instead of a BLOB. In that case, return an error and message.
                if (content != null) {
                    FileUtil.write(path, content);

                    // CMSPLT-38 First time request for a file is not found after file creation. For some reason,
                    // Tomcat's default servlet does not return the file even though it is hydrated onto the file system.
                    // Attempted to return a 302 with the same URL hoping Tomcat's default servlet would return the
                    // file but the file is still not returned. Unfortunately, we have to duplicate what Tomcat's
                    // default servlet will do and return the content. Next time though, Tomcat's default servlet
                    // will return the content just fine.
                    file = new File(path);
                    HttpServletResponse httpResponse = (HttpServletResponse) response;

                    String contentType = httpServletRequest.getSession().getServletContext().getMimeType(path);
                    httpResponse.setHeader("Content-Type", contentType);
                    httpResponse.setDateHeader("Last-Modified", file.lastModified());
                    httpResponse.setHeader("Content-Length", Long.toString(file.length()));
                    httpResponse.setStatus(HttpServletResponse.SC_OK);
                    httpResponse.getOutputStream().write(content);
                    httpResponse.flushBuffer();
                    return;
                } else {
                    log.error("Attachment guid {} has no data.  If you were using SimpleContent 1.2.x or prior, you must"
                                    + " export the data from your database using v1.2.x and import it using v2.0.0+. Follow the"
                                    + " instructions at https://wiki.jasig.org/display/PLT/Upgrading+from+1.2.x+or+prior+to+2.0.0",
                            attachment.getGuid());

                    String notFoundResponse = "<HTML><BODY><P>File content not found. Report issue to system"
                            + " administrator. They may need to export attachments from"
                            + " prior version and import into new version</P></BODY></HTML>";
                    HttpServletResponse httpResponse = (HttpServletResponse) response;
                    httpResponse.setHeader("Content-Type", "text/html");
                    httpResponse.setHeader("Content-Length", Long.toString(notFoundResponse.length()));
                    httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    httpResponse.getOutputStream().write(notFoundResponse.getBytes());
                    httpResponse.flushBuffer();
                    return;
                }
            } else {
                log.info("Attachment not found: {}", path);
            }
        }

        chain.doFilter(request,response);

    }

    public void destroy() {}

}
