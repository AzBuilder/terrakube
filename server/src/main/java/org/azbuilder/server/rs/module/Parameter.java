package org.azbuilder.server.rs.module;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.UUID;

@Include(type = "parameter")
@Getter
@Setter
@Entity
public class Parameter {

    @Id
    @GeneratedValue
    private UUID id;

    private ParameterType parameterType;

    private String key;

    private String value;

    @ManyToOne
    private Version version;
}

enum ParameterType{
    SECRET,
    VARIABLE,
    ENVIRONMENT
}
