package org.tendiwa.client.ui.widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.google.inject.Provider;

import java.io.FileNotFoundException;

public class UiAtlasProvider implements Provider<TextureAtlas> {

    @Override
    public TextureAtlas get() {

        String filePath = "pack/ui.atlas";
        FileHandle atlasFile = Gdx.files.internal(filePath);
        if (!atlasFile.exists()) {
            try {
                throw new FileNotFoundException(filePath + " does not exist");
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Could not load atlas");
            }
        }
        return new TextureAtlas(atlasFile, true);
    }
}
