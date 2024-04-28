/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.personage;

import fr.ubx.poo.ugarden.engine.Timer;
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
    private int diseaseLevel;
    private int key;
    private int hedgehog;
    private Direction direction;
    private boolean moveRequested = false;
    private boolean takenAppleRequested = false;
    private Timer recoveryTimer;
    private Timer diseaseTimer;
    private int insecticide;
    public Gardener(Game game, Position position) {

        super(game, position);
        this.direction = Direction.DOWN;
        this.energy = game.configuration().gardenerEnergy();
        this.diseaseLevel=1;
        this.key=0;
        this.hedgehog=0;
        this.recoveryTimer = new Timer(game.configuration().energyRecoverDuration());
        this.diseaseTimer = new Timer (game.configuration().diseaseDuration());
        this.insecticide=0;
    }

    public void startRecoveryTimer(long now) {
        recoveryTimer.start(now);
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
    public void take(PoisonedApple poisonedApple) {
        System.out.println(" Picked up a poisoned apple ...");

        // Start apple timer on update
        takenAppleRequested = true;

        // Increment the disease level
        this.diseaseLevel++;
    }

    public void take(Insecticide insecticide){
        System.out.println("de quoi tuer un frelon");
        this.insecticide+=1;
    }

    public int getInsecticide() {
        return insecticide;
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
    return ((-1< nextPos.x())&&(nextPos.x() < game.world().getGrid().width())
        ) &&
        ((-1< nextPos.y())&& (nextPos.y() < game.world().getGrid().height())
        ) &&
        (game.world().getGrid().get(nextPos).walkableBy(this));
    }

    @Override
    public void doMove(Direction direction) {


        Position nextPos = direction.nextPosition(getPosition());

        Decor next = game.world().getGrid().get(nextPos);
        Decor here = game.world().getGrid().get(getPosition());

        setPosition(nextPos);

        if (next != null)
            next.takenBy(this);

        this.energy -= here.energyConsumptionWalk() * this.diseaseLevel;

    }
    private void recoverEnergy() {
        if (energy < game.configuration().gardenerEnergy()) {
            energy++;
        }
    }

    public void update(long now) {
        if (moveRequested) {
            if (canMove(direction)) {
                // Restart timer
                this.recoveryTimer.start(now);
                doMove(direction);

            }

        } else if (takenAppleRequested) {
            this.diseaseTimer.start(now);
            takenAppleRequested = false;

        } else {
            recoveryTimer.update(now); // Update the recovery timer
            diseaseTimer.update(now);

            if (!recoveryTimer.isRunning()) {
                // If the recovery timer has finished, recover energy
                recoverEnergy();
                recoveryTimer.start(now); // Restart the recovery timer
            }

            // Check if the poisoned apple timer has finished
            if (!diseaseTimer.isRunning() && diseaseLevel > 1) {
                // Decrement the disease level
                this.diseaseLevel--;

                // Restart the poisoned apple timer if there are still poisoned apples
                if (diseaseLevel > 1) {
                    diseaseTimer.start(now);
                }
            }
        }
        moveRequested = false;

    }

    public void hurt(int damage) {
        if(this.insecticide>0){
            this.insecticide-=1;
        }
        else
            this.energy-=damage;
    }

    public int getDiseaseLevel() {
        return diseaseLevel;
    }

    public void hurt() {
        hurt(1);
    }

    public Direction getDirection() {
        return direction;
    }

}
