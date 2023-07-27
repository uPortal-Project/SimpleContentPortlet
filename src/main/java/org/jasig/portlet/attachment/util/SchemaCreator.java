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
package org.jasig.portlet.attachment.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.schema.TargetType;
import org.jasig.portlet.attachment.model.Attachment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.Resource;

/**
 * This tool is responsible for creating the SimpleCMS portlet database schema (and dropping
 * it first, if necessary).  It leverages the org.hibernate:hibernate-tools library, but integrates
 * with SimpleCMS' Spring-managed ORM strategy and SimpleCMS' configuration features (esp.
 * encrypted properties).  It is invokable from the command line with '$ java', but designed to be
 * integrated with build tools like Gradle.
 */
public class SchemaCreator implements ApplicationContextAware {

    private static final String APPLICATION_CONTEXT_LOCATION = "classpath:/context/baseContext.xml";

    private static final String DATA_SOURCE_BEAN_NAME = "dataSource";

    private ApplicationContext applicationContext;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * We <em>must</em> obtain the value of this property from the PropertySourcesPlaceholderConfigurer;
     * not from hibernate.properties or any static resource within the war file.
     */
    @Value("${hibernate.dialect}")
    private String hibernateDialect;

    public static void main(String[] args) {

        // Bootstrap an ApplicationContext...
        final GenericApplicationContext context = new GenericApplicationContext();
        final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(context);

        final Resource resource = context.getResource(APPLICATION_CONTEXT_LOCATION);
        reader.loadBeanDefinitions(resource);

        context.refresh();
        context.registerShutdownHook(); // close this context on JVM shutdown unless it has already been closed.

        final SchemaCreator schemaCreator = context.getBean("schemaCreator", SchemaCreator.class);
        System.exit(schemaCreator.create());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private int create() {

        /*
         * We will need to provide a Configuration and a Connection;  both should be properly
         * managed by the Spring ApplicationContext.
         */

        final DataSource dataSource = applicationContext.getBean(DATA_SOURCE_BEAN_NAME, DataSource.class);

        try (final Connection conn = dataSource.getConnection()) {

            Map<String, String> settings = new HashMap<>();
	    settings.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
            settings.put("hibernate.connection.validationQuery", "select 1 from INFORMATION_SCHEMA.SYSTEM_USERS");	    

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(settings).build();
            MetadataSources metadata = new MetadataSources(serviceRegistry);
            metadata.addAnnotatedClass(Attachment.class);

            EnumSet<TargetType> enumSet = EnumSet.of(TargetType.DATABASE);
            SchemaExport schemaExport = new SchemaExport();
            schemaExport.execute(enumSet, SchemaExport.Action.BOTH, metadata.buildMetadata());

            final List<Exception> exceptions = schemaExport.getExceptions();
            if (exceptions.size() != 0) {
                logger.error("Schema Create Failed;  see below for details");
                for (Exception e : exceptions) {
                    logger.error("Exception from Hibernate Tools SchemaExport", e);
                }
                return 1;
            }
        } catch (SQLException sqle) {
            logger.error("Failed to initialize & invoke the SchemaExport tool", sqle);
            return 1;
        }

        return 0;

    }

}
