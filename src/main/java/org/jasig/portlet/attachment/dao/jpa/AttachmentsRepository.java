package org.jasig.portlet.attachment.dao.jpa;

import org.jasig.portlet.attachment.model.Attachment;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AttachmentsRepository extends CrudRepository<Attachment, Long> {

    Optional<Attachment> findByGuid(String guid);

}
