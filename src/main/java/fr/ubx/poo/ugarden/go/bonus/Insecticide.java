package fr.ubx.poo.ugarden.go.bonus;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.personage.Gardener;
import fr.ubx.poo.ugarden.go.personage.Hornet;

public class Insecticide extends Bonus{
    public Insecticide(Position position, Decor decor) {
        super(position, decor);
    }
    public void takenBy(Hornet hornet){
        hornet.take(this);
        remove();
    }
    @Override
    public void takenBy(Gardener gardener) {
        gardener.take(this);
        remove();
    }
}
