package com.sdftdusername.nicer_sounds;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import de.pottgames.tuningfork.SoundBuffer;
import finalforeach.cosmicreach.GameAssetLoader;
import finalforeach.cosmicreach.audio.SoundManager;

import java.util.*;

public class Sounds {
    public static Map<String, Map<String, List<SoundBuffer>>> Materials = new HashMap<>();
    // {"water": {"wander": [...], "through": [...]}, "snow": {"walk": [...], "wander": [...]}}

    public static Map<String, Map<String, String>> SoundActions = new HashMap<>();
    public static Map<String, String> BlockMaterials = new HashMap<>();

    public static SoundBuffer OpenMenuSound;
    public static SoundBuffer CloseMenuSound;

    public static SoundBuffer SwitchHandSound;
    public static SoundBuffer SwitchSlotSound;

    public static void LoadSounds() {
        NicerSoundsMod.LOGGER.info("Loading UI sounds");

        OpenMenuSound = GameAssetLoader.getSound("assets/sounds/ui/menu_open.ogg");
        CloseMenuSound = GameAssetLoader.getSound("assets/sounds/ui/menu_close.ogg");

        SwitchHandSound = GameAssetLoader.getSound("assets/sounds/ui/switch_hand.ogg");
        SwitchSlotSound = GameAssetLoader.getSound("assets/sounds/ui/switch_slot.ogg");

        NicerSoundsMod.LOGGER.info("Loading material sounds");

        String materialsJson = GameAssetLoader.loadAsset("assets/sounds/footsteps/materials.json").readString();
        JsonReader reader = new JsonReader();
        JsonValue materials = reader.parse(materialsJson);

        for (JsonValue material : materials) {
            String materialName = material.name(); // water
            NicerSoundsMod.LOGGER.info("Loading sounds for {}", materialName);

            Map<String, List<SoundBuffer>> types = new HashMap<>();
            // {"wander": [...], "through": [...]}

            for (JsonValue value : material) {
                String fileName = value.asString();
                // water_wander4.ogg

                String info = fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('.'));

                int digitIndex = 0;
                while (info.charAt(digitIndex) < '0' || info.charAt(digitIndex) > '9') {
                    ++digitIndex;
                    if (digitIndex >= info.length())
                        break;
                }

                String type = info.substring(0, digitIndex);
                if (!types.containsKey(type))
                    types.put(type, new ArrayList<>());

                String absolutePath = "assets/sounds/footsteps/" + materialName + "/" + fileName;
                types.get(type).add(GameAssetLoader.getSound(absolutePath));
            }

            Materials.put(materialName, types);
        }

        NicerSoundsMod.LOGGER.info("Loading sound actions");

        String configJson = GameAssetLoader.loadAsset("assets/sounds/footsteps/config.json").readString();
        JsonValue config = reader.parse(configJson);

        for (JsonValue material : config.get("materials")) {
            String materialName = material.name(); // concrete

            Map<String, String> actions = new HashMap<>();
            // {"walk": "walk", "run": "run"}

            //NicerSoundsMod.LOGGER.info("{}", material.size);
            for (int i = 0; i < material.size; ++i) {
                NicerSoundsMod.LOGGER.info("1");
                JsonValue value = material.get(i);
                NicerSoundsMod.LOGGER.info("2");

                String action = value.name(); // walk
                NicerSoundsMod.LOGGER.info("3");
                String type = value.asString(); // walk
                NicerSoundsMod.LOGGER.info("4");

                actions.put(action, type);
                NicerSoundsMod.LOGGER.info("{} => {}", action, type);
            }

            SoundActions.put(materialName, actions);
        }

        NicerSoundsMod.LOGGER.info("Loading block materials");

        for (JsonValue block : config.get("blocks")) {
            String id = block.name(); // base:grass
            String material = block.asString(); // grass

            BlockMaterials.put(id, material);
        }

        NicerSoundsMod.LOGGER.info("Successfully loaded all sounds!");
    }

    private static void playSound(String block, String type, boolean playIn3D, Vector3 position) {
        if (type.isEmpty())
            return;

        if (!Materials.containsKey(block)) {
            NicerSoundsMod.LOGGER.error("No sound effects for block {}", block);
            return;
        }

        Map<String, List<SoundBuffer>> types = Materials.get(block);

        if (!types.containsKey(type)) {
            NicerSoundsMod.LOGGER.error("Block {} does not have type {}", block, type);
            return;
        }

        List<SoundBuffer> soundBuffers = types.get(type);
        int randomIndex = MathUtils.random(0, soundBuffers.size() - 1);

        if (playIn3D)
            SoundManager.INSTANCE.playSound3D(soundBuffers.get(randomIndex), position, 1.0f, MathUtils.random(0.9f, 1.1f));
        else
            SoundManager.INSTANCE.playSound(soundBuffers.get(randomIndex), 1.0f, MathUtils.random(0.9f, 1.1f));
    }

    public static void PlaySound(String block, String type) {
        playSound(block, type, false, Vector3.Zero);
    }

    public static void PlaySound3D(String block, String type, Vector3 position) {
        playSound(block, type, true, position);
    }
}
