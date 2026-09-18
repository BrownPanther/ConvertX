package com.convertx.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class PdfFontResourceTest {
    @Test
    void allUnicodePdfFontsAreBundled() {
        assertNotNull(PdfFontResourceTest.class.getResourceAsStream("/fonts/NotoSans-Regular.ttf"));
        assertNotNull(PdfFontResourceTest.class.getResourceAsStream("/fonts/NotoSansDevanagari-Regular.ttf"));
        assertNotNull(PdfFontResourceTest.class.getResourceAsStream("/fonts/NotoSansGurmukhi-Regular.ttf"));
        assertNotNull(PdfFontResourceTest.class.getResourceAsStream("/fonts/NotoSansArabic-Regular.ttf"));
        assertNotNull(PdfFontResourceTest.class.getResourceAsStream("/fonts/NotoSansCJKSC-Regular.ttf"));
    }
}
