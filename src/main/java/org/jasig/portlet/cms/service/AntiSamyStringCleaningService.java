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

import java.io.InputStream;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * AntiSamyStringCleaningService provides an implementation of the 
 * IStringCleaningService interface that uses OWASP's AntiSamy tool to perform
 * HTML string cleaning.  This implementation's behavior is controlled by
 * a configured policy file.
 * 
 * @author Jen Bourey, jbourey@unicon.net
 * @version $Revision$
 */
@Component
public class AntiSamyStringCleaningService implements IStringCleaningService,
        InitializingBean {

    private Policy policy;

    private Resource resource;
    
    /**
     * Set the resource to be used as the AntiSamy policy file.
     * 
     * @param resource
     */
    @javax.annotation.Resource(name = "policyFile")
    @Required
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        // create an AntiSamy policy object from the configured policy file
        InputStream stream = resource.getInputStream();
        policy = Policy.getInstance(stream);
    }
    
    /*
     * (non-Javadoc)
     * @see org.jasig.portlet.cms.service.IStringCleaningService#getSafeContent(java.lang.String)
     */
    public String getSafeContent(String content) {
        try {
            
            AntiSamy as = new AntiSamy();
            CleanResults cr = as.scan(content, policy);
            return cr.getCleanHTML();
            
        } catch (ScanException e) {
            throw new RuntimeException("Exception while cleaning content");
        } catch (PolicyException e) {
            throw new RuntimeException("Exception while cleaning content");
        }
    }

}
