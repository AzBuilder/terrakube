package org.terrakube.api.rs.token.pat;

import lombok.*;
import org.hibernate.annotations.Type;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;

import jakarta.persistence.*;
import org.terrakube.api.rs.IdConverter;

import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "pat")
public class Pat extends GenericAuditFields {
    @Id
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int days;
    private boolean deleted;
    private String description;
}
