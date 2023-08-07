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
<c:if test="${!usePortalJsLibs}">
    <script src="<rs:resourceURL value='/rs/jquery/1.11.0/jquery-1.11.0.min.js'/>" type="text/javascript"></script>
</c:if>

<script src="<c:url value='/webjars/fine-uploader/5.13.0/dist/fine-uploader.min.js'/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/fine-uploader/5.13.0/dist/fine-uploader.min.css'/>" />
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/fine-uploader/5.13.0/dist/fine-uploader-gallery.css'/>" />
<link rel="stylesheet" type="text/css" href="<c:url value='/css/attachments.css'/>" />

<div id="${n}attachments">
    <div class="lb_backdrop"></div>
    <div class="lb_container">
        <div class="fl-widget portlet" role="section">
            <div class="fl-widget-titlebar portlet-title" role="sectionhead">
                <h2 role="heading">Attachments</h2>
            </div> <!-- end: portlet-title -->
            <div class="fl-widget-content portlet-body" role="main">
                <div class="portlet-section" role="region">
                    <div class="lb_body">
                        <div id="${n}file-uploader"></div>
                    </div>
                </div>
                <div class="portlet-section" role="region">
                    <div class="lb_controls">
                    <input type="button" id="${n}triggerUpload" class="btn btn-primary" value="Upload"/>
                    <input type="button" id="${n}cancelUpload" class="btn btn-primary lb_close" value="Cancel"/>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>


<script type="text/template" id="qq-template-gallery">
    <div class="qq-uploader-selector qq-uploader qq-gallery" qq-drop-area-text="Drop files here">
        <div class="qq-total-progress-bar-container-selector qq-total-progress-bar-container">
            <div role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" class="qq-total-progress-bar-selector qq-progress-bar qq-total-progress-bar"></div>
        </div>
        <div class="qq-upload-drop-area-selector qq-upload-drop-area" qq-hide-dropzone>
            <span class="qq-upload-drop-area-text-selector"></span>
        </div>
        <div class="qq-upload-button-selector qq-upload-button">
            <div>Upload a file</div>
        </div>
        <span class="qq-drop-processing-selector qq-drop-processing">
            <span>Processing dropped files...</span>
            <span class="qq-drop-processing-spinner-selector qq-drop-processing-spinner"></span>
        </span>
        <ul class="qq-upload-list-selector qq-upload-list" role="region" aria-live="polite" aria-relevant="additions removals">
            <li>
                <span role="status" class="qq-upload-status-text-selector qq-upload-status-text"></span>
                <div class="qq-progress-bar-container-selector qq-progress-bar-container">
                    <div role="progressbar" aria-valuenow="0" aria-valuemin="0" aria-valuemax="100" class="qq-progress-bar-selector qq-progress-bar"></div>
                </div>
                <span class="qq-upload-spinner-selector qq-upload-spinner"></span>
                <div class="qq-thumbnail-wrapper">
                    <img class="qq-thumbnail-selector" qq-max-size="120" qq-server-scale>
                </div>
                <button type="button" class="qq-upload-cancel-selector qq-upload-cancel">X</button>
                <button type="button" class="qq-upload-retry-selector qq-upload-retry">
                    <span class="qq-btn qq-retry-icon" aria-label="Retry"></span>
                    Retry
                </button>

                <div class="qq-file-info">
                    <div class="qq-file-name">
                        <span class="qq-upload-file-selector qq-upload-file"></span>
                        <span class="qq-edit-filename-icon-selector qq-edit-filename-icon" aria-label="Edit filename"></span>
                    </div>
                    <input id="${n}filename" class="qq-edit-filename-selector qq-edit-filename" tabindex="0" type="text">
                    <span class="qq-upload-size-selector qq-upload-size"></span>
                    <button type="button" class="qq-btn qq-upload-delete-selector qq-upload-delete">
                        <span class="qq-btn qq-delete-icon" aria-label="Delete"></span>
                    </button>
                    <button type="button" class="qq-btn qq-upload-pause-selector qq-upload-pause">
                        <span class="qq-btn qq-pause-icon" aria-label="Pause"></span>
                    </button>
                    <button type="button" class="qq-btn qq-upload-continue-selector qq-upload-continue">
                        <span class="qq-btn qq-continue-icon" aria-label="Continue"></span>
                    </button>
                </div>
            </li>
        </ul>

        <dialog class="qq-alert-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <div class="qq-dialog-buttons">
                <button type="button" class="qq-cancel-button-selector">Close</button>
            </div>
        </dialog>

        <dialog class="qq-confirm-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <div class="qq-dialog-buttons">
                <button type="button" class="qq-cancel-button-selector">No</button>
                <button type="button" class="qq-ok-button-selector">Yes</button>
            </div>
        </dialog>

        <dialog class="qq-prompt-dialog-selector">
            <div class="qq-dialog-message-selector"></div>
            <input type="text">
            <div class="qq-dialog-buttons">
                <button type="button" class="qq-cancel-button-selector">Cancel</button>
                <button type="button" class="qq-ok-button-selector">Ok</button>
            </div>
        </dialog>
    </div>
</script>

<script type="text/javascript">
    var ${n} = ${n} || {};

    <c:choose>
        <c:when test="${!usePortalJsLibs}">
            ${n}.jQuery = jQuery.noConflict(true);
        </c:when>
        <c:otherwise>
            <c:set var="ns"><c:if test="${ not empty portalJsNamespace }">${ portalJsNamespace }.</c:if></c:set>
            ${n}.jQuery = ${ ns }jQuery;
        </c:otherwise>
    </c:choose>
    
    var upAttachments = upAttachments || {};

    ${n}.jQuery(function () {
        var $ = ${n}.jQuery;

        var open_box = function()
        {
            var $ = ${n}.jQuery;
            $("#${n}attachments .lb_backdrop, #${n}attachments .lb_container").animate({'opacity': '.50'}, 300, 'linear');
            $("#${n}attachments .lb_container").animate({'opacity': '1.00'}, 300, 'linear');
            $("#${n}attachments .lb_backdrop, #${n}attachments .lb_container").css('display', 'block');
        }

        var close_box = function()
        {
            var $ = ${n}.jQuery;
            $("#${n}attachments .lb_backdrop, #${n}attachments .lb_container").animate({'opacity':'0'}, 300, 'linear', function(){
                $("#${n}attachments .lb_backdrop, #${n}attachments .lb_container").css('display', 'none');
            });
        }
        
        if (!upAttachments.show) {
            upAttachments = {
                hide: function() {
                    var $ = ${n}.jQuery;
                    $("#${n}filename").val('');
                    $("#${n}source").val('');
                    close_box();
                },
                show: function(callback) {
                    var $ = ${n}.jQuery;
                    var uploader = new qq.FineUploader({
                        element: $("#${n}file-uploader")[0],
                        template: 'qq-template-gallery',
                        request: {
                            endpoint: "<c:url value='/api/content/attach/local.json'/>",
                            forceMultipart: true,
                            paramsInBody: true,
                            params: {
                                filename: function () { return $("#${n}filename").val(); },
                                source: function () { return $("#${n}source").val(); }
                            }
                        },

                        multiple: false,

                        validation: {
                            allowedExtensions: [],
                            sizeLimit: 20971520,
                            stopOnFirstInvalidFile: true
                        },

                        autoUpload: false,

                        text: {
                            uploadButton: "Select File",
                            cancelButton: 'Cancel',
                            retryButton: 'Retry',
                            failUpload: 'Upload failed',
                            dragZone: 'Drop files here to upload',
                            formatProgress: "{percent}% of {total_size}",
                            waitingForResponse: "Processing..."
                        },

                        messages: {
                            typeError: "{file} has an invalid extension. Valid extension(s): {extensions}.",
                            sizeError: "{file} is too large, maximum file size is {sizeLimit}.",
                            minSizeError: "{file} is too small, minimum file size is {minSizeLimit}.",
                            emptyError: "{file} is empty, please select files again without it.",
                            noFilesError: "No files to upload.",
                            onLeave: "The files are being uploaded, if you leave now the upload will be cancelled."
                        },

                        debug: true,

                        callbacks: {
                            onSubmit: function (id, filename) {
                                var $ = ${n}.jQuery;
                            },
                            onComplete: function (id, fileName, responseJSON) {
                                var $ = ${n}.jQuery;
                                delete responseJSON.success;
                                callback(responseJSON);
                            },
                            showMessage: function (message) {
                                var $ = ${n}.jQuery;
                                $("#${n}file-uploader").append('<div class="alert alert-error">' + message + '</div>');
                            }
                        }

                    });

                    $("#${n}attachments .lb_close, #${n}attachments .lb_backdrop").click(function () {
                        var $ = ${n}.jQuery;
                        uploader.reset();
                        $("#${n}filename").val('');
                        close_box();
                    });

                    $("#${n}triggerUpload").click(function () {
                        uploader.uploadStoredFiles();
                    })

                    open_box();
                }
            };
        }

    });
</script>