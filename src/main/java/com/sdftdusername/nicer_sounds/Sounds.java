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

    public static Map<String, String[]> SoundActions = new HashMap<>();
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

        String contents = GameAssetLoader.loadAsset("assets/sounds/footsteps/materials.json").readString();
        JsonReader reader = new JsonReader();
        JsonValue blocks = reader.parse(contents);

        for (JsonValue block : blocks) {
            String blockName = block.name(); // water
            NicerSoundsMod.LOGGER.info("Loading sounds for {}", blockName);

            Map<String, List<SoundBuffer>> materials = new HashMap<>();
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
                if (!materials.containsKey(type))
                    materials.put(type, new ArrayList<>());

                String absolutePath = "assets/sounds/footsteps/" + blockName + "/" + fileName;
                materials.get(type).add(GameAssetLoader.getSound(absolutePath));
            }

            Materials.put(blockName, materials);
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

        BlockMaterials.put("base:air",                 "");
        BlockMaterials.put("base:aluminium_panel",     "metal");
        BlockMaterials.put("base:asphalt",             "stone");
        BlockMaterials.put("base:boombox",             "metal");
        BlockMaterials.put("base:c4",                  "metal");
        BlockMaterials.put("base:cheese",              "gravel");
        BlockMaterials.put("base:coconut",             "wood");
        BlockMaterials.put("base:crate_wooden",        "wood");
        BlockMaterials.put("base:debug",               "");
        BlockMaterials.put("base:dirt",                "dirt");
        BlockMaterials.put("base:glass",               "glass");
        BlockMaterials.put("base:grass",               "grass");
        BlockMaterials.put("base:hazard",              "stone");
        BlockMaterials.put("base:leaves",              "leaves");
        BlockMaterials.put("base:light",               "glass");
        BlockMaterials.put("base:lunar_soil",          "stone");
        BlockMaterials.put("base:lunar_soil_packed",   "concrete");
        BlockMaterials.put("base:magma",               "stone");
        BlockMaterials.put("base:metal_panel",         "metal");
        BlockMaterials.put("base:sand",                "sand");
        BlockMaterials.put("base:snow",                "snow");
        BlockMaterials.put("base:stone_basalt",        "stone");
        BlockMaterials.put("base:stone_gabbro",        "stone");
        BlockMaterials.put("base:stone_gravel",        "gravel");
        BlockMaterials.put("base:stone_limestone",     "stone");
        BlockMaterials.put("base:tree_log",            "wood");
        BlockMaterials.put("base:water",               "water");
        BlockMaterials.put("base:wood_planks",         "wood");
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
