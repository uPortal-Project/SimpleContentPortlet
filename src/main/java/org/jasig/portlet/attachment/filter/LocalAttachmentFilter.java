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
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.jasig.portlet.util.DataUtil;
import org.jasig.portlet.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
public class LocalAttachmentFilter implements Filter {
    @Autowired
    private IAttachmentService attachmentService = null;

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        String relative = httpServletRequest.getServletPath();
        String path = httpServletRequest.getSession().getServletContext().getRealPath(relative);
        File file = new File(path);
        if(!file.exists())
        {
            String[] parts = path.split("/");
            int guidIndex = parts.length - 2;
            String guid = parts[guidIndex];
            Attachment attachment = attachmentService.get(guid,httpServletRequest);
            String content = DataUtil.decodeAsString(attachment.getData());
            FileUtil.write(path,content);
        }

        chain.doFilter(request,response);
    }

    public void destroy() {
    }
}
