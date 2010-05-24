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
	var loaded = {};

	var loadImage = function( image, callback )
	{
		var doCallback = function()
			{
				img.removeAllListeners();
				loaded[ image ] = 1;
				callback();
			};

		var img = new CKEDITOR.dom.element( 'img' );
		img.on( 'load', doCallback );
		img.on( 'error', doCallback );
		img.setAttribute( 'src', image );
	};

	/**
	 * Load images into the browser cache.
	 * @namespace
	 * @example
	 */
 	CKEDITOR.imageCacher =
	{
		/**
		 * Loads one or more images.
		 * @param {Array} images The URLs for the images to be loaded.
		 * @param {Function} callback The function to be called once all images
		 *		are loaded.
		 */
		load : function( images, callback )
		{
			var pendingCount = images.length;

			var checkPending = function()
			{
				if ( --pendingCount === 0 )
					callback();
			};

			for ( var i = 0 ; i < images.length ; i++ )
			{
				var image = images[ i ];

				if ( loaded[ image ] )
					checkPending();
				else
					loadImage( image, checkPending );
			}
		}
	};
})();
