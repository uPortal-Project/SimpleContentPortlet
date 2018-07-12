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
<%@ page contentType="text/html" isELIgnored="false" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="rs" uri="http://www.jasig.org/resource-server" %>

<script src="<rs:resourceURL value='/rs/jquery/1.11.0/jquery-1.11.0.min.js'/>" type="text/javascript"></script>

<script type='text/javascript'>
    function loadImage() {
        console.log('loadImage');
        $.ajax({
            url: '<c:out value="${attachment.path}"/>',
            type: 'GET',
            success: function(data){ 
                parent.CKEDITOR.tools.callFunction(<c:out value="${functionNumber}"/>, '<c:out value="${attachment.path}"/>');
            },
            error: function(data) {
                setTimeout(function(){loadImage();}, 2000);
            }
        });
    };
    loadImage();

</script>
