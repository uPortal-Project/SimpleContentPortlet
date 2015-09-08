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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.jasig.portlet.attachment.model.Attachment;
import org.jasig.portlet.attachment.service.IAttachmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

/**
 * Provides import and export capability for Attachments.
 *
 * @author James Wennmacher, jwennmacher@unicon.net
 */

@Service
public class ImportExport {
    private static final String FILENAME_SUFFIX = ".attachment.xml";

    Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    IAttachmentService attachmentService;

    @Value("${hibernate.connection.url}")
    private String hibernateURI;

    @Value("${hibernate.attachments.batch.fetch.size:100}")
    private int batchFetchSize = 100;

    public static void main(String[] args) throws Exception {

        if (args.length != 2) {
            usage();
        }

        ApplicationContext context =
                new ClassPathXmlApplicationContext(new String[] {"context/importExportContext.xml"});
        ImportExport importExport = (ImportExport) context.getBean("importExport");

        importExport.notifyUserIfUsingInMemoryDb();

        if ("export".equals(args[0])) {
            importExport.export(args[1]);
        } else if ("import".equalsIgnoreCase(args[0])) {
            importExport.importDir(args[1]);
        } else {
            usage();
        }
    }

    private void notifyUserIfUsingInMemoryDb() {
        if (hibernateURI.startsWith("jdbc:hsqldb:mem")) {
            log.warn("");
            log.warn("NOTICE:");
            log.warn("");
            log.warn("Operation aborted.  Update hibernate.properties. It is currently set to the in-memory DB.");
            log.warn("");
            System.exit(1);
        }
    }

    private void importDir(String directoryName) {
        File dir = checkIfDirExists(directoryName, false);
        Collection<File> files = FileUtils.listFiles(dir, new WildcardFileFilter("*" + FILENAME_SUFFIX), null);
        if (files.size() == 0) {
            log.info("No files of form {} to import in {}", "*" + FILENAME_SUFFIX, directoryName);
        }
        boolean importSucceeded = true;
        for (File file : files) {
            importSucceeded &= importFile(file);
        }
        if (!importSucceeded) {
            System.exit(1);
        }
    }

    private boolean importFile(File filename) {
        try {
            JAXBContext jc = JAXBContext.newInstance(Attachment.class);
            StreamSource xml = new StreamSource(filename);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            JAXBElement<Attachment> je1 = unmarshaller.unmarshal(xml, Attachment.class);
            Attachment attachment = je1.getValue();

            // An export run after having added SCP v2.0.0+ and uploading some attachments (which would populate
            // column bdata, not data) would
            if (attachment.getData() == null && System.getProperty("importEmpty") == null) {
                log.warn("Attachment with guid {} has no data and will be skipped.  This can occur if you had put"
                        + " SimpleContentPortlet v2.0.0+ on, uploaded attachments which wrote the data to the"
                        + " BDATA column instead of the deprecated DATA column, then went back to a pre 2.0.0"
                        + " version to do the data-export.  Typically you do not want these records imported; they"
                        + " will be correct in the database if you are running import on the same database"
                        + " you ran export on.",
                        attachment.getGuid());
                return false;
            }
            saveOrUpdate(attachment);
        } catch (JAXBException e) {
            log.error("Unable to import attachment {}", filename.getAbsolutePath(), e);
            return false;
        }
        return true;
    }

    private void saveOrUpdate(Attachment attachment) {
        try {
            attachmentService.save(attachment, attachment.getModifiedBy() != null ? attachment.getModifiedBy() : "system");
            log.info("Imported attachment with guid {}", attachment.getGuid());
        } catch (DataAccessException e) {
            log.error("Unable to import attachment with guid {}", attachment.getGuid(), e);
            System.exit(1);
        }
    }

    private void export(String directoryName) {
        boolean operationSucceeded = true;
        File dir = checkIfDirExists(directoryName, true);

        int offset = 0;
        List<Attachment> attachments;
        do {
            attachments = attachmentService.findAll(offset, batchFetchSize);
            if (attachments.size() > 0) {
                try {
                    JAXBContext jc = JAXBContext.newInstance(Attachment.class);
                    Marshaller marshaller = jc.createMarshaller();
                    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

                    for (Attachment attachment : attachments) {
                        File entityFile = new File(dir, attachment.getGuid() + FILENAME_SUFFIX);

                        if (attachment.getData() == null) {
                            log.error("File {}, guid {} has no data.  This typically occurs if you are running export"
                                    + " against a SimpleContentPortlet database prior to v2.0.0. You probably want"
                                    + " to delete this file before running import and instead run export using"
                                    + " a version prior to 2.0.0",
                                    entityFile.getAbsolutePath(), attachment.getGuid());
                            operationSucceeded = false;
                        } else {
                            log.info("Creating file {}", entityFile.getAbsolutePath());
                        }
                        try {
                            JAXBElement<Attachment> je2 = new JAXBElement<>(new QName("Attachment"), Attachment.class, attachment);
                            marshaller.marshal(je2, new FileWriter(entityFile));
                        } catch (IOException e) {
                            log.error("Unable to write to file {}", entityFile.getAbsolutePath(), e);
                            operationSucceeded = false;
                        }
                    }

                } catch (JAXBException e) {
                    log.error("Unable to create XML file", e);
                    System.exit(1);
                }
            }
            offset += attachments.size();

        } while (attachments.size() == batchFetchSize);

        if (attachments.size() == 0 && offset == 0) {
            log.info("No attachments found in the database.");
        }

        if (!operationSucceeded) {
            System.exit(1);
        }
    }

    private File checkIfDirExists(String directoryName, boolean createIfAbsent) {
        File dir = new File(directoryName);
        if (!dir.exists() && createIfAbsent) {
            if (!dir.mkdirs()) {
                log.error("Unable to create directory at location {}", directoryName);
                System.exit(1);
            }
        } else if (dir.exists() && !dir.isDirectory()) {
            log.error("{} is not a directory", directoryName);
            System.exit(1);
        }
        return dir;
    }

    private static void usage() {
        System.out.println("Usage:");
        System.out.println("  java -Dlogback.configurationFile=command-line.logback.xml "
                + ImportExport.class.getCanonicalName() + " export <dir>");
        System.out.println("  java -Dlogback.configurationFile=command-line.logback.xml [-Ddir=<dir>] [-Dfile=<file>]"
                + ImportExport.class.getCanonicalName() + " import");
        System.exit(0);
    }
}
