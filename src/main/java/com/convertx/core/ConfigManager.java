package com.convertx.core;

import java.io.IOException;
import java.nio.file.*;
import java.util.Properties;

public final class ConfigManager {
    private final Path file = Path.of(System.getProperty("user.home"), ".convertx", "config.properties");
    public ConversionConfig load() {
        Properties p = new Properties();
        try { if (Files.exists(file)) try (var in = Files.newInputStream(file)) { p.load(in); } } catch (IOException ignored) {}
        boolean overwrite = Boolean.parseBoolean(p.getProperty("overwrite", "false"));
        boolean keep = Boolean.parseBoolean(p.getProperty("keepIntermediates", "false"));
        Path temp = Path.of(p.getProperty("tempDirectory", Path.of(System.getProperty("java.io.tmpdir"), "convertx").toString()));
        return new ConversionConfig(overwrite, keep, temp);
    }
    public void save(ConversionConfig config) throws IOException {
        Files.createDirectories(file.getParent());
        Properties p = new Properties();
        p.setProperty("overwrite", Boolean.toString(config.overwrite()));
        p.setProperty("keepIntermediates", Boolean.toString(config.keepIntermediates()));
        p.setProperty("tempDirectory", config.tempDirectory().toString());
        try (var out = Files.newOutputStream(file)) { p.store(out, "ConvertX configuration"); }
    }
}
