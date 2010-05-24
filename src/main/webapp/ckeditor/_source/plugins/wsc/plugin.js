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
 * @file Spell checker
 */

// Register a plugin named "wsc".
CKEDITOR.plugins.add( 'wsc',
{
	requires : [ 'dialog' ],
	init : function( editor )
	{
		var commandName = 'checkspell';

		var command = editor.addCommand( commandName, new CKEDITOR.dialogCommand( commandName ) );

		// SpellChecker doesn't work in Opera and with custom domain
		command.modes = { wysiwyg : ( !CKEDITOR.env.opera && document.domain == window.location.hostname ) };

		editor.ui.addButton( 'SpellChecker',
			{
				label : editor.lang.spellCheck.toolbar,
				command : commandName
			});
		CKEDITOR.dialog.add( commandName, this.path + 'dialogs/wsc.js' );
	}
});

CKEDITOR.config.wsc_customerId			= CKEDITOR.config.wsc_customerId || '1:ua3xw1-2XyGJ3-GWruD3-6OFNT1-oXcuB1-nR6Bp4-hgQHc-EcYng3-sdRXG3-NOfFk' ;
CKEDITOR.config.wsc_customLoaderScript	= CKEDITOR.config.wsc_customLoaderScript || null;
