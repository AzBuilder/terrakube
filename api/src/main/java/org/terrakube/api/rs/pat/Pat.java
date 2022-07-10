package org.terrakube.api.rs.pat;

import lombok.*;
import org.hibernate.annotations.Type;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
public class Pat extends GenericAuditFields {
    @Id
    @Type(type="uuid-char")
    private UUID id;

    private int days;
    private String description;
}
