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


import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.jasig.portlet.attachment.dao.jpa.Queries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.security.MessageDigest;
import java.util.Date;
import java.util.UUID;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Entity
@Table(name = "SCM_ATTACHMENT")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
        name="SCM_ATTACHMENT_SEQ_GEN",
        sequenceName="UP_ATTACHMENT_SEQ",
        allocationSize=1000
)
@TableGenerator(
        name = "SCM_ATTACHMENT_GEN",
        pkColumnValue="UP_ATTACHMENT_PROP",
        allocationSize=1000
)
@org.hibernate.annotations.Table(
        appliesTo = "SCM_ATTACHMENT",
        indexes = {
                @Index(name = "IDX_SCM_ATTACHMENT_FILENAME", columnNames = { "FILENAME"}),
                @Index(name = "IDX_SCM_ATTACHMENT_GUID", columnNames = { "GUID" })
        }
)
@NamedQueries({
        @NamedQuery(name= Queries.GET_THIN_ATTACHMENT,
                    query="SELECT NEW Attachment(a.id,a.filename,a.guid) FROM Attachment a WHERE a.id = :id"),
        @NamedQuery(name= Queries.GET_ATTACHMENTS_BY_FOLDER,
                    query="SELECT a FROM Attachment a WHERE a.folder = :folder"),
        @NamedQuery(name= Queries.GET_ATTACHMENTS,
                    query="SELECT a FROM Attachment a"),
        @NamedQuery(name= Queries.GET_ATTACHMENT_CONTENT,
                    query="SELECT a.encodedContent FROM Attachment a WHERE a.id = :id"),
        @NamedQuery(name= Queries.ATTACHMENT_EXISTS,
                    query="SELECT a.id AS id FROM Attachment a WHERE a.filename=:filename"),
        @NamedQuery(name= Queries.UPDATE_ATTACHMENT_LAST_ACCESSED_AT,
                    query="UPDATE Attachment a SET a.lastAccessedAt=:date WHERE a.id=:id")
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Attachment {

    @Transient
    private final Logger logger = LoggerFactory.getLogger(Attachment.class);

    @Transient
    private final Base64 base64 = new Base64();

    @Id
    @GeneratedValue(generator = "SCM_ATTACHMENT_GEN")
    @Column(name="ID")
    private final long id;

    @Column(name = "FILENAME", nullable = false, length = 128)
    private String filename;

    @Column(name = "VERSION")
    private int version;

    @ManyToOne(targetEntity=Folder.class)
    @JoinColumn(name = "FOLDER_ID", nullable = true)
    private Folder folder;

    @Column(name = "GUID", nullable = false, length=64)
    private String guid;

    @Lob
    @Column(name="CONTENT",nullable=false,length=Integer.MAX_VALUE)
    private String encodedContent;

    @Transient
    private String rawContent;

    @Column(name = "CHECKSUM", nullable = false,length=64)
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

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "LAST_ACCESSED_AT", nullable=false)
    private Date lastAccessedAt;

    public Attachment()
    {
        id = -1;
        version = 0;
        guid = UUID.randomUUID().toString();
    }

    public Attachment(long id,String filename,String guid)
    {
        this.id = id;
        this.filename = filename;
        this.guid = guid;
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

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Folder getFolder() {
        return folder;
    }

    public void setFolder(Folder folder) {
        this.folder = folder;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getEncodedContent()
    {
        return encodedContent;
    }

    public String getContent() {
        if(StringUtils.isEmpty(rawContent))
        {
            rawContent = new String(base64.decode(encodedContent));
        }
        return rawContent;
    }

    public void setContent(String rawContent) {
        this.rawContent = rawContent;
        this.encodedContent = base64.encodeAsString(rawContent.getBytes());
        this.checksum = generateChecksum();
        this.version++;
    }

    public String getChecksum() {
        return checksum;
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

    public Date getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(Date lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    private String generateChecksum()
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(rawContent.getBytes(), 0, rawContent.getBytes().length);
            return new String(Hex.encodeHex(digest.digest()));
        } catch(Exception e) {
            e.printStackTrace();
            return "0";
        }
    }
}
