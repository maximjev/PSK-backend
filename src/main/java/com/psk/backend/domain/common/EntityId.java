package com.psk.backend.domain.common;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EntityId {
    private String id;

    public EntityId(String id) {
        this.id = id;
    }

    public static EntityId entityId(String id) {
        return new EntityId(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
