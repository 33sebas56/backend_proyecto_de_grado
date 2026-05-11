package com.ucc.convenios.shared.enums;

public enum ConvenioType {
    MARCO(
            "Convenio marco",
            "RECTOR_MEDELLIN",
            "Rector sede Medellín"
    ),

    PRACTICA(
            "Convenio de prácticas",
            "RECTORIA",
            "Rector de la institución"
    ),

    BIENESTAR(
            "Convenio de bienestar",
            "RECTOR_MEDELLIN",
            "Rector sede Medellín"
    ),

    DESCUENTO(
            "Convenio de descuento",
            "RECTOR_MEDELLIN",
            "Rector sede Medellín"
    );

    private final String displayName;
    private final String rectorRoleName;
    private final String rectorSignerLabel;

    ConvenioType(String displayName, String rectorRoleName, String rectorSignerLabel) {
        this.displayName = displayName;
        this.rectorRoleName = rectorRoleName;
        this.rectorSignerLabel = rectorSignerLabel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getRectorRoleName() {
        return rectorRoleName;
    }

    public String getRectorSignerLabel() {
        return rectorSignerLabel;
    }

    public boolean requiresInstitutionRector() {
        return this == PRACTICA;
    }
}