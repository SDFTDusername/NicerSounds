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
    public static Map<String, Map<String, List<SoundBuffer>>> SoundEffects = new HashMap<>();
    // {"water": {"wander": [...], "through": [...]}, "snow": {"walk": [...], "wander": [...]}}

    public static Map<String, String[]> SoundActions = new HashMap<>();
    public static Map<String, String> BlockRoutes = new HashMap<>();

    public static SoundBuffer OpenMenuSound;
    public static SoundBuffer CloseMenuSound;

    public static SoundBuffer SwitchHandSound;
    public static SoundBuffer SwitchSlotSound;

    public static void LoadSounds() {
        NicerSoundsMod.LOGGER.info("Loading sounds");

        OpenMenuSound = GameAssetLoader.getSound("assets/sounds/ui/menu_open.ogg");
        CloseMenuSound = GameAssetLoader.getSound("assets/sounds/ui/menu_close.ogg");

        SwitchHandSound = GameAssetLoader.getSound("assets/sounds/ui/switch_hand.ogg");
        SwitchSlotSound = GameAssetLoader.getSound("assets/sounds/ui/switch_slot.ogg");

        String contents = GameAssetLoader.loadAsset("assets/sounds/footsteps/contents.json").readString();
        JsonReader reader = new JsonReader();
        JsonValue blocks = reader.parse(contents);

        for (JsonValue block : blocks) {
            String blockName = block.name(); // water
            NicerSoundsMod.LOGGER.info("Loading sounds for {}", blockName);

            Map<String, List<SoundBuffer>> soundEffects = new HashMap<>();
            // {"wander": [...], "through": [...]}

            for (int i = 0; i < block.size; ++i) {
                String fileName = block.getString(i);
                // water_wander4.ogg

                String info = fileName.substring(fileName.indexOf('_') + 1, fileName.indexOf('.'));

                int digitIndex = 0;
                while (info.charAt(digitIndex) < '0' || info.charAt(digitIndex) > '9') {
                    ++digitIndex;
                    if (digitIndex >= info.length())
                        break;
                }

                String type = info.substring(0, digitIndex);
                if (!soundEffects.containsKey(type))
                    soundEffects.put(type, new ArrayList<>());

                String absolutePath = "assets/sounds/footsteps/" + blockName + "/" + fileName;
                soundEffects.get(type).add(GameAssetLoader.getSound(absolutePath));
            }

            SoundEffects.put(blockName, soundEffects);
        }

        NicerSoundsMod.LOGGER.info("Successfully loaded sounds!");

        // 0: Walk
        // 1: Run
        // 2: Crouch
        // 3: Prone
        // 4: Jump
        // 5: Land
        // 6: Place
        // 7: Destroy
        SoundActions.put("concrete", new String[]{"walk", "run", "walk", "walk", "run", "run", "run", "run"});
        SoundActions.put("dirt", new String[]{"walk", "run", "walk", "walk", "land", "land", "land", "land"});
        SoundActions.put("glass", new String[]{"hit", "hit", "hit", "hit", "hard", "hard", "hard", "hard"});
        SoundActions.put("grass", new String[]{"walk", "run", "walk", "walk", "run", "run", "run", "run"});
        SoundActions.put("gravel", new String[]{"walk", "run", "wander", "wander", "land", "land", "land", "land"});
        SoundActions.put("leaves", new String[]{"through", "through", "through", "through", "through", "through", "through", "through"});
        SoundActions.put("metal", new String[]{"walk", "run", "wander", "wander", "land", "walk", "land", "land"});
        SoundActions.put("sand", new String[]{"walk", "run", "walk", "walk", "run", "run", "run", "run"});
        SoundActions.put("snow", new String[]{"walk", "run", "walk", "walk", "run", "run", "run", "run"});
        SoundActions.put("stone", new String[]{"walk", "run", "walk", "walk", "run", "run", "run", "run"});
        SoundActions.put("water", new String[]{"wander", "wander", "wander", "wander", "wander", "wander", "through", "through"});
        SoundActions.put("wood", new String[]{"walk", "walk", "walk", "walk", "walk", "walk", "walk", "walk"});

        BlockRoutes.put("base:air",                 "");
        BlockRoutes.put("base:aluminium_panel",     "metal");
        BlockRoutes.put("base:asphalt",             "stone");
        BlockRoutes.put("base:boombox",             "metal");
        BlockRoutes.put("base:c4",                  "metal");
        BlockRoutes.put("base:cheese",              "gravel");
        BlockRoutes.put("base:coconut",             "wood");
        BlockRoutes.put("base:crate_wooden",        "wood");
        BlockRoutes.put("base:debug",               "");
        BlockRoutes.put("base:dirt",                "dirt");
        BlockRoutes.put("base:glass",               "glass");
        BlockRoutes.put("base:grass",               "grass");
        BlockRoutes.put("base:hazard",              "stone");
        BlockRoutes.put("base:leaves",              "leaves");
        BlockRoutes.put("base:light",               "glass");
        BlockRoutes.put("base:lunar_soil",          "stone");
        BlockRoutes.put("base:lunar_soil_packed",   "concrete");
        BlockRoutes.put("base:magma",               "stone");
        BlockRoutes.put("base:metal_panel",         "metal");
        BlockRoutes.put("base:sand",                "sand");
        BlockRoutes.put("base:snow",                "snow");
        BlockRoutes.put("base:stone_basalt",        "stone");
        BlockRoutes.put("base:stone_gabbro",        "stone");
        BlockRoutes.put("base:stone_gravel",        "gravel");
        BlockRoutes.put("base:stone_limestone",     "stone");
        BlockRoutes.put("base:tree_log",            "wood");
        BlockRoutes.put("base:water",               "water");
        BlockRoutes.put("base:wood_planks",         "wood");
    }

    public static void PlaySound(String block, String type) {
        if (type.isEmpty())
            return;

        if (!SoundEffects.containsKey(block)) {
            NicerSoundsMod.LOGGER.error("No sound effects for block {}", block);
            return;
        }

        Map<String, List<SoundBuffer>> types = SoundEffects.get(block);

        if (!types.containsKey(type)) {
            NicerSoundsMod.LOGGER.error("Block {} does not have type {}", block, type);
            return;
        }

        List<SoundBuffer> soundBuffers = types.get(type);
        int randomIndex = MathUtils.random(0, soundBuffers.size() - 1);
        SoundManager.INSTANCE.playSound(soundBuffers.get(randomIndex), 1.0f, MathUtils.random(0.9f, 1.1f));
    }

    public static void PlaySound3D(String block, String type, Vector3 position) {
        if (type.isEmpty())
            return;

        if (!SoundEffects.containsKey(block)) {
            NicerSoundsMod.LOGGER.error("No sound effects for block {}", block);
            return;
        }

        Map<String, List<SoundBuffer>> types = SoundEffects.get(block);

        if (!types.containsKey(type)) {
            NicerSoundsMod.LOGGER.error("Block {} does not have type {}", block, type);
            return;
        }

        List<SoundBuffer> soundBuffers = types.get(type);
        int randomIndex = MathUtils.random(0, soundBuffers.size() - 1);
        SoundManager.INSTANCE.playSound3D(soundBuffers.get(randomIndex), position, 1.0f, MathUtils.random(0.9f, 1.1f));
    }
}
