package org.terrakube.api.rs.provider.implementation;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.terrakube.api.rs.provider.Provider;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Include(rootLevel = false)
@Getter
@Setter
@Entity(name = "version")
public class Version {

    @Id
    @Type(type = "uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name = "version_number")
    private String versionNumber;

    @Column(name = "protocols")
    private String protocols;

    @ManyToOne
    private Provider provider;

    @OneToMany(mappedBy = "version")
    List<Implementation> implementation;

}
