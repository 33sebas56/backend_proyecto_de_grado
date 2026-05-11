package com.ucc.convenios.roles.controller;

import com.ucc.convenios.roles.dto.AssignRoleRequest;
import com.ucc.convenios.roles.dto.RoleResponse;
import com.ucc.convenios.roles.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RoleResponse> findAllRoles() {
        return roleService.findAllRoles();
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN')")
    public void assignRole(@Valid @RequestBody AssignRoleRequest request) {
        roleService.assignRoleToUser(request.getEmail(), request.getRoleName());
    }

    @PostMapping("/remove")
    @PreAuthorize("hasRole('ADMIN')")
    public void removeRole(@Valid @RequestBody AssignRoleRequest request) {
        roleService.removeRoleFromUser(request.getEmail(), request.getRoleName());
    }
}