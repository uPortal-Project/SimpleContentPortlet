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

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Index;
import org.jasig.portlet.attachment.dao.jpa.QueryName;
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
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import java.util.Date;

/**
 * @author Chris Waymire (chris@waymire.net)
 */
@Entity
@Table(name = "UP_ATTACHMENT_FOLDER")
@Inheritance(strategy = InheritanceType.JOINED)
@SequenceGenerator(
        name="UP_ATTACHMENT_FOLDER_SEQ_GEN",
        sequenceName="UP_ATTACHMENT_FOLDER_SEQ",
        allocationSize=1000
)
@TableGenerator(
        name = "UP_ATTACHMENT_FOLDER_GEN",
        pkColumnValue="UP_ATTACHMENT_FOLDER_PROP",
        allocationSize=1000
)
@org.hibernate.annotations.Table(
        appliesTo = "UP_ATTACHMENT_FOLDER",
        indexes = {
                @Index(name = "IDX_UP_ATTACHMENT_FOLDER_NAME", columnNames = { "NAME"})
        }
)
@NamedQueries({
        @NamedQuery(name=QueryName.GET_FOLDERS_BY_PARENT,
                    query="SELECT f FROM Folder f WHERE f.parent = :parent"),
        @NamedQuery(name=QueryName.GET_FOLDERS,
                    query="SELECT f FROM Folder f"),
})
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Folder {
    @Transient
    private final Logger logger = LoggerFactory.getLogger(Folder.class);

    @Id
    @GeneratedValue(generator = "UP_ATTACHMENT_FOLDER_GEN")
    @Column(name="ID")
    private final long id;

    @Column(name="NAME", nullable=false, length=64)
    private String name;

    @ManyToOne(targetEntity=Folder.class)
    @JoinColumn(name = "PARENT_ID", nullable = true)
    private Folder parent;

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

    public Folder()
    {
        this.id = -1;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getParent() {
        return parent;
    }

    public void setParent(Folder parent) {
        this.parent = parent;
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

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Date getModifiedAt() {
        return modifiedAt;
    }

    @PrePersist
    @PreUpdate
    protected void prePersist()
    {
        Date now = new Date();

        if(createdAt == null)
        {
            createdAt = now;
        }
        modifiedAt = now;
    }
}
