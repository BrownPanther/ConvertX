package com.convertx.core;
import com.convertx.model.ConversionResult;import com.fasterxml.jackson.core.type.TypeReference;import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.*;import java.util.*;
public class HistoryManager{
 private final List<ConversionResult> history=new ArrayList<>(); private final Path file; private final ObjectMapper mapper=new ObjectMapper().findAndRegisterModules();
 public HistoryManager(){this(Path.of(System.getProperty("user.home"),".convertx","history.json"));}
 public HistoryManager(Path file){this.file=file;load();}
 private synchronized void load(){try{if(Files.exists(file))history.addAll(mapper.readValue(file.toFile(),new TypeReference<List<ConversionResult>>(){}));}catch(Exception ignored){}}
 public synchronized void add(ConversionResult r){history.add(r);try{Files.createDirectories(file.getParent());mapper.writerWithDefaultPrettyPrinter().writeValue(file.toFile(),history);}catch(Exception ignored){}}
 public synchronized void print(){if(history.isEmpty()){System.out.println("No conversions recorded.");return;}System.out.println("\\n--- Conversion History ---");int start=Math.max(0,history.size()-20);for(int i=start;i<history.size();i++){ConversionResult r=history.get(i);System.out.printf("%s | %s -> %s | %s | %d ms%n",r.timestamp(),r.input(),r.output(),r.success()?"SUCCESS":"FAILED",r.durationMs());}}
 public synchronized void clear(){history.clear();try{Files.deleteIfExists(file);}catch(Exception ignored){}}
}
