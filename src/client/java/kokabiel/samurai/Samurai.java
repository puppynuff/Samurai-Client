package kokabiel.samurai;

import net.fabricmc.api.ClientModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Samurai implements ClientModInitializer {
	public static final String MOD_ID = "samurai";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static SamuraiClient INSTANCE;


	@Override
	public void onInitializeClient() {
		INSTANCE = new SamuraiClient();
		INSTANCE.Initialize();

		LOGGER.info("Hello Fabric world!");
	}

	public static SamuraiClient getInstance() {
		return INSTANCE;
	}
}