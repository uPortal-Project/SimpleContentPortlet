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

/**
 * @fileOverview Defines the {@link CKEDITOR.dom.comment} class, which represents
 *		a DOM comment node.
 */

CKEDITOR.dom.comment = CKEDITOR.tools.createClass(
{
	base : CKEDITOR.dom.node,

	$ : function( text, ownerDocument )
	{
		if ( typeof text == 'string' )
			text = ( ownerDocument ? ownerDocument.$ : document ).createComment( text );

		this.base( text );
	},

	proto :
	{
		type : CKEDITOR.NODE_COMMENT,

		getOuterHtml : function()
		{
			return '<!--' + this.$.nodeValue + '-->';
		}
	}
});
