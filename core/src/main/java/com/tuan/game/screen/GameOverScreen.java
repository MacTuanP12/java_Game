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
    private int finalLevel;


    public GameOverScreen(MyGdxGame game, int level) {
        this.game = game;
        this.finalLevel = level;

    }

    @Override
    public  void show() {
        stage = new Stage(new ScreenViewport(), game.batch);
        //cho phép stage xử lý input
        Gdx.input.setInputProcessor(stage);
        // tạo table để sắp xếp thành phần UI
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // game over
        Label.LabelStyle labelStyle = new Label.LabelStyle(game.font, com.badlogic.gdx.graphics.Color.WHITE);
        Label gameOverLabel = new Label("Game Over", labelStyle);
        gameOverLabel.setFontScale(3); // Tăng kích thước font

//thông tin kết quả
        Label levelLabel = new Label("You reached level: " + finalLevel, labelStyle);
        levelLabel.setFontScale(2); // Tăng kích thước font

        // nút restart
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = game.font;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.overFontColor = Color.YELLOW; // Đổi màu khi di chuột qua

        TextButton restartButton = new TextButton("RESTART GAME", buttonStyle);

        // sự kiện khi nhấn nút
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Chuyển lại về PlayScreen để chơi lại từ đầu
                game.setScreen(new PlayScreen(game));
            }
        });
        table.add(gameOverLabel).padBottom(20).row();// cách nhau giữa các thành phần
        table.add(levelLabel).padBottom(50).row();
        table.add(restartButton);
    }
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0,0,0,1);
        stage.act(delta);
        stage.draw();
    }
    @Override
    public void dispose() {
        stage.dispose();
    }
    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}


}
