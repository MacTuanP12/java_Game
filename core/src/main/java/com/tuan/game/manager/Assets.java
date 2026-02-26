package com.tuan.game.manager;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    private final AssetManager assetManager = new AssetManager();

    public static final String PLAYER_IMG = "images/player.png";
    public static final String BUG_IMG = "images/bug.png";
    public static final String PROJECTILE_IMG = "images/projectile.png";
    public static final String EXP_GEM_IMG = "images/experienceGem.png";

    public void load() {
        assetManager.load(PLAYER_IMG, com.badlogic.gdx.graphics.Texture.class);
        assetManager.load(BUG_IMG, com.badlogic.gdx.graphics.Texture.class);
        assetManager.load(PROJECTILE_IMG, com.badlogic.gdx.graphics.Texture.class);
        assetManager.load(EXP_GEM_IMG, com.badlogic.gdx.graphics.Texture.class);
        assetManager.finishLoading();
    }
    public Texture getTexture(String name) {
        return assetManager.get(name, Texture.class);
    }
    public void dispose() {
        assetManager.dispose();
    }



}
