package io.terrakube.api.rs.token.pat;

import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import io.terrakube.api.plugin.security.audit.GenericAuditFields;

import jakarta.persistence.*;
import io.terrakube.api.rs.IdConverter;

import java.sql.Types;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity(name = "pat")
public class Pat extends GenericAuditFields {
    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int days;
    private boolean deleted;
    private String description;
}
