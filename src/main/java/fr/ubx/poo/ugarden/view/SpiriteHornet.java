package fr.ubx.poo.ugarden.view;

import fr.ubx.poo.ugarden.game.Direction;
import fr.ubx.poo.ugarden.go.GameObject;
import fr.ubx.poo.ugarden.go.personage.Gardener;
import fr.ubx.poo.ugarden.go.personage.Hornet;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

public class SpiriteHornet extends Sprite{
    public SpiriteHornet(Pane layer, Hornet hornet) {
        super(layer, null, hornet);
        updateImage();
    }
    @Override
    public void updateImage() {
        Gardener gardener = (Gardener) getGameObject();
        Image image = getImage(gardener.getDirection());
        setImage(image);
    }

    public Image getImage(Direction direction) {
        return ImageResourceFactory.getInstance().getGardener(direction);
    }
}
