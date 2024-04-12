package org.terrakube.api.rs.token.group;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;

import jakarta.persistence.*;
import org.terrakube.api.rs.IdConverter;

import java.sql.Types;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "team_token")
public class Group extends GenericAuditFields {
    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int days;

    private int hours;

    private int minutes;
    
    @Column(name="group_name")
    private String group;
    private String description;
    private boolean deleted;
}
