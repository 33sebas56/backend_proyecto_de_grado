package com.ucc.convenios.companies.entity;

import com.ucc.convenios.shared.enums.CompanyStatus;
import com.ucc.convenios.users.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "nit", nullable = false, unique = true, length = 30)
    private String nit;

    @Column(name = "business_name", nullable = false, length = 200)
    private String businessName;

    @Column(name = "trade_name", length = 200)
    private String tradeName;

    @Column(name = "identification_type", nullable = false, length = 50)
    private String identificationType = "NIT";

    @Column(name = "legal_representative_name", length = 150)
    private String legalRepresentativeName;

    @Column(name = "contact_email", nullable = false, length = 180)
    private String contactEmail;

    @Column(name = "contact_phone", length = 50)
    private String contactPhone;

    @Column(name = "address", length = 255)
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private CompanyStatus status = CompanyStatus.BORRADOR;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    private User validatedBy;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Company() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;

        if (this.status == null) {
            this.status = CompanyStatus.BORRADOR;
        }

        if (this.identificationType == null || this.identificationType.isBlank()) {
            this.identificationType = "NIT";
        }

        normalizeFields();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
        normalizeFields();
    }

    private void normalizeFields() {
        if (this.nit != null) {
            this.nit = this.nit.trim().toUpperCase();
        }

        if (this.contactEmail != null) {
            this.contactEmail = this.contactEmail.trim().toLowerCase();
        }

        if (this.businessName != null) {
            this.businessName = this.businessName.trim();
        }

        if (this.tradeName != null) {
            this.tradeName = this.tradeName.trim();
        }

        if (this.legalRepresentativeName != null) {
            this.legalRepresentativeName = this.legalRepresentativeName.trim();
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getIdentificationType() {
        return identificationType;
    }

    public void setIdentificationType(String identificationType) {
        this.identificationType = identificationType;
    }

    public String getLegalRepresentativeName() {
        return legalRepresentativeName;
    }

    public void setLegalRepresentativeName(String legalRepresentativeName) {
        this.legalRepresentativeName = legalRepresentativeName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void setStatus(CompanyStatus status) {
        this.status = status;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public User getValidatedBy() {
        return validatedBy;
    }

    public void setValidatedBy(User validatedBy) {
        this.validatedBy = validatedBy;
    }

    public LocalDateTime getValidatedAt() {
        return validatedAt;
    }

    public void setValidatedAt(LocalDateTime validatedAt) {
        this.validatedAt = validatedAt;
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
}