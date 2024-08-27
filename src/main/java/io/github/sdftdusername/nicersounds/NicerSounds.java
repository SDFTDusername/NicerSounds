package io.github.sdftdusername.nicersounds;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NicerSounds implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Nicer Sounds");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Nicer Sounds Mod Initialized!");
		Sounds.LoadSounds();
	}
}

