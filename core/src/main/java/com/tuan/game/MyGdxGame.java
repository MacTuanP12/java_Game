package com.tuan.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.tuan.game.manager.Assets;
import com.tuan.game.screen.PlayScreen;

public class MyGdxGame extends Game {
    public SpriteBatch batch;
    public Assets assets;
    public BitmapFont font;
    public ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        font = new BitmapFont(); // Sử dụng font mặc định của LibGDX
        assets = new Assets();
        shapeRenderer = new ShapeRenderer();
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
