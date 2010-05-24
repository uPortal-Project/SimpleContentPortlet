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

(function()
{

	/**
	 * A lightweight representation of HTML text.
	 * @constructor
	 * @example
	 */
	CKEDITOR.htmlParser.cdata = function( value )
	{
		/**
		 * The CDATA value.
		 * @type String
		 * @example
		 */
		this.value = value;
	};

	CKEDITOR.htmlParser.cdata.prototype =
	{
		/**
		 * CDATA has the same type as {@link CKEDITOR.htmlParser.text} This is
		 * a constant value set to {@link CKEDITOR.NODE_TEXT}.
		 * @type Number
		 * @example
		 */
		type : CKEDITOR.NODE_TEXT,

		/**
		 * Writes write the CDATA with no special manipulations.
		 * @param {CKEDITOR.htmlWriter} writer The writer to which write the HTML.
		 */
		writeHtml : function( writer )
		{
			writer.write( this.value );
		}
	};
})();
