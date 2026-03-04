package com.tuan.game.system;

public class GameConfig {
    // === MÀN HÌNH ===
    public static final int WORLD_WIDTH  = 2560; // Kích thước thế giới (lớn hơn màn hình)
    public static final int WORLD_HEIGHT = 1440;
    public static final int VIEW_WIDTH   = 1280;
    public static final int VIEW_HEIGHT  = 720;

    // === Bug ===
    public static final float MAX_BUG_SPEED       = 400f;
    public static final float MIN_SPAWN_INTERVAL  = 0.3f;

    // === Player ===
    public static final float MIN_SHOOT_INTERVAL  = 0.1f;
    public static final float MAX_PLAYER_SPEED    = 500f;

    // === FOG / VISION ===
    public static final float BASE_VISION_RADIUS  = 600f;  // px, visionMultiplier = 1.0
    public static final float MIN_VISION_RADIUS   = 200f;  // giới hạn dưới khi bị stress
    public static final float VISION_REGEN_RATE   = 0.3f;  // tốc độ phục hồi tầm nhìn/s

    // === BUG AI ===
    /** Tầm phát hiện player của bug thường — lớn hơn tầm nhìn player */
    public static final float BUG_DETECTION_RADIUS  = BASE_VISION_RADIUS * 1.30f;
    /** Tầm phát hiện của boss */
    public static final float BOSS_DETECTION_RADIUS = BASE_VISION_RADIUS * 1.50f;

    // === BOSS ===
    public static final float BOSS_MAX_HEALTH      = 3000f; // máu boss rất cao
    /** Tầm tấn công từ xa của boss  */
    public static final float BOSS_ATTACK_RANGE    = BASE_VISION_RADIUS; // 600px
    /** Sát thương mỗi đòn tấn công từ xa */
    public static final float BOSS_RANGED_DAMAGE   = 15f;
    /** Giây giữa hai lần tấn công từ xa */
    public static final float BOSS_ATTACK_INTERVAL = 1.5f;
    /** XP boss drop = x3 bug thường (bug thường = 10, boss = 30 per gem, drop 3 gem = 90 total) */
    public static final float BOSS_XP_PER_GEM      = 30f;
    public static final int   BOSS_GEM_COUNT       = 3;    // số gem boss drop khi chết

    // === STRESS ===
    public static final float STRESS_ON_HIT       = 0.25f; // tăng stress khi bị đánh
    public static final float STRESS_DECAY_RATE   = 0.1f;  // stress giảm dần mỗi giây
    public static final float LOW_HEALTH_THRESHOLD = 30f;  // % máu kích hoạt giảm tầm nhìn

    // === BẪY ===
    public static final float TRAP_DPS            = 20f;   // damage per second khi đứng trên bẫy

    // === WAVE ===
    public static final float WAVE_DURATION       = 20f;   // giây mỗi wave
    public static final float WAVE_SPEED_SCALE    = 0.1f;  // speed += baseSpeed * wave * 0.1
    public static final int   BOSS_WAVE_INTERVAL  = 5;     // boss xuất hiện mỗi 5 wave
}
