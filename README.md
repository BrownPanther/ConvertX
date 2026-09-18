# ConvertX

## Project Title

**ConvertX — A Graph-Routed, Multi-Format File Conversion CLI (Java)**

VITyarthi Build Your Own Project (BYOP) — Programming in Java

Author: Simrandeep (25BAI11132), B.Tech AI & ML, VIT Bhopal University

## Overview

ConvertX is a Java 17 **command-line** file conversion system. Instead of hard-coding
every possible pair of formats, ConvertX models every available converter as a
directed edge in a graph (`sourceFormat -> targetFormat`). When a user requests a
conversion that has no direct converter, ConvertX automatically searches the graph
for a multi-step route and runs each converter in the route in sequence — for
example `DOCX -> PDF -> JPG`.

Two routing algorithms are implemented over the same graph:

- **BFS (Breadth-First Search)** — finds the route with the fewest conversion steps.
- **Dijkstra's algorithm** — finds the route with the lowest total converter cost,
  using a per-converter `cost()` weight.

Around this routing core, the project adds batch conversion, persistent history,
configuration, PDF utilities, and safe archive extraction, so it behaves like a
small, self-contained file-conversion utility rather than a single demo script.

## Features

ConvertX is organized into three major functional modules, each with a clear
input/output structure:

1. **Conversion routing & execution module** — accepts a source file and a desired
   output file, detects formats from file names, finds a route through the
   conversion graph (BFS or Dijkstra), executes each converter stage in order using
   temporary intermediate files, reports per-stage progress, and writes the final
   output file.
2. **Batch & utility operations module** — batch-converts every matching file in a
   directory using a thread pool; merges multiple PDFs into one; splits a PDF into
   per-page files; safely extracts ZIP archives with path-traversal protection.
3. **History & configuration module** — records every conversion attempt (source,
   target, route, duration, success/failure) to persistent storage and lets the
   user view or clear it; loads and displays user-editable configuration
   (overwrite behavior, temp directory, whether to keep intermediate files).

Other notable features:

- Interactive menu **and** direct command-line arguments — both are fully
  keyboard/terminal driven (see *Project Executability* below).
- Unicode-aware TXT/DOCX → PDF conversion using bundled Noto fonts (Latin,
  Cyrillic, Devanagari, Gurmukhi, Arabic, CJK) instead of the `?` glyph
  substitution that PDF's Standard 14 fonts would produce.
- Clean ASCII console output for reliable display on Windows terminals.
- Packaged as a single runnable JAR via the Maven Shade plugin.

**Supported conversion groups**

| Category  | Formats |
|---|---|
| Documents | TXT, PDF, DOCX, HTML, MD |
| Images    | JPG/JPEG, PNG, BMP, GIF |
| Data      | CSV, JSON, XML, XLSX |
| Audio     | MP3, WAV, OGG (requires FFmpeg) |
| Video     | MP4, MKV, AVI, MOV (requires FFmpeg) |
| Archives  | ZIP, TAR, GZ |

## Technologies / Tools Used

- **Language:** Java 17
- **Build tool:** Maven (`maven-shade-plugin` for the runnable JAR, `maven-surefire-plugin` for tests)
- **Libraries:** Apache PDFBox 3.0.8 (PDF read/write), Apache POI (`poi-ooxml`) 5.4.1
  (DOCX/XLSX), Jackson Databind + Jackson Dataformat XML 2.19.2 (JSON/XML), Apache
  Commons Compress 1.27.1 (TAR/GZ/ZIP), Log4j 2.26.1
- **Testing:** JUnit 5 (Jupiter)
- **External tool (optional):** FFmpeg on `PATH`, only for audio/video conversion
- **Version control:** Git / GitHub

## Project Executability

ConvertX is **fully executable from the command line**. It requires no GUI
framework and no GUI-based setup step:

- The interactive menu runs entirely inside the terminal (text prompts, numbered
  choices, `Scanner`-based input).
- Every operation is also available as a single non-interactive command-line
  invocation (see *Direct command examples* below), so the whole tool can be
  scripted or run in a plain terminal/CI environment.
- The only external dependency (FFmpeg) is itself a command-line tool.

## Steps to Install & Run the Project

**Requirements**

- Java 17 or newer
- Maven 3.9 or newer
- FFmpeg on `PATH` — only needed for MP3/WAV/OGG and MP4/MKV/AVI/MOV conversions

**Build**

From the project root (`ConvertX/`):

```text
mvn clean test
mvn clean package
```

This produces a runnable JAR at:

```text
target/convertx-1.0.0.jar
```

**Run — interactive menu**

```text
java -jar target/convertx-1.0.0.jar
```

```text
=== CONVERTX JAVA CLI v5 ===

1. Convert File
2. Find BFS Path
3. Find Best Path
4. Batch Convert
5. Supported Formats
6. History
7. Clear History
8. PDF Merge
9. PDF Split
10. Config
11. Exit
Choice:
```

**Run — direct command-line arguments**

```text
java -jar target/convertx-1.0.0.jar convert input.csv output.json
java -jar target/convertx-1.0.0.jar path docx jpg
java -jar target/convertx-1.0.0.jar bestpath docx jpg
java -jar target/convertx-1.0.0.jar batch ./photos jpg png
java -jar target/convertx-1.0.0.jar pdfmerge a.pdf b.pdf merged.pdf
java -jar target/convertx-1.0.0.jar pdfsplit input.pdf ./pages
java -jar target/convertx-1.0.0.jar zipextract archive.zip ./extracted
java -jar target/convertx-1.0.0.jar formats
java -jar target/convertx-1.0.0.jar history
java -jar target/convertx-1.0.0.jar config
```

Windows users can also double-click `build-and-run.bat`, which runs the same
Maven build/run steps.

**Automatic multi-step routing example**

If no direct converter is registered for a pair of formats, ConvertX searches for
an intermediate route, e.g.:

```text
DOCX -> PDF -> JPG
```

## Instructions for Testing

Automated unit tests (JUnit 5) cover the graph routing algorithms and Unicode PDF
generation:

```text
mvn clean test
```

This runs, among others:

- `ConversionRegistryTest` — BFS and weighted-routing correctness
- `WeightedPathTest` — Dijkstra selects the lower-cost route when costs differ
- `DocumentUnicodeConversionTest` — end-to-end DOCX → PDF Unicode regression test
- `PdfFontResourceTest`, `CjkFontCompatibilityTest` — bundled-font loading

**Manual verification checklist** (see `FINAL_TEST_PLAN.md` and
`SUBMISSION_CHECKLIST.md` for the full list) using the sample files under
`test-files/`:

1. Run `mvn clean test` and `mvn clean package` — expect all tests to pass and the
   JAR to build.
2. Start the JAR and confirm the interactive menu opens without errors.
3. Convert a document (`DOCX -> PDF`), a data file (`CSV -> JSON`), and an image
   (`PNG -> JPG`) and confirm each output file is created.
4. Run `path docx jpg` and `bestpath docx jpg` and confirm both report an
   automatic route through PDF, with the best-path command also showing a cost.
5. Convert `test-files/documents/sample.docx` to PDF and confirm accented Latin,
   Cyrillic, Devanagari, Gurmukhi and CJK text render correctly (no `?` glyphs).
6. Run a batch conversion on a test directory and confirm all matching files are
   converted.
7. Merge and split PDFs using the `pdfmerge` / `pdfsplit` commands.
8. Extract a ZIP archive and confirm files land inside the destination directory
   only (no path-traversal entries).
9. Perform a conversion, view History, restart the app, and confirm the record
   persisted.

## Screenshots

Terminal screenshots of the running application (main menu, routing, batch
conversion, format catalog, PDF utilities, configuration, and Unicode conversion
results) are included in `Screenshots/` and in `FINAL_REPORT.docx`.

## Unicode PDF Support

DOCX → PDF and TXT → PDF use bundled Noto fonts instead of PDF's Standard 14
fonts, so accented Latin, Cyrillic, Devanagari, Gurmukhi and CJK text render
correctly instead of being replaced with `?`. Font files and license are under
`src/main/resources/fonts/`; see `FONTS.md` for details.

## Important Limitations

- CSV parsing is intentionally lightweight and targets ordinary classroom
  datasets rather than full RFC-4180 quoting/escaping.
- PDF/image conversion is intentionally lightweight and educational rather than a
  full document-layout engine.
- GIF conversion through ImageIO uses ImageIO's image representation and may not
  preserve animation.
- FFmpeg is an external requirement for audio/video conversion.
- WEBP and TIFF are retained as recognized format identifiers but do not
  currently have registered ImageIO conversion edges.

## Project Structure

```text
src/main/java/com/convertx/
├── cli/          CLI entry point (interactive menu + direct commands)
├── core/         conversion graph, BFS/Dijkstra routing, engine, history,
│                 configuration, PDF utilities, batch conversion
├── converters/   format-specific Converter implementations
├── model/        FileFormat enum and ConversionResult model
└── exceptions/   ConversionException

src/main/resources/fonts/   bundled Unicode PDF fonts
src/test/java/               JUnit 5 tests
test-files/                  sample input files for manual verification
```

### Final verification

Before submission, run `mvn clean test` and `mvn clean package`. The test suite
includes an end-to-end Unicode DOCX-to-PDF regression test.
