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
 * @fileOverview Defines the "virtual" {@link CKEDITOR.pluginDefinition} class, which
 *		contains the defintion of a plugin. This file is for documentation
 *		purposes only.
 */

/**
 * (Virtual Class) Do not call this constructor. This class is not really part
 *		of the API. It just illustrates the features of plugin objects to be
 *		passed to the {@link CKEDITOR.plugins.add} function.
 * @name CKEDITOR.pluginDefinition
 * @constructor
 * @example
 */

/**
 * A list of plugins that are required by this plugin. Note that this property
 * doesn't guarantee the loading order of the plugins.
 * @name CKEDITOR.pluginDefinition.prototype.requires
 * @type Array
 * @example
 * CKEDITOR.plugins.add( 'sample',
 * {
 *     requires : [ 'button', 'selection' ]
 * });
 */

 /**
 * Function called on initialization of every editor instance created in the
 * page before the init() call task. The beforeInit function will be called for
 * all plugins, after that the init function is called for all of them. This
 * feature makes it possible to initialize things that could be used in the
 * init function of other plugins.
 * @name CKEDITOR.pluginDefinition.prototype.beforeInit
 * @function
 * @param {CKEDITOR.editor} editor The editor instance being initialized.
 * @example
 * CKEDITOR.plugins.add( 'sample',
 * {
 *     beforeInit : function( editor )
 *     {
 *         alert( 'Editor "' + editor.name + '" is to be initialized!' );
 *     }
 * });
 */

 /**
 * Function called on initialization of every editor instance created in the
 * page.
 * @name CKEDITOR.pluginDefinition.prototype.init
 * @function
 * @param {CKEDITOR.editor} editor The editor instance being initialized.
 * @example
 * CKEDITOR.plugins.add( 'sample',
 * {
 *     init : function( editor )
 *     {
 *         alert( 'Editor "' + editor.name + '" is being initialized!' );
 *     }
 * });
 */
