package com.convertx.core;

import com.convertx.exceptions.ConversionException;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.multipdf.Splitter;
import org.apache.pdfbox.pdmodel.PDDocument;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public final class PdfTools {
    private PdfTools() {}

    public static void merge(List<Path> inputs, Path output) throws ConversionException {
        if (inputs.size() < 2) throw new ConversionException("PDF merge requires at least two input files.");
        try {
            PDFMergerUtility merger = new PDFMergerUtility();
            for (Path p : inputs) {
                if (!Files.exists(p)) throw new ConversionException("Input PDF does not exist: " + p);
                merger.addSource(p.toFile());
            }
            merger.setDestinationFileName(output.toString());
            merger.mergeDocuments(null);
        } catch (ConversionException e) { throw e; }
          catch (Exception e) { throw new ConversionException("PDF merge failed.", e); }
    }

    public static void split(Path input, Path outputDirectory) throws ConversionException {
        try {
            Files.createDirectories(outputDirectory);
            try (PDDocument source = Loader.loadPDF(input.toFile())) {
                List<PDDocument> pages = new Splitter().split(source);
                for (int i = 0; i < pages.size(); i++) {
                    try (PDDocument page = pages.get(i)) {
                        page.save(outputDirectory.resolve("page-" + (i + 1) + ".pdf").toFile());
                    }
                }
            }
        } catch (IOException e) { throw new ConversionException("PDF split failed.", e); }
    }
}
