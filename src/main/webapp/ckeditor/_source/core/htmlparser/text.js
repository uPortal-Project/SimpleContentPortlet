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
	var spacesRegex = /[\t\r\n ]{2,}|[\t\r\n]/g;

	/**
	 * A lightweight representation of HTML text.
	 * @constructor
	 * @example
	 */
 	CKEDITOR.htmlParser.text = function( value )
	{
		/**
		 * The text value.
		 * @type String
		 * @example
		 */
		this.value = value;

		/** @private */
		this._ =
		{
			isBlockLike : false
		};
	};

	CKEDITOR.htmlParser.text.prototype =
	{
		/**
		 * The node type. This is a constant value set to {@link CKEDITOR.NODE_TEXT}.
		 * @type Number
		 * @example
		 */
		type : CKEDITOR.NODE_TEXT,

		/**
		 * Writes the HTML representation of this text to a CKEDITOR.htmlWriter.
		 * @param {CKEDITOR.htmlWriter} writer The writer to which write the HTML.
		 * @example
		 */
		writeHtml : function( writer, filter )
		{
			var text = this.value;

			if ( filter && !( text = filter.onText( text, this ) ) )
				return;

			writer.text( text );
		}
	};
})();
