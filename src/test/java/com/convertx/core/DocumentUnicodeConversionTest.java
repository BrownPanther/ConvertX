package com.convertx.core;

import com.convertx.converters.DocumentConverter;
import com.convertx.model.FileFormat;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DocumentUnicodeConversionTest {
    @Test
    void docxWithInternationalTextConvertsToPdf() throws Exception {
        Path dir = Files.createTempDirectory("convertx-unicode-");
        Path docx = dir.resolve("unicode.docx");
        Path pdf = dir.resolve("unicode.pdf");

        Path fixture = Path.of("test-files", "documents", "sample.docx");
        if (Files.exists(fixture)) {
            Files.copy(fixture, docx);
        } else {
            try (XWPFDocument document = new XWPFDocument()) {
                document.createParagraph().createRun().setText(
                        "Unicode: café, naïve, Zürich, Łódź, 日本語, العربية, Ελληνικά, हिन्दी"
                );
                try (var out = Files.newOutputStream(docx)) {
                    document.write(out);
                }
            }
        }

        new DocumentConverter(FileFormat.DOCX, FileFormat.PDF).convert(docx, pdf);

        assertTrue(Files.exists(pdf));
        assertTrue(Files.size(pdf) > 1000);
        try (PDDocument result = Loader.loadPDF(pdf.toFile())) {
            assertTrue(result.getNumberOfPages() > 0);
        }
    }
}
