/**
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
package org.jasig.portlet.attachment.mvc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.util.FileHelper;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;

/**
 * @author Chris Waymire (chris@waymire)
 */
public class FileWriteTask extends Task {
    private static final Log log = LogFactory.getLog(FileWriteTask.class);

    private final String file;
    private final byte[] content;

    public FileWriteTask(String file, String content)
    {
        this(file,content.getBytes());
    }

    public FileWriteTask(String file, byte[] content)
    {
        this.file = file;
        this.content = content;
    }

    public void execute()
    {
        try
        {
            File target = new File(file);
            if(!target.exists())
            {
                FileHelper.write(file, content);
            }
        } catch(IOException ioException) {
            log.error("Error writing file '" + file + "' to disk.");
        }

    }

}
