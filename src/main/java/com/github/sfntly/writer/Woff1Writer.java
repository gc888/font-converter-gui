package com.github.sfntly.writer;

import java.io.FileOutputStream;
import java.io.IOException;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.tools.conversion.woff.WoffWriter;

public class Woff1Writer extends AbstractSfntWriter {

	final WoffWriter writter = new WoffWriter();

	@Override
	protected void write(Font font, String output) {
		WritableFontData data = writter.convert(font);
		try {
			data.copyTo(new FileOutputStream(output));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
