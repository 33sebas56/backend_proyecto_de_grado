package com.ucc.convenios.shared.enums;

public enum RectorSignerType {
    INSTITUCION("Rector de la institución"),
    MEDELLIN("Rector sede Medellín");

    private final String displayName;

    RectorSignerType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}