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
package org.jasig.portlet.attachment.model;


import java.util.Date;
import java.util.UUID;

import javax.persistence.*;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.jasig.portlet.attachment.dao.jpa.Queries;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Entity
@Table(name = "SCM_ATTACHMENT")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
        name="SCM_ATTACHMENT_GEN",
        sequenceName="UP_ATTACHMENT_SEQ",
        allocationSize=10
)
@TableGenerator(
        name = "SCM_ATTACHMENT_GEN",
        pkColumnValue="UP_ATTACHMENT_PROP",
        allocationSize=10
)
@org.hibernate.annotations.Table(
        appliesTo = "SCM_ATTACHMENT",
        indexes = {
                @Index(name = "IDX_SCM_ATTACHMENT_FILENAME", columnNames = { "FILENAME"}),
                @Index(name = "IDX_SCM_ATTACHMENT_GUID", columnNames = { "GUID" })
        }
)
@NamedQueries({
        @NamedQuery(name=Queries.GET_ATTACHMENT_BY_GUID,
                    query="SELECT a FROM Attachment a WHERE a.guid = :guid"),
        @NamedQuery(name=Queries.FIND_ATTACHMENTS_BY_CREATOR,
                    query="SELECT a FROM Attachment a WHERE a.createdBy = :creator"),
        @NamedQuery(name=Queries.FIND_ATTACHMENTS_BY_FILENAME,
                    query="SELECT a FROM Attachment a WHERE a.createdBy = :creator AND a.filename = :filename")
})
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

    @Lob
    @Column(name="DATA",nullable=true,length=Integer.MAX_VALUE)
    private String data;

    @Column(name = "CHECKSUM", nullable=false, length=64)
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

    public String getData()
    {
        return data;
    }

    public void setData(String data)
    {
        this.data = data;
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
}
