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
CKEDITOR.dialog.add( 'hiddenfield', function( editor )
{
	return {
		title : editor.lang.hidden.title,
		minWidth : 350,
		minHeight : 110,
		onShow : function()
		{
			delete this.hiddenField;

			var element = this.getParentEditor().getSelection().getSelectedElement();
			if ( element && element.getName() == "input" && element.getAttribute( 'type' ) == "checkbox" )
			{
				this.hiddenField = element;
				this.setupContent( element );
			}
		},
		onOk : function()
		{
			var editor,
				element = this.hiddenField,
				isInsertMode = !element;

			if ( isInsertMode )
			{
				editor = this.getParentEditor();
				element = editor.document.createElement( 'input' );
				element.setAttribute( 'type', 'hidden' );
			}

			if ( isInsertMode )
				editor.insertElement( element );
			this.commitContent( element );
		},
		contents : [
			{
				id : 'info',
				label : editor.lang.hidden.title,
				title : editor.lang.hidden.title,
				elements : [
					{
						id : '_cke_saved_name',
						type : 'text',
						label : editor.lang.hidden.name,
						'default' : '',
						accessKey : 'N',
						setup : function( element )
						{
							this.setValue(
									element.getAttribute( '_cke_saved_name' ) ||
									element.getAttribute( 'name' ) ||
									'' );
						},
						commit : function( element )
						{
							if ( this.getValue() )
								element.setAttribute( '_cke_saved_name', this.getValue() );
							else
							{
								element.removeAttribute( '_cke_saved_name' );
								element.removeAttribute( 'name' );
							}
						}
					},
					{
						id : 'value',
						type : 'text',
						label : editor.lang.hidden.value,
						'default' : '',
						accessKey : 'V',
						setup : function( element )
						{
							this.setValue( element.getAttribute( 'value' ) || '' );
						},
						commit : function( element )
						{
							if ( this.getValue() )
								element.setAttribute( 'value', this.getValue() );
							else
								element.removeAttribute( 'value' );
						}
					}
				]
			}
		]
	};
});
