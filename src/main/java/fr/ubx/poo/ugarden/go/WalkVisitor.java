package fr.ubx.poo.ugarden.go;

import fr.ubx.poo.ugarden.go.decor.Decor;
import fr.ubx.poo.ugarden.go.decor.Flowers;
import fr.ubx.poo.ugarden.go.bonus.Nest;
import fr.ubx.poo.ugarden.go.decor.Tree;

public interface WalkVisitor {
    default boolean canWalkOn(Decor decor) {
        return true;
    }

    default boolean canWalkOn(Tree tree) {
        /*
        *ca devra etre un truc du genre si c'est le frelon alors sinon alors
        */
        return false;
    }
    default boolean canWalkOn(Flowers flowers) {
        return false;
    }
    default boolean canWalkOn(Nest nest) {
        return false;
    }
    // TODO
}
