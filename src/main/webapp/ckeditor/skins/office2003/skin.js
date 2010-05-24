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

CKEDITOR.skins.add('office2003',(function(){var a=[];if(CKEDITOR.env.ie&&CKEDITOR.env.version<7)a.push('icons.png','images/sprites_ie6.png','images/dialog_sides.gif');return{preload:a,editor:{css:['editor.css']},dialog:{css:['dialog.css']},templates:{css:['templates.css']},margins:[0,14,18,14]};})());(function(){CKEDITOR.dialog?a():CKEDITOR.on('dialogPluginReady',a);function a(){CKEDITOR.dialog.on('resize',function(b){var c=b.data,d=c.width,e=c.height,f=c.dialog,g=f.parts.contents;if(c.skin!='office2003')return;g.setStyles({width:d+'px',height:e+'px'});if(!CKEDITOR.env.ie)return;var h=function(){var i=f.parts.dialog.getChild([0,0,0]),j=i.getChild(0),k=i.getChild(2);k.setStyle('width',j.$.offsetWidth+'px');k=i.getChild(7);k.setStyle('width',j.$.offsetWidth-28+'px');k=i.getChild(4);k.setStyle('height',j.$.offsetHeight-31-14+'px');k=i.getChild(5);k.setStyle('height',j.$.offsetHeight-31-14+'px');};setTimeout(h,100);if(b.editor.lang.dir=='rtl')setTimeout(h,1000);});};})();
