package com.tuan.game.domain;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.tuan.game.system.GameConfig;

public class Bug {
    private Vector2 position;
    private float speed;
    private Texture texture;
    private float width, height;
    private float maxHealth;
    private float currentHealth;

    /** Bug tàng hình chỉ hiện khi Player cách gần hơn stealthRadius */
    private boolean stealth = false;
    private float stealthRadius = 150f;
    private boolean visibleToPlayer = true;

    private boolean isBoss = false;

    // ─── AI: tầm phát hiện & trạng thái aggro ───────────────────────────────
    /** Khoảng cách để bug "thấy" player và bắt đầu đuổi */
    private final float detectionRadius;
    /** true = đang đuổi player; false = đứng yên chờ */
    private boolean isAggro = false;

    // ─── Animation fields ────────────────────────────────────────────────────
    /** Flash đỏ khi bị đánh */
    private float hitFlashTimer = 0f;
    private static final float HIT_FLASH_DURATION = 0.15f;

    /** Fade alpha cho bug tàng hình (hiện/ẩn mượt mà) */
    private float currentAlpha = 1f;
    private static final float FADE_SPEED = 3f; // alpha thay đổi/giây

    /** Sway: lắc trái/phải nhẹ khi di chuyển */
    private float swayTimer = 0f;
    private static final float SWAY_SPEED = 8f;
    private static final float SWAY_AMOUNT = 3f; // pixels

    /** Boss pulse: phình co để tạo cảm giác thở */
    private float pulseTimer = 0f;
    private static final float PULSE_SPEED = 2.5f;

    /** Spawn animation: scale từ 0 → 1 khi mới xuất hiện */
    private float spawnScale = 0f;
    private static final float SPAWN_SPEED = 4f;

    // ─── Boss ranged attack ──────────────────────────────────────────────────
    /** Tầm tấn công từ xa (chỉ > 0 với boss) */
    private final float attackRange;
    /** Giây giữa 2 đòn tấn công từ xa */
    private final float attackInterval;
    /** Đếm ngược đến lần tấn công tiếp theo */
    private float attackTimer;

    // ─── Constructor mặc định ───────────────────────────────────────────────
    public Bug(Texture texture, float x, float y) {
        this(texture, x, y, 100f, 32, 32, false);
    }

    // ─── Constructor đầy đủ ─────────────────────────────────────────────────
    public Bug(Texture texture, float x, float y, float speed, float width, float height, boolean stealth) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.width = width;
        this.height = height;
        this.stealth = stealth;
        this.isBoss = (width > 32);
        // Boss có máu từ GameConfig, bug thường 50 HP
        this.maxHealth    = this.isBoss ? GameConfig.BOSS_MAX_HEALTH : 50f;
        this.currentHealth = maxHealth;
        this.visibleToPlayer = !stealth;
        this.currentAlpha = stealth ? 0f : 1f;
        this.detectionRadius = this.isBoss
                ? GameConfig.BOSS_DETECTION_RADIUS
                : GameConfig.BUG_DETECTION_RADIUS;
        // Boss: tấn công từ xa trong attackRange
        this.attackRange    = this.isBoss ? GameConfig.BOSS_ATTACK_RANGE : 0f;
        this.attackInterval = this.isBoss ? GameConfig.BOSS_ATTACK_INTERVAL : 0f;
        this.attackTimer    = 0f;
    }

    public Vector2 getPosition() { return position; }
    public boolean isBoss()      { return isBoss; }

    /**
     * Cập nhật logic bug/boss.
     * @return lượng damage ranged boss gây cho player frame này (0 nếu không tấn công)
     */
    public float update(float delta, Vector2 playerPosition) {
        float dist = position.dst(playerPosition);
        float rangedDamage = 0f;

        // ── Aggro logic ──────────────────────────────────────────────────────
        if (!isAggro && dist <= detectionRadius) isAggro = true;
        if (isAggro  && dist > detectionRadius * 1.5f) isAggro = false;

        // ── Boss: tấn công từ xa khi player trong attackRange ────────────────
        if (isBoss && isAggro && attackRange > 0f) {
            if (dist <= attackRange) {
                // Đếm giờ tấn công
                attackTimer += delta;
                if (attackTimer >= attackInterval) {
                    attackTimer = 0f;
                    rangedDamage = GameConfig.BOSS_RANGED_DAMAGE;
                    hitFlashTimer = HIT_FLASH_DURATION * 0.5f; // flash nhẹ khi bắn
                }
                // Boss dừng lại khi trong tầm đánh (không cần đến gần hơn)
                // → không gọi move bên dưới
            } else if (isAggro) {
                // Còn ngoài tầm → tiến vào
                moveTo(playerPosition, delta);
            }
        } else if (isAggro) {
            // Bug thường: luôn tiến vào
            moveTo(playerPosition, delta);
        }

        // Sway (chỉ khi di chuyển)
        if (isAggro && !(isBoss && dist <= attackRange)) swayTimer += delta;

        // Boss pulse
        if (isBoss) pulseTimer += delta;

        // Spawn scale in
        if (spawnScale < 1f) spawnScale = Math.min(1f, spawnScale + SPAWN_SPEED * delta);

        // Hit flash giảm dần
        if (hitFlashTimer > 0f) hitFlashTimer = Math.max(0f, hitFlashTimer - delta);

        // Tàng hình fade
        if (stealth) {
            boolean shouldShow = (dist <= stealthRadius);
            currentAlpha = lerp(currentAlpha, shouldShow ? 1f : 0f, FADE_SPEED * delta);
            visibleToPlayer = (currentAlpha > 0.05f);
        } else {
            currentAlpha = 1f;
            visibleToPlayer = true;
        }

        return rangedDamage;
    }

    private void moveTo(Vector2 target, float delta) {
        Vector2 direction = new Vector2(target).sub(position).nor();
        position.add(direction.scl(speed * delta));
        swayTimer += delta;
    }

    public void draw(SpriteBatch batch) {
        if (!visibleToPlayer && currentAlpha < 0.05f) return;

        // Tính scale spawn + boss pulse
        float scaleX = spawnScale;
        float scaleY = spawnScale;
        if (isBoss) {
            float pulse = 1f + MathUtils.sin(pulseTimer * PULSE_SPEED) * 0.06f;
            scaleX *= pulse;
            scaleY *= pulse;
        }

        // Sway offset (chỉ ngang khi không phải boss)
        float swayOffset = isBoss ? 0f : MathUtils.sin(swayTimer * SWAY_SPEED) * SWAY_AMOUNT;

        // Màu sắc: flash đỏ khi bị đánh, mờ khi tàng hình
        if (hitFlashTimer > 0f) {
            float flashAlpha = (hitFlashTimer / HIT_FLASH_DURATION);
            batch.setColor(1f, 1f - flashAlpha * 0.8f, 1f - flashAlpha * 0.8f, currentAlpha);
        } else if (isBoss) {
            // Boss có màu tím + sáng hơn
            batch.setColor(1f, 0.85f, 1f, currentAlpha);
        } else {
            batch.setColor(1f, 1f, 1f, currentAlpha);
        }

        float drawW = width  * scaleX;
        float drawH = height * scaleY;
        float ox = position.x + (width  - drawW) / 2f + swayOffset;
        float oy = position.y + (height - drawH) / 2f;
        batch.draw(texture, ox, oy, drawW, drawH);
        batch.setColor(Color.WHITE);
    }

    public Rectangle getBounds() {
        if (!visibleToPlayer || currentAlpha < 0.05f)
            return new Rectangle(-9999, -9999, 0, 0);
        return new Rectangle(position.x, position.y, width, height);
    }

    public void takeDamage(float amount) {
        currentHealth = Math.max(0, currentHealth - amount);
        hitFlashTimer = HIT_FLASH_DURATION; // kích hoạt flash đỏ
        isAggro = true; // bị đánh → lập tức aggro
    }

    /** true nếu bug đang đuổi player */
    public boolean isAggro() { return isAggro; }

    public float  getHealthRatio()      { return currentHealth / maxHealth; }
    public boolean isDead()             { return currentHealth <= 0; }
    public boolean isVisibleToPlayer()  { return visibleToPlayer; }
    public float getWidth()             { return width; }
    public float getHeight()            { return height; }
    public float getAttackRange()       { return attackRange; }

    private float lerp(float a, float b, float t) {
        t = Math.min(1f, t);
        return a + (b - a) * t;
    }
}
