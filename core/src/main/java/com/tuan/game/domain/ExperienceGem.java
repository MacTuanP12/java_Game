package com.tuan.game.domain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ExperienceGem {
    private final Vector2 position;
    private Texture texture;
    private float xpValue = 10;
    private boolean active = true;
    private float width = 16, height = 16;

    // ─── Animation ──────────────────────────────────────────────────────────
    private float animTimer;
    private float rotation = 0f;
    private static final float BOB_SPEED    = 4f;   // tốc độ nảy lên xuống
    private static final float BOB_AMOUNT   = 4f;   // pixels nảy
    private static final float ROTATE_SPEED = 90f;  // độ/giây
    private static final float SHIMMER_SPEED = 5f;  // tốc độ nhấp nháy

    /** Offset ngẫu nhiên để các gem không đồng pha nhau */
    private final float phaseOffset;

    public ExperienceGem(Texture texture, float x, float y) {
        this(texture, x, y, 10f);
    }

    /** Constructor cho phép tùy chỉnh xpValue (dùng cho boss drop) */
    public ExperienceGem(Texture texture, float x, float y, float xpValue) {
        this.texture     = texture;
        this.xpValue     = xpValue;
        this.position    = new Vector2(x, y);
        this.phaseOffset = MathUtils.random(0f, MathUtils.PI2);
        this.animTimer   = 0f;
    }

    public void update(float delta) {
        if (active) {
            animTimer += delta;
            rotation = (rotation + ROTATE_SPEED * delta) % 360f;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        // Bob: offset Y theo sin
        float bobY = MathUtils.sin(animTimer * BOB_SPEED + phaseOffset) * BOB_AMOUNT;

        // Shimmer: alpha nhấp nháy giữa 0.75 và 1.0
        float shimmer = 0.75f + 0.25f * (0.5f + 0.5f * MathUtils.sin(animTimer * SHIMMER_SPEED + phaseOffset));

        // Scale nhỏ theo sin để tạo cảm giác đập tim
        float scale = 0.9f + 0.1f * MathUtils.sin(animTimer * BOB_SPEED * 1.5f + phaseOffset);
        float drawW = width  * scale;
        float drawH = height * scale;
        float ox = position.x + (width  - drawW) / 2f;
        float oy = position.y + (height - drawH) / 2f + bobY;

        batch.setColor(0.4f, 1f, 0.4f, shimmer); // Xanh lá sáng
        batch.draw(texture,
                ox, oy,
                drawW / 2f, drawH / 2f, // origin ở giữa để xoay đúng
                drawW, drawH,
                1f, 1f, rotation,
                0, 0, texture.getWidth(), texture.getHeight(),
                false, false);
        batch.setColor(Color.WHITE);
    }

    public Vector2 getPosition()  { return position; }
    public float getXpValue()     { return xpValue; }
    public boolean isActive()     { return active; }
    public void collect()         { active = false; }

    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }

}
