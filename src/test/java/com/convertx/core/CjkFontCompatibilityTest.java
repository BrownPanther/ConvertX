package com.convertx.core;

import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

class CjkFontCompatibilityTest {
    @Test
    void bundledCjkFontIsAnOpenTypeFont() throws Exception {
        try (InputStream in = CjkFontCompatibilityTest.class
                .getResourceAsStream("/fonts/NotoSansCJKSC-Regular.ttf")) {
            assertNotNull(in);
            byte[] header = in.readNBytes(4);
            assertArrayEquals(new byte[] {'O', 'T', 'T', 'O'}, header,
                    "The bundled CJK font is OpenType/CFF and must not be TrueType-subsetted.");
        }
    }
}
