package com.convertx.converters;
import com.convertx.core.Converter;import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;import javax.imageio.ImageIO;import java.awt.image.BufferedImage;import java.nio.file.*;
public class ImageConverter implements Converter{
 private final FileFormat a,b;public ImageConverter(FileFormat a,FileFormat b){this.a=a;this.b=b;}public boolean supports(FileFormat x,FileFormat y){return x==a&&y==b;}
 public void convert(Path in,Path out)throws ConversionException{try{BufferedImage im=ImageIO.read(in.toFile());if(im==null)throw new ConversionException("Unsupported or corrupt image.");String f=b==FileFormat.JPEG?"jpg":b.name().toLowerCase();if(!ImageIO.write(im,f,out.toFile()))throw new ConversionException("ImageIO cannot write "+b);}catch(Exception e){if(e instanceof ConversionException x)throw x;throw new ConversionException("Image conversion failed.",e);}}
}