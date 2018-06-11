package com.example.example.util.jasper;

public enum Formato {

    PDF(".pdf"),
    HTML(".html"),
    EXCEL(".xls");
    private final String value;

    Formato(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static Formato fromValue(String v) {
        for (Formato c: Formato.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v);
    }
}