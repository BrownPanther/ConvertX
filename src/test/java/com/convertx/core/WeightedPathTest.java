package com.convertx.core;
import com.convertx.model.FileFormat;import org.junit.jupiter.api.Test;import java.nio.file.Path;import java.util.List;import static org.junit.jupiter.api.Assertions.*;
class WeightedPathTest{
 static class C implements Converter{FileFormat a,b;int cost;C(FileFormat a,FileFormat b,int cost){this.a=a;this.b=b;this.cost=cost;}public boolean supports(FileFormat x,FileFormat y){return x==a&&y==b;}public void convert(Path i,Path o){}public int cost(){return cost;}}
 @Test void weightedRoutingChoosesLowerCost(){ConversionRegistry r=new ConversionRegistry();r.register(new C(FileFormat.TXT,FileFormat.PDF,8),FileFormat.TXT,FileFormat.PDF);r.register(new C(FileFormat.TXT,FileFormat.HTML,2),FileFormat.TXT,FileFormat.HTML);r.register(new C(FileFormat.HTML,FileFormat.PDF,2),FileFormat.HTML,FileFormat.PDF);assertEquals(List.of(FileFormat.TXT,FileFormat.HTML,FileFormat.PDF),r.findBestPath(FileFormat.TXT,FileFormat.PDF));}
}
