# Bundled PDF Fonts

ConvertX bundles Noto Sans font files so PDF output can preserve Unicode text without depending on fonts installed on the user's computer.

Included families:
- Noto Sans
- Noto Sans Devanagari
- Noto Sans Gurmukhi
- Noto Sans Arabic
- Noto Sans CJK SC

The PDF writer selects a bundled font by Unicode script. This covers Latin/extended Latin, Greek, Devanagari, Gurmukhi, Arabic, and major CJK scripts used by the included test data.

The Noto Sans CJK SC resource is an OpenType/CFF font. ConvertX embeds that resource without TrueType subsetting because PDFBox does not support TrueType-style subsetting for OpenType/CFF fonts. The other fonts are embedded as subsets.

The font files are distributed under the SIL Open Font License (OFL). The applicable license text is included at `src/main/resources/fonts/OFL.txt`.


## PDFBox compatibility
ConvertX uses PDFBox 3.0.8. The Unicode PDF regression test loads the bundled fonts and exercises an end-to-end DOCX-to-PDF conversion.
