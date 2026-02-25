package com.tuan.game.screen;


import com.badlogic.gdx.Screen;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.tuan.game.MyGdxGame;
import com.tuan.game.domain.Bug;
import com.tuan.game.domain.Player;
import com.tuan.game.manager.Assets;

public class PlayScreen implements Screen {
    private MyGdxGame game;
    private Player player;
    private Array<Bug> bugs; // Danh sách kẻ địch
    private float spawnTimer = 0; // Bộ đếm thời gian sinh quái

    public PlayScreen(MyGdxGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        //Khởi tạo nhân vật
        player = new Player(game.assets.getTexture(com.tuan.game.manager.Assets.PLAYER_IMG), 640, 360);
        bugs = new Array<>();
    }

    @Override
    public void render(float delta ) {
        // Cập nhật logic game

        player.update(delta);
        spawnTimer += delta;
        if (spawnTimer > 2f) {
            spawnBug();
            spawnTimer = 0;
        }
        for(Bug bug: bugs) {
            bug.update(delta, player.getPosition());
        }
// Vẽ game
        ScreenUtils.clear(0.1f,0.1f,0.2f,1);
        game.batch.begin();
        player.draw(game.batch);
        for (Bug bug : bugs) {
            bug.draw(game.batch);
        }
        game.batch.end();

    }
    private void spawnBug() {
        // Sinh quái ở một vị trí ngẫu nhiên ngoài màn hình hoặc ở góc
        float x = MathUtils.random(0, 1280);
        float y = MathUtils.random(0, 720);
        bugs.add(new Bug(game.assets.getTexture(Assets.BUG_IMG), x, y));
    }

    @Override
    public void resize(int wight, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
