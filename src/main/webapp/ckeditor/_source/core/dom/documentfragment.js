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
 * DocumentFragment is a "lightweight" or "minimal" Document object. It is
 * commonly used to extract a portion of a document's tree or to create a new
 * fragment of a document. Various operations may take DocumentFragment objects
 * as arguments and results in all the child nodes of the DocumentFragment being
 * moved to the child list of this node.
 *
 * @param {Object} ownerDocument
 */
CKEDITOR.dom.documentFragment = function( ownerDocument )
{
	ownerDocument = ownerDocument || CKEDITOR.document;
	this.$ = ownerDocument.$.createDocumentFragment();
};

CKEDITOR.tools.extend( CKEDITOR.dom.documentFragment.prototype,
	CKEDITOR.dom.element.prototype,
	{
		type : CKEDITOR.NODE_DOCUMENT_FRAGMENT,
		insertAfterNode : function( node )
		{
			node = node.$;
			node.parentNode.insertBefore( this.$, node.nextSibling );
		}
	},
	true,
	{
		'append' : 1,
		'appendBogus' : 1,
		'getFirst' : 1,
		'getLast' : 1,
		'appendTo' : 1,
		'moveChildren' : 1,
		'insertBefore' : 1,
		'insertAfterNode' : 1,
		'replace' : 1,
		'trim' : 1,
		'type' : 1,
		'ltrim' : 1,
		'rtrim' : 1,
		'getDocument' : 1,
		'getChildCount' : 1,
		'getChild' : 1,
		'getChildren' : 1
	} );
