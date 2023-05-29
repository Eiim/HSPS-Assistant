package io.github.eiim.hspsassistant;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientChatEvent;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ChatListener {
	
	public static final Pattern CP_NUM_REGEX = Pattern.compile("Checkpoint #(\\d+)");
	public static final Pattern TIMER_REGEX = Pattern.compile("\\d\\d:\\d\\d\\.\\d\\d\\d");
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	@SubscribeEvent
    public void onChatSend(ClientChatEvent event) {
		LOGGER.debug("sending message: "+event.getMessage());
    }
	
	@SubscribeEvent
    public void onChatGet(ClientChatReceivedEvent event) {
		Component message = event.getMessage();
		String text = flattenMessage(message);
		
		try {
			if(text.startsWith("Parkour challenge started!")) {
				LOGGER.debug("Got parkour start message");
				RenderUpdater.startTiming();
			} else if(text.startsWith("Reset your timer to 00:00!")) {
				LOGGER.debug("Got reset message");
				RenderUpdater.startTiming();
			} else if(text.startsWith("You reached Checkpoint")) {
				Matcher cpMatcher = CP_NUM_REGEX.matcher(text);
				cpMatcher.find();
				String cpnum = cpMatcher.group(1);
				Matcher tmMatcher = TIMER_REGEX.matcher(text);
				tmMatcher.find();
				String time = tmMatcher.group();
				LOGGER.debug("Recieved total time of"+time+" at "+cpnum);
				RenderUpdater.splitTotal(Integer.parseInt(cpnum), Timing.timestringToMillis(time));
			} else if(text.startsWith("You finished this part")) {
				if(text.contains("personal best:")) {
					// Worse than PB
					Matcher timerMatcher = TIMER_REGEX.matcher(text);
					timerMatcher.find();
					String splitTime = timerMatcher.group();
					timerMatcher.find();
					String pbTime = timerMatcher.group();
					LOGGER.debug("Recieved split of "+splitTime+" (PB: "+pbTime+")");
					RenderUpdater.splitDelta(Timing.timestringToMillis(splitTime));
				} else if(text.contains("beat your personal best")) {
					// Improved segment PB
					Matcher timerMatcher = TIMER_REGEX.matcher(text);
					timerMatcher.find();
					String splitTime = timerMatcher.group();
					timerMatcher.find();
					String pbTime = timerMatcher.group();
					LOGGER.debug("Recieved split of "+splitTime+" (old PB: "+pbTime+")");
					RenderUpdater.splitDelta(Timing.timestringToMillis(splitTime));
				} else {
					// First completion of segment/cp
					Matcher timerMatcher = TIMER_REGEX.matcher(text);
					timerMatcher.find();
					String splitTime = timerMatcher.group();
					LOGGER.debug("Recieved split of "+splitTime);
					RenderUpdater.splitDelta(Timing.timestringToMillis(splitTime));
				}
			} else if(text.startsWith("Parkour challenge failed!")) {
				LOGGER.debug("Recieved failed message");
				RenderUpdater.stopTiming();
			} else if(text.startsWith("Parkour challenge cancelled!")) {
				LOGGER.debug("Recieved cancelled message");
				RenderUpdater.stopTiming();
			} else if(text.startsWith("Your time of ")) {
				// Worse than PB overall
				Matcher timerMatcher = TIMER_REGEX.matcher(text);
				timerMatcher.find();
				String time = timerMatcher.group();
				timerMatcher.find();
				String pbTime = timerMatcher.group();
				LOGGER.debug("Recieved finish message with time "+time+" and pb "+pbTime);
				RenderUpdater.finalTime(Timing.timestringToMillis(time));
			} else if(text.startsWith("That's a new record")) {
				// Improved total PB
				Matcher timerMatcher = TIMER_REGEX.matcher(text);
				timerMatcher.find();
				String time = timerMatcher.group();
				LOGGER.debug("Recieved finish message with time "+time);
				RenderUpdater.finalTime(Timing.timestringToMillis(time));
			} else if(text.startsWith("Congratulations on completing the parkour!")) {
				// First time completions
				Matcher timerMatcher = TIMER_REGEX.matcher(text);
				timerMatcher.find();
				String time = timerMatcher.group();
				LOGGER.debug("Recieved new finish message with time "+time);
				RenderUpdater.finalTime(Timing.timestringToMillis(time));
			} else {
				LOGGER.debug("Recieved other message: "+text);
			}
		} catch(IllegalStateException e) {
			// Regex fail, should probably notify in chat
			LOGGER.warn("Failed to parse message "+text);
			e.printStackTrace();
		}
    }
	
	// Should never nest more than a few times, so recursion should be fine
	private static String flattenMessage(Component message) {
		String text = message.getString();
		for(Component m : message.getSiblings()) {
			text += flattenMessage(m);
		}
		return text;
	}
}
