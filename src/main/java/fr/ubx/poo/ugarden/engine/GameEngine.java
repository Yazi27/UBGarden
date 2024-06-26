    /*
     * Copyright (c) 2020. Laurent Réveillère
     */

    package fr.ubx.poo.ugarden.engine;

    import fr.ubx.poo.ugarden.game.Direction;
    import fr.ubx.poo.ugarden.game.Game;
    import fr.ubx.poo.ugarden.game.Position;
    import fr.ubx.poo.ugarden.go.GameObject;
    import fr.ubx.poo.ugarden.go.bonus.Insecticide;
    import fr.ubx.poo.ugarden.go.bonus.Nest;
    import fr.ubx.poo.ugarden.go.decor.Decor;
    import fr.ubx.poo.ugarden.go.decor.DoorNextClosed;
    import fr.ubx.poo.ugarden.go.decor.DoorNextOpened;
    import fr.ubx.poo.ugarden.go.decor.DoorPrevOpened;
    import fr.ubx.poo.ugarden.go.decor.ground.Grass;
    import fr.ubx.poo.ugarden.go.decor.ground.Land;
    import fr.ubx.poo.ugarden.go.personage.Gardener;
    import fr.ubx.poo.ugarden.go.personage.Hornet;
    import fr.ubx.poo.ugarden.view.*;
    import javafx.animation.AnimationTimer;
    import javafx.application.Platform;
    import javafx.geometry.Pos;
    import javafx.scene.Group;
    import javafx.scene.Scene;
    import javafx.scene.layout.Pane;
    import javafx.scene.layout.StackPane;
    import javafx.scene.paint.Color;
    import javafx.scene.text.Font;
    import javafx.scene.text.Text;
    import javafx.scene.text.TextAlignment;
    import javafx.stage.Stage;

    import java.util.*;


    public final class GameEngine {

        private static AnimationTimer gameLoop;
        private final Game game;
        private final Gardener gardener;
        private final List<Sprite> sprites = new LinkedList<>();
        private final Set<Sprite> cleanUpSprites = new HashSet<>();
        private final List<Hornet> hornets=new ArrayList<>();
        private final Stage stage;
        private final Pane layer = new Pane();
        private StatusBar statusBar;
        private Input input;
        private final Timer nestTimer;
        private final List<Position> nestPositions = new ArrayList<>();
        private Insecticide lastInsecticide;
        private static final Random randomGenerator = new Random();

        public GameEngine(Game game, final Stage stage) {
            this.stage = stage;
            this.game = game;
            this.gardener = game.getGardener();
            this.nestTimer =new Timer(7);
            initialize();
            buildAndSetGameLoop();
        }

        private void initialize() {

            Group root = new Group();

            int height = game.world().getGrid().height();
            int width = game.world().getGrid().width();
            int sceneWidth = width * ImageResource.size;
            int sceneHeight = height * ImageResource.size;
            Scene scene = new Scene(root, sceneWidth, sceneHeight + StatusBar.height);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/application.css")).toExternalForm());

            stage.setScene(scene);
            stage.setResizable(false);
            stage.sizeToScene();
            stage.hide();
            stage.show();

            input = new Input(scene);
            root.getChildren().add(layer);
            statusBar = new StatusBar(root, sceneWidth, sceneHeight);

            for (var decor : game.world().getGrid().values()) {
                sprites.add(SpriteFactory.create(layer, decor));
                decor.setModified(true);
                var bonus = decor.getBonus();
                var hornet = decor.getHornet();
                if (bonus != null) {
                    sprites.add(SpriteFactory.create(layer, bonus));
                    bonus.setModified(true);

                    // Check if we found the Nest
                    if (bonus instanceof Nest) {
                        // If we found a Nest, add its position to the list
                        nestPositions.add(bonus.getPosition());
                        spawnHornet(bonus.getPosition());
                        System.out.println("Found Nest position at");
                        System.out.println(bonus.getPosition());
                    }
                }
            }

            sprites.add(new SpriteGardener(layer, gardener));
        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {

                    // If changing levels, reset timer
                    if (game.isSwitchLevelRequested()) {
                        nestTimer.start(now);
                    }
                    checkLevel();

                    // Check keyboard actions
                    processInput();

                    // Do actions
                    update(now);
                    checkCollision();
                    gardener.update(now);
                    // Graphic update
                    cleanupSprites();
                    render();
                    statusBar.update(game);

                }
            };

        }


        private void checkLevel() {
            if (game.isSwitchLevelRequested()) {
                int requestedLevel = game.getSwitchLevel();

                // Switching from level X to level Y
                System.out.println("Switching from level " + game.world().currentLevel() + " to level " + requestedLevel);
                // Clear all sprites
                sprites.clear();
                layer.getChildren().clear();

                // Old requested level
                int oldRequestedLevel = game.world().currentLevel();

                // Change the current level
                game.world().setCurrentLevel(requestedLevel);

                // Find the position of the door to reach in the requested level
                Position doorPosition = null;
                for (var decor : game.world().getGrid().values()) {

                    // Print position and class of decor
                    System.out.println("Position: " + decor.getPosition());
                    System.out.println("Class: " + decor.getClass());
                    if (game.world().currentLevel() < oldRequestedLevel) {
                        // Going back to the previous level
                        System.out.println("Checking for previous level door");
                        if (decor instanceof DoorNextOpened) {
                            System.out.println("Going back to the previous level");
                            doorPosition = decor.getPosition();
                            System.out.println("Found door position at");
                            System.out.println(doorPosition);
                            break;
                        }
                    } else {
                        // Going to the next level
                        if (decor instanceof DoorPrevOpened) {
                            System.out.println("Going to the next level");
                            doorPosition = decor.getPosition();
                            System.out.println("Found door position at");
                            System.out.println(doorPosition);
                            break;
                        }
                    }
                }

                // Clear hornets list
                hornets.clear();

                nestPositions.clear();

                // Clear the last insecticide
                lastInsecticide = null;

                // Set the position of the gardener to the door position
                if (doorPosition != null) {
                    gardener.setPosition(doorPosition);
                }

                // Clear the switch level request
                game.clearSwitchLevel();

                // Reinitialize the game engine for the requested level
                initialize();
            }
        }

        private void checkCollision() {
            for (int i = 0; i < hornets.size(); i++) {
                Hornet frelon = hornets.get(i);
                if (frelon.getPosition().equals(gardener.getPosition())){
                    gardener.hurt(20);
                    frelon.remove();
                    hornets.remove(i);
                    //hornets.add();
                }
            }
        }
        private void processInput() {
            if (input.isExit()) {
                gameLoop.stop();
                Platform.exit();
                System.exit(0);
            } else if (input.isMoveDown()) {
                gardener.requestMove(Direction.DOWN);
            } else if (input.isMoveLeft()) {
                gardener.requestMove(Direction.LEFT);
            } else if (input.isMoveRight()) {
                gardener.requestMove(Direction.RIGHT);
            } else if (input.isMoveUp()) {
                gardener.requestMove(Direction.UP);
            }
            input.clear();
        }

        private void showMessage(String msg, Color color) {
            Text waitingForKey = new Text(msg);
            waitingForKey.setTextAlignment(TextAlignment.CENTER);
            waitingForKey.setFont(new Font(60));
            waitingForKey.setFill(color);
            StackPane root = new StackPane();
            root.getChildren().add(waitingForKey);
            Scene scene = new Scene(root, 400, 200, Color.WHITE);
            stage.setScene(scene);
            input = new Input(scene);
            stage.show();
            new AnimationTimer() {
                public void handle(long now) {
                    processInput();
                }
            }.start();
        }

        // Spawn hornet at given location
        public void spawnHornet(Position position) {
            Hornet hornet = new Hornet(game, position);
            sprites.add(SpriteFactory.create(layer, hornet));
            hornets.add(hornet);
        }

        private Position generateValidPosition() {
            int x, y;
            Position position;
            Decor decorAtPosition;

            do {
                x = randomGenerator.nextInt(game.world().getGrid().width());
                y = randomGenerator.nextInt(game.world().getGrid().height());
                position = new Position(game.world().currentLevel(), x, y);
                decorAtPosition = game.world().getGrid().get(position);
            } while (!(decorAtPosition instanceof Grass) || decorAtPosition.getBonus() != null);

            return position;
        }

        public boolean isPositionOccupiedByHornet(Position position) {
            for (Hornet hornet : hornets) {
                if (hornet.getPosition().equals(position)) {
                    return true;
                }
            }
            return false;
        }

        private void update(long now) {
            game.world().getGrid().values().forEach(decor -> decor.update(now));

            gardener.update(now);
            nestTimer.update(now);


            // Check if hornet picks up an insecticide
            for (Hornet hornet : hornets) {
                hornet.update(now);
                if (hornet.getInsecticide()==1)
                    hornet.remove();
            }

            // Check if gardener no longer has any energy
            if (gardener.getEnergy() < 0) {
                gameLoop.stop();
                showMessage("Perdu!", Color.RED);
            }

            // Check if we should spawn hornets and an insecticide
            if (!nestTimer.isRunning()) {
                // Spawn a Hornet at each Nest position
                for (Position nestPosition : nestPositions) {
                    spawnHornet(nestPosition);
                }

                // Remove last insecticide from the map if it's present
                if (lastInsecticide != null) {
                    Decor lastInsecticideDecor = game.world().getGrid().get(lastInsecticide.getPosition());
                    // Make sure we don't remove another bonus
                    if (lastInsecticideDecor.getBonus() instanceof Insecticide) {
                        lastInsecticideDecor.setBonus(null);
                        lastInsecticide.remove();
                    }
                }

                /* ************ Spawn insecticide at random location ******** */

                // First we prepare a random location
                Position position = generateValidPosition();
                Decor decorAtPosition = game.world().getGrid().get(position);

                // Create a new Insecticide object at position
                Insecticide insecticide = new Insecticide(position, game.world().getGrid().get(position));

                // Save the last insecticide
                lastInsecticide = insecticide;

                // Set the Insecticide as the Bonus of the Decor object
                decorAtPosition.setBonus(insecticide);

                // Place sprite in world
                sprites.add(SpriteFactory.create(layer, insecticide));

                insecticide.setModified(true);

                // Now the Insecticide is placed in the Map as a Bonus of the Decor object at the specified position

                insecticide.setModified(true);
                System.out.println("Insecticide spawned at");
                System.out.println(position);
                nestTimer.start(now);
            }


            if(gardener.getHedgehog()==1){
                win();
                System.out.println("MERCI D'AVOIR JOUÉ");
            }
        }

        public void win(){
            gameLoop.stop();
            showMessage("Gagne!", Color.GREEN);
        }

        public void cleanupSprites() {
            sprites.forEach(sprite -> {
                if (sprite.getGameObject().isDeleted()) {
                    cleanUpSprites.add(sprite);
                }
            });
            cleanUpSprites.forEach(Sprite::remove);
            sprites.removeAll(cleanUpSprites);
            cleanUpSprites.clear();
        }

        private void render() {
            sprites.forEach(Sprite::render);
        }

        public void start() {
            gameLoop.start();
        }
    }