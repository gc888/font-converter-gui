package com.github.sfntly.writer;

import java.io.FileOutputStream;
import java.io.IOException;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;

public class TruetypeWriter extends AbstractSfntWriter {

	@Override
	protected void write(Font font, String output) {
		try {
			FontFactory.getInstance().serializeFont(font, new FileOutputStream(output));
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
