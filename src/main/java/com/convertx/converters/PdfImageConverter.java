package com.convertx.converters;
import com.convertx.core.Converter;import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;import org.apache.pdfbox.Loader;import org.apache.pdfbox.rendering.*;import org.apache.pdfbox.pdmodel.*;import org.apache.pdfbox.pdmodel.common.PDRectangle;import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import java.awt.image.BufferedImage;import java.nio.file.*;
public class PdfImageConverter implements Converter{
 private final FileFormat a,b;public PdfImageConverter(FileFormat a,FileFormat b){this.a=a;this.b=b;}public boolean supports(FileFormat x,FileFormat y){return x==a&&y==b;}
 public void convert(Path in,Path out)throws ConversionException{try{if(a==FileFormat.PDF){try(PDDocument d=Loader.loadPDF(in.toFile())){if(d.getNumberOfPages()==0)throw new ConversionException("PDF has no pages.");BufferedImage img=new PDFRenderer(d).renderImageWithDPI(0,150);String fmt=b==FileFormat.JPG?"jpg":"png";javax.imageio.ImageIO.write(img,fmt,out.toFile());}}else{BufferedImage img=javax.imageio.ImageIO.read(in.toFile());if(img==null)throw new ConversionException("Unsupported image format or unreadable image.");try(PDDocument d=new PDDocument()){PDPage p=new PDPage(new PDRectangle(img.getWidth(),img.getHeight()));d.addPage(p);p.getResources();var pd=LosslessFactory.createFromImage(d,img);try(var c=new org.apache.pdfbox.pdmodel.PDPageContentStream(d,p)){c.drawImage(pd,0,0,img.getWidth(),img.getHeight());}d.save(out.toFile());}}}catch(Exception e){if(e instanceof ConversionException x)throw x;throw new ConversionException("PDF/image conversion failed.",e);}}

 public int cost(){return 3;}
}
