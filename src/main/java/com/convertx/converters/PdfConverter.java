package com.convertx.converters;
import com.convertx.core.Converter;import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;import org.apache.pdfbox.Loader;import org.apache.pdfbox.pdmodel.PDDocument;import org.apache.pdfbox.text.PDFTextStripper;import java.nio.charset.StandardCharsets;import java.nio.file.*;
public class PdfConverter implements Converter{
 public boolean supports(FileFormat a,FileFormat b){return a==FileFormat.PDF&&b==FileFormat.TXT;}
 public void convert(Path in,Path out)throws ConversionException{try(PDDocument d=Loader.loadPDF(in.toFile())){Files.writeString(out,new PDFTextStripper().getText(d),StandardCharsets.UTF_8);}catch(Exception e){throw new ConversionException("PDF conversion failed.",e);}}
}