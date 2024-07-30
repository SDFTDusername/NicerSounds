package com.sdftdusername.nicer_sounds;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NicerSoundsMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Nicer Sounds");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Nicer Sounds Mod Initialized!");
		Sounds.LoadSounds();
	}
}

