package com.ucc.convenios.roles.dto;

import jakarta.validation.constraints.NotBlank;

public class AssignRoleRequest {

    @NotBlank(message = "El correo del usuario es obligatorio")
    private String email;

    @NotBlank(message = "El rol es obligatorio")
    private String roleName;

    public AssignRoleRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}