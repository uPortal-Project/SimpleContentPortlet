/*
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
﻿/*
Copyright (c) 2003-2010, CKSource - Frederico Knabben. All rights reserved.
For licensing, see LICENSE.html or http://ckeditor.com/license
*/

CKEDITOR.dialog.add('textarea',function(a){return{title:a.lang.textarea.title,minWidth:350,minHeight:150,onShow:function(){var c=this;delete c.textarea;var b=c.getParentEditor().getSelection().getSelectedElement();if(b&&b.getName()=='textarea'){c.textarea=b;c.setupContent(b);}},onOk:function(){var b,c=this.textarea,d=!c;if(d){b=this.getParentEditor();c=b.document.createElement('textarea');}this.commitContent(c);if(d)b.insertElement(c);},contents:[{id:'info',label:a.lang.textarea.title,title:a.lang.textarea.title,elements:[{id:'_cke_saved_name',type:'text',label:a.lang.common.name,'default':'',accessKey:'N',setup:function(b){this.setValue(b.getAttribute('_cke_saved_name')||b.getAttribute('name')||'');},commit:function(b){if(this.getValue())b.setAttribute('_cke_saved_name',this.getValue());else{b.removeAttribute('_cke_saved_name');b.removeAttribute('name');}}},{id:'cols',type:'text',label:a.lang.textarea.cols,'default':'',accessKey:'C',style:'width:50px',validate:CKEDITOR.dialog.validate.integer(a.lang.common.validateNumberFailed),setup:function(b){var c=b.hasAttribute('cols')&&b.getAttribute('cols');this.setValue(c||'');},commit:function(b){if(this.getValue())b.setAttribute('cols',this.getValue());else b.removeAttribute('cols');}},{id:'rows',type:'text',label:a.lang.textarea.rows,'default':'',accessKey:'R',style:'width:50px',validate:CKEDITOR.dialog.validate.integer(a.lang.common.validateNumberFailed),setup:function(b){var c=b.hasAttribute('rows')&&b.getAttribute('rows');this.setValue(c||'');},commit:function(b){if(this.getValue())b.setAttribute('rows',this.getValue());else b.removeAttribute('rows');}}]}]};});
