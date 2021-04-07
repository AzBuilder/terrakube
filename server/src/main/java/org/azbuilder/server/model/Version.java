package org.azbuilder.server.model;


import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Map;
import java.util.UUID;

@Include(type = "version")
@Getter
@Setter
@Entity
public class Version {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private Status status;

    @ManyToOne
    private Module module;
}

enum Status{
    preAlpha,
    alpha,
    beta,
    releaseCandidate,
    releaseToManufacturing,
    generalAvailability
}
