package fr.ubx.poo.ugarden.go.bonus;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.personage.Gardener;

public class Hedgehog extends Bonus{
    public Hedgehog(Position position, Decor decor) {
        super(position, decor);
    }
    @Override
    public void takenBy(Gardener gardener) {gardener.take(this);
        remove();
    }
}
