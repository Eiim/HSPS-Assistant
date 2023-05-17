package io.github.eiim.hspsassistant;

import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;

public class GraphicsHelper {
	
	private static Font ft = Minecraft.getInstance().font;
	
	public static void drawRect(PoseStack ps, int x, int y, int width, int height, int fill) {
		GuiComponent.fill(ps, x, y, x+width, y+height, fill);
	}
	
	public static void drawRect(PoseStack ps, int x, int y, int width, int height, int lineWidth, int fill, int border) {
		// Fill
		GuiComponent.fill(ps, x, y, x+width, y+height, fill);
		
		// Border
		int offsetIn = (int)Math.ceil(lineWidth/2.0);
		int offsetOut = (int)Math.floor(lineWidth/2.0);
		GuiComponent.fill(ps, x-offsetOut, y-offsetOut, x+offsetIn, y+height+offsetOut, border); // Left
		GuiComponent.fill(ps, x+width-offsetIn, y-offsetOut, x+width+offsetOut, y+height+offsetOut, border); // Right
		GuiComponent.fill(ps, x+offsetIn, y-offsetOut, x+width-offsetIn, y+offsetIn, border); // Top
		GuiComponent.fill(ps, x+offsetIn, y+height-offsetIn, x+width-offsetIn, y+height+offsetOut, border); // Bottom
	}
	
	public static void drawText(PoseStack ps, String text, float x, float y) {
		ft.draw(ps, text, x, y, 0xFFFFFF);
	}
	
	public static void drawText(PoseStack ps, String text, float x, float y, int color) {
		ft.draw(ps, text, x, y, color);
	}
	
	public static void drawTextShadow(PoseStack ps, String text, float x, float y) {
		ft.drawShadow(ps, text, x, y, 0xFFFFFF);
	}
	
	public static void drawTextShadow(PoseStack ps, String text, float x, float y, int color) {
		ft.drawShadow(ps, text, x, y, color);
	}
}
