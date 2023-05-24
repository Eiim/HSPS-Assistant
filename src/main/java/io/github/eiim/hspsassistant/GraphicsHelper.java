package io.github.eiim.hspsassistant;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;

public class GraphicsHelper {
	
	private static Font ft = Minecraft.getInstance().font;
	
	public static void drawRect(PoseStack ps, int x, int y, int width, int height, ColorSettings cs) {
		GuiComponent.fill(ps, x, y, x+width, y+height, cs.fillColor);
	}
	
	public static void drawRectBordered(PoseStack ps, int x, int y, int width, int height, int lineWidth, ColorSettings cs) {
		// Fill
		GuiComponent.fill(ps, x, y, x+width, y+height, cs.fillColor);
		
		// Border
		int offsetIn = (int)Math.ceil(lineWidth/2.0);
		int offsetOut = (int)Math.floor(lineWidth/2.0);
		GuiComponent.fill(ps, x-offsetOut, y-offsetOut, x+offsetIn, y+height+offsetOut, cs.borderColor); // Left
		GuiComponent.fill(ps, x+width-offsetIn, y-offsetOut, x+width+offsetOut, y+height+offsetOut, cs.borderColor); // Right
		GuiComponent.fill(ps, x+offsetIn, y-offsetOut, x+width-offsetIn, y+offsetIn, cs.borderColor); // Top
		GuiComponent.fill(ps, x+offsetIn, y+height-offsetIn, x+width-offsetIn, y+height+offsetOut, cs.borderColor); // Bottom
	}
	
	public static void drawText(PoseStack ps, String text, float x, float y, ColorSettings cs) {
		ft.draw(ps, text, x, y, cs.textColor);
	}
	
	public static void drawTextShadow(PoseStack ps, String text, float x, float y, ColorSettings cs) {
		ft.drawShadow(ps, text, x, y, cs.textColor);
	}
	
	public static void drawRectText(PoseStack ps, int x, int y, int width, int height, String text, ColorSettings cs) {
		drawRect(ps, x, y, width, height, cs);
		int textWidth = ft.width(text);
		int textHeight = ft.lineHeight-2; // Returned lineHeight includes more ascenders/descenders than we want, rough adjustment
		drawText(ps, text, x + (width-textWidth)/2, y + (height-textHeight)/2, cs);
	}
	
	public static void drawRectTextBordered(PoseStack ps, int x, int y, int width, int height, String text, int lineWidth, ColorSettings cs) {
		drawRectBordered(ps, x, y, width, height, lineWidth, cs);
		int textWidth = ft.width(text);
		int textHeight = ft.lineHeight-2; // Returned lineHeight includes more ascenders/descenders than we want, rough adjustment
		drawText(ps, text, x + (width-textWidth)/2, y + (height-textHeight)/2, cs);
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
