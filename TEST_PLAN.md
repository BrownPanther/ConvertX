# ConvertX Stage 5 Test Plan

| ID | Test | Expected result |
|---|---|---|
| T1 | TXT -> PDF | PDF generated |
| T2 | CSV -> JSON | JSON generated |
| T3 | DOCX -> PDF | PDF generated |
| T4 | DOCX -> JPG | Automatic multi-step path executes |
| T5 | `path txt jpg` | BFS path displayed |
| T6 | `bestpath txt jpg` | Weighted path and cost displayed |
| T7 | Existing output without overwrite | Conversion rejected safely |
| T8 | PDF merge | Multiple PDFs become one PDF |
| T9 | PDF split | One PDF per page created |
| T10 | ZIP extraction with `../evil.txt` | Entry rejected |
| T11 | Missing input | Clear conversion error |
| T12 | Media conversion without FFmpeg | Clear dependency error |
| T13 | Weighted graph unit test | Lower-cost route selected |
| T14 | BFS graph unit test | Shortest-edge route selected |

Run automated tests with:
```bash
mvn test
```
