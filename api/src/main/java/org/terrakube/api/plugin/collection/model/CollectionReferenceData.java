package org.terrakube.api.plugin.collection.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollectionReferenceData {
    String type;
    String id;
    CollectionReferenceAttributes attributes;
}
