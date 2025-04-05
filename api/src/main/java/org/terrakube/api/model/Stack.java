package org.terrakube.api.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.terrakube.api.plugin.security.audit.GenericAuditFields;
import org.terrakube.api.rs.IdConverter;
import org.terrakube.api.rs.Organization;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.sql.Types;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "stack")
public class Stack extends GenericAuditFields {
    @Id
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    private Organization organization;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "tool_type")
    private String toolType;

    @Column(name = "iac_engine")
    private String iacEngine;

    @Column(name = "repo_url")
    private String repoUrl;

    @Column(name = "default_branch")
    private String defaultBranch;

    @Column(name = "vcs_id")
    @JdbcTypeCode(Types.VARCHAR)
    @Convert(converter = IdConverter.class)
    private UUID vcsId;
} 