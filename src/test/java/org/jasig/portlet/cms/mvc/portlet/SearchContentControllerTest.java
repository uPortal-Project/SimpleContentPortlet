package org.jasig.portlet.cms.mvc.portlet;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import javax.portlet.Event;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;

import org.apereo.portal.search.SearchRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SearchContentControllerTest {
   @Mock EventRequest request;
   @Mock EventResponse response;

   SearchContentController controller = new SearchContentController();

   @Test
   public void testSearchContentNullEvent() {
      var event = mock(Event.class);

      when(event.getValue()).thenReturn(null);
      when(request.getEvent()).thenReturn(event);

      controller.searchContent(request, response);

      verifyNoInteractions(response);
   }

   @Test
   public void testSearchContentNullTerms() {
      var event = mock(Event.class);
      var searchRequest = mock(SearchRequest.class);

      when(event.getValue()).thenReturn(searchRequest);
      when(request.getEvent()).thenReturn(event);
      when(searchRequest.getSearchTerms()).thenReturn(null);

      controller.searchContent(request, response);

      verifyNoInteractions(response);
   }
}
