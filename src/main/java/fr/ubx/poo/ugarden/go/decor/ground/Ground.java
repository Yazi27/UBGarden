/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go.decor.ground;

import fr.ubx.poo.ugarden.game.Position;
import fr.ubx.poo.ugarden.go.bonus.Bonus;
import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.personage.Gardener;

public abstract class Ground extends Decor {
    @Override
    public void takenBy(Gardener gardener) {
        Bonus bonus = getBonus();
        if (bonus != null) {
            bonus.takenBy(gardener);
        }
    }

    public Ground(Position position) {
        super(position);
    }

}
