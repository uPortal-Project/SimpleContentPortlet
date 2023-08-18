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
package org.jasig.portlet.attachment.model;


import java.util.Date;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.jasig.portlet.attachment.dao.jpa.Queries;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Entity
@Table(
        name = "SCM_ATTACHMENT",
        indexes = {
            @Index(name = "IDX_SCM_ATTACHMENT_FILENAME", columnList = "FILENAME"),
            @Index(name = "IDX_SCM_ATTACHMENT_GUID", columnList = "GUID" )
        }
)
@Inheritance(strategy = InheritanceType.JOINED)

// CMSPLT-54 For Oracle and Postrgres, sequence generator in the DB had allocationSize=1 even though annotation
// had allocationSize=10 and this was causing negative ID numbers and ID conflicts (because Hibernate would
// get the sequence value returned, subtract 10 to get the first ID in the range, and end up with a negative
// number).  Fixed by changing allocationSize=1 since attachment inserts are fairly infrequent and doing an extra
// fetch of the next sequence # per insert is not that big a deal.

@SequenceGenerator(
        name="SCM_ATTACHMENT_GEN",
        sequenceName="UP_ATTACHMENT_SEQ",
        allocationSize=1
)
@TableGenerator(
        name = "SCM_ATTACHMENT_GEN",
        pkColumnValue="UP_ATTACHMENT_PROP",
        allocationSize=1
)
@NamedQuery(name=Queries.GET_ATTACHMENT_BY_GUID,
        query="SELECT a FROM Attachment a WHERE a.guid = :guid")
@NamedQuery(name=Queries.FIND_ATTACHMENTS_BY_CREATOR,
        query="SELECT a FROM Attachment a WHERE a.createdBy = :creator")
@NamedQuery(name=Queries.FIND_ATTACHMENTS_BY_FILENAME,
        query="SELECT a FROM Attachment a WHERE a.createdBy = :creator AND a.filename = :filename")
@NamedQuery(name=Queries.FIND_ALL_ATTACHMENTS,
        query="SELECT a FROM Attachment a order by a.id")
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Attachment {
    @Id
    @GeneratedValue(generator = "SCM_ATTACHMENT_GEN")
    @Column(name="ID")
    private final long id;

    @Column(name = "GUID", nullable = false, length=64)
    private String guid;

    @Column(name = "FILENAME", nullable = false, length = 128)
    private String filename;

    @Column(name="PATH", nullable=false,length=255)
    private String path;

    @Column(name="SOURCE",nullable=true,length=255)
    private String source;

    /**
     * In v2.0.0 switched from base64-encoded text field in column DATA to a binary BLOB field.
     */
    @Lob
    @Column(name="BDATA",nullable=true,length=Integer.MAX_VALUE)
    private byte[] data;

    /**
     * Checksum as 32-digit hex value
     */
    @Column(name = "CHECKSUM", nullable=false, length=32)
    private String checksum;

    @Column(name = "CREATED_BY", nullable=false, length=128)
    private String createdBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATED_AT", nullable=false)
    private Date createdAt;

    @Column(name = "MODIFIED_BY", nullable=false, length=128)
    private String modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "MODIFIED_AT", nullable=false)
    private Date modifiedAt;

    /**
     * Mime content type. Typically not needed unless persisting the attachment to S3 since most web
     * servers will return a content type based on the filename extension, and value not needed for
     * attachments maintenance. Field is made nullable since pre-2.0.3 it did not exist.  Data import of
     * a data export will populate the field's value on old data since data import guesses at content type
     * based on file extension.
     *
     * @since 2.0.3
     */
    @Column(name = "CONTENT_TYPE", nullable = true, length=128)
    private String contentType;

    public Attachment()
    {
        id = -1;
        guid = UUID.randomUUID().toString();
        checksum = "0";
    }

    public long getId()
    {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public byte[] getData()
    {
        return data;
    }

    public void setData(byte[] bdata)
    {
        this.data = bdata;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Date modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
