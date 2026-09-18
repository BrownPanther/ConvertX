package com.convertx.core;
import com.convertx.exceptions.ConversionException;
import com.convertx.model.FileFormat;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
public class BatchConverter {
 private final ConvertXEngine engine;
 public BatchConverter(ConvertXEngine e){engine=e;}
 public void convertDirectory(Path dir,FileFormat from,FileFormat to,Path outDir)throws Exception{
  if(!Files.isDirectory(dir))throw new IllegalArgumentException("Not a directory: "+dir);
  Files.createDirectories(outDir);
  List<Path> files;
  try(var s=Files.list(dir)){files=s.filter(Files::isRegularFile).filter(p->FileFormat.fromFileName(p.getFileName().toString())==from).toList();}
  if(files.isEmpty()){System.out.println("No "+from+" files found.");return;}
  int threads=Math.max(2,Math.min(Runtime.getRuntime().availableProcessors(),8));
  ExecutorService pool=Executors.newFixedThreadPool(threads);
  List<Future<String>> jobs=new ArrayList<>();
  for(Path in:files)jobs.add(pool.submit(()->{
   String n=in.getFileName().toString();int dot=n.lastIndexOf('.');if(dot>0)n=n.substring(0,dot);
   Path out=outDir.resolve(n+"."+to.name().toLowerCase());
   try{engine.convert(in,out);return "OK: "+in.getFileName()+" -> "+out.getFileName();}
   catch(ConversionException e){return "ERROR: "+in.getFileName()+" : "+e.getMessage();}
  }));
  pool.shutdown();
  int done=0;for(Future<String> f:jobs){try{System.out.println(f.get());}catch(ExecutionException e){System.out.println("ERROR: "+e.getCause());}System.out.println("Progress: "+(++done)+"/"+files.size());}
  if(!pool.awaitTermination(1,TimeUnit.MINUTES))pool.shutdownNow();
 }
}
