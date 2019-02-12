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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.typography.font.sfntly.Font;
import com.google.typography.font.sfntly.FontFactory;
import com.google.typography.font.sfntly.Tag;
import com.google.typography.font.sfntly.data.SfStringUtils;
import com.google.typography.font.sfntly.data.WritableFontData;
import com.google.typography.font.sfntly.table.core.CMapTable;
import com.google.typography.font.tools.conversion.eot.EOTWriter;
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
						final FontFactory fontFactory = FontFactory.getInstance();
						Font newFont = fontFactory.loadFonts(new FileInputStream(ttf))[0];
						String fileName = removeExt(fontName);
						boolean hasText = hasSubstring != null && hasSubstring.trim().length() != 0;
						if (hasText) {
							fileName = fileName + "_subset";
						}
						mkdir(absolutePath, format);
						final String newFileName = absolutePath + File.separatorChar + format + File.separatorChar
								+ fileName;
						try {
							final Font subsetFont = subsetFont(fontFactory, newFont, hasSubstring, stripHinting);
							if (EXT_WOFF.equals(format)) {
								WoffWriter writter = new WoffWriter();
								WritableFontData data = writter.convert(subsetFont);
								data.copyTo(new FileOutputStream(newFileName + "." + EXT_WOFF));
							} else if (EXT_TTF.equals(format)) {
								fontFactory
										.serializeFont(subsetFont, new FileOutputStream(newFileName + "." + EXT_TTF));
							} else if (EXT_EOT.equals(format)) {
								WritableFontData eotData = new EOTWriter(Boolean.TRUE).convert(newFont);
								eotData.copyTo(new FileOutputStream(newFileName + "." + EXT_EOT));
							}
						} catch (Exception e) {
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

	private static final Set<Integer> REMOVETABLES = new HashSet<Integer>();
	private static final Set<Integer> HINTSREMOVETABLES = new HashSet<Integer>();

	private static final String basicChars = "\"!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}";
	static {
		REMOVETABLES.add(Tag.GDEF);
		REMOVETABLES.add(Tag.GPOS);
		REMOVETABLES.add(Tag.GSUB);
		REMOVETABLES.add(Tag.kern);
		REMOVETABLES.add(Tag.hdmx);
		REMOVETABLES.add(Tag.VDMX);
		REMOVETABLES.add(Tag.LTSH);
		REMOVETABLES.add(Tag.DSIG);
		// for simsun with opentype table
		REMOVETABLES.add(Tag.EBLC);
		REMOVETABLES.add(Tag.EBDT);
		REMOVETABLES.add(Tag.morx);
		REMOVETABLES.add(Tag.mort);
		REMOVETABLES.add(Tag.vhea);
		REMOVETABLES.add(Tag.vmtx);
	}

	static {
		HINTSREMOVETABLES.addAll(REMOVETABLES);
		HINTSREMOVETABLES.add(Tag.fpgm);
		HINTSREMOVETABLES.add(Tag.prep);
		HINTSREMOVETABLES.add(Tag.cvt);
		HINTSREMOVETABLES.add(Tag.hdmx);
		HINTSREMOVETABLES.add(Tag.VDMX);
		HINTSREMOVETABLES.add(Tag.LTSH);
		HINTSREMOVETABLES.add(Tag.DSIG);
		HINTSREMOVETABLES.add(Tag.vhea);
		HINTSREMOVETABLES.add(Tag.vmtx);
	}

	private static Font subsetFont(FontFactory fontFactory, Font font, String text, boolean stripHinting)
			throws IOException {
		Font newFont = font;
		if (text != null && !"".equals(text)) {
			List<CMapTable.CMapId> cmapIds = new ArrayList<CMapTable.CMapId>();

			cmapIds.add(CMapTable.CMapId.WINDOWS_BMP);

			Subsetter subsetter = new RenumberingSubsetter(newFont, fontFactory);
			final Set<Integer> codepoints = SfStringUtils.getAllCodepoints(text + basicChars);
			List<Integer> glyphs = GlyphCoverage.getGlyphCoverage(font, codepoints);
			subsetter.setGlyphs(glyphs);
			subsetter.setCMaps(cmapIds, 1);
			subsetter.setRemoveTables(REMOVETABLES);

			newFont = subsetter.subset().build();
		}

		// strip hinting
		if (stripHinting) {
			Subsetter hintStripper = new HintStripper(newFont, fontFactory);
			hintStripper.setRemoveTables(HINTSREMOVETABLES);
			newFont = hintStripper.subset().build();
		}
		return newFont;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException {
		final FontFactory fontFactory = FontFactory.getInstance();
		Font font = fontFactory.loadFonts(new FileInputStream("/media/Guice/fonts/Alex-color.ttf"))[0];
		Font newFont = font;
		String text = "易企秀简单免费好用" + basicChars;
		if (text != null) {
			List<CMapTable.CMapId> cmapIds = new ArrayList<CMapTable.CMapId>();

			cmapIds.add(CMapTable.CMapId.WINDOWS_BMP);

			Subsetter subsetter = new RenumberingSubsetter(newFont, fontFactory);
			Set<Integer> codepoints = SfStringUtils.getAllCodepoints(text);
			List<Integer> glyphs = GlyphCoverage.getGlyphCoverage(font, codepoints);
			subsetter.setGlyphs(glyphs);
			subsetter.setCMaps(cmapIds, 1);
			subsetter.setRemoveTables(REMOVETABLES);
			subsetter.setCharsCodePoints(codepoints);
			newFont = subsetter.subset().build();
		}

		// strip hintingalse
		if (true) {
			Subsetter hintStripper = new HintStripper(newFont, fontFactory);
			hintStripper.setRemoveTables(HINTSREMOVETABLES);
			newFont = hintStripper.subset().build();
		}
		WoffWriter writter = new WoffWriter();
		WritableFontData data = writter.convert(newFont);
		data.copyTo(new FileOutputStream("/media/Guice/chinesefonts/woff/a3bc1b037bb15b59fce5a85cda104fe4_min.woff"));

		fontFactory.serializeFont(
				newFont, new FileOutputStream("/media/Guice/chinesefonts/woff/a3bc1b037bb15b59fce5a85cda104fe4_min.ttf")
		);

	}

}
