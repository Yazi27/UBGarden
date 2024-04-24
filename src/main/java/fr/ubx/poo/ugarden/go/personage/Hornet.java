package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.decor.Decor;


public class Hornet extends GameObject implements Movable, TakeVisitor, WalkVisitor {
    private Direction direction;
    private boolean moveRequested = false;

    public Hornet(Game game, Position position) {
        super(game, position);
        this.direction=Direction.LEFT;
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
        return (((-1< nextPos.x())&&(nextPos.x() < game.world().getGrid().width())) &&
                ((-1< nextPos.y())&& (nextPos.y() < game.world().getGrid().height()))&&
                (game.world().getGrid().get(nextPos).walkableBy(this)));
    }

    @Override
    public void doMove(Direction direction) {
        this.direction=direction;
        Decor next=game.world().getGrid().get(getPosition());
        Position nextPos= next.getPosition();
        setPosition(nextPos);
    }

    public void update(long now) {
        direction=Direction.random();
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);

            }
        }
        moveRequested = false;
    }
}
