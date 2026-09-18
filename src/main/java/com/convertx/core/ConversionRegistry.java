package com.convertx.core;

import com.convertx.model.FileFormat;
import java.util.*;

/** Registry of conversion edges plus BFS and weighted shortest-path routing. */
public class ConversionRegistry {
    private final Map<FileFormat, List<FileFormat>> graph = new EnumMap<>(FileFormat.class);
    private final List<Converter> converters = new ArrayList<>();

    public void register(Converter converter, FileFormat from, FileFormat to) {
        if (direct(from, to).isEmpty()) {
            converters.add(converter);
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
        }
    }

    public Optional<Converter> direct(FileFormat from, FileFormat to) {
        return converters.stream().filter(c -> c.supports(from, to)).findFirst();
    }

    public List<FileFormat> findPath(FileFormat from, FileFormat to) {
        return findBfsPath(from, to);
    }

    public List<FileFormat> findBfsPath(FileFormat from, FileFormat to) {
        if (from == to) return List.of(from);
        Queue<FileFormat> queue = new ArrayDeque<>();
        Map<FileFormat, FileFormat> previous = new EnumMap<>(FileFormat.class);
        Set<FileFormat> visited = EnumSet.noneOf(FileFormat.class);
        queue.add(from); visited.add(from);
        while (!queue.isEmpty()) {
            FileFormat current = queue.remove();
            for (FileFormat next : graph.getOrDefault(current, List.of())) {
                if (visited.add(next)) {
                    previous.put(next, current);
                    if (next == to) return reconstruct(from, to, previous);
                    queue.add(next);
                }
            }
        }
        return List.of();
    }

    /** Dijkstra routing: lower converter cost is preferred when routes have different costs. */
    public List<FileFormat> findBestPath(FileFormat from, FileFormat to) {
        if (from == to) return List.of(from);
        Map<FileFormat, Integer> distance = new EnumMap<>(FileFormat.class);
        Map<FileFormat, FileFormat> previous = new EnumMap<>(FileFormat.class);
        Set<FileFormat> done = EnumSet.noneOf(FileFormat.class);
        for (FileFormat f : FileFormat.values()) distance.put(f, Integer.MAX_VALUE);
        distance.put(from, 0);
        PriorityQueue<FileFormat> pq = new PriorityQueue<>(Comparator.comparingInt(distance::get));
        pq.add(from);
        while (!pq.isEmpty()) {
            FileFormat current = pq.remove();
            if (!done.add(current)) continue;
            if (current == to) return reconstruct(from, to, previous);
            for (FileFormat next : graph.getOrDefault(current, List.of())) {
                Optional<Converter> c = direct(current, next);
                if (c.isEmpty()) continue;
                int edge = Math.max(1, c.get().cost());
                long candidate = (long) distance.get(current) + edge;
                if (candidate < distance.get(next)) {
                    distance.put(next, (int)Math.min(Integer.MAX_VALUE, candidate));
                    previous.put(next, current);
                    pq.add(next);
                }
            }
        }
        return List.of();
    }

    public int pathCost(List<FileFormat> path) {
        int total = 0;
        for (int i = 0; i + 1 < path.size(); i++) {
            total += direct(path.get(i), path.get(i + 1)).map(c -> Math.max(1, c.cost())).orElse(0);
        }
        return total;
    }

    public Set<FileFormat> supportedFormats() {
        Set<FileFormat> result = EnumSet.noneOf(FileFormat.class);
        for (Converter c : converters) {
            // graph keys/values capture every registered endpoint
        }
        result.addAll(graph.keySet());
        for (List<FileFormat> values : graph.values()) result.addAll(values);
        return result;
    }

    private List<FileFormat> reconstruct(FileFormat from, FileFormat to, Map<FileFormat, FileFormat> previous) {
        LinkedList<FileFormat> path = new LinkedList<>();
        FileFormat current = to;
        while (current != null) {
            path.addFirst(current);
            if (current == from) break;
            current = previous.get(current);
        }
        return path.getFirst() == from ? path : List.of();
    }
}
