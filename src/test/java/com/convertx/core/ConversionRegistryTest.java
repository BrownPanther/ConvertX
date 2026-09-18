package com.convertx.core;
import com.convertx.model.FileFormat;import org.junit.jupiter.api.Test;import java.util.List;import static org.junit.jupiter.api.Assertions.*;
class ConversionRegistryTest{
 @Test void bfsFindsShortestRegisteredPath(){ConversionRegistry r=new ConversionRegistry();r.register(new Stub(FileFormat.CSV,FileFormat.JSON),FileFormat.CSV,FileFormat.JSON);r.register(new Stub(FileFormat.JSON,FileFormat.XML),FileFormat.JSON,FileFormat.XML);r.register(new Stub(FileFormat.CSV,FileFormat.XML),FileFormat.CSV,FileFormat.XML);assertEquals(List.of(FileFormat.CSV,FileFormat.XML),r.findPath(FileFormat.CSV,FileFormat.XML));}
 static class Stub implements Converter{FileFormat a,b;Stub(FileFormat a,FileFormat b){this.a=a;this.b=b;}public boolean supports(FileFormat x,FileFormat y){return x==a&&y==b;}public void convert(java.nio.file.Path i,java.nio.file.Path o){}}
}
