package com.github.sfntly.gui;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.table.core.CMap;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.sfntly.table.extra.VerticalHeaderTable;
import com.google.typography.font.sfntly.table.extra.VerticalMetricsTable;
import com.google.typography.font.sfntly.table.truetype.GlyphTable;

public class Test {
	public static void main(String[] args) throws FileNotFoundException, IOException {
		final FontFactory fontFactory = FontFactory.getInstance();

		Font[]  srcFontarray = fontFactory
				.loadFonts(new FileInputStream("/Users/jijun/Devkit/fonts/a3bc1b037bb15b59fce5a85cda104fe4.ttf"));
	    Font font = srcFontarray[0];
	    VerticalHeaderTable vhea = font.getTable(Tag.vhea);
	    // use the bigger cmap table if available
	    System.out.println("ascender: " + vhea.ascender());
	    System.out.println("descender: " + vhea.descender());
	    System.out.println("lineGap: " + vhea.lineGap());
	    System.out.println("advanceHeightMax: " + vhea.advanceHeightMax());
	    System.out.println("minTopSideBearing: " + vhea.minTopSideBearing());
	    System.out.println("minBottomSideBearing: " + vhea.minBottomSideBearing());
	    System.out.println("yMaxExtent: " + vhea.yMaxExtent());
	    System.out.println("numberOfVMetrics: " + vhea.numberOfVMetrics());
	    System.out.println("metricDataFormat: " + vhea.metricDataFormat());

	    
	    VerticalMetricsTable vmtx = font.getTable(Tag.vmtx);

	    System.out.println("vMetricTSB :" + vmtx.numberOfVMetrics());
	    System.out.println(vmtx.numberOfTSBs());
	    
	    
	    GlyphTable glyf = font.getTable(Tag.glyf);
	    // use the bigger cmap table if available
	    System.out.println(glyf.dataLength());
	    
	   CMapTable cMapTable = font.getTable(Tag.cmap);
	   
	   CMap oneCmap =  getCMapFormat(font);
	   for(Integer unicode : oneCmap) {
	            int glyph = oneCmap.glyphId(unicode);
	            if(unicode==20225) {
	            	System.out.println("glyph : " +glyph);
	            }
	   }
	   
	}
	
	  private static CMap getCMapFormat(Font font) {
		    CMapTable cmapTable = font.getTable(Tag.cmap);
		    
		    for (CMap cmap : cmapTable) {
		      if (cmap.format() == CMap.CMapFormat.Format12.value()) {
		        return  cmap;
		      }
		    }
		    for (CMap cmap : cmapTable) {
		        if (cmap.format() == CMap.CMapFormat.Format4.value()) {
		          return  cmap;
		        }
		      }
		    return null;
		  }

}
