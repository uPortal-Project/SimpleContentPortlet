<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<c:set var="includeJQuery" value="${renderRequest.preferences.map['includeJQuery'][0]}"/>
<c:set var="n"><portlet:namespace/></c:set>
<portlet:actionURL var="formUrl" escapeXml="false"><portlet:param name="action" value="updateConfiguration"/></portlet:actionURL>
<portlet:actionURL var="cancelUrl"><portlet:param name="action" value="cancelUpdate"/></portlet:actionURL>
<portlet:resourceURL var="previewUrl" id="preview" escapeXml="false"/>

<c:if test="${includeJQuery}">
    <rs:aggregatedResources path="skin.xml"/>
</c:if>

<style type="text/css">
    #${n}contentForm .flc-inlineEdit-text { min-height: 100px; border: thin dashed #666; padding: 10px; margin: 10px; }
</style>

<h2>Content Preview</h2>
<p>Click the content below to begin editing</p>

<form:form id="${n}contentForm" commandName="form" action="${formUrl}" method="post">

    <div class="flc-inlineEdit-text">
        ${ form.content }
    </div>

    <div class="flc-inlineEdit-editContainer" style="display:none">

        <form:textarea path="content"/>

        <button class="portlet-form-button portlet-button button portlet-button-primary primary save">
            <spring:message code="configurationForm.preview"/>
        </button>

        <button class="cancel portlet-form-button portlet-button button portlet-button-secondary secondary">
            <spring:message code="configurationForm.cancel"/>
        </button>

    </div>
    
    <div class="save-configuration-button portlet-button-group buttons">
        <input class="portlet-form-button portlet-button portlet-button-primary" type="submit" value="<spring:message code="configurationForm.save"/>"/>
    </div>

    <p>
        <a href="${ cancelUrl }">
            <spring:message code="configurationForm.return"/>
        </a>
    </p>   
     
</form:form>
    
<script type="text/javascript">
<rs:compressJs>
    var ${n} = ${n} || {};
    ${n}.jQuery = jQuery.noConflict(${includeJQuery});
    ${n}.fluid = fluid;
    <c:if test="${includeJQuery}">fluid = null; fluid_1_2 = null;</c:if>
    
    ${n}.scriptCapableViewAccessor = function (element) {
        return {
            value: function (newValue) {
                if (newValue) {
                    element.innerHTML = newValue;
                    return ${n}.jQuery(element);
                } else {
                    return element.innerHTML;
                }
            }
        };
    };
    
    ${n}.jQuery(function(){
        var $ = ${n}.jQuery;
        var fluid = ${n}.fluid;
        var cleanContent = ${cleanContent};

        var makeButtons = function (editor) {
            $(".save", editor.container).click(function(){
                editor.finish();
                return false;
            });

            $(".cancel", editor.container).click(function(){
                editor.cancel();
                $(".save-configuration-button").show();
                return false;
            });
        }; 

        $(document).ready(function(){
            // Create an CKEditor 3.x-based Rich Inline Edit component.
            var ckEditor = fluid.inlineEdit.CKEditor("#${n}contentForm", {
                displayAccessor: {
                    type: "${n}.scriptCapableViewAccessor"
                },
                listeners: {
                    onBeginEdit: function(){ $(".save-configuration-button").hide(); },
                    afterFinishEdit: function(newVal, old, edit, view){
                        if (cleanContent) {
                            $.ajax({
                                url: "${ previewUrl }",
                                data: { content: newVal },
                                dataType: "json",
                                async: false,
                                type: "POST",
                                success: function(data) {
                                    ckEditor.updateModelValue(data.content);
                                }
                            });
                        } else {
                            ckEditor.updateModelValue(newVal);                            
                        }
                        $(".save-configuration-button").show();
                    }
                }
            });
            makeButtons(ckEditor);  
        });
        
    });
</rs:compressJs>
</script>