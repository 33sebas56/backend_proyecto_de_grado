package com.ucc.convenios.companies.entity;

import com.ucc.convenios.shared.enums.CompanyStatus;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "company_validation_history")
public class CompanyValidationHistory {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 40)
    private CompanyStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false, length = 40)
    private CompanyStatus newStatus;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "performed_by", nullable = false)
    private User performedBy;

    @Column(name = "performed_at", nullable = false)
    private LocalDateTime performedAt;

    public CompanyValidationHistory() {
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

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public CompanyStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(CompanyStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public CompanyStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(CompanyStatus newStatus) {
        this.newStatus = newStatus;
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