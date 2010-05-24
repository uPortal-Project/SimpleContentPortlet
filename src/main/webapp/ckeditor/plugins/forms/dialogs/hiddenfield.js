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

CKEDITOR.dialog.add('hiddenfield',function(a){return{title:a.lang.hidden.title,minWidth:350,minHeight:110,onShow:function(){var c=this;delete c.hiddenField;var b=c.getParentEditor().getSelection().getSelectedElement();if(b&&b.getName()=='input'&&b.getAttribute('type')=='checkbox'){c.hiddenField=b;c.setupContent(b);}},onOk:function(){var b,c=this.hiddenField,d=!c;if(d){b=this.getParentEditor();c=b.document.createElement('input');c.setAttribute('type','hidden');}if(d)b.insertElement(c);this.commitContent(c);},contents:[{id:'info',label:a.lang.hidden.title,title:a.lang.hidden.title,elements:[{id:'_cke_saved_name',type:'text',label:a.lang.hidden.name,'default':'',accessKey:'N',setup:function(b){this.setValue(b.getAttribute('_cke_saved_name')||b.getAttribute('name')||'');},commit:function(b){if(this.getValue())b.setAttribute('_cke_saved_name',this.getValue());else{b.removeAttribute('_cke_saved_name');b.removeAttribute('name');}}},{id:'value',type:'text',label:a.lang.hidden.value,'default':'',accessKey:'V',setup:function(b){this.setValue(b.getAttribute('value')||'');},commit:function(b){if(this.getValue())b.setAttribute('value',this.getValue());else b.removeAttribute('value');}}]}]};});
