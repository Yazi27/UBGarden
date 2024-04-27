/*
 * Copyright (c) 2020. Laurent Réveillère
 */

package fr.ubx.poo.ugarden.go;


import fr.ubx.poo.ugarden.go.personage.Gardener;
import fr.ubx.poo.ugarden.go.personage.Hornet;

public interface Takeable {
    default void takenBy(Gardener gardener) {
    }
    default void takenBy (Hornet hornet){}
}
