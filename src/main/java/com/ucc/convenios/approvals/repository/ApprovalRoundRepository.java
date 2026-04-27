package com.ucc.convenios.approvals.repository;

import com.ucc.convenios.approvals.entity.ApprovalRound;
import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.shared.enums.ApprovalRoundStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApprovalRoundRepository extends JpaRepository<ApprovalRound, UUID> {

    List<ApprovalRound> findByConvenioOrderByRoundNumberDesc(Convenio convenio);

    Optional<ApprovalRound> findFirstByConvenioOrderByRoundNumberDesc(Convenio convenio);

    @EntityGraph(attributePaths = {"convenio", "convenioVersion"})
    Optional<ApprovalRound> findFirstByConvenioAndStatusOrderByRoundNumberDesc(
            Convenio convenio,
            ApprovalRoundStatus status
    );
}