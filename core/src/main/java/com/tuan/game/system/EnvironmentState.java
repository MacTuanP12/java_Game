package com.tuan.game.system;

/**
 * Quản lý trạng thái môi trường: tầm nhìn động, stress.
 * Tách khỏi PlayScreen để dễ mở rộng (item tăng tầm nhìn, debuff v.v.)
 */
public class EnvironmentState {

    private float baseVisionRadius;   // bán kính tầm nhìn cơ bản (px)
    private float visionMultiplier;   // nhân tố bổ sung (upgrade, item)
    private float stress;             // [0,1] — 0=bình thường, 1=hoảng loạn

    public EnvironmentState() {
        this.baseVisionRadius = GameConfig.BASE_VISION_RADIUS;
        this.visionMultiplier = 1.0f;
        this.stress = 0f;
    }

    /**
     * Gọi mỗi frame để phục hồi stress và tính lại tầm nhìn.
     * @param delta          thời gian frame
     * @param playerHealth   máu hiện tại của Player (0–100)
     */
    public void update(float delta, float playerHealth) {
        // Stress tự giảm dần theo thời gian
        stress = Math.max(0f, stress - GameConfig.STRESS_DECAY_RATE * delta);

        // Khi máu thấp: stress tăng liên tục
        if (playerHealth < GameConfig.LOW_HEALTH_THRESHOLD) {
            float lowHealthFactor = 1f - (playerHealth / GameConfig.LOW_HEALTH_THRESHOLD);
            stress = Math.min(1f, stress + lowHealthFactor * delta * 0.5f);
        }
    }

    /** Gọi khi Player bị đánh để tăng stress ngay lập tức */
    public void onPlayerHit() {
        stress = Math.min(1f, stress + GameConfig.STRESS_ON_HIT);
    }

    /**
     * Trả về bán kính fog hiện tại (px) tính theo stress.
     * Stress cao → tầm nhìn nhỏ lại.
     */
    public float getCurrentVisionRadius() {
        float radius = baseVisionRadius * visionMultiplier * (1f - stress * 0.6f);
        return Math.max(GameConfig.MIN_VISION_RADIUS, radius);
    }

    /** Đặt hệ số nhân tầm nhìn (từ item/upgrade) */
    public void setVisionMultiplier(float multiplier) {
        this.visionMultiplier = Math.max(0.1f, multiplier);
    }

    public float getVisionMultiplier() { return visionMultiplier; }
    public float getStress()           { return stress; }
    public float getBaseVisionRadius() { return baseVisionRadius; }
    public void  setBaseVisionRadius(float r) { this.baseVisionRadius = r; }
}

