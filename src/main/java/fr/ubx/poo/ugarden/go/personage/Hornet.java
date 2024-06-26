package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.bonus.Insecticide;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.engine.Timer;

public class Hornet extends GameObject implements Movable, TakeVisitor, WalkVisitor {
    private Direction direction;
    private boolean moveRequested = false;
    private Timer moveTimer;
    private int insecticide;

    public Hornet(Game game, Position position) {
        super(game, position);
        this.direction=Direction.LEFT;
        this.moveTimer = new Timer(game.configuration().hornetMoveFrequency());
        this.insecticide=0;
    }

    public int getInsecticide() {
        return insecticide;
    }

    public Direction getDirection(){
        return direction;
    }
    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());

        // Check if the next position is within the grid bounds
        if (nextPos.x() < 0 || nextPos.x() >= game.world().getGrid().width() ||
                nextPos.y() < 0 || nextPos.y() >= game.world().getGrid().height()) {
            return false;
        }

        // Check if the decor at the next position is walkable by the hornet
        Decor decor = game.world().getGrid().get(nextPos);
        if (decor == null || !decor.walkableBy(this)) {
            return false;
        }

        // Check if the next position is occupied by another hornet
        if (game.getEngine().isPositionOccupiedByHornet(nextPos)) {
            return false;
        }

        return true;
    }

    @Override
    public void doMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        setPosition(nextPos);
        Decor here = game.world().getGrid().get(getPosition());
        if (here != null)
            here.takenBy(this);

        moveRequested = false;
    }

    public void update(long now) {
        moveTimer.update(now);
        // Si on veux bouger
        if (moveRequested) {
            if (canMove(direction)) {

                doMove(direction);

                // Et on restart timer
                moveTimer.start(now);
            } else {
                moveRequested = false;
            }
            // Si on pas bougé
        } else {
            if (!moveTimer.isRunning()) {
                requestMove(Direction.random());
            }
        }

    }
    public void take(Insecticide insecticide){
        this.insecticide+=1;
    }
}
