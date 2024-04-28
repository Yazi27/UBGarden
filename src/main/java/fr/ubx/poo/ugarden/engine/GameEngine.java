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
        private Timer nestTimer;
        private Position nestPosition;
        private static final Random randomGenerator = new Random();



        public GameEngine(Game game, final Stage stage) {
            this.stage = stage;
            this.game = game;
            this.gardener = game.getGardener();
            this.nestTimer =new Timer(5);
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

            // Create sprites
            int currentLevel = game.world().currentLevel();

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

                        // If we did, then save the Nest Position
                        this.nestPosition = bonus.getPosition();

                        System.out.println("Found Nest position at");
                        System.out.println(this.nestPosition);
                    }
                }
            }

            sprites.add(new SpriteGardener(layer, gardener));
        }

        void buildAndSetGameLoop() {
            gameLoop = new AnimationTimer() {
                public void handle(long now) {
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

                    // Gardener logic
                }
            };

        }


        private void checkLevel() {
            if (game.isSwitchLevelRequested()) {
                // Find the new level to switch to
                // clear all sprites
                // change the current level
                // Find the position of the door to reach
                // Set the position of the gardener
                //stage.close();
                //initialize();
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

            // Check if we should spawn an insecticide and a hornet
            if (!nestTimer.isRunning()) {
                // Spawn Hornet at nest Location
                Hornet hornet = new Hornet(game, nestPosition);
                sprites.add(SpriteFactory.create(layer, hornet));
                hornets.add(hornet);

                // Spawn insecticide at random location

                // First we prepare a random location
                // Create a random number from 0 to grid.width
                int x = randomGenerator.nextInt(game.world().getGrid().width());
                // Create a random number from 0 to grid.height
                int y = randomGenerator.nextInt(game.world().getGrid().height());

                Position position = new Position(game.world().currentLevel(), x, y);

                // Check if its land
                while (!(game.world().getGrid().get(position) instanceof Grass)) {
                    // If its not land, then we need to find a new random location
                    x = randomGenerator.nextInt(game.world().getGrid().width());
                    y = randomGenerator.nextInt(game.world().getGrid().height());
                    position = new Position(game.world().currentLevel(), x, y);
                }

                Insecticide insecticide = new Insecticide(position, game.world().getGrid().get(position));
                // Place in world
                sprites.add(SpriteFactory.create(layer, insecticide));

                game.world().getGrid().set(position, insecticide.getDecor());

                insecticide.setModified(true);
                System.out.println("Insecticide spawned at");
                System.out.println(position);
                nestTimer.start(now);
            }


            if(gardener.getHedgehog()==1){
                win();
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