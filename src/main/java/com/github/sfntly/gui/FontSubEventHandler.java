/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.github.sfntly.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.sfntly.writer.EOTsWriter;
import com.github.sfntly.writer.SfntWriter;
import com.github.sfntly.writer.TruetypeWriter;
import com.github.sfntly.writer.Woff1Writer;
import com.github.sfntly.writer.Woff2Writer;
import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.SfStringUtils;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.tools.conversion.woff.WoffWriter;
import com.google.typography.font.tools.sfnttool.GlyphCoverage;
import com.google.typography.font.tools.subsetter.HintStripper;
import com.google.typography.font.tools.subsetter.RenumberingSubsetter;
import com.google.typography.font.tools.subsetter.Subsetter;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

/**
 * 裁剪字体的具体类
 *
 * @author ranger
 */
public class FontSubEventHandler implements EventHandler<ActionEvent> {

	private final TextField directory;
	private final TextArea subsetTxtArea;
	private final CheckBox stripHintCb;
	private final ToggleGroup formatGroup;
	private String hasSubstring;
	private final String EXT_TTF = "ttf";
	private final String EXT_WOFF = "woff";
	private final String EXT_EOT = "eot";
	private final String EXT_WOFF2 = "woff2";
	Map<String, SfntWriter> holders = new HashMap<>();
	{
		holders.put(EXT_TTF, new TruetypeWriter());
		holders.put(EXT_WOFF, new Woff1Writer());
		holders.put(EXT_EOT, new EOTsWriter());
		holders.put(EXT_WOFF2, new Woff2Writer());

	}

	public FontSubEventHandler(TextField directory, TextArea subsetTxtArea, CheckBox stripHintCb,
			ToggleGroup formatGroup) {
		this.directory = directory;
		this.subsetTxtArea = subsetTxtArea;
		this.stripHintCb = stripHintCb;
		this.formatGroup = formatGroup;
	}

	Boolean existTTF = Boolean.FALSE;

	private String removeExt(String name) {
		return name.replaceAll(".ttf|.TTF", "");
	}

	@Override
	public void handle(ActionEvent ae) {

		if (directory == null || "".equals(directory.getText())) {
			new AlertBox().display("TTF Directory", "Select Font Directory");
			return;
		}
		hasSubstring = subsetTxtArea.getText();
		final String format = (String) formatGroup.getSelectedToggle().getUserData();
		final boolean stripHinting = stripHintCb.isSelected();

		try {
			final String absolutePath = directory.getText();
			Files.walkFileTree(Paths.get(absolutePath), new SimpleFileVisitor<java.nio.file.Path>() {
				@Override
				public FileVisitResult visitFile(java.nio.file.Path file, BasicFileAttributes attrs)
						throws IOException {
					File ttf = file.toFile();
					String fontName = ttf.getName();
					if ((fontName.endsWith("." + EXT_TTF) || fontName.endsWith("." + EXT_TTF.toUpperCase()))
							&& !fontName.contains("_subset.")) {
						existTTF = Boolean.TRUE;
						String fileName = removeExt(fontName);
						boolean hasText = hasSubstring != null && hasSubstring.trim().length() != 0;
						if (hasText) {
							fileName = fileName + "_subset";
						}
						mkdir(absolutePath, format);
						final String newFileName = absolutePath + File.separatorChar + format + File.separatorChar
								+ fileName;
						try {
							holders.get(format)
									.subset(ttf.getAbsolutePath(), newFileName + "." + format, hasSubstring, stripHinting);
						} catch (Exception e) {
							e.printStackTrace();
							// ignore exception
						}

					}
					return super.visitFile(file, attrs);
				}

				private void mkdir(String parent, String child) {
					File file = new File(parent, child);
					file.mkdir();
				}
			});

			if (existTTF) {
				new AlertBox().display("Convert Finished", "The " + format + " files created");
			} else {
				new AlertBox().display("TTF Not Found ", "Your directory does not contain ttf files!");
			}

		} catch (IOException ex) {
			new AlertBox(300, 200).display("Convert Exception", ex.getMessage());
		}
	}

}
