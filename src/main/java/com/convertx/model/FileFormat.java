package com.convertx.model;

import java.util.Locale;

public enum FileFormat {
    TXT, PDF, DOCX, HTML, MD, JPG, JPEG, PNG, WEBP, BMP, GIF, TIFF, CSV, JSON, XML, XLSX,
    MP3, WAV, OGG, MP4, MKV, AVI, MOV, ZIP, TAR, GZ, UNKNOWN;

    public static FileFormat fromExtension(String s) {
        if (s == null || s.isBlank()) return UNKNOWN;
        String value = s.trim();
        int slash = Math.max(value.lastIndexOf('/'), value.lastIndexOf('\\'));
        if (slash >= 0) value = value.substring(slash + 1);
        int dot = value.lastIndexOf('.');
        if (dot >= 0) value = value.substring(dot + 1);
        if (value.isBlank()) return UNKNOWN;
        String e = value.toLowerCase(Locale.ROOT);
        if (e.equals("jpeg")) return JPEG;
        try { return valueOf(e.toUpperCase(Locale.ROOT)); }
        catch (IllegalArgumentException ex) { return UNKNOWN; }
    }

    public static FileFormat fromFileName(String s) {
        return fromExtension(s);
    }
}
