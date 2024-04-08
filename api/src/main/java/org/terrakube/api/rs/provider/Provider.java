package org.terrakube.api.rs.provider;

import com.yahoo.elide.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.Organization;
import org.terrakube.api.rs.provider.implementation.Version;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@ReadPermission(expression = "team view provider")
@CreatePermission(expression = "team manage provider")
@UpdatePermission(expression = "team manage provider")
@DeletePermission(expression = "team manage provider")
@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "provider")
public class Provider {
    @Id
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "provider")
    private List<Version> version;
}
