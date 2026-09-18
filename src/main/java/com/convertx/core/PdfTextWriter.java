package com.convertx.core;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.io.IOException;
import java.io.InputStream;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

/**
 * Writes UTF-8 text to PDF using bundled Unicode fonts with script-aware fallback.
 *
 * PDFBox 3.0.x embeds the bundled TrueType script fonts for normal text. CJK
 * text is rendered through Java2D when present because the bundled CJK resource
 * uses CFF/OpenType outlines that PDFBox cannot load as a Type0 TrueType font.
 */
public final class PdfTextWriter {
    private static final float FONT_SIZE = 10.5f;
    private static final float LEADING = 14f;
    private static final float LEFT = 45f;
    private static final float TOP = 750f;
    private static final float BOTTOM = 45f;
    private static final float MAX_WIDTH = 522f;

    private PdfTextWriter() {}

    public static void write(PDDocument document, String text) throws IOException {
        String normalized = text == null ? "" : text.replace("\r\n", "\n").replace('\r', '\n');

        // PDFBox cannot embed the bundled CJK CFF font as a Type0 TrueType font.
        // For lines containing CJK/Hangul text, render through Java2D's logical
        // "Dialog" font. Java2D performs platform font fallback and shaping,
        // which makes the PDF visually faithful without requiring a CFF-to-TTF
        // conversion or a machine-specific font installation.
        if (containsCjk(normalized)) {
            writeWithJava2DFallback(document, normalized);
            return;
        }

        Fonts fonts = new Fonts(document);
        PDPageContentStream stream = null;
        float y = TOP;
        try {
            stream = newStream(document);
            stream.beginText();
            stream.newLineAtOffset(LEFT, y);
            for (String sourceLine : normalized.split("\\n", -1)) {
                List<List<Run>> wrappedLines = wrap(sourceLine, fonts);
                if (wrappedLines.isEmpty()) {
                    y = newLine(stream, y);
                    if (y < BOTTOM) {
                        stream.endText(); stream.close();
                        stream = newStream(document); stream.beginText();
                        stream.newLineAtOffset(LEFT, TOP); y = TOP;
                    }
                    continue;
                }
                for (List<Run> runs : wrappedLines) {
                    for (Run run : runs) {
                        stream.setFont(run.font(), FONT_SIZE);
                        stream.showText(run.text());
                    }
                    y = newLine(stream, y);
                    if (y < BOTTOM) {
                        stream.endText(); stream.close();
                        stream = newStream(document); stream.beginText();
                        stream.newLineAtOffset(LEFT, TOP); y = TOP;
                    }
                }
            }
            stream.endText();
        } finally { if (stream != null) stream.close(); }
    }

    private static boolean containsCjk(String text) {
        for (int i = 0; i < text.length();) {
            int cp = text.codePointAt(i);
            Character.UnicodeScript script = Character.UnicodeScript.of(cp);
            if (script == Character.UnicodeScript.HAN || script == Character.UnicodeScript.HIRAGANA
                    || script == Character.UnicodeScript.KATAKANA || script == Character.UnicodeScript.HANGUL) {
                return true;
            }
            i += Character.charCount(cp);
        }
        return false;
    }

    private static void writeWithJava2DFallback(PDDocument document, String text) throws IOException {
        // Java2D's logical Dialog font uses the platform's installed font fallback
        // and text shaping. This is used only when CJK characters are present,
        // because the bundled CJK resource is CFF/OpenType and PDFBox cannot load
        // it through its TrueType Type0 font loader.
        final int imageWidth = 612;
        final int imageHeight = 792;
        final int marginX = 45;
        final int marginTop = 42;
        final int marginBottom = 42;
        final int lineHeight = 16;
        final Font font = new Font("Dialog", Font.PLAIN, 11);

        List<String> visualLines = new ArrayList<>();
        BufferedImage probe = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D pg = probe.createGraphics();
        pg.setFont(font);
        FontMetrics fm = pg.getFontMetrics();
        for (String raw : text.split("\\n", -1)) {
            String line = raw.isEmpty() ? " " : raw;
            int start = 0;
            while (start < line.length()) {
                int end = start;
                int used = 0;
                while (end < line.length()) {
                    int cp = line.codePointAt(end);
                    String part = new String(Character.toChars(cp));
                    int pw = fm.stringWidth(part);
                    if (used + pw > imageWidth - 2 * marginX && end > start) break;
                    used += pw;
                    end += Character.charCount(cp);
                }
                visualLines.add(line.substring(start, end));
                start = end;
            }
        }
        pg.dispose();

        int linesPerPage = Math.max(1, (imageHeight - marginTop - marginBottom) / lineHeight);
        for (int pageStart = 0; pageStart < visualLines.size(); pageStart += linesPerPage) {
            BufferedImage pageImage = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = pageImage.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g.setFont(font);
            g.setColor(java.awt.Color.WHITE);
            g.fillRect(0, 0, imageWidth, imageHeight);
            g.setColor(java.awt.Color.BLACK);
            FontMetrics metrics = g.getFontMetrics();
            int y = marginTop + metrics.getAscent();
            int end = Math.min(visualLines.size(), pageStart + linesPerPage);
            for (int i = pageStart; i < end; i++) {
                g.drawString(visualLines.get(i), marginX, y);
                y += lineHeight;
            }
            g.dispose();

            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            PDImageXObject image = LosslessFactory.createFromImage(document, pageImage);
            try (PDPageContentStream cs = new PDPageContentStream(document, page)) {
                cs.drawImage(image, 0, 0, PDRectangle.LETTER.getWidth(), PDRectangle.LETTER.getHeight());
            }
        }
    }

    private static PDPageContentStream newStream(PDDocument document) throws IOException {
        PDPage page = new PDPage(PDRectangle.LETTER);
        document.addPage(page);
        return new PDPageContentStream(document, page);
    }

    private static float newLine(PDPageContentStream stream, float y) throws IOException {
        stream.newLineAtOffset(0, -LEADING);
        return y - LEADING;
    }

    private static List<List<Run>> wrap(String line, Fonts fonts) throws IOException {
        List<List<Run>> lines = new ArrayList<>();
        if (line.isEmpty()) return lines;

        List<Run> currentLine = new ArrayList<>();
        StringBuilder currentText = new StringBuilder();
        PDType0Font currentFont = null;
        float width = 0f;

        int offset = 0;
        while (offset < line.length()) {
            int cp = line.codePointAt(offset);
            int chars = Character.charCount(cp);
            String part = line.substring(offset, offset + chars);
            PDType0Font font = fonts.forCodePoint(cp);
            float partWidth = font.getStringWidth(part) / 1000f * FONT_SIZE;

            if (currentFont == null) currentFont = font;

            if (font != currentFont || width + partWidth > MAX_WIDTH) {
                if (currentText.length() > 0) {
                    currentLine.add(new Run(currentFont, currentText.toString()));
                    currentText.setLength(0);
                }
                if (font != currentFont) {
                    currentFont = font;
                    width = 0f;
                } else {
                    if (!currentLine.isEmpty()) lines.add(currentLine);
                    currentLine = new ArrayList<>();
                    width = 0f;
                }
            }

            currentText.append(part);
            width += partWidth;
            offset += chars;
        }

        if (currentText.length() > 0) currentLine.add(new Run(currentFont, currentText.toString()));
        if (!currentLine.isEmpty()) lines.add(currentLine);
        return lines;
    }

    private static InputStream resource(String name) throws IOException {
        InputStream in = PdfTextWriter.class.getResourceAsStream("/fonts/" + name);
        if (in == null) throw new IOException("Bundled PDF font missing: " + name);
        return in;
    }

    private static PDType0Font loadSubsetFont(PDDocument document, String name) throws IOException {
        try (InputStream in = resource(name)) {
            return PDType0Font.load(document, in, true);
        } catch (IOException e) {
            throw new IOException("Unable to load bundled PDF font: " + name + ". " + e.getMessage(), e);
        }
    }


    private record Run(PDType0Font font, String text) {}

    private static final class Fonts {
        private final PDType0Font latin;
        private final PDType0Font devanagari;
        private final PDType0Font gurmukhi;
        private final PDType0Font arabic;
        private final PDDocument document;

        Fonts(PDDocument document) throws IOException {
            this.document = document;
            latin = loadSubsetFont(document, "NotoSans-Regular.ttf");
            devanagari = loadSubsetFont(document, "NotoSansDevanagari-Regular.ttf");
            gurmukhi = loadSubsetFont(document, "NotoSansGurmukhi-Regular.ttf");
            arabic = loadSubsetFont(document, "NotoSansArabic-Regular.ttf");
            // CJK text is handled by the Java2D fallback before Fonts is created.
        }

        PDType0Font forCodePoint(int cp) throws IOException {
            Character.UnicodeScript script = Character.UnicodeScript.of(cp);
            return switch (script) {
                case DEVANAGARI -> devanagari;
                case GURMUKHI -> gurmukhi;
                case ARABIC -> arabic;
                default -> latin;
            };
        }
    }
}
