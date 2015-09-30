<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<c:set var="includeJQuery" value="${renderRequest.preferences.map['includeJQuery'][0]}"/>
<c:set var="n"><portlet:namespace/></c:set>
<portlet:actionURL var="formUrl" escapeXml="false"><portlet:param name="action" value="updateConfiguration"/></portlet:actionURL>
<portlet:actionURL var="cancelUrl"><portlet:param name="action" value="cancelUpdate"/></portlet:actionURL>
<%--<portlet:resourceURL var="previewUrl" id="preview" escapeXml="false"/>--%>

<c:if test="${includeJQuery}">
    <rs:aggregatedResources path="skin.xml"/>
</c:if>
<script src="<rs:resourceURL value='/rs/ckeditor/4.3.2/ckeditor.js'/>" type="text/javascript"></script>

<style type="text/css">
    #${n}contentForm { min-height: 100px; padding: 10px; margin: 10px; }
    .cke_source { color: #000000; }
</style>

<h2><spring:message code="configurationForm.title"/></h2>

<form:form id="${n}contentForm" commandName="form" action="${formUrl}" method="post">
    <form:textarea id="${n}content" path="content"/>

    <p>
        <a href="${ cancelUrl }"><spring:message code="configurationForm.return"/></a>
    </p>   
     
</form:form>
    
<script type="text/javascript">
<rs:compressJs>
    var ${n} = ${n} || {};
<c:choose>
    <c:when test="${!usePortalJsLibs}">
        ${n}.jQuery = jQuery.noConflict(true);
    </c:when>
    <c:otherwise>
        ${n}.jQuery = up.jQuery;
    </c:otherwise>
</c:choose>
    ${n}.jQuery(function(){
        var $ = ${n}.jQuery;
        $(document).ready(function(){
            CKEDITOR.dtd.$removeEmpty['span'] = false;  // allow empty span elements for font-awesome
            // Create an CKEditor 4.x Editor
            CKEDITOR.replace('${n}content', {
                toolbarGroups : [
                    { name: 'document',    groups: [ 'mode', 'document', 'doctools' ] },
                    { name: 'clipboard',   groups: [ 'clipboard', 'undo' ] },
                    { name: 'editing',     groups: [ 'find', 'selection', 'spellchecker' ] },
                    { name: 'tools' },
                    { name: 'others' },
                    { name: 'about' },
                    '/',
                    { name: 'basicstyles', groups: [ 'basicstyles', 'cleanup' ] },
                    { name: 'paragraph',   groups: [ 'list', 'indent', 'blocks', 'align' ] },
                    { name: 'links' },
                    '/',
                    { name: 'styles' },
                    { name: 'colors' },
                    { name: 'insert' }
                ],
                filebrowserUploadUrl : '/SimpleContentPortlet/api/content/attach/local',
                allowedContent: true
            });
        });
        
    });
</rs:compressJs>
</script>