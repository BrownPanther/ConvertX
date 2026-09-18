package com.convertx.converters;
import com.convertx.core.Converter;import com.convertx.core.PdfTextWriter;import com.convertx.exceptions.ConversionException;import com.convertx.model.FileFormat;
import org.apache.poi.xwpf.usermodel.*;import org.apache.pdfbox.pdmodel.PDDocument;
import java.nio.charset.StandardCharsets;import java.nio.file.*;import java.util.*;
public class DocumentConverter implements Converter{
 private final FileFormat a,b; public DocumentConverter(FileFormat a,FileFormat b){this.a=a;this.b=b;} public boolean supports(FileFormat x,FileFormat y){return x==a&&y==b;}
 public void convert(Path in,Path out)throws ConversionException{try{
  if(a==FileFormat.DOCX&&b==FileFormat.TXT)Files.writeString(out,docxText(in),StandardCharsets.UTF_8);
  else if(a==FileFormat.DOCX&&b==FileFormat.PDF)textToPdf(docxText(in),out);
  else if(a==FileFormat.HTML&&b==FileFormat.TXT)Files.writeString(out,htmlToText(Files.readString(in)),StandardCharsets.UTF_8);
  else if(a==FileFormat.MD&&b==FileFormat.HTML)Files.writeString(out,mdToHtml(Files.readString(in)),StandardCharsets.UTF_8);
  else if(a==FileFormat.HTML&&b==FileFormat.MD)Files.writeString(out,htmlToMd(Files.readString(in)),StandardCharsets.UTF_8);
  else throw new ConversionException("Unsupported document conversion.");
 }catch(Exception e){if(e instanceof ConversionException x)throw x;throw new ConversionException("Document conversion failed.",e);}}
 private String docxText(Path p)throws Exception{
  StringBuilder s=new StringBuilder();
  try(XWPFDocument d=new XWPFDocument(Files.newInputStream(p))){
   for(XWPFParagraph q:d.getParagraphs()){s.append(q.getText()).append(System.lineSeparator());}
   for(XWPFTable table:d.getTables()){
    for(XWPFTableRow row:table.getRows()){
     for(int i=0;i<row.getTableCells().size();i++){
      if(i>0)s.append(" | ");
      s.append(row.getCell(i).getText().replaceAll("\\R"," "));
     }
     s.append(System.lineSeparator());
    }
   }
  }
  return s.toString();
 }
 private void textToPdf(String t,Path o)throws Exception{
  try(PDDocument d=new PDDocument()){PdfTextWriter.write(d,t);d.save(o.toFile());}
 }
 private String htmlToText(String h){return h.replaceAll("(?is)<script.*?</script>|<style.*?</style>","").replaceAll("(?i)<br\\s*/?>","\\n").replaceAll("(?i)</p>|</div>|</h[1-6]>","\\n").replaceAll("<[^>]+>","").replace("&nbsp;"," ").replace("&amp;","&").replace("&lt;","<").replace("&gt;",">");}
 private String esc(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;");}
 private String mdToHtml(String md){StringBuilder h=new StringBuilder("<!doctype html><html><body>\\n");for(String l:md.split("\\R",-1)){if(l.startsWith("### "))h.append("<h3>").append(esc(l.substring(4))).append("</h3>\\n");else if(l.startsWith("## "))h.append("<h2>").append(esc(l.substring(3))).append("</h2>\\n");else if(l.startsWith("# "))h.append("<h1>").append(esc(l.substring(2))).append("</h1>\\n");else if(l.startsWith("- "))h.append("<li>").append(esc(l.substring(2))).append("</li>\\n");else if(l.isBlank())h.append("<br>\\n");else h.append("<p>").append(esc(l)).append("</p>\\n");}return h.append("</body></html>\\n").toString();}
 private String htmlToMd(String h){String x=h.replaceAll("(?is)<h1>(.*?)</h1>","# $1\\n").replaceAll("(?is)<h2>(.*?)</h2>","## $1\\n").replaceAll("(?is)<h3>(.*?)</h3>","### $1\\n").replaceAll("(?is)<li>(.*?)</li>","- $1\\n").replaceAll("(?is)</p>","\\n").replaceAll("<[^>]+>","");return x.replace("&amp;","&").replace("&lt;","<").replace("&gt;",">").trim()+"\\n";}

 public int cost(){return 2;}
}
