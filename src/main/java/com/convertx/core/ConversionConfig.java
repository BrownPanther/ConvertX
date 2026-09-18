package com.convertx.core;

import java.nio.file.Path;

public record ConversionConfig(boolean overwrite, boolean keepIntermediates, Path tempDirectory) {
    public static ConversionConfig defaults() {
        return new ConversionConfig(false, false, Path.of(System.getProperty("java.io.tmpdir"), "convertx"));
    }
}
