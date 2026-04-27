package com.ucc.convenios.roles.dto;

import com.ucc.convenios.roles.entity.Role;

import java.util.UUID;

public class RoleResponse {

    private UUID id;
    private String name;
    private String description;

    public RoleResponse() {
    }

    public static RoleResponse fromEntity(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        return response;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}