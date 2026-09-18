# ConvertX Final Submission Checklist

## Build verification

- [ ] Install Java 17+
- [ ] Install Maven 3.9+
- [ ] Run `mvn clean test`
- [ ] Run `mvn clean package`
- [ ] Confirm `target/convertx-1.0.0.jar`
- [ ] Start with `java -jar target/convertx-1.0.0.jar`

## Functional verification

- [ ] Main interactive menu opens without warnings/errors
- [ ] TXT -> PDF
- [ ] DOCX -> PDF
- [ ] DOCX -> TXT
- [ ] PDF -> TXT
- [ ] CSV -> JSON
- [ ] CSV -> XML
- [ ] CSV -> XLSX
- [ ] XLSX -> CSV
- [ ] JSON -> XML
- [ ] XML -> JSON
- [ ] PNG -> JPG
- [ ] JPG -> PNG
- [ ] PNG -> PDF
- [ ] PDF -> PNG
- [ ] BFS path search
- [ ] Dijkstra best-path search
- [ ] Automatic multi-step conversion
- [ ] Batch conversion
- [ ] PDF merge
- [ ] PDF split
- [ ] ZIP extraction
- [ ] Persistent history
- [ ] Configuration display
- [ ] WAV -> MP3 after installing FFmpeg

## Unicode verification

- [ ] DOCX -> PDF preserves accented Latin text
- [ ] DOCX -> PDF preserves Cyrillic text
- [ ] DOCX -> PDF preserves Devanagari text
- [ ] DOCX -> PDF preserves Gurmukhi text
- [ ] DOCX -> PDF preserves CJK text
- [ ] No Unicode characters are replaced with `?`

## Submission materials

- [ ] README.md
- [ ] statement.md
- [ ] ARCHITECTURE.md
- [ ] FINAL_TEST_PLAN.md
- [ ] FONTS.md
- [ ] SUBMISSION_CHECKLIST.md
- [ ] Source code
- [ ] JUnit tests
- [ ] Final report
- [ ] Presentation
- [ ] GitHub repository updated
- [ ] Terminal screenshots captured

Recommended screenshots:
1. Main menu
2. Supported formats
3. BFS path search
4. Best path search
5. Successful multi-step conversion
6. Unicode DOCX -> PDF result
7. Batch progress
8. PDF utility
9. History
10. GitHub project structure
