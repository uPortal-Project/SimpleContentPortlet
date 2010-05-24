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

CKEDITOR.plugins.add( 'menubutton',
{
	requires : [ 'button', 'contextmenu' ],
	beforeInit : function( editor )
	{
		editor.ui.addHandler( CKEDITOR.UI_MENUBUTTON, CKEDITOR.ui.menuButton.handler );
	}
});

/**
 * Button UI element.
 * @constant
 * @example
 */
CKEDITOR.UI_MENUBUTTON = 5;

(function()
{
	var clickFn = function( editor )
	{
		var _ = this._;

		// Do nothing if this button is disabled.
		if ( _.state === CKEDITOR.TRISTATE_DISABLED )
			return;

		_.previousState = _.state;

		// Check if we already have a menu for it, otherwise just create it.
		var menu = _.menu;
		if ( !menu )
		{
			menu = _.menu = new CKEDITOR.plugins.contextMenu( editor );
			menu.definition.panel.attributes[ 'aria-label' ] = editor.lang.common.options;

			menu.onHide = CKEDITOR.tools.bind( function()
				{
					this.setState( _.previousState );
				},
				this );

			// Initialize the menu items at this point.
			if ( this.onMenu )
			{
				menu.addListener( this.onMenu );
			}
		}

		if ( _.on )
		{
			menu.hide();
			return;
		}

		this.setState( CKEDITOR.TRISTATE_ON );

		menu.show( CKEDITOR.document.getById( this._.id ), 4 );
	};


	CKEDITOR.ui.menuButton = CKEDITOR.tools.createClass(
	{
		base : CKEDITOR.ui.button,

		$ : function( definition )
		{
			// We don't want the panel definition in this object.
			var panelDefinition = definition.panel;
			delete definition.panel;

			this.base( definition );

			this.hasArrow = true;

			this.click = clickFn;
		},

		statics :
		{
			handler :
			{
				create : function( definition )
				{
					return new CKEDITOR.ui.menuButton( definition );
				}
			}
		}
	});
})();
