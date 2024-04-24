/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Movable;
import fr.ubx.poo.ugarden.go.TakeVisitor;
import fr.ubx.poo.ugarden.go.WalkVisitor;
import fr.ubx.poo.ugarden.go.bonus.Insecticide;
import fr.ubx.poo.ugarden.go.bonus.*;
import fr.ubx.poo.ugarden.go.decor.*;
import fr.ubx.poo.ugarden.go.decor.ground.Carrots;
import fr.ubx.poo.ugarden.go.decor.ground.Land;

import javafx.scene.paint.Color;
import javafx.util.Duration;


public class Gardener extends GameObject implements Movable, TakeVisitor, WalkVisitor {

    private int energy;
    private int nbPapple;
    private int key;
    private int hedgehog;
    private Direction direction;
    private boolean moveRequested = false;

    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
        this.nbPapple=0;
        this.key=0;
        this.hedgehog=0;
    }

    public int getHedgehog() {
        return hedgehog;
    }

    public int getKey() {
        return key;
    }

    @Override
    public void take(Key key) {
// TODO
        this.key+=1;
        System.out.println("I am taking the key, I should do something ...");

    }// TODO
    public void take(Apple apple) {

        energy += game.configuration().energyBoost();
        if (energy>100){
            energy=100;
        }
        System.out.println("I am taking the Apple, i feel better");

    }
    public void take(Hedgehog hedgehog) {
        this.hedgehog+=1;
        System.out.println("oh hedgehog");
    }
    //TODO
    public void take(PoisonedApple PoisonnedAPpple) {
        this.nbPapple+=1;
        System.out.println("oops poisonnedApple ...");

    }

    public void take(Insecticide insecticide){
        System.out.println("de quoi tuer un frelon");
    }



    public int getEnergy() {
        return this.energy;
    }


    public void requestMove(Direction direction) {
        if (direction != this.direction) {
            this.direction = direction;
            setModified(true);
        }
        moveRequested = true;
    }
@Override
    public final boolean canMove(Direction direction) {
        Position nextPos = direction.nextPosition(getPosition());
        return (((-1< nextPos.x())&&(nextPos.x() < game.world().getGrid().width())) &&
                ((-1< nextPos.y())&& (nextPos.y() < game.world().getGrid().height()))&&
                (game.world().getGrid().get(nextPos).walkableBy(this)));
    }

    @Override
    public void doMove(Direction direction) {
        // Restart the timer
        Position nextPos = direction.nextPosition(getPosition());
        Decor next = game.world().getGrid().get(nextPos);
        Decor here=game.world().getGrid().get(getPosition());
        setPosition(nextPos);
        if (next != null)
            next.takenBy(this);
        if (here instanceof Land){
            this.energy-=2;
        }
        else if (here instanceof Carrots){
            this.energy-=3;
        }
        else {
            this.energy-=1;
        }


    }


    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                doMove(direction);

            }
        }
       /* else {        this.energy+=1; ca cree une regeneration illimite

        }*/
        moveRequested = false;
    }

    public void hurt(int damage) {
        this.energy-=damage;
    }

    public int getNbPapple() {
        return nbPapple;
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }

}
