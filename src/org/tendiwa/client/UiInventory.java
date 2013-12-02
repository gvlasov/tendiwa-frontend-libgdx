package org.tendiwa.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import org.tendiwa.events.RequestDrop;
import tendiwa.core.Item;
import tendiwa.core.RequestInitialTerrain;
import tendiwa.core.Tendiwa;

public class UiInventory extends Table {
public UiInventory() {
	setBackground(TendiwaUiStage.createImage(new Color(0.2f, 0.2f, 0.2f, 1.0f)).getDrawable());
	update();
}

public void update() {
	clearChildren();
	int childrenCount = 0;
	for (Item item : Tendiwa.getPlayer().getInventory()) {
		add(createItemIcon(item)).size(GameScreen.TILE_SIZE, GameScreen.TILE_SIZE);
		childrenCount++;
		if (childrenCount % 5 == 0) {
			row();
		}
	}
}

private Image createItemIcon(final Item item) {
	TextureAtlas.AtlasRegion region = AtlasItems.getInstance().findRegion(item.getType().getName());
	TextureRegion newRegion = new TextureAtlas.AtlasRegion(region);
	newRegion.flip(false, true);
	Image image = new Image(newRegion);
	image.addListener(new InputListener() {
		@Override
		public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
			if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
				Tendiwa.getServer().pushRequest(new RequestDrop(item));
			}
			return true;
		}
	});
	return image;
}

}
