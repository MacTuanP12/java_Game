package com.tuan.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.tuan.game.manager.Assets;
import com.tuan.game.screen.PlayScreen;

public class MyGdxGame extends Game {
    public SpriteBatch batch;
    public Assets assets;
    public BitmapFont font;       // cỡ 18 – HUD thông thường
    public BitmapFont fontLarge;  // cỡ 32 – tiêu đề, thông báo lớn
    public BitmapFont fontSmall;  // cỡ 14 – chú thích nhỏ
    public ShapeRenderer shapeRenderer;

    /** Bộ ký tự tiếng Việt đầy đủ + ASCII */
    private static final String VI_CHARS = FreeTypeFontGenerator.DEFAULT_CHARS
        + "àáâãèéêìíòóôõùúýăđơưạảấầẩẫậắằẳẵặẹẻẽếềểễệỉịọỏốồổỗộớờởỡợụủứừửữựỳỷỹỵ"
        + "ÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚÝĂĐƠƯẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼẾỀỂỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪỬỮỰỲỶỸỴ"
        + "\u2013\u2014\u2026\u201C\u201D\u2018\u2019\u2022";

    @Override
    public void create() {
        batch        = new SpriteBatch();
        assets       = new Assets();
        shapeRenderer = new ShapeRenderer();
        assets.load();

        // ── Tạo font tiếng Việt với FreeType ──────────────────────────────
        FreeTypeFontGenerator gen = new FreeTypeFontGenerator(Gdx.files.internal("arial.ttf"));

        FreeTypeFontParameter p18 = new FreeTypeFontParameter();
        p18.size      = 18;
        p18.characters = VI_CHARS;
        p18.mono      = false;
        font = gen.generateFont(p18);

        FreeTypeFontParameter p32 = new FreeTypeFontParameter();
        p32.size       = 32;
        p32.characters = VI_CHARS;
        fontLarge = gen.generateFont(p32);

        FreeTypeFontParameter p14 = new FreeTypeFontParameter();
        p14.size       = 14;
        p14.characters = VI_CHARS;
        fontSmall = gen.generateFont(p14);

        gen.dispose(); // generator có thể dispose sau khi đã generate xong

        setScreen(new PlayScreen(this));
    }

    @Override
    public void render() { super.render(); }

    @Override
    public void dispose() {
        batch.dispose();
        assets.dispose();
        font.dispose();
        fontLarge.dispose();
        fontSmall.dispose();
        shapeRenderer.dispose();
    }
}
