# Attachments

Attachments are stored in the `SCM_ATTACHMENTS` table and in the `content`
directory.

If the servlet container (Tomcat) service account is differnt from the
account that deployed the portlet, the service account needs to
have write access to `$CATALINA_HOME/webapps/SimpleContentPortlet/content`.
