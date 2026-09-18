package com.convertx.cli;
import com.convertx.converters.ArchiveConverter;
import com.convertx.converters.DataConverter;
import com.convertx.converters.DocumentConverter;
import com.convertx.converters.ImageConverter;
import com.convertx.converters.MediaConverter;
import com.convertx.converters.PdfConverter;
import com.convertx.converters.PdfImageConverter;
import com.convertx.converters.TextConverter;
import com.convertx.core.BatchConverter;
import com.convertx.core.ConfigManager;
import com.convertx.core.ConversionConfig;
import com.convertx.core.ConversionRegistry;
import com.convertx.core.ConvertXEngine;
import com.convertx.core.FormatCatalog;
import com.convertx.core.HistoryManager;
import com.convertx.core.PathFormatter;
import com.convertx.core.PdfTools;
import com.convertx.model.ConversionResult;
import com.convertx.model.FileFormat;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;
public class Main{
 private static final HistoryManager HISTORY=new HistoryManager();private static final ConfigManager CONFIG=new ConfigManager();
 public static void main(String[] args){ConversionRegistry r=buildRegistry();ConvertXEngine e=new ConvertXEngine(r,CONFIG.load());BatchConverter b=new BatchConverter(e);
  if(args.length>=3&&args[0].equalsIgnoreCase("convert")){convert(e,args[1],args[2]);return;}if(args.length>=3&&args[0].equalsIgnoreCase("path")){showPath(e,args[1],args[2],false);return;}if(args.length>=3&&args[0].equalsIgnoreCase("bestpath")){showPath(e,args[1],args[2],true);return;}if(args.length>=4&&args[0].equalsIgnoreCase("batch")){batch(b,args[1],args[2],args[3]);return;}if(args.length>=3&&args[0].equalsIgnoreCase("pdfmerge")){pdfMerge(args);return;}if(args.length>=3&&args[0].equalsIgnoreCase("pdfsplit")){pdfSplit(args[1],args[2]);return;}if(args.length>=3&&args[0].equalsIgnoreCase("zipextract")){zipExtract(args[1],args[2]);return;}if(args.length>=1&&args[0].equalsIgnoreCase("formats")){FormatCatalog.print();return;}if(args.length>=1&&args[0].equalsIgnoreCase("history")){HISTORY.print();return;}if(args.length>=1&&args[0].equalsIgnoreCase("config")){config();return;}menu(e,b);}
 static ConversionRegistry buildRegistry(){ConversionRegistry r=new ConversionRegistry();FileFormat[] imgs={FileFormat.JPG,FileFormat.PNG,FileFormat.BMP,FileFormat.GIF};for(FileFormat a:imgs)for(FileFormat c:imgs)if(a!=c)r.register(new ImageConverter(a,c),a,c);
  r.register(new TextConverter(FileFormat.TXT,FileFormat.PDF),FileFormat.TXT,FileFormat.PDF);r.register(new TextConverter(FileFormat.TXT,FileFormat.HTML),FileFormat.TXT,FileFormat.HTML);r.register(new PdfConverter(),FileFormat.PDF,FileFormat.TXT);
  r.register(new DocumentConverter(FileFormat.DOCX,FileFormat.TXT),FileFormat.DOCX,FileFormat.TXT);r.register(new DocumentConverter(FileFormat.DOCX,FileFormat.PDF),FileFormat.DOCX,FileFormat.PDF);r.register(new DocumentConverter(FileFormat.HTML,FileFormat.TXT),FileFormat.HTML,FileFormat.TXT);r.register(new DocumentConverter(FileFormat.MD,FileFormat.HTML),FileFormat.MD,FileFormat.HTML);r.register(new DocumentConverter(FileFormat.HTML,FileFormat.MD),FileFormat.HTML,FileFormat.MD);
  for(FileFormat[] p:new FileFormat[][]{{FileFormat.JPG,FileFormat.PDF},{FileFormat.PNG,FileFormat.PDF},{FileFormat.BMP,FileFormat.PDF},{FileFormat.GIF,FileFormat.PDF},{FileFormat.PDF,FileFormat.JPG},{FileFormat.PDF,FileFormat.PNG}})r.register(new PdfImageConverter(p[0],p[1]),p[0],p[1]);
  data(r,FileFormat.CSV,FileFormat.JSON);data(r,FileFormat.JSON,FileFormat.CSV);data(r,FileFormat.CSV,FileFormat.XML);data(r,FileFormat.JSON,FileFormat.XML);data(r,FileFormat.XML,FileFormat.JSON);data(r,FileFormat.CSV,FileFormat.XLSX);data(r,FileFormat.XLSX,FileFormat.CSV);
  FileFormat[] aud={FileFormat.MP3,FileFormat.WAV,FileFormat.OGG};for(FileFormat a:aud)for(FileFormat c:aud)if(a!=c)r.register(new MediaConverter(a,c),a,c);FileFormat[] vid={FileFormat.MP4,FileFormat.MKV,FileFormat.AVI,FileFormat.MOV};for(FileFormat a:vid)for(FileFormat c:vid)if(a!=c)r.register(new MediaConverter(a,c),a,c);
  r.register(new ArchiveConverter(FileFormat.ZIP,FileFormat.TAR),FileFormat.ZIP,FileFormat.TAR);r.register(new ArchiveConverter(FileFormat.TAR,FileFormat.ZIP),FileFormat.TAR,FileFormat.ZIP);r.register(new ArchiveConverter(FileFormat.GZ,FileFormat.TXT),FileFormat.GZ,FileFormat.TXT);return r;}
 static void data(ConversionRegistry r,FileFormat a,FileFormat b){r.register(new DataConverter(a,b),a,b);}
 static void convert(ConvertXEngine e,String in,String out){long t=System.currentTimeMillis();try{FileFormat af=FileFormat.fromFileName(in),bf=FileFormat.fromFileName(out);List<FileFormat>p=e.bestPath(af,bf);if(p.isEmpty()){System.out.println("ERROR: No conversion path found: "+af+" -> "+bf);return;}System.out.println("Best path: "+PathFormatter.format(p));e.convert(Path.of(in),Path.of(out),(stage,done,total)->System.out.println("  ["+done+"/"+total+"] "+stage));long ms=System.currentTimeMillis()-t;HISTORY.add(new ConversionResult(in,out,p.toString(),true,ms,LocalDateTime.now(),"Success"));System.out.println("Complete in "+ms+" ms.");}catch(Exception x){HISTORY.add(new ConversionResult(in,out,"",false,System.currentTimeMillis()-t,LocalDateTime.now(),x.getMessage()));System.out.println("ERROR: "+x.getMessage());}}
 static void showPath(ConvertXEngine e,String a,String b,boolean best){List<FileFormat>p=best?e.bestPath(FileFormat.fromExtension(a),FileFormat.fromExtension(b)):e.path(FileFormat.fromExtension(a),FileFormat.fromExtension(b));System.out.println((best?"Best path: ":"BFS path: ")+PathFormatter.format(p)+(best&&!p.isEmpty()?" (cost="+buildRegistry().pathCost(p)+")":""));}
 static void batch(BatchConverter b,String dir,String from,String to){try{b.convertDirectory(Path.of(dir),FileFormat.fromExtension(from),FileFormat.fromExtension(to),Path.of(dir,"converted"));}catch(Exception e){System.out.println("ERROR: "+e.getMessage());}}
 static void pdfMerge(String[] args){try{List<Path> in=new ArrayList<>();for(int i=1;i<args.length-1;i++)in.add(Path.of(args[i]));PdfTools.merge(in,Path.of(args[args.length-1]));System.out.println("PDFs merged.");}catch(Exception e){System.out.println("ERROR: "+e.getMessage());}}
 static void pdfSplit(String input,String dir){try{PdfTools.split(Path.of(input),Path.of(dir));System.out.println("PDF split into pages: "+dir);}catch(Exception e){System.out.println("ERROR: "+e.getMessage());}}
 static void zipExtract(String input,String dir){try{ArchiveConverter.extractZip(Path.of(input),Path.of(dir));System.out.println("ZIP extracted.");}catch(Exception e){System.out.println("ERROR: "+e.getMessage());}}
 static void config(){ConversionConfig c=CONFIG.load();System.out.println("Config: overwrite="+c.overwrite()+", keepIntermediates="+c.keepIntermediates()+", tempDirectory="+c.tempDirectory());System.out.println("Edit ~/.convertx/config.properties to change settings.");}
 static void menu(ConvertXEngine e,BatchConverter b){Scanner s=new Scanner(System.in);System.out.println("\n=== CONVERTX JAVA CLI v5 ===");while(true){System.out.println("\n1. Convert File\n2. Find BFS Path\n3. Find Best Path\n4. Batch Convert\n5. Supported Formats\n6. History\n7. Clear History\n8. PDF Merge\n9. PDF Split\n10. Config\n11. Exit");System.out.print("Choice: ");String c=s.nextLine();try{switch(c){case "1"->{System.out.print("Input: ");String in=s.nextLine();System.out.print("Output: ");String out=s.nextLine();convert(e,in,out);}case "2"->{System.out.print("From: ");String a=s.nextLine();System.out.print("To: ");String z=s.nextLine();showPath(e,a,z,false);}case "3"->{System.out.print("From: ");String a=s.nextLine();System.out.print("To: ");String z=s.nextLine();showPath(e,a,z,true);}case "4"->{System.out.print("Directory: ");String d=s.nextLine();System.out.print("From: ");String a=s.nextLine();System.out.print("To: ");String z=s.nextLine();batch(b,d,a,z);}case "5"->FormatCatalog.print();case "6"->HISTORY.print();case "7"->{HISTORY.clear();System.out.println("History cleared.");}case "8"->{System.out.print("Input PDFs (space-separated): ");String[] xs=s.nextLine().trim().split("\\s+");System.out.print("Output PDF: ");String out=s.nextLine();String[] argv=new String[xs.length+2];argv[0]="pdfmerge";System.arraycopy(xs,0,argv,1,xs.length);argv[argv.length-1]=out;pdfMerge(argv);}case "9"->{System.out.print("Input PDF: ");String in=s.nextLine();System.out.print("Output directory: ");String d=s.nextLine();pdfSplit(in,d);}case "10"->config();case "11"->{return;}default->System.out.println("Invalid choice.");}}catch(Exception x){System.out.println("ERROR: "+x.getMessage());}}}
}
