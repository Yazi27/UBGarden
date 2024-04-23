package fr.ubx.poo.ugarden.go.decor.ground;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.personage.Gardener;

public class Land extends Ground{
    public Land(Position position) {
        super(position);
    }
    public void takenBy(Gardener gardener) {
        Bonus bonus = getBonus();
        if (bonus != null) {
            bonus.takenBy(gardener);
        }
    }

    @Override
    public int energyConsumptionWalk() {
        return 2;
    }
}
