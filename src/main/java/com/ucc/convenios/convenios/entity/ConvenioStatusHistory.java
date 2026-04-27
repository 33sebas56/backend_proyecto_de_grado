package com.ucc.convenios.convenios.entity;

import com.ucc.convenios.shared.enums.ConvenioStage;
import com.ucc.convenios.shared.enums.ConvenioStatus;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "convenio_status_history")
public class ConvenioStatusHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "convenio_id", nullable = false)
    private Convenio convenio;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 40)
    private ConvenioStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 40)
    private ConvenioStatus newStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_stage", length = 40)
    private ConvenioStage previousStage;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_stage", length = 40)
    private ConvenioStage newStage;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    public ConvenioStatusHistory() {
    }

    @PrePersist
    public void prePersist() {
        if (this.performedAt == null) {
            this.performedAt = LocalDateTime.now();
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

    public ConvenioStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ConvenioStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ConvenioStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ConvenioStatus newStatus) {
        this.newStatus = newStatus;
    }

    public ConvenioStage getPreviousStage() {
        return previousStage;
    }

    public void setPreviousStage(ConvenioStage previousStage) {
        this.previousStage = previousStage;
    }

    public ConvenioStage getNewStage() {
        return newStage;
    }

    public void setNewStage(ConvenioStage newStage) {
        this.newStage = newStage;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(User performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getPerformedAt() {
        return performedAt;
    }

    public void setPerformedAt(LocalDateTime performedAt) {
        this.performedAt = performedAt;
    }
}