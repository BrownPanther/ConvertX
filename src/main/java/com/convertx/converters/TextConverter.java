package com.convertx.converters;
import com.convertx.core.Converter;import com.convertx.core.PdfTextWriter;import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;import java.nio.charset.StandardCharsets;import java.nio.file.*;import org.apache.pdfbox.pdmodel.PDDocument;
public class TextConverter implements Converter{
 private final FileFormat a,b;public TextConverter(FileFormat a,FileFormat b){this.a=a;this.b=b;}public boolean supports(FileFormat x,FileFormat y){return x==a&&y==b;}
 public void convert(Path in,Path out)throws ConversionException{
  try{
   String t=Files.readString(in,StandardCharsets.UTF_8);
   if(b==FileFormat.HTML){
    String escaped=t.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");
    Files.writeString(out,"<html><body><pre>"+escaped+"</pre></body></html>",StandardCharsets.UTF_8);
   }else if(b==FileFormat.PDF){
    try(PDDocument d=new PDDocument()){PdfTextWriter.write(d,t);d.save(out.toFile());}
   }else throw new ConversionException("Unsupported text conversion.");
  }catch(Exception e){if(e instanceof ConversionException x)throw x;throw new ConversionException("Text conversion failed.",e);}
 }
}