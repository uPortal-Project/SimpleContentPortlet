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
<jsp:directive.include file="/WEB-INF/jsp/common/include.jsp"/>
<c:set var="n"><portlet:namespace/></c:set>
<portlet:actionURL var="formUrl" escapeXml="false"><portlet:param name="action" value="updateConfiguration"/></portlet:actionURL>
<portlet:renderURL var="cancelUrl" portletMode="VIEW" />

<style type="text/css">
    #${n}contentForm { min-height: 100px; padding: 10px; margin: 10px; }
    #${n}contentForm div.btn-group {
        display: block;
    }
    #${n}contentForm textarea {
         width: 100%;
         display: block;
         font-family: monospace;
         white-space: pre-wrap;
     }
</style>

<h2><spring:message code="configurationForm.title"/></h2>

<form:form id="${n}contentForm" modelAttribute="form" action="${formUrl}" method="post">
    <form:textarea id="${n}content" path="content" cols="100" rows="20" wrap="hard"/>
    <div class="btn-group" style="margin: 5px 0 10px 0;">
      <button type="submit" name="update" value="update" class="btn-primary">
        <spring:message code="configurationForm.save"/>
      </button>
      <form action="${cancelUrl}" method="post">
      <button type="submit" name="cancel" value="cancel" class="btn-default">
        <spring:message code="configurationForm.return"/>
      </button>
      </form>
    </div>
</form:form>
