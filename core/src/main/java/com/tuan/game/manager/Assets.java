package com.tuan.game.manager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;

public class Assets {

    public static final String PLAYER_IMG           = "player";
    public static final String BUG_IMG              = "bug";
    public static final String BUG_BOSS_IMG         = "bugBoss";
    public static final String PROJECTILE_IMG       = "projectile";
    public static final String PROJECTILE_TRAIL_IMG = "projectileTrail";
    public static final String EXP_GEM_IMG          = "expGem";
    public static final String BOSS_GEM_IMG         = "bossGem";
    public static final String FOG_IMG              = "fog";
    public static final String WALL_IMG             = "wall";
    public static final String TRAP_IMG             = "trap";

    private final Map<String, Texture> textures = new HashMap<>();

    public void load() {
        textures.put(PLAYER_IMG,           createPlayer(64, 64));
        textures.put(BUG_IMG,              createBug(32, 32, false));
        textures.put(BUG_BOSS_IMG,         createBug(96, 96, true));
        textures.put(PROJECTILE_IMG,       createProjectile(32, 12));
        textures.put(PROJECTILE_TRAIL_IMG, createProjectileTrail(24, 8));
        textures.put(EXP_GEM_IMG,          createGem(20, 20, false));
        textures.put(BOSS_GEM_IMG,         createGem(28, 28, true));
        textures.put(FOG_IMG,              createFog(512));
        textures.put(WALL_IMG,             createWall(64, 64));
        textures.put(TRAP_IMG,             createTrap(64, 64));
    }

    // ─── Player: hình tròn cyan với viền trắng ──────────────────────────────
    private Texture createPlayer(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0); p.fill();
        // Thân: hình tròn cyan
        p.setColor(0f, 0.85f, 1f, 1f);
        p.fillCircle(w/2, h/2, w/2 - 3);
        // Viền trắng
        p.setColor(1f, 1f, 1f, 0.85f);
        p.drawCircle(w/2, h/2, w/2 - 3);
        p.drawCircle(w/2, h/2, w/2 - 4);
        // "Mắt" để biết hướng
        p.setColor(0.1f, 0.1f, 0.3f, 1f);
        p.fillCircle(w/2 + 8, h/2 + 5, 6);
        p.fillCircle(w/2 + 8, h/2 - 5, 6);
        p.setColor(1f, 1f, 1f, 1f);
        p.fillCircle(w/2 + 10, h/2 + 5, 2);
        p.fillCircle(w/2 + 10, h/2 - 5, 2);
        Texture t = new Texture(p); p.dispose(); return t;
    }

    // ─── Bug: hình bọ 6 chân, màu đỏ/tím ──────────────────────────────────
    private Texture createBug(int w, int h, boolean isBoss) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0); p.fill();

        if (isBoss) {
            // Boss: hình oval tím lớn với aura
            // Aura glow (ngoài cùng, mờ)
            p.setColor(0.6f, 0f, 0.9f, 0.3f);
            p.fillCircle(w/2, h/2, w/2 - 1);
            // Thân chính
            p.setColor(0.75f, 0f, 0.85f, 1f);
            p.fillCircle(w/2, h/2, w/2 - 8);
            // Đốm sáng ở giữa
            p.setColor(1f, 0.5f, 1f, 0.7f);
            p.fillCircle(w/2 - 5, h/2 - 5, w/6);
            // Viền đen
            p.setColor(0.2f, 0f, 0.3f, 1f);
            p.drawCircle(w/2, h/2, w/2 - 8);
            // Mắt đỏ
            p.setColor(1f, 0f, 0f, 1f);
            p.fillCircle(w/2 + w/5, h/2 - h/8, w/10);
            p.fillCircle(w/2 - w/5, h/2 - h/8, w/10);
            p.setColor(1f, 1f, 0f, 1f);
            p.fillCircle(w/2 + w/5 + 2, h/2 - h/8 - 2, w/20);
            p.fillCircle(w/2 - w/5 + 2, h/2 - h/8 - 2, w/20);
        } else {
            // Bug thường: đỏ tươi, hình tròn với "chân"
            // Chân (vẽ trước để bị đè)
            p.setColor(0.7f, 0f, 0f, 1f);
            int cx = w/2, cy = h/2;
            // 3 cặp chân
            p.drawLine(cx - 4, cy - 2, cx - w/2, cy - 6);
            p.drawLine(cx - 4, cy,     cx - w/2, cy);
            p.drawLine(cx - 4, cy + 2, cx - w/2, cy + 6);
            p.drawLine(cx + 4, cy - 2, cx + w/2, cy - 6);
            p.drawLine(cx + 4, cy,     cx + w/2, cy);
            p.drawLine(cx + 4, cy + 2, cx + w/2, cy + 6);
            // Thân
            p.setColor(0.9f, 0.1f, 0.1f, 1f);
            p.fillCircle(cx, cy, w/2 - 4);
            // Highlight
            p.setColor(1f, 0.4f, 0.4f, 0.7f);
            p.fillCircle(cx - 3, cy - 3, w/5);
            // Viền
            p.setColor(0.4f, 0f, 0f, 1f);
            p.drawCircle(cx, cy, w/2 - 4);
            // Mắt
            p.setColor(0f, 0f, 0f, 1f);
            p.fillCircle(cx + 5, cy - 3, 3);
            p.fillCircle(cx - 5, cy - 3, 3);
            p.setColor(1f, 0.8f, 0f, 1f);
            p.fillCircle(cx + 6, cy - 4, 1);
            p.fillCircle(cx - 4, cy - 4, 1);
        }
        Texture t = new Texture(p); p.dispose(); return t;
    }

    // ─── Projectile: hình capsule vàng sáng ─────────────────────────────────
    private Texture createProjectile(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0); p.fill();
        int r = h / 2;
        // Thân oval
        p.setColor(1f, 0.95f, 0.2f, 1f);
        // Vẽ hình elipse bằng các hình tròn nhỏ
        for (int x = r; x <= w - r; x++) {
            for (int y = 0; y < h; y++) {
                float dy = y - h / 2f;
                if (dy * dy <= (float)(r * r)) {
                    p.drawPixel(x, y, Color.rgba8888(1f, 0.95f, 0.2f, 1f));
                }
            }
        }
        p.fillCircle(r, h/2, r);
        p.fillCircle(w - r, h/2, r);
        // Highlight sáng ở giữa-trên
        p.setColor(1f, 1f, 0.85f, 0.9f);
        p.fillCircle(w/2, h/2 - 1, r - 2);
        // Nhân trắng ở giữa
        p.setColor(1f, 1f, 1f, 0.7f);
        p.fillCircle(w/2, h/2, r / 2);
        Texture t = new Texture(p); p.dispose(); return t;
    }

    // ─── Projectile Trail: nhỏ hơn, mờ hơn ─────────────────────────────────
    private Texture createProjectileTrail(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0); p.fill();
        int r = h / 2;
        p.setColor(1f, 0.7f, 0.1f, 0.8f);
        p.fillCircle(r, h/2, r);
        p.fillCircle(w - r, h/2, r);
        for (int x = r; x <= w - r; x++) {
            for (int y = 0; y < h; y++) {
                float dy = y - h / 2f;
                if (dy * dy <= (float)(r * r)) {
                    p.drawPixel(x, y, Color.rgba8888(1f, 0.7f, 0.1f, 0.8f));
                }
            }
        }
        Texture t = new Texture(p); p.dispose(); return t;
    }

    // ─── Gem: hình thoi — xanh lá (thường) hoặc vàng-cam (boss) ────────────
    private Texture createGem(int w, int h, boolean isBoss) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0, 0, 0, 0); p.fill();
        int cx = w/2, cy = h/2;
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                float dx = Math.abs(x - cx) / (float)(w/2);
                float dy = Math.abs(y - cy) / (float)(h/2);
                if (dx + dy <= 1.0f) {
                    float dist   = dx + dy;
                    float bright = 1f - dist * 0.35f;
                    int pixel;
                    if (isBoss) {
                        // Vàng-cam rực: R cao, G trung bình, B thấp
                        pixel = Color.rgba8888(bright, bright * 0.75f, 0.05f, 1f);
                    } else {
                        // Xanh lá sáng
                        pixel = Color.rgba8888(0.1f * bright, bright, 0.3f * bright, 1f);
                    }
                    p.drawPixel(x, y, pixel);
                }
            }
        }
        // Highlight trắng nhỏ
        p.setColor(1f, 1f, 1f, 0.85f);
        p.fillCircle(cx - 2, cy - 2, isBoss ? 3 : 2);
        // Viền ngoài đậm hơn
        if (isBoss) {
            p.setColor(1f, 0.5f, 0f, 0.6f);
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    float dx = Math.abs(x - cx) / (float)(w/2);
                    float dy = Math.abs(y - cy) / (float)(h/2);
                    float s  = dx + dy;
                    if (s > 0.88f && s <= 1.0f) p.drawPixel(x, y, Color.rgba8888(1f, 0.3f, 0f, 0.9f));
                }
            }
        }
        Texture t = new Texture(p); p.dispose(); return t;
    }

    // ─── Wall: gạch nâu có texture ──────────────────────────────────────────
    private Texture createWall(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        // Nền nâu đậm
        p.setColor(0.35f, 0.25f, 0.15f, 1f); p.fill();
        // Đường kẻ ngang (gạch)
        p.setColor(0.2f, 0.14f, 0.08f, 1f);
        p.drawLine(0, h/2, w, h/2);
        // Đường kẻ dọc xen kẽ
        p.drawLine(w/4, 0, w/4, h/2);
        p.drawLine(3*w/4, h/2, 3*w/4, h);
        // Highlight nhẹ góc trên-trái
        p.setColor(0.55f, 0.42f, 0.28f, 0.5f);
        p.drawLine(1, 1, w-1, 1);
        p.drawLine(1, 1, 1, h-1);
        Texture t = new Texture(p); p.dispose(); return t;
    }

    // ─── Trap: màu cam tối với sọc cảnh báo ─────────────────────────────────
    private Texture createTrap(int w, int h) {
        Pixmap p = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        p.setColor(0.6f, 0.2f, 0f, 1f); p.fill(); // Nền đỏ tối
        // Sọc chéo cảnh báo (vàng-đen xen kẽ)
        int stripeW = 10;
        for (int x = -h; x < w + h; x += stripeW * 2) {
            p.setColor(0.9f, 0.7f, 0f, 0.6f);
            for (int s = 0; s < stripeW; s++) {
                int sx = x + s;
                p.drawLine(sx, 0, sx + h, h);
            }
        }
        // Viền ngoài đỏ đậm
        p.setColor(0.9f, 0.1f, 0.0f, 0.8f);
        p.drawRectangle(0, 0, w, h);
        p.drawRectangle(1, 1, w-2, h-2);
        // Dấu "!" cảnh báo
        p.setColor(1f, 1f, 0f, 1f);
        p.fillRectangle(w/2 - 2, h/4, 4, h/2 - 6);
        p.fillCircle(w/2, 3*h/4, 3);
        Texture t = new Texture(p); p.dispose(); return t;
    }

    // ─── Fog gradient ────────────────────────────────────────────────────────
    private Texture createFog(int size) {
        Pixmap pixmap = new Pixmap(size, size, Pixmap.Format.RGBA8888);
        float cx = size / 2f, cy = size / 2f, maxR = size / 2f;
        float fadeStart = 0.65f;
        for (int px = 0; px < size; px++) {
            for (int py = 0; py < size; py++) {
                float dist  = (float) Math.sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy));
                float ratio = dist / maxR;
                float alpha;
                if (ratio <= fadeStart) {
                    alpha = 0f;
                } else {
                    float t = (ratio - fadeStart) / (1f - fadeStart);
                    alpha = t * t * (3f - 2f * t);
                    alpha *= 0.88f;
                }
                pixmap.setColor(0f, 0f, 0f, alpha);
                pixmap.drawPixel(px, py);
            }
        }
        Texture texture = new Texture(pixmap); pixmap.dispose(); return texture;
    }

    public Texture getTexture(String name) { return textures.get(name); }

    public void dispose() {
        for (Texture t : textures.values()) t.dispose();
        textures.clear();
    }
}
