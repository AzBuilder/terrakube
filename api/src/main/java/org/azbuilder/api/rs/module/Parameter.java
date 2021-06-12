package org.azbuilder.api.rs.module;

import com.yahoo.elide.annotation.Include;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Include(type = "parameter")
@Getter
@Setter
@Entity
public class Parameter {

    @Id
    @Type(type="uuid-char")
    @GeneratedValue
    private UUID id;

    @Column(name="parameter_type")
    @Enumerated(EnumType.STRING)
    private ParameterType parameterType;

    @Column(name="parameter_key")
    private String key;

    @Column(name="parameter_value")
    private String value;

    @ManyToOne
    private Definition definition;
}

enum ParameterType{
    SECRET,
    VARIABLE,
    ENVIRONMENT
}
