package dev.smo.shortener.urlservice.model;

public enum UrlStatus {
    ACTIVE,
    INACTIVE,
    DELETED;

    public static UrlStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return ACTIVE;
        }

        try {
            return UrlStatus.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            return ACTIVE;
        }
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}