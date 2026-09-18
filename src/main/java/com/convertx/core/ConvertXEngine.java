package com.convertx.core;
import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;import java.nio.file.*;import java.util.*;
public class ConvertXEngine{
 private final ConversionRegistry registry;private final ConversionConfig config;
 public ConvertXEngine(ConversionRegistry registry){this(registry,ConversionConfig.defaults());}
 public ConvertXEngine(ConversionRegistry registry,ConversionConfig config){this.registry=registry;this.config=config;}
 public List<FileFormat> path(FileFormat a,FileFormat b){return registry.findPath(a,b);} public List<FileFormat> bestPath(FileFormat a,FileFormat b){return registry.findBestPath(a,b);}
 public void convert(Path in,Path out)throws ConversionException{convert(in,out,registry.findBestPath(FileFormat.fromFileName(in.toString()),FileFormat.fromFileName(out.toString())),null);}
 public void convert(Path in,Path out,ProgressListener listener)throws ConversionException{FileFormat a=FileFormat.fromFileName(in.toString()),b=FileFormat.fromFileName(out.toString());convert(in,out,registry.findBestPath(a,b),listener);}
 private void convert(Path in,Path out,List<FileFormat> p,ProgressListener listener)throws ConversionException{
  if(!Files.exists(in))throw new ConversionException("Input file does not exist.");if(p.isEmpty())throw new ConversionException("No conversion path found.");
  if(Files.exists(out)&&!config.overwrite())throw new ConversionException("Output already exists. Enable overwrite in config or choose another output.");
  try{Files.createDirectories(config.tempDirectory());Path cur=in;int total=p.size()-1;
   for(int i=0;i<total;i++){FileFormat x=p.get(i),y=p.get(i+1);Converter c=registry.direct(x,y).orElseThrow(()->new ConversionException("Converter missing: "+x+" -> "+y));boolean last=i==total-1;Path next=last?out:Files.createTempFile(config.tempDirectory(),"convertx-","."+y.name().toLowerCase(Locale.ROOT));
    if(listener!=null)listener.onProgress(x+" -> "+y,i,total);c.convert(cur,next);if(!cur.equals(in)&&!config.keepIntermediates())Files.deleteIfExists(cur);cur=next;if(listener!=null)listener.onProgress(x+" -> "+y,i+1,total);}
  }catch(ConversionException e){throw e;}catch(Exception e){throw new ConversionException("Conversion failed.",e);}
 }
}
