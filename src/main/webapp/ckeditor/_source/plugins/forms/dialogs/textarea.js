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
CKEDITOR.dialog.add( 'textarea', function( editor )
{
	return {
		title : editor.lang.textarea.title,
		minWidth : 350,
		minHeight : 150,
		onShow : function()
		{
			delete this.textarea;

			var element = this.getParentEditor().getSelection().getSelectedElement();
			if ( element && element.getName() == "textarea" )
			{
				this.textarea = element;
				this.setupContent( element );
			}
		},
		onOk : function()
		{
			var editor,
				element = this.textarea,
				isInsertMode = !element;

			if ( isInsertMode )
			{
				editor = this.getParentEditor();
				element = editor.document.createElement( 'textarea' );
			}
			this.commitContent( element );

			if ( isInsertMode )
				editor.insertElement( element );
		},
		contents : [
			{
				id : 'info',
				label : editor.lang.textarea.title,
				title : editor.lang.textarea.title,
				elements : [
					{
						id : '_cke_saved_name',
						type : 'text',
						label : editor.lang.common.name,
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
						id : 'cols',
						type : 'text',
						label : editor.lang.textarea.cols,
						'default' : '',
						accessKey : 'C',
						style : 'width:50px',
						validate : CKEDITOR.dialog.validate.integer( editor.lang.common.validateNumberFailed ),
						setup : function( element )
						{
							var value = element.hasAttribute( 'cols' ) && element.getAttribute( 'cols' );
							this.setValue( value || '' );
						},
						commit : function( element )
						{
							if ( this.getValue() )
								element.setAttribute( 'cols', this.getValue() );
							else
								element.removeAttribute( 'cols' );
						}
					},
					{
						id : 'rows',
						type : 'text',
						label : editor.lang.textarea.rows,
						'default' : '',
						accessKey : 'R',
						style : 'width:50px',
						validate : CKEDITOR.dialog.validate.integer( editor.lang.common.validateNumberFailed ),
						setup : function( element )
						{
							var value = element.hasAttribute( 'rows' ) && element.getAttribute( 'rows' );
							this.setValue( value || '' );
						},
						commit : function( element )
						{
							if ( this.getValue() )
								element.setAttribute( 'rows', this.getValue() );
							else
								element.removeAttribute( 'rows' );
						}
					}
				]
			}
		]
	};
});
