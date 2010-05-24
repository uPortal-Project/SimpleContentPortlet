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
 * A lightweight representation of an HTML comment.
 * @constructor
 * @example
 */
CKEDITOR.htmlParser.comment = function( value )
{
	/**
	 * The comment text.
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

CKEDITOR.htmlParser.comment.prototype =
{
	/**
	 * The node type. This is a constant value set to {@link CKEDITOR.NODE_COMMENT}.
	 * @type Number
	 * @example
	 */
	type : CKEDITOR.NODE_COMMENT,

	/**
	 * Writes the HTML representation of this comment to a CKEDITOR.htmlWriter.
	 * @param {CKEDITOR.htmlWriter} writer The writer to which write the HTML.
	 * @example
	 */
	writeHtml : function( writer, filter )
	{
		var comment = this.value;

		if ( filter )
		{
			if ( !( comment = filter.onComment( comment, this ) ) )
				return;

			if ( typeof comment != 'string' )
			{
				comment.parent = this.parent;
				comment.writeHtml( writer, filter );
				return;
			}
		}

		writer.comment( comment );
	}
};
