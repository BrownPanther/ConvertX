package com.convertx.core;
import com.convertx.exceptions.ConversionException;
import com.convertx.model.FileFormat;
import java.nio.file.Path;

public interface Converter {
    boolean supports(FileFormat from, FileFormat to);
    void convert(Path input, Path output) throws ConversionException;
    default int cost() { return 1; }
}
