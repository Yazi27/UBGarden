package fr.ubx.poo.ugarden.go.decor;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.Takeable;
import fr.ubx.poo.ugarden.go.Walkable;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.personage.Gardener;
import fr.ubx.poo.ugarden.go.personage.Hornet;

public abstract class Decor extends GameObject implements Walkable, Takeable {

    private Bonus bonus;

    private  Hornet hornet;

    public Decor(Position position) {
        super(position);
    }

    public Decor(Position position, Bonus bonus) {
        super(position);
        this.bonus = bonus;
    }
    public Decor(Position position, Hornet hornet) {
        super(position);
        this.hornet=hornet;
    }

    public Bonus getBonus() {
        return bonus;
    }


    public void setBonus(Bonus bonus) {
        this.bonus = bonus;
    }

    @Override
    public boolean walkableBy(Gardener gardener) {
        return gardener.canWalkOn(this);
    }

    public boolean walkableBy(Hornet hornet) {
        return hornet.canWalkOn(this);
    }

    public Hornet getHornet() {
        return hornet;
    }

    public void setHornet(Hornet hornet) {
        this.hornet = hornet;
    }

    @Override
    public void update(long now) {
        super.update(now);
        if (bonus != null) bonus.update(now);
    }

}