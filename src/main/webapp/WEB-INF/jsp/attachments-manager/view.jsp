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
<script src="<c:url value='/webjars/pdfobject/2.0.201604172/pdfobject.min.js'/>"></script>
<link rel="stylesheet" type="text/css" href="<c:url value='/webjars/fine-uploader/5.13.0/dist/fine-uploader.min.css'/>" />
<link rel="stylesheet" type="text/css" href="<c:url value='/css/attachments.css'/>" />

<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
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
                        <input class="qq-edit-filename-selector qq-edit-filename" tabindex="0" type="text">
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

    <title>Fine Uploader Gallery View Demo</title>
</head>

<style type="text/css">
#content-wrapper{
    display:table;
    width: 100%;
}

#content{
    display:table-row;
}

#content>div{
    display:table-cell;
    width:50%;
    padding: 10px;
}

#detail_preview{
    max-width:100%;
}

div.col_content{
    width: 100%;
}

.form-group{
    padding: 0px 0px 10px 0px;
}

.pos_col{
    min-width: 93px;
}
</style>

<div>
    <div id="content">
        <div style="height:100px;overflow:auto;" id="left_col">
            <div id="fine-uploader-gallery"></div>
            <table id="filetable" class="table table-striped">
                <c:forEach items="${attachments}" var="attachment">
                    <tr id="${attachment.id}" data-filename="${attachment.filename}" data-contentType="${attachment.contentType}" data-path="${attachment.path}" data-createdBy="${attachment.createdBy}" data-createdAt="${attachment.createdAt}">
                        <td data-attribute="${attachment.filename}">${attachment.filename}</td>
                        <td data-attribute="${attachment.contentType}">${attachment.contentType}</td>
                    </tr>
                </c:forEach>
            </table>
        </div>
        <div id="right_col">
            <table id="detailstable" class="table">
                <tr>
                    <td>File name: </td>
                    <td id="detail_filename">Select a file</td>
                </tr>
                <tr>
                    <td>File type: </td>
                    <td id="detail_contentType"></td>
                </tr>
                <tr>
                    <td>File ID: </td>
                    <td id="detail_id"></td>
                </tr>
                <tr>
                    <td>Created By: </td>
                    <td id="detail_createdBy"></td>
                </tr>
                <tr>
                    <td>Created At: </td>
                    <td id="detail_createdAt"></td>
                </tr>
                <tr>
                    <td>URL: </td>
                    <td id="detail_path"></td>
                </tr>
                <tr>
                </tr>
            </table>
            <p>preview:</p>
            <div id="detail_preview">
                <img id="detail_preview_image" style="width:100%" src=""/>
                <div id="detail_preview_pdf" style="height:800px;"></div>
            </div>
        </div>
    </div>
</div>

<script>
function addRowHandlers() {
    var table = document.getElementById("filetable");
    var rows = table.getElementsByTagName("tr");
    for (i = 0; i < rows.length; i++) {
        var currentRow = table.rows[i];
        var createClickHandler = 
            function(row) 
            {
                return function() { 
                    var cell = row.getElementsByTagName("td")[0];
                    var id = cell.innerHTML;
                    updateDetails(row);
                 };
            };

        currentRow.onclick = createClickHandler(currentRow);
    }
}

function updateDetails(row)
{
    var detail = document.getElementById("detail_filename");
    detail.innerHTML = row.getAttribute("data-filename");

    detail = document.getElementById("detail_contentType");
    detail.innerHTML = row.getAttribute("data-contentType");

    detail = document.getElementById("detail_id");
    detail.innerHTML = row.id;

    detail = document.getElementById("detail_createdBy");
    detail.innerHTML = row.getAttribute("data-createdBy");

    detail = document.getElementById("detail_createdAt");
    detail.innerHTML = row.getAttribute("data-createdAt");

    detail = document.getElementById("detail_path");
    var path = "https://" + window.location.hostname + row.getAttribute("data-path");
    detail.innerHTML = "<a href=\"" + path + "\">"+path+"</a>";

    detail = document.getElementById("detail_preview");
    var detail_img = document.getElementById("detail_preview_image");
    var detail_pdf = document.getElementById("detail_preview_pdf");
    if (row.getAttribute("data-contentType").includes("pdf"))
    {
        PDFObject.embed(row.getAttribute("data-path"),"#detail_preview_pdf");
        detail_img.style.display = "none"; 
        detail_pdf.style.display = ""; 
    }
    else
    {
        detail_img.style.display = ""; 
        detail_img.src = row.getAttribute("data-path");
        detail_pdf.style.display = "none"; 
    }
}

$(document).ready(function(){
    addRowHandlers();
    });

    var galleryUploader = new qq.FineUploader({
        element: document.getElementById("fine-uploader-gallery"),
        template: 'qq-template-gallery',
        request: {
            endpoint: "<c:url value='/api/content/attach/local.json'/>"
        },
        callbacks: {
            onSubmit: function (id, filename) {
                var $ = ${n}.jQuery;
            },
            onComplete: function (id, fileName, responseJSON) {
                var table = document.getElementById("filetable");
                var row = table.insertRow(-1);
                var namecell = row.insertCell(0);
                var typecell = row.insertCell(1);
                namecell.innerHTML = responseJSON['filename'];
                var extloc = responseJSON['filename'].lastIndexOf(".");
                typecell.innerHTML = responseJSON['filename'].substring(extloc);

                row.setAttribute("id",responseJSON['id']);
                row.setAttribute("data-filename",responseJSON['filename']);
                row.setAttribute("data-contentType",responseJSON['filename'].substring(extloc));
                row.setAttribute("data-createdBy","You");
                row.setAttribute("data-createdAt","just now");
                row.setAttribute("data-path",responseJSON['path']);
                
                var createClickHandler = 
                    function(row) 
                    {
                        return function() { 
                            var cell = row.getElementsByTagName("td")[0];
                            var id = cell.innerHTML;
                            updateDetails(row);
                         };
                    };

                row.onclick = createClickHandler(row);
                
                var $ = ${n}.jQuery;
                callback(responseJSON);
            },
            showMessage: function (message) {
                var $ = ${n}.jQuery;
                $("#${n}file-uploader").append('<div class="alert alert-error">' + message + '</div>');
            }
        },
        thumbnails: {
            placeholders: {
                waitingPath: '/source/placeholders/waiting-generic.png',
                notAvailablePath: '/source/placeholders/not_available-generic.png'
            }
        }
    });
</script>
