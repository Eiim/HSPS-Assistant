package io.github.eiim.hspsassistant;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class HSPSConfig {
	
	public static ForgeConfigSpec SPEC;
	private static final ForgeConfigSpec.Builder BUILDER;
	
	public static final ConfigValue<Boolean> renderKeys;
	public static final ConfigValue<Boolean> savePartial;
	public static final ConfigValue<Boolean> saveAll;
	public static final ConfigValue<Integer> uiColor;
	public static final ConfigValue<Integer> bgColor;
	public static final ConfigValue<Integer> keyOpacity;
	public static final ConfigValue<Integer> bgOpacity;

	static {
		BUILDER = new ForgeConfigSpec.Builder();
		BUILDER.push("hsps");
		renderKeys = BUILDER
				.comment("Enable key visualization?")
				.translation("hspsassistant.config.renderKeys")
				.define("renderKeys", true);
		savePartial = BUILDER
				.comment("Save partial runs? (Not currently implemented)")
				.translation("hspsassistant.config.savePartial")
				.define("savePartial", false);
		saveAll = BUILDER
				.comment("Save all runs?")
				.translation("hspsassistant.config.saveAll")
				.define("saveAll", true);
		uiColor = BUILDER
				.comment("UI Color")
				.translation("hspsassistant.config.uiColor")
				.define("uiColor", 0xFFFFFF);
		bgColor = BUILDER
				.comment("Background Color")
				.translation("hspsassistant.config.bgColor")
				.define("bgColor", 0x000000);
		keyOpacity = BUILDER
				.comment("Key Opacity")
				.translation("hspsassistant.config.keyOpacity")
				.define("keyOpacity", 0x88);
		bgOpacity = BUILDER
				.comment("Backgorund Opacity")
				.translation("hspsassistant.config.bgOpacity")
				.define("bgOpacity", 0x44);
		BUILDER.pop();
		SPEC = BUILDER.build();
	}

}
