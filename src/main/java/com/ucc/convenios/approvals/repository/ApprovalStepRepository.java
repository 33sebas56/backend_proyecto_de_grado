package com.ucc.convenios.approvals.repository;

import com.ucc.convenios.approvals.entity.ApprovalRound;
import com.ucc.convenios.approvals.entity.ApprovalStep;
import com.ucc.convenios.shared.enums.ApprovalStepStatus;
import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.users.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, UUID> {

    @EntityGraph(attributePaths = {
            "approvalRound",
            "approvalRound.convenio",
            "approvalRound.convenio.createdBy",
            "approvalRound.convenioVersion",
            "assignedUser"
    })
    Optional<ApprovalStep> findWithDetailsById(UUID id);

    @EntityGraph(attributePaths = {
            "approvalRound",
            "approvalRound.convenio",
            "approvalRound.convenio.createdBy",
            "approvalRound.convenioVersion",
            "assignedUser"
    })
    List<ApprovalStep> findByAssignedUserAndStatusOrderByAssignedAtDesc(
            User assignedUser,
            ApprovalStepStatus status
    );

    @EntityGraph(attributePaths = {
            "approvalRound",
            "approvalRound.convenio",
            "approvalRound.convenio.createdBy",
            "approvalRound.convenioVersion",
            "assignedUser"
    })
    List<ApprovalStep> findByStatus(ApprovalStepStatus status);

    @EntityGraph(attributePaths = {
            "approvalRound",
            "approvalRound.convenio",
            "approvalRound.convenio.createdBy",
            "approvalRound.convenioVersion",
            "assignedUser"
    })
    List<ApprovalStep> findByApprovalRoundOrderByStageOrderAsc(ApprovalRound approvalRound);

    Optional<ApprovalStep> findByApprovalRoundAndStage(ApprovalRound approvalRound, ConvenioStage stage);

    boolean existsByApprovalRoundAndStage(ApprovalRound approvalRound, ConvenioStage stage);
}