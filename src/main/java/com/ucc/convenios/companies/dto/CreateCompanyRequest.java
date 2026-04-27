package com.ucc.convenios.companies.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCompanyRequest {

    @NotBlank(message = "El NIT es obligatorio")
    @Size(max = 30, message = "El NIT no puede superar 30 caracteres")
    private String nit;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(max = 200, message = "La razón social no puede superar 200 caracteres")
    private String businessName;

    @Size(max = 200, message = "El nombre comercial no puede superar 200 caracteres")
    private String tradeName;

    @Size(max = 50, message = "El tipo de identificación no puede superar 50 caracteres")
    private String identificationType;

    @Size(max = 150, message = "El representante legal no puede superar 150 caracteres")
    private String legalRepresentativeName;

    @Email(message = "El correo de contacto no tiene un formato válido")
    @Size(max = 180, message = "El correo de contacto no puede superar 180 caracteres")
    private String contactEmail;

    @Size(max = 50, message = "El teléfono no puede superar 50 caracteres")
    private String contactPhone;

    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    private String address;

    public CreateCompanyRequest() {
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
}