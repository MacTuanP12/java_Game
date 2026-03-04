package com.tuan.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tuan.game.MyGdxGame;


public class GameOverScreen implements Screen {
    private final MyGdxGame game;
    private Stage stage;
    private final int finalLevel;

    public GameOverScreen(MyGdxGame game, int level) {
        this.game       = game;
        this.finalLevel = level;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport(), game.batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Tiêu đề
        Label.LabelStyle bigStyle = new Label.LabelStyle(game.fontLarge, Color.RED);
        Label gameOverLabel = new Label("GAME OVER", bigStyle);

        // Kết quả
        Label.LabelStyle normalStyle = new Label.LabelStyle(game.font, Color.WHITE);
        Label levelLabel = new Label("Ban da dat cap do: " + finalLevel, normalStyle);

        // Nút
        TextButton.TextButtonStyle btnStyle = new TextButton.TextButtonStyle();
        btnStyle.font         = game.fontLarge;
        btnStyle.fontColor    = Color.WHITE;
        btnStyle.overFontColor = Color.YELLOW;

        TextButton btnRestart = new TextButton("↺  Choi lai", btnStyle);
        TextButton btnQuit    = new TextButton("✕  Thoat", btnStyle);

        btnRestart.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });
        btnQuit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });

        table.add(gameOverLabel).padBottom(30).row();
        table.add(levelLabel).padBottom(50).row();
        table.add(btnRestart).padBottom(20).row();
        table.add(btnQuit);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.05f, 0f, 0f, 1);
        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int w, int h) { stage.getViewport().update(w, h, true); }
    @Override public void dispose() { stage.dispose(); }
    @Override public void pause()   {}
    @Override public void resume()  {}
    @Override public void hide()    {}
}
