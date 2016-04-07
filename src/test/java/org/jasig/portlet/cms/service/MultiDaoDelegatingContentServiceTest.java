/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.cms.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;

import org.apache.commons.lang.NullArgumentException;
import org.jasig.portlet.cms.service.dao.IContentDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * JUnit test class for {@link MultiDaoDelegatingContentService}.
 */
public class MultiDaoDelegatingContentServiceTest {

    @Mock private IContentDao dao1;
    @Mock private IContentDao dao2;
    @Mock private ActionRequest request;
    @Mock private IContentDaosDelegatingStrategy strategy;

    private String content;
    private String localeKey;
    private List<IContentDao> contentDaos;
    private MultiDaoDelegatingContentService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.contentDaos = new ArrayList<IContentDao>(3);
        this.content = "<h1>BLAH, BLAH, BLAH</h1>";
        this.localeKey = "";
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = NullArgumentException.class)
    public void constructorThrowsExceptionIfStrategyIsNull() {
        // given
        this.contentDaosListContains(this.dao1, this.dao2);
        // when
        new MultiDaoDelegatingContentService(null, this.contentDaos);
    }

    @Test (expected = NullArgumentException.class)
    public void constructorThrowsExceptionIfDaosListIsNull() {
        // when
        new MultiDaoDelegatingContentService(this.strategy, null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsExceptionIfDaosListContainsLessThanTwoDaos() {
        // given
        this.contentDaosListContains(this.dao1);
        // when
        new MultiDaoDelegatingContentService(this.strategy, this.contentDaos);
    }

    @Test
    public void getContentMethodDelegatesToStrategy() {
        // given
        this.contentDaosListContains(this.dao1, this.dao2);
        this.service = new MultiDaoDelegatingContentService(this.strategy, this.contentDaos);
        given(this.strategy.getContent(this.contentDaos, this.request, this.localeKey)).willReturn(this.content);
        // when
        final String result = this.service.getContent(this.request, this.localeKey);
        // then
        verify(this.strategy).getContent(this.contentDaos, this.request, this.localeKey);
        assertEquals(this.content, result);
    }

    @Test
    public void saveContentMethodDelegatesToStrategy() {
        // given
        this.contentDaosListContains(this.dao1, this.dao2);
        this.service = new MultiDaoDelegatingContentService(this.strategy, this.contentDaos);
        // when
        this.service.saveContent(this.request, this.content, this.localeKey);
        // then
        verify(this.strategy).saveContent(this.contentDaos, this.request, this.content, this.localeKey);
    }

    private void contentDaosListContains(final IContentDao... daos) {
        for (IContentDao dao : daos) {
            this.contentDaos.add(dao);
        }
    }

}
