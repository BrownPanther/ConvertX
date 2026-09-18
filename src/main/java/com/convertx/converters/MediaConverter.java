package com.convertx.converters;
import com.convertx.core.Converter;import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;import java.nio.file.*;import java.io.*;
public class MediaConverter implements Converter{
 private final FileFormat from,to;public MediaConverter(FileFormat from,FileFormat to){this.from=from;this.to=to;}
 public boolean supports(FileFormat a,FileFormat b){return a==from&&b==to;} public int cost(){return 5;}
 public void convert(Path in,Path out)throws ConversionException{try{Process p=new ProcessBuilder("ffmpeg","-y","-i",in.toString(),out.toString()).redirectErrorStream(true).start();String log=new String(p.getInputStream().readAllBytes());int code=p.waitFor();if(code!=0)throw new ConversionException("FFmpeg failed (exit "+code+"): "+lastLine(log));}catch(IOException e){throw new ConversionException("FFmpeg is not installed or not available on PATH.",e);}catch(InterruptedException e){Thread.currentThread().interrupt();throw new ConversionException("FFmpeg conversion interrupted.",e);}}
 private String lastLine(String s){String[] x=s.strip().split("\\R");return x.length==0?"unknown error":x[x.length-1];}
}
