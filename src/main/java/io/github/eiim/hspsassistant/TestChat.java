package io.github.eiim.hspsassistant;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent.LoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TestChat {
	
	@SubscribeEvent
    public void onChatSend(ClientChatEvent event) {
		LOGGER.debug("sending message: "+event.getMessage());
    }
	
	@SubscribeEvent
    public void onChatGet(ClientChatReceivedEvent event) {
		LOGGER.debug("message: "+event.getMessage());
		LOGGER.debug("uuid: "+event.getSenderUUID());
		LOGGER.debug("type: "+event.getType());
    }
	
	@SubscribeEvent
	public static void onLogin(LoggedInEvent event) {
		LOGGER.debug("Logged in event: "+event.getResult());
	}
	
	private static final Logger LOGGER = LogManager.getLogger();
}
