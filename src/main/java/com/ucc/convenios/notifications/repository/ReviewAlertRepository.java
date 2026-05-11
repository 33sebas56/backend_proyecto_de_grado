package com.ucc.convenios.notifications.repository;

import com.ucc.convenios.notifications.entity.ReviewAlert;
import com.ucc.convenios.shared.enums.ReviewAlertAudience;
import com.ucc.convenios.users.entity.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ReviewAlertRepository extends JpaRepository<ReviewAlert, UUID> {

    @EntityGraph(attributePaths = {"approvalStep", "convenio", "recipientUser"})
    List<ReviewAlert> findByRecipientUserOrderByCreatedAtDesc(User recipientUser);

    @EntityGraph(attributePaths = {"approvalStep", "convenio", "recipientUser"})
    List<ReviewAlert> findByRecipientUserOrderByCreatedAtDesc(User recipientUser, Pageable pageable);

    @EntityGraph(attributePaths = {"approvalStep", "convenio", "recipientUser"})
    List<ReviewAlert> findByAudienceOrderByCreatedAtDesc(ReviewAlertAudience audience);

    @EntityGraph(attributePaths = {"approvalStep", "convenio", "recipientUser"})
    List<ReviewAlert> findAllByOrderByCreatedAtDesc();

    long countByRecipientUser(User recipientUser);

    long countByRecipientUserAndReadAtIsNull(User recipientUser);

    long countByReadAtIsNull();
}