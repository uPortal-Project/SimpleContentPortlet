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

CKEDITOR.skins.add( 'v2', (function()
{
	var preload = [];

	if ( CKEDITOR.env.ie && CKEDITOR.env.version < 7 )
	{
		// For IE6, we need to preload some images, otherwhise they will be
		// downloaded several times (CSS background bug).
		preload.push( 'icons.png', 'images/sprites_ie6.png', 'images/dialog_sides.gif' );
	}

	return {
		preload		: preload,
		editor		: { css : [ 'editor.css' ] },
		dialog		: { css : [ 'dialog.css' ] },
		templates	: { css : [ 'templates.css' ] },
		margins		: [ 0, 14, 18, 14 ]
	};
})() );

(function()
{
	CKEDITOR.dialog ? dialogSetup() : CKEDITOR.on( 'dialogPluginReady', dialogSetup );

	function dialogSetup()
	{
		CKEDITOR.dialog.on( 'resize', function( evt )
			{
				var data = evt.data,
					width = data.width,
					height = data.height,
					dialog = data.dialog,
					contents = dialog.parts.contents;

				if ( data.skin != 'v2' )
					return;

				contents.setStyles(
					{
						width : width + 'px',
						height : height + 'px'
					});

				if ( !CKEDITOR.env.ie )
					return;

				// Fix the size of the elements which have flexible lengths.
				setTimeout( function()
					{
						var innerDialog = dialog.parts.dialog.getChild( [ 0, 0, 0 ] ),
							body = innerDialog.getChild( 0 );

						// tc
						var el = innerDialog.getChild( 2 );
						el.setStyle( 'width', ( body.$.offsetWidth ) + 'px' );

						// bc
						el = innerDialog.getChild( 7 );
						el.setStyle( 'width', ( body.$.offsetWidth - 28 ) + 'px' );

						// ml
						el = innerDialog.getChild( 4 );
						el.setStyle( 'height', ( body.$.offsetHeight - 31 - 14 ) + 'px' );

						// mr
						el = innerDialog.getChild( 5 );
						el.setStyle( 'height', ( body.$.offsetHeight - 31 - 14 ) + 'px' );
					},
					100 );
			});
	}
})();
