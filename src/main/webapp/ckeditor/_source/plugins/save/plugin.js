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
 * @fileSave plugin.
 */

(function()
{
	var saveCmd =
	{
		modes : { wysiwyg:1, source:1 },

		exec : function( editor )
		{
			var $form = editor.element.$.form;

			if ( $form )
			{
				try
				{
					$form.submit();
				}
				catch( e )
				{
					// If there's a button named "submit" then the form.submit
					// function is masked and can't be called in IE/FF, so we
					// call the click() method of that button.
					if ( $form.submit.click )
						$form.submit.click();
				}
			}
		}
	};

	var pluginName = 'save';

	// Register a plugin named "save".
	CKEDITOR.plugins.add( pluginName,
	{
		init : function( editor )
		{
			var command = editor.addCommand( pluginName, saveCmd );
			command.modes = { wysiwyg : !!( editor.element.$.form ) };

			editor.ui.addButton( 'Save',
				{
					label : editor.lang.save,
					command : pluginName
				});
		}
	});
})();
