package org.jasig.portlet.attachment.dao.jpa;

public class Queries {
    public static final String GET_FOLDERS = "Folder.getFolders";
    public static final String GET_FOLDERS_BY_PARENT = "Folder.getByParent";

    public static final String GET_THIN_ATTACHMENT = "Attachment.getThinAttachment";
    public static final String GET_ATTACHMENTS = "Attachment.getAttachments";
    public static final String GET_ATTACHMENTS_BY_FOLDER = "Attachment.getByFolder";
    public static final String GET_ATTACHMENT_CONTENT = "Attachment.getContent";
    public static final String ATTACHMENT_EXISTS = "Attachment.exists";
    public static final String UPDATE_ATTACHMENT_LAST_ACCESSED_AT = "Attachment.updateLastAccessedAt";
    private Queries() {}
}
