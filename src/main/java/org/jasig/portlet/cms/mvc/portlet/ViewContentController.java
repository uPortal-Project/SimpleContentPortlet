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

package org.jasig.portlet.cms.mvc.portlet;

import java.util.Locale;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletConfig;
import javax.portlet.PortletRequest;

import org.jasig.portal.search.PortletUrl;
import org.jasig.portal.search.SearchConstants;
import org.jasig.portal.search.SearchRequest;
import org.jasig.portal.search.SearchResult;
import org.jasig.portal.search.SearchResults;
import org.jasig.portlet.cms.service.dao.IContentDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.EventMapping;
import org.springframework.web.portlet.context.PortletConfigAware;

/**
 * ViewContentController provides the main view of the portlet.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
@Controller
@RequestMapping("VIEW")
public class ViewContentController implements PortletConfigAware {
    
    private int searchSummaryLength = 1000;
    private PortletConfig portletConfig;
    private IContentDao contentDao;
    
    @Autowired(required = true)
    public void setContentDao(IContentDao contentDao) {
        this.contentDao = contentDao;
    }
    
    public void setPortletConfig(PortletConfig portletConfig) {
        this.portletConfig = portletConfig;
    }

    @EventMapping(SearchConstants.SEARCH_REQUEST_QNAME_STRING)
    public void searchContent(EventRequest request, EventResponse response) {
        final Event event = request.getEvent();
        final SearchRequest searchQuery = (SearchRequest)event.getValue();
        
        final String content = getContent(request);
        final String[] searchTerms = searchQuery.getSearchTerms().split(" ");
        for (final String term : searchTerms) {
            if (content.contains(term)) {
                //matched, create results object and copy over the query id
                final SearchResults searchResults = new SearchResults();
                searchResults.setQueryId(searchQuery.getQueryId());
                searchResults.setWindowId(request.getWindowID());
               
                //Build the result object for the match
                final SearchResult searchResult = new SearchResult();
                searchResult.setTitle(this.portletConfig.getPortletName());
                searchResult.setSummary(getContentSummary(content));
                searchResult.setPortletUrl(new PortletUrl());
                
                //Add the result to the results and send the event
                searchResults.getSearchResult().add(searchResult);
                response.setEvent(SearchConstants.SEARCH_RESULTS_QNAME, searchResults);
                
                //Stop processing
                return;
            }
        }
    }

    protected String getContentSummary(final String content) {
        if (content.length() > searchSummaryLength) {
            return content.substring(0, searchSummaryLength) + "...";
        }
        
        return content;
    }
    
    /**
     * Display the main user-facing view of the portlet.
     * 
     * @param request
     * @return
     */
    @RequestMapping
    public String viewContent() {
        return "viewContent";
    }
    
    /**
     * Get the configured user-facing content for this portlet configuration.
     * 
     * @param request
     * @return
     */
    @ModelAttribute("content")
    public String getContent(PortletRequest request){
    	Locale locale = request.getLocale();
        return this.contentDao.getContent(request, locale.toString());
    }
    
}
