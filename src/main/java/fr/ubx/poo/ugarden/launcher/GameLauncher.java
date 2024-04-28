package fr.ubx.poo.ugarden.launcher;

import fr.ubx.poo.ugarden.game.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.Properties;
import java.io.IOException;

public class GameLauncher {

    private int levels;
    private MapLevel[] mapLevels;

    private GameLauncher() {
    }

    public static GameLauncher getInstance() {
        return LoadSingleton.INSTANCE;
    }

    private int integerProperty(Properties properties, String name, int defaultValue) {
        return Integer.parseInt(properties.getProperty(name, Integer.toString(defaultValue)));
    }

    private boolean booleanProperty(Properties properties, String name, boolean defaultValue) {
        return Boolean.parseBoolean(properties.getProperty(name, Boolean.toString(defaultValue)));
    }

    private Configuration getConfiguration(Properties properties) {

        // Load parameters
        int hornetMoveFrequency = integerProperty(properties, "hornetMoveFrequency", 1);
        int gardenerEnergy = integerProperty(properties, "gardenerEnergy", 100);
        int energyBoost = integerProperty(properties, "energyBoost", 50);
        int energyRecoverDuration = integerProperty(properties, "energyRecoverDuration", 1);
        int diseaseDuration = integerProperty(properties, "diseaseDuration", 5);

        return new Configuration(gardenerEnergy, energyBoost, hornetMoveFrequency, energyRecoverDuration, diseaseDuration);
    }

    public Game load(File file) {
        Properties properties = new Properties();

        try (Reader reader = new FileReader(file)) {
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException("Probleme pour lire" + file, e);
        }

        boolean compression = booleanProperty(properties, "compression", false);
        int levels = integerProperty(properties, "levels", 1);

        System.out.println("Compression: " + compression);
        System.out.println("Number of levels: " + levels);

        World world = new World(levels);
        Configuration configuration = getConfiguration(properties);

        System.out.println("Configuration: " + configuration);

        Position gardenerPosition = null;

        Game game = new Game(world, configuration, null);

        for (int i = 1; i <= levels; i++) {
            String levelStage = "level" + i;
            String levelData = properties.getProperty(levelStage);

            System.out.println("Loading level " + i);
            System.out.println("Level data: " + levelData);

            MapLevel mapLevel;
            if (compression) {
                System.out.println("Decompressing level data...");
                mapLevel = loadCompressedLevel(levelData);
            } else {
                System.out.println("Loading uncompressed level data...");
                mapLevel = loadUncompressedLevel(levelData);
            }

            System.out.println("MapLevel created for level " + i);
            System.out.println("MapLevel dimensions: " + mapLevel.width() + "x" + mapLevel.height());

            if (i == 1) {
                gardenerPosition = mapLevel.getGardenerPosition();
                game.getGardener().setPosition(gardenerPosition);
                System.out.println("Gardener position: " + gardenerPosition);
            }

            Map level = new Level(game, i, mapLevel);
            world.put(i, level);

            System.out.println("Level " + i + " added to the world");
        }

        System.out.println("Game created with gardener position: " + gardenerPosition);

        return game;
    }

    private MapLevel loadCompressedLevel(String levelData) {
        String decompressedData = decompressLevel(levelData);
        return createMapLevel(decompressedData);
    }

    private MapLevel loadUncompressedLevel(String levelData) {
        return createMapLevel(levelData);
    }

    private String decompressLevel(String input) {
        StringBuilder output = new StringBuilder();

        for (String part : input.split("x")) {
            StringBuilder partBuilder = new StringBuilder();

            for (int i = 0; i < part.length(); i++) {
                char chr = part.charAt(i);

                if (Character.isDigit(chr)) {
                    int count = Character.getNumericValue(chr);
                    char prevChar = partBuilder.charAt(partBuilder.length() - 1);

                    for (int j = 1; j < count; j++) {
                        partBuilder.append(prevChar);
                    }
                } else {
                    partBuilder.append(chr);
                }
            }

            output.append(partBuilder);
            output.append("x");
        }

        return output.toString();
    }

    private MapLevel createMapLevel(String levelData) {
        String[] rows = levelData.split("x");
        int height = rows.length;
        int width = rows[0].length();

        MapLevel mapLevel = new MapLevel(width, height);

        for (int j = 0; j < height; j++) {
            String row = rows[j];
            for (int i = 0; i < width; i++) {
                char chr = row.charAt(i);
                MapEntity entity = MapEntity.fromCode(chr);
                mapLevel.set(i, j, entity);
            }
        }

        return mapLevel;
    }

    public Game load() {
        Properties emptyConfig = new Properties();
        MapLevel mapLevel = new MapLevelDefault();
        Position gardenerPosition = mapLevel.getGardenerPosition();
        if (gardenerPosition == null)
            throw new RuntimeException("Gardener not found");
        Configuration configuration = getConfiguration(emptyConfig);//ici on mets les configuration initialiser dans la methodes
        World world = new World(1);
        Game game = new Game(world, configuration, gardenerPosition);
        Map level = new Level(game, 1, mapLevel);
        world.put(1, level);
        return game;
    }

    private static class LoadSingleton {
        static final GameLauncher INSTANCE = new GameLauncher();
    }

}
