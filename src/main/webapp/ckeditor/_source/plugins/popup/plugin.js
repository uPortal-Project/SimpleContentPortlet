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

CKEDITOR.plugins.add( 'popup');

CKEDITOR.tools.extend( CKEDITOR.editor.prototype,
{
	/**
	 * Opens Browser in a popup. The "width" and "height" parameters accept
	 * numbers (pixels) or percent (of screen size) values.
	 * @param {String} url The url of the external file browser.
	 * @param {String} width Popup window width.
	 * @param {String} height Popup window height.
	 */
	popup : function( url, width, height )
	{
		width = width || '80%';
		height = height || '70%';

		if ( typeof width == 'string' && width.length > 1 && width.substr( width.length - 1, 1 ) == '%' )
			width = parseInt( window.screen.width * parseInt( width, 10 ) / 100, 10 );

		if ( typeof height == 'string' && height.length > 1 && height.substr( height.length - 1, 1 ) == '%' )
			height = parseInt( window.screen.height * parseInt( height, 10 ) / 100, 10 );

		if ( width < 640 )
			width = 640;

		if ( height < 420 )
			height = 420;

		var top = parseInt( ( window.screen.height - height ) / 2, 10 ),
			left = parseInt( ( window.screen.width  - width ) / 2, 10 ),
			options = 'location=no,menubar=no,toolbar=no,dependent=yes,minimizable=no,modal=yes,alwaysRaised=yes,resizable=yes' +
			',width='  + width +
			',height=' + height +
			',top='  + top +
			',left=' + left;

		var popupWindow = window.open( '', null, options, true );

		// Blocked by a popup blocker.
		if ( !popupWindow )
			return false;

		try
		{
			popupWindow.moveTo( left, top );
			popupWindow.resizeTo( width, height );
			popupWindow.focus();
			popupWindow.location.href = url;
		}
		catch (e)
		{
			popupWindow = window.open( url, null, options, true );
		}

		return true ;
	}
});
