package org.jasig.portlet.attachment.mvc;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class AttachmentServletRequestWrapper extends HttpServletRequestWrapper {

    private final String remoteUser;

    public AttachmentServletRequestWrapper(HttpServletRequest request,String remoteUser)
    {
        super(request);
        this.remoteUser = remoteUser;
    }

    @Override
    public String getRemoteUser()
    {
        return this.remoteUser;
    }
}
