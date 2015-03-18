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

import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.jasig.portlet.cms.mvc.exception.StringCleaningException;
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
    private Policy textOnlyPolicy;

    private Resource resource;
    private Resource textOnlyResource;
    
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
    
    /**
     * Set the resource to be used as the AntiSamy policy file.
     * 
     * @param resource
     */
    @javax.annotation.Resource(name = "textOnlyPolicyFile")
    @Required
    public void setTextOnlyPolicy(Resource resource) {
        this.textOnlyResource = resource;
    }

    /*
     * (non-Javadoc)
     * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        // create an AntiSamy policy object from the configured policy file
        final InputStream policyStream = resource.getInputStream();
        try {
        	policy = Policy.getInstance(policyStream);
        }
        finally {
        	IOUtils.closeQuietly(policyStream);
        }
        
        final InputStream textOnlyolicyStream = textOnlyResource.getInputStream();
        try {
        	textOnlyPolicy = Policy.getInstance(textOnlyolicyStream);
        }
        finally {
        	IOUtils.closeQuietly(textOnlyolicyStream);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see org.jasig.portlet.cms.service.IStringCleaningService#getSafeContent(java.lang.String)
     */
    public String getSafeContent(String content) {
        try {
            AntiSamy as = getAntiSamyInstance();
            CleanResults cr = as.scan(content, policy);
            return cr.getCleanHTML();
        } catch (ScanException e) {
            throw new StringCleaningException("Failed to scan new content", e);
        } catch (PolicyException e) {
            throw new StringCleaningException("Exception while cleaning content", e);
        }
    }
    
    /* (non-Javadoc)
	 * @see org.jasig.portlet.cms.service.IStringCleaningService#getTextContent(java.lang.String)
	 */
	public String getTextContent(String content) {
		try {
            AntiSamy as = getAntiSamyInstance();
            CleanResults cr = as.scan(content, textOnlyPolicy);
            return cr.getCleanHTML();
        } catch (ScanException e) {
            throw new StringCleaningException("Failed to scan content for text summary", e);
        } catch (PolicyException e) {
            throw new StringCleaningException("Exception while getting text summary of content", e);
        }
	}

	/**
     * Just returns a new AntiSamy instance.  This method is mostly to help
     * enable unit tests.
     * 
     * @return new AntiSamy instance
     */
    protected AntiSamy getAntiSamyInstance() {
        return new AntiSamy();
    }

}
