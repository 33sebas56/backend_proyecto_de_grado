package com.ucc.convenios.users.repository;

import com.ucc.convenios.roles.entity.Role;
import com.ucc.convenios.users.entity.ReviewerProfile;
import com.ucc.convenios.users.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewerProfileRepository extends JpaRepository<ReviewerProfile, UUID> {

    @EntityGraph(attributePaths = {"user", "role"})
    List<ReviewerProfile> findByRoleAndAvailableTrue(Role role);

    @EntityGraph(attributePaths = {"user", "role"})
    Optional<ReviewerProfile> findByUserAndRole(User user, Role role);

    @EntityGraph(attributePaths = {"user", "role"})
    List<ReviewerProfile> findByUser(User user);

    boolean existsByUserAndRole(User user, Role role);
}