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
package org.jasig.portlet.cms.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class AntiSamyStringCleaningServiceTest {

    private IStringCleaningService cleaningService;

    @Autowired(required = true)
    public void setCleaningService(IStringCleaningService cleaningService) {
        this.cleaningService = cleaningService;
    }
    
    @Test
    public void testGetSafeContent() {
        String clean = cleaningService.getSafeContent("<h1>Title</h1><p>Content<script type=\"text/javascript\">alert('uhoh!');</script></p>");
        assert ("Title<p>Content</p>").equals(clean);
    }
    
}
