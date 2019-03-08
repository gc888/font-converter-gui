package com.github.sfntly.writer;

import java.io.FileOutputStream;
import java.io.IOException;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.tools.conversion.eot.EOTWriter;

public class EOTsWriter extends AbstractSfntWriter {

	@Override
	protected void write(Font font, String output) {
		WritableFontData eotData;
		try {
			eotData = new EOTWriter(Boolean.TRUE).convert(font);
			eotData.copyTo(new FileOutputStream(output));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
