# Project Statement — ConvertX

## Problem Statement

People regularly need to convert files between formats — a report from DOCX to
PDF, a dataset from CSV to JSON, an image from PNG to JPG — but no single common
tool converts every pair of formats directly. When a direct converter for a given
pair doesn't exist, the usual workaround is to chain several separate tools by
hand (e.g. convert DOCX to PDF in one program, then PDF to JPG in another),
which is slow, error-prone, and hard to repeat consistently, especially across a
batch of files.

## Scope of the Project

ConvertX is a Java 17 command-line application that:

- Represents every available converter as a directed edge in a conversion graph
  (`sourceFormat -> targetFormat`) rather than hard-coding every possible pair.
- Automatically discovers a multi-step conversion route when no direct converter
  is registered, using **BFS** (fewest steps) or **Dijkstra's algorithm**
  (lowest total converter cost).
- Executes the discovered route end-to-end, managing temporary intermediate
  files and reporting progress per stage.
- Extends beyond single-file conversion with batch conversion, PDF
  merge/split, safe ZIP extraction, persistent conversion history, and
  user-editable configuration.

The scope is intentionally a **CLI-only** Java application: there is no GUI, no
web server, and no external database — persistence (history, configuration) uses
local files. Audio/video conversion depends on an external FFmpeg installation;
all other conversions are self-contained.

## Target Users

- Students and self-learners who need a single tool to convert between common
  document, data, image, audio, video and archive formats without installing a
  separate program for every pair of formats.
- Anyone working from a terminal (developers, coursework submissions, scripted
  workflows) who needs file conversion to be scriptable rather than GUI-driven.
- As a course project, its secondary audience is the evaluator, who can inspect
  the graph-routing algorithms (BFS/Dijkstra), modular converter design, and
  automated test suite directly from the CLI and source code.

## High-Level Features

1. **Automatic graph-based routing** — BFS and Dijkstra routing over a
   directed graph of registered converters, with automatic multi-step
   conversion when no direct converter exists (e.g. `DOCX -> PDF -> JPG`).
2. **Wide format coverage** — documents (TXT, PDF, DOCX, HTML, MD), images
   (JPG, PNG, BMP, GIF), data (CSV, JSON, XML, XLSX), audio (MP3, WAV, OGG),
   video (MP4, MKV, AVI, MOV), and archives (ZIP, TAR, GZ).
3. **Batch conversion** — converts every matching file in a directory using a
   thread pool.
4. **PDF and archive utilities** — PDF merge, PDF page-splitting, and ZIP
   extraction with path-traversal protection.
5. **Persistence** — conversion history and configuration survive across runs.
6. **Unicode-aware PDF output** — bundled Noto fonts preserve non-Latin scripts
   (Cyrillic, Devanagari, Gurmukhi, Arabic, CJK) in generated PDFs.
7. **Dual interface** — a guided interactive menu and direct one-line CLI
   commands, both fully terminal-based.
