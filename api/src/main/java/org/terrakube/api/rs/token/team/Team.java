package org.terrakube.api.rs.token.team;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "team_token")
public class Team extends GenericAuditFields {
    @Id
    @Type(type="uuid-char")
    private UUID id;

    private int days;
    private String group;
    private String description;
}
