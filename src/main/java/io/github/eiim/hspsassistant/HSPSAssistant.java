package io.github.eiim.hspsassistant;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(HSPSAssistant.MODID)
public class HSPSAssistant {
  public static final String MODID = "hspsassistant";

  // Used for certain event registrations that we're not using
  public static IEventBus MOD_EVENT_BUS;

  public HSPSAssistant() {
	MOD_EVENT_BUS = FMLJavaModLoadingContext.get().getModEventBus();
    registerServerEvents();
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> HSPSAssistant::registerClientEvents);
  }

  public static void registerServerEvents() {
	  // Probably will be unused
  }

  public static void registerClientEvents() {
	  MinecraftForge.EVENT_BUS.register(new TestHud());
	  MinecraftForge.EVENT_BUS.register(new TestChat());
	  MOD_EVENT_BUS.register(TestHud.class);
	  MOD_EVENT_BUS.register(TestChat.class);
  }
}
