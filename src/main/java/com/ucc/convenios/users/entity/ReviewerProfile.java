package com.ucc.convenios.users.entity;

import com.ucc.convenios.roles.entity.Role;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "reviewer_profiles",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_reviewer_profiles_user_role", columnNames = {"user_id", "role_id"})
        }
)
public class ReviewerProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "available", nullable = false)
    private Boolean available = true;

    @Column(name = "max_active_cases", nullable = false)
    private Integer maxActiveCases = 5;

    @Column(name = "current_active_cases", nullable = false)
    private Integer currentActiveCases = 0;

    @Column(name = "notes", length = 255)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "seal_name", length = 120)
    private String sealName;

    public ReviewerProfile() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.available == null) {
            this.available = true;
        }

        if (this.maxActiveCases == null) {
            this.maxActiveCases = 5;
        }

        if (this.currentActiveCases == null) {
            this.currentActiveCases = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Integer getMaxActiveCases() {
        return maxActiveCases;
    }

    public void setMaxActiveCases(Integer maxActiveCases) {
        this.maxActiveCases = maxActiveCases;
    }

    public Integer getCurrentActiveCases() {
        return currentActiveCases;
    }

    public void setCurrentActiveCases(Integer currentActiveCases) {
        this.currentActiveCases = currentActiveCases;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }
}