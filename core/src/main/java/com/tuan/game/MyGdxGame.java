package com.tuan.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.tuan.game.manager.Assets;
import com.tuan.game.screen.PlayScreen;

public class MyGdxGame extends Game {
    public SpriteBatch batch;
    public Assets assets;

    @Override
    public void create() {
        batch = new SpriteBatch();
        assets = new Assets();
        assets.load();

        //Chuyênnr sang màn hình chơi game
        setScreen(new PlayScreen(this));
    }
    @Override
    public void render() {
        super.render();
    }
    @Override
    public void dispose(){
        batch.dispose();
        assets.dispose();
    }
}
