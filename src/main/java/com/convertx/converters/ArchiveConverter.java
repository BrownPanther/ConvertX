package com.convertx.converters;
import com.convertx.core.Converter;import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;import org.apache.commons.compress.archivers.tar.*;import org.apache.commons.compress.compressors.gzip.*;
import java.io.*;import java.nio.file.*;import java.util.zip.*;
public class ArchiveConverter implements Converter{
 private final FileFormat a,b;public ArchiveConverter(FileFormat a,FileFormat b){this.a=a;this.b=b;}public boolean supports(FileFormat x,FileFormat y){return x==a&&y==b;}
 public void convert(Path in,Path out)throws ConversionException{try{
  if(a==FileFormat.ZIP&&b==FileFormat.TAR){try(ZipInputStream z=new ZipInputStream(Files.newInputStream(in));TarArchiveOutputStream t=new TarArchiveOutputStream(Files.newOutputStream(out))){ZipEntry e;while((e=z.getNextEntry())!=null){if(e.isDirectory())continue;TarArchiveEntry te=new TarArchiveEntry(e.getName());te.setSize(e.getSize()<0?0:e.getSize());t.putArchiveEntry(te);z.transferTo(t);t.closeArchiveEntry();}}}
  else if(a==FileFormat.TAR&&b==FileFormat.ZIP){try(TarArchiveInputStream t=new TarArchiveInputStream(Files.newInputStream(in));ZipOutputStream z=new ZipOutputStream(Files.newOutputStream(out))){TarArchiveEntry e;while((e=t.getNextTarEntry())!=null){if(e.isDirectory())continue;z.putNextEntry(new ZipEntry(e.getName()));t.transferTo(z);z.closeEntry();}}}
  else if(a==FileFormat.GZ&&b==FileFormat.TXT){try(GzipCompressorInputStream g=new GzipCompressorInputStream(Files.newInputStream(in));OutputStream o=Files.newOutputStream(out)){g.transferTo(o);}}
  else throw new ConversionException("Unsupported archive conversion. ZIP/TAR and GZ/TXT are supported.");
 }catch(Exception e){if(e instanceof ConversionException x)throw x;throw new ConversionException("Archive conversion failed.",e);}}
 public static void extractZip(Path input,Path outputDirectory)throws ConversionException{
  try{Files.createDirectories(outputDirectory);Path root=outputDirectory.toAbsolutePath().normalize();try(ZipInputStream z=new ZipInputStream(Files.newInputStream(input))){ZipEntry e;while((e=z.getNextEntry())!=null){Path target=root.resolve(e.getName()).normalize();if(!target.startsWith(root))throw new ConversionException("Unsafe ZIP entry: "+e.getName());if(e.isDirectory())Files.createDirectories(target);else{Files.createDirectories(target.getParent());try(OutputStream o=Files.newOutputStream(target)){z.transferTo(o);}}}}
  }catch(Exception e){if(e instanceof ConversionException x)throw x;throw new ConversionException("ZIP extraction failed.",e);}
 }

}
