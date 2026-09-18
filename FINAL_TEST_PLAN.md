# ConvertX Final Verification Plan

Run from the project root after extracting the submission ZIP.

## 1. Build

```text
mvn clean test
mvn clean package
java -jar target/convertx-1.0.0.jar
```

Expected: tests pass and the application opens the main menu without dependency errors.

## 2. Document conversion

Use the supplied ConvertX test files.

```text
DOCX -> PDF
DOCX -> TXT
TXT  -> PDF
TXT  -> HTML
PDF  -> TXT
```

For DOCX -> PDF, verify that accented, Cyrillic, Devanagari, Gurmukhi and CJK characters are visible instead of `?`.

## 3. Data conversion

```text
CSV  -> JSON
CSV  -> XML
CSV  -> XLSX
XLSX -> CSV
JSON -> XML
XML  -> JSON
```

## 4. Image conversion

```text
PNG -> JPG
JPG -> PNG
GIF -> PNG
PNG -> PDF
PDF -> PNG
```

## 5. Routing algorithms

```text
Find BFS Path:   DOCX -> JPG
Find Best Path:  DOCX -> JPG
```

Expected route includes the available intermediate PDF conversion:

```text
DOCX -> PDF -> JPG
```

## 6. PDF utilities

- Merge two PDFs.
- Split a multi-page PDF into individual pages.

## 7. Archive utility

Extract a ZIP archive and verify that files are created in the selected directory.

## 8. Batch conversion

Run a batch conversion on a directory containing several files of the same source format.

## 9. Media conversion

Install FFmpeg and verify:

```text
WAV -> MP3
```

Without FFmpeg, ConvertX should show a normal `ERROR:` message rather than a stack trace.

## 10. History

Perform a conversion, select History, restart ConvertX, and confirm the record remains available.

## Unicode regression test
Use `test/documents/sample.docx` and convert it to PDF. The fixture includes accented Latin, CJK, Arabic, Greek, and Devanagari text. Verify that the PDF contains readable glyphs and no `?` substitutions.


## Unicode DOCX regression test
The automated test `DocumentUnicodeConversionTest` creates a DOCX containing accented Latin, Japanese, Arabic and Devanagari text, converts it to PDF, and verifies that the generated PDF opens successfully. Run `mvn clean test` before submission.


## PDFBox 3.x Test API
The Unicode PDF regression test uses `org.apache.pdfbox.Loader.loadPDF(...)` and `PDDocument.getNumberOfPages()`, which are the PDFBox 3.x APIs.
