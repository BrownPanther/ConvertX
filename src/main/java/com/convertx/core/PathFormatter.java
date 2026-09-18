package com.convertx.core;

import com.convertx.model.FileFormat;
import java.util.List;

public final class PathFormatter {
    private PathFormatter() {}
    public static String format(List<FileFormat> path) {
        return path.isEmpty() ? "No path" : String.join(" -> ", path.stream().map(Enum::name).toList());
    }
}
