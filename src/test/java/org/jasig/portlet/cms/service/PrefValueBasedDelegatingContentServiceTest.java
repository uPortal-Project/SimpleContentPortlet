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

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.PortletPreferences;

import org.apache.commons.lang.NullArgumentException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

/**
 * JUnit test class for {@link PrefValueBasedDelegatingContentService}.
 */
public class PrefValueBasedDelegatingContentServiceTest {

    @Mock private ActionRequest request;
    @Mock private IContentService delegate1;
    @Mock private IContentService delegate2;
    @Mock private PortletPreferences portletPreferences;

    private String content;
    private Map<String, IContentService> delegatesMap; // mock delegate hash code will be used as key
    private PrefValueBasedDelegatingContentService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.content = "<a href='blah'>blah</a>";
        given(this.request.getPreferences()).willReturn(this.portletPreferences);
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test (expected = NullArgumentException.class)
    public void constructorThrowsExceptionIfDelegatesMapIsNull() {
        this.service = new PrefValueBasedDelegatingContentService(null);
    }

    @Test (expected = IllegalArgumentException.class)
    public void constructorThrowsExceptionIfNoDefaultDelegateProvided() {
        this.createDelegatesMap(null, this.delegate1);
        this.createServiceClassWithDelegatesMap();
    }

    @Test
    public void saveContentMethodUsesDefaultDelegateIfNoDelegateKeyFound() {
        // given
        this.createDelegatesMap(this.delegate1, this.delegate2);
        this.createServiceClassWithDelegatesMap();
        // when
        this.service.saveContent(this.request, this.content, null);
        // then
        verify(this.delegate1).saveContent(this.request, this.content, null);
    }

    @Test
    public void saveContentMethodUsesDelegateSpecifiedByDelegateKey() {
        // given
    	final String targetDelegateKey = this.getDelegateKey(this.delegate2);
        this.createDelegatesMap(this.delegate1, this.delegate2);
        this.createServiceClassWithDelegatesMap();
        this.portletPrefsContainsKeyValue(PrefValueBasedDelegatingContentService.DELEGATE_KEY_PREF_NAME_DEFAULT, targetDelegateKey);
        // when
        this.service.saveContent(request, content, null);
        // then
        verify(this.delegate2).saveContent(this.request, this.content, null);
        verify(this.delegate1, never()).saveContent(this.request, this.content, null);
    }

    @Test (expected = ContentServiceNotFoundException.class)
    public void saveContentMethodThrowsExceptionIfNoDelegateFoundForKey() {
        // given
        this.createDelegatesMap(this.delegate1, this.delegate2);
        this.createServiceClassWithDelegatesMap();
        this.portletPrefsContainsKeyValue(PrefValueBasedDelegatingContentService.DELEGATE_KEY_PREF_NAME_DEFAULT, "badvalue");
        // when
        this.service.saveContent(request, content, null);
    }

    private void createDelegatesMap(final IContentService defaultDelegate, final IContentService... delegates) {
        this.delegatesMap = new HashMap<String, IContentService>(3);
        if (defaultDelegate != null) {
            this.delegatesMap.put(PrefValueBasedDelegatingContentService.DELEGATE_KEY_FOR_DEFAULT, defaultDelegate);
        }
        for (IContentService delegate : delegates) {
            this.delegatesMap.put(this.getDelegateKey(delegate), delegate);
        }
    }

    private String getDelegateKey(final IContentService delegate) {
        return String.valueOf(delegate.hashCode());
    }

    private void createServiceClassWithDelegatesMap() {
        this.service = new PrefValueBasedDelegatingContentService(this.delegatesMap);
    }

    private void portletPrefsContainsKeyValue(final String key, final String value) {
        given(this.portletPreferences.getValue(key, null)).willReturn(value);
    }

}
