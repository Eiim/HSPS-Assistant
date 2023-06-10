package io.github.eiim.hspsassistant;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;

public class GraphicsHelper {
	
	private static final Minecraft mc = Minecraft.getInstance();
	private static final Font ft = mc.font;
	private static final GuiGraphics guig = new GuiGraphics(mc, mc.renderBuffers().bufferSource());
	
	public static void drawRect(int x, int y, int width, int height, ColorSettings cs) {
		guig.fill(x, y, x+width, y+height, cs.fillColor);
	}
	
	public static void drawRectBordered(int x, int y, int width, int height, int lineWidth, ColorSettings cs) {
		// Fill
		guig.fill(x, y, x+width, y+height, cs.fillColor);
		
		// Border
		int offsetIn = (int)Math.ceil(lineWidth/2.0);
		int offsetOut = (int)Math.floor(lineWidth/2.0);
		guig.fill(x-offsetOut, y-offsetOut, x+offsetIn, y+height+offsetOut, cs.borderColor); // Left
		guig.fill(x+width-offsetIn, y-offsetOut, x+width+offsetOut, y+height+offsetOut, cs.borderColor); // Right
		guig.fill(x+offsetIn, y-offsetOut, x+width-offsetIn, y+offsetIn, cs.borderColor); // Top
		guig.fill(x+offsetIn, y+height-offsetIn, x+width-offsetIn, y+height+offsetOut, cs.borderColor); // Bottom
	}
	
	public static void drawText(String text, int x, int y, ColorSettings cs) {
		guig.drawString(ft, text, x, y, cs.textColor);
	}
	
	public static void drawTextCentered(String text, int x, int y, ColorSettings cs) {
		guig.drawCenteredString(ft, text, x, y, cs.textColor);
	}
	
	public static void drawRectText(int x, int y, int width, int height, String text, ColorSettings cs) {
		drawRect(x, y, width, height, cs);
		int textWidth = ft.width(text);
		int textHeight = ft.lineHeight-2; // Returned lineHeight includes more ascenders/descenders than we want, rough adjustment
		drawText(text, x + (width-textWidth)/2, y + (height-textHeight)/2, cs);
	}
	
	public static void drawRectTextBordered(int x, int y, int width, int height, String text, int lineWidth, ColorSettings cs) {
		drawRectBordered(x, y, width, height, lineWidth, cs);
		int textWidth = ft.width(text);
		int textHeight = ft.lineHeight-2; // Returned lineHeight includes more ascenders/descenders than we want, rough adjustment
		drawText(text, x + (width-textWidth)/2, y + (height-textHeight)/2, cs);
	}
	
	// varargs is nice to allow us to calculate a max of multiple strings for alignment, but might remove in the future if not necessary.
	public static int getTextWidth(String... text) {
		int max = 0;
		for(String s : text) {
			int len = ft.width(s);
			if(len > max) max = len;
		}
		return max;
	}
	
	public static class ColorSettings {
		public final int textColor;
		public final int fillColor;
		public final int borderColor;
		
		public ColorSettings(int text, int fill, int border) {
			textColor = text;
			fillColor = fill;
			borderColor = border;
		}
	}
}
