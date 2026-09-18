# ConvertX Stage 5 Architecture

```text
                    +-------------------+
                    |      Main CLI     |
                    +---------+---------+
                              |
                    +---------v---------+
                    |  ConvertXEngine   |
                    +---------+---------+
                              |
              +---------------+----------------+
              |                                |
       +------v------+                  +------v------+
       | Conversion  |                  | Configuration|
       |  Registry   |                  |   Manager    |
       +------+------+                  +-------------+
              |
       +------+-------------------------------+
       |                                      |
 +-----v-----+                         +------v------+
 | BFS route |                         | Dijkstra    |
 | fewest    |                         | weighted    |
 | edges     |                         | route       |
 +-----------+                         +-------------+
              |
       +------v------------------+
       | Converter implementations|
       +---+----+----+----+------+
           |    |    |    |
         Data Docs Images Media/Archives
           |    |    |    |
           +----+----+----+----> Output file

Special operations: PDF merge/split, ZIP extraction
```

## Stage 5 routing
Each registered conversion is a directed graph edge. Every converter exposes a non-negative integer `cost()` with default value 1.

- BFS: minimizes number of edges.
- Dijkstra: minimizes total converter cost.
- Actual file conversion uses the weighted route.
- Intermediate files are placed in the configured temporary directory and removed unless `keepIntermediates=true`.

## Security consideration
ZIP extraction normalizes every output path and rejects entries that escape the selected destination directory, preventing basic Zip Slip/path traversal attacks.
