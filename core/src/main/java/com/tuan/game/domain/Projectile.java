package com.tuan.game.domain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tuan.game.system.GameConfig;

public class Projectile {
    private final Vector2 position;
    private final Vector2 velocity;
    private static final float SPEED = 550f;
    private Texture texture;
    private Texture trailTexture; // texture trail (nhỏ hơn, mờ hơn)
    private boolean active = true;
    private final float width = 18, height = 10; // dạng viên đạn dài
    private float rotation; // góc xoay theo hướng bay (độ)

    // Trail: lưu các vị trí cũ để vẽ vệt
    private static final int TRAIL_LEN = 8;
    private final float[] trailX = new float[TRAIL_LEN];
    private final float[] trailY = new float[TRAIL_LEN];
    private int trailHead = 0;
    private float trailTimer = 0f;
    private static final float TRAIL_INTERVAL = 0.018f;

    // Hiệu ứng nổ khi va chạm
    private boolean exploding = false;
    private float explodeTimer = 0f;
    private static final float EXPLODE_DURATION = 0.18f;
    private float explodeRadius = 0f;

    public Projectile(Texture texture, Texture trailTexture, float x, float y, Vector2 direction) {
        this.texture      = texture;
        this.trailTexture = trailTexture;
        this.position     = new Vector2(x, y);
        Vector2 dir = new Vector2(direction).nor();
        this.velocity = dir.scl(SPEED);
        // Tính góc xoay: atan2 → degrees
        this.rotation = MathUtils.atan2(dir.y, dir.x) * MathUtils.radiansToDegrees;
        // Khởi tạo trail tại vị trí xuất phát
        for (int i = 0; i < TRAIL_LEN; i++) { trailX[i] = x; trailY[i] = y; }
    }

    /** Constructor tương thích cũ (không có trailTexture) */
    public Projectile(Texture texture, float x, float y, Vector2 direction) {
        this(texture, texture, x, y, direction);
    }

    public void update(float delta) {
        if (exploding) {
            explodeTimer += delta;
            explodeRadius = (explodeTimer / EXPLODE_DURATION) * 40f;
            if (explodeTimer >= EXPLODE_DURATION) active = false;
            return;
        }
        position.add(velocity.x * delta, velocity.y * delta);

        // Cập nhật trail
        trailTimer += delta;
        if (trailTimer >= TRAIL_INTERVAL) {
            trailTimer = 0f;
            trailX[trailHead] = position.x;
            trailY[trailHead] = position.y;
            trailHead = (trailHead + 1) % TRAIL_LEN;
        }

        // Vô hiệu hóa khi ra khỏi world bounds
        if (position.x < -50 || position.x > GameConfig.WORLD_WIDTH + 50
         || position.y < -50 || position.y > GameConfig.WORLD_HEIGHT + 50) {
            active = false;
        }
    }

    public void draw(SpriteBatch batch) {
        if (!active) return;

        if (exploding) {
            // Hiệu ứng nổ: vẽ vòng tròn mờ dần bằng cách scale texture
            float alpha = 1f - (explodeTimer / EXPLODE_DURATION);
            batch.setColor(1f, 0.6f, 0.1f, alpha);
            float sz = explodeRadius * 2f;
            batch.draw(trailTexture,
                    position.x - explodeRadius, position.y - explodeRadius, sz, sz);
            batch.setColor(Color.WHITE);
            return;
        }

        // Vẽ TRAIL (từ già nhất → mới nhất, mờ dần)
        for (int i = 0; i < TRAIL_LEN; i++) {
            int idx = (trailHead + i) % TRAIL_LEN; // từ già nhất
            float age = (float)(TRAIL_LEN - i) / TRAIL_LEN; // 1=già, 0=mới
            float alpha  = (1f - age) * 0.55f;  // mờ ở đuôi, đậm gần đầu
            float scale  = 0.35f + (1f - age) * 0.5f;
            float tw = width * scale, th = height * scale;
            batch.setColor(1f, 0.85f, 0.2f, alpha);
            batch.draw(trailTexture,
                    trailX[idx] - tw / 2f, trailY[idx] - th / 2f,
                    tw / 2f, th / 2f, tw, th,
                    1f, 1f, rotation, 0, 0,
                    (int) trailTexture.getWidth(), (int) trailTexture.getHeight(),
                    false, false);
        }

        // Vẽ ĐẠN CHÍNH (xoay theo hướng bay)
        batch.setColor(1f, 1f, 0.5f, 1f); // Vàng sáng
        batch.draw(texture,
                position.x - width / 2f, position.y - height / 2f,
                width / 2f, height / 2f, // origin ở giữa để xoay đúng
                width, height,
                1f, 1f, rotation,
                0, 0, texture.getWidth(), texture.getHeight(),
                false, false);
        batch.setColor(Color.WHITE);
    }

    public Rectangle getBounds() {
        return new Rectangle(position.x - width / 2f, position.y - height / 2f, width, height);
    }

    public boolean isActive()   { return active; }

    /** Kích hoạt hiệu ứng nổ thay vì biến mất ngay */
    public void setInactive() {
        if (!exploding) {
            exploding = true;
            explodeTimer = 0f;
        }
    }

    public Vector2 getPosition() { return position; }
}
