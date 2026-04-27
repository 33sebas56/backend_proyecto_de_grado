package com.ucc.convenios.approvals.entity;

import com.ucc.convenios.convenios.entity.Convenio;
import com.ucc.convenios.convenios.entity.ConvenioVersion;
import com.ucc.convenios.shared.enums.ApprovalRoundStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "approval_rounds",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_approval_round_convenio_round",
                        columnNames = {"convenio_id", "round_number"}
                )
        }
)
public class ApprovalRound {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_version_id", nullable = false)
    private ConvenioVersion convenioVersion;

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ApprovalRoundStatus status = ApprovalRoundStatus.EN_PROCESO;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    public ApprovalRound() {
    }

    @PrePersist
    public void prePersist() {
        if (this.startedAt == null) {
            this.startedAt = LocalDateTime.now();
        }

        if (this.status == null) {
            this.status = ApprovalRoundStatus.EN_PROCESO;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Convenio getConvenio() {
        return convenio;
    }

    public void setConvenio(Convenio convenio) {
        this.convenio = convenio;
    }

    public ConvenioVersion getConvenioVersion() {
        return convenioVersion;
    }

    public void setConvenioVersion(ConvenioVersion convenioVersion) {
        this.convenioVersion = convenioVersion;
    }

    public Integer getRoundNumber() {
        return roundNumber;
    }

    public void setRoundNumber(Integer roundNumber) {
        this.roundNumber = roundNumber;
    }

    public ApprovalRoundStatus getStatus() {
        return status;
    }

    public void setStatus(ApprovalRoundStatus status) {
        this.status = status;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
}