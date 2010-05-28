package org.jasig.portlet.cms.mvc.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portlet.cms.service.IStringCleaningService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/preview")
public class ContentPreviewAjaxController {

    protected final Log log = LogFactory.getLog(getClass());

    private IStringCleaningService cleaningService;
    
    @Autowired(required = true)
    public void setStringCleaningService(IStringCleaningService cleaningService) {
        this.cleaningService = cleaningService;
    }

    @RequestMapping(method = RequestMethod.GET)
    public ModelAndView getPreview(@RequestParam("content") String content) {
        
        Map<String, String> model = new HashMap<String, String>();
        String cleanContent = cleaningService.getSafeContent(content);
        model.put("content", cleanContent);
        
        return new ModelAndView("jsonView", model);
    }
    

}
