package fr.ubx.poo.ugarden.go;


import fr.ubx.poo.ugarden.go.personage.Gardener;
import fr.ubx.poo.ugarden.go.personage.Hornet;

public interface Walkable {
    boolean walkableBy(Gardener gardener);
    default int energyConsumptionWalk() {
        return 0;
    }
}
