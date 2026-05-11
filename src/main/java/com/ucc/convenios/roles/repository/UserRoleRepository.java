package com.ucc.convenios.roles.repository;

import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.roles.entity.UserRole;
import com.ucc.convenios.users.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRoleRepository extends JpaRepository<UserRole, UUID> {

    @EntityGraph(attributePaths = {"role"})
    List<UserRole> findByUser(User user);

    @EntityGraph(attributePaths = {"user", "role"})
    List<UserRole> findByRole(Role role);

    @EntityGraph(attributePaths = {"user", "role"})
    Optional<UserRole> findByUserAndRole(User user, Role role);

    boolean existsByUserAndRole(User user, Role role);
}