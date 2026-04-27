package com.ucc.convenios.roles.repository;

import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.users.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    @EntityGraph(attributePaths = {"role"})
    List<UserRole> findByUser(User user);

    boolean existsByUserAndRole(User user, Role role);
}