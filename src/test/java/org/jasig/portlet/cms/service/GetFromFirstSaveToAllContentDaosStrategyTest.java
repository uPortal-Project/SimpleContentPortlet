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

import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;

import org.jasig.portlet.cms.service.dao.ContentPersistenceException;
import org.jasig.portlet.cms.service.dao.IContentDao;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * JUnit test class for {@link GetFromFirstSaveToAllContentDaosStrategy}.
 */
public class GetFromFirstSaveToAllContentDaosStrategyTest {

    @Mock private ActionRequest actionRequest;
    @Mock private IContentDao dao1;
    @Mock private IContentDao dao2;
    @Mock private IContentDao dao3;
    @Mock private PortletRequest portletRequest;

    private List<IContentDao> contentDaos;
    private String localeKey;
    private String content;

    private GetFromFirstSaveToAllContentDaosStrategy strategy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.contentDaos = new ArrayList<IContentDao>(3);
        this.content = "<h1>BLAH, BLAH, BLAH</h1>";
        this.localeKey = "";
        this.strategy = new GetFromFirstSaveToAllContentDaosStrategy();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getContentMethodDelegatesToFirstDaoInListProvided() {
        // given
        this.contentDaosListContains(this.dao1, this.dao2, this.dao3);
        // when
        this.strategy.getContent(this.contentDaos, this.portletRequest, this.localeKey);
        // then
        verify(this.dao1).getContent(this.portletRequest, this.localeKey);
        verify(this.dao2, never()).getContent(this.portletRequest, this.localeKey);
        verify(this.dao3, never()).getContent(this.portletRequest, this.localeKey);
    }

    @Test
    public void saveContentMethodDelegatesToEachDaoInListProvided() {
        // given
        this.strategy.setAbortOnException(true);
        this.contentDaosListContains(this.dao1, this.dao2, this.dao3);
        // when
        this.strategy.saveContent(this.contentDaos, this.actionRequest, this.content, this.localeKey);
        // then
        verify(this.dao1).saveContent(this.actionRequest, this.content, this.localeKey);
        verify(this.dao2).saveContent(this.actionRequest, this.content, this.localeKey);
        verify(this.dao3).saveContent(this.actionRequest, this.content, this.localeKey);
    }

    @Test
    public void saveContentMethodDelegatesToEachDaoInListWhenAbortOnExceptionIsFalse() {
        // given
        this.strategy.setAbortOnException(false);
        this.contentDaosListContains(this.dao1, this.dao2, this.dao3);
        this.daoThrowsExceptionOnSaveContentCall(this.dao1);
        this.daoThrowsExceptionOnSaveContentCall(this.dao2);
        // when
        this.strategy.saveContent(this.contentDaos, this.actionRequest, this.content, this.localeKey);
        // then
        verify(this.dao1).saveContent(this.actionRequest, this.content, this.localeKey);
        verify(this.dao2).saveContent(this.actionRequest, this.content, this.localeKey);
        verify(this.dao3).saveContent(this.actionRequest, this.content, this.localeKey);
    }

    @Test (expected = ContentPersistenceException.class)
    public void saveContentMethodAbortsOnceDelegateThrowsExceptionWhenAbortOnExceptionIsTrue() {
        // given
        this.strategy.setAbortOnException(true);
        this.contentDaosListContains(this.dao1, this.dao2, this.dao3);
        this.daoThrowsExceptionOnSaveContentCall(this.dao1);
        // when
        this.strategy.saveContent(this.contentDaos, this.actionRequest, this.content, this.localeKey);
        // then
        verify(this.dao1).saveContent(this.actionRequest, this.content, this.localeKey);
        verify(this.dao2, never()).saveContent(this.actionRequest, this.content, this.localeKey);
        verify(this.dao3, never()	).saveContent(this.actionRequest, this.content, this.localeKey);
    }

    private void contentDaosListContains(final IContentDao... daos) {
        for (IContentDao dao : daos) {
            this.contentDaos.add(dao);
        }
    }

    private void daoThrowsExceptionOnSaveContentCall(final IContentDao dao) {
        willThrow(new ContentPersistenceException()).given(dao).saveContent(this.actionRequest, this.content, this.localeKey);
    }

}
