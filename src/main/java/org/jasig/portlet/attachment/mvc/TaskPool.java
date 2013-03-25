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

import org.springframework.stereotype.Component;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Chris Waymire (chris@waymire)
 */
@Component
public class TaskPool {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final BlockingQueue<Task> tasks = new ArrayBlockingQueue(1000);

    private TaskPool()
    {
        new Thread()
        {
            public void run() {
                while(true)
                {
                    try {
                        Task task = tasks.take();
                        executor.execute(task);
                    } catch(InterruptedException interruptedException) {  }
                }
            }
        }.start();
    }

    public boolean add(Task task)
    {
        return tasks.add(task);
    }

    public boolean put(Task task)
    {
        try
        {
            tasks.put(task);
            return true;
        } catch(InterruptedException interruptedException) {
            return false;
        }
    }
}
