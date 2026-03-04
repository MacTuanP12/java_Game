package com.tuan.game.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.tuan.game.MyGdxGame;
import com.tuan.game.domain.Bug;
import com.tuan.game.domain.ExperienceGem;
import com.tuan.game.domain.Player;
import com.tuan.game.domain.Projectile;
import com.tuan.game.manager.Assets;
import com.tuan.game.system.EnvironmentState;
import com.tuan.game.system.GameConfig;

import java.util.Iterator;

public class PlayScreen implements Screen {

    public enum State { RUNNING, LEVEL_UP, PAUSED, GAME_OVER }

    // ─── Core ───────────────────────────────────────────────────────────────
    private final MyGdxGame game;
    private State currentState = State.RUNNING;

    // ─── Camera ─────────────────────────────────────────────────────────────
    private OrthographicCamera camera;

    // ─── Entities ───────────────────────────────────────────────────────────
    private Player player;
    private Array<Bug>           bugs;
    private Array<Projectile>    projectiles;
    private Array<ExperienceGem> gems;

    // ─── Map: Tường & Bẫy ───────────────────────────────────────────────────
    private Array<Rectangle> walls;
    private Array<Rectangle> traps;

    // ─── Timers & shooting ──────────────────────────────────────────────────
    private float spawnTimer    = 0;
    private float shootTimer    = 0.5f; // Bắn ngay khi vào game (không chờ)
    private float shootInterval = 0.5f;
    private float bugBaseSpeed  = 100f;

    // ─── XP & Level ─────────────────────────────────────────────────────────
    private float currentXP      = 0;
    private float xpToNextLevel  = 100;
    private int   level          = 1;

    // ─── Wave system ────────────────────────────────────────────────────────
    private int   currentWave    = 1;
    private float waveTimer      = 0;

    // ─── Fog / Environment ──────────────────────────────────────────────────
    private EnvironmentState envState;

    // ─── Trap animation ─────────────────────────────────────────────────────
    private float trapAnimTimer = 0f;

    // ─── Boss ───────────────────────────────────────────────────────────────
    /** Đã spawn boss trong wave hiện tại chưa (mỗi wave boss chỉ spawn 1 lần) */
    private boolean bossSpawnedThisWave = false;
    /** Timer hiển thị thông báo "BOSS XUẤT HIỆN!" */
    private float bossAnnounceTimer = 0f;
    private static final float BOSS_ANNOUNCE_DURATION = 3f;

    // ─── UI Stages ───────────────────────────────────────────────────────────
    private Stage upgradeStage;
    private Stage pauseStage;

    // ════════════════════════════════════════════════════════════════════════
    public PlayScreen(MyGdxGame game) {
        this.game = game;
    }

    // ─── show ───────────────────────────────────────────────────────────────
    @Override
    public void show() {
        // Camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, GameConfig.VIEW_WIDTH, GameConfig.VIEW_HEIGHT);

        // Player
        player = new Player(game.assets.getTexture(Assets.PLAYER_IMG),
                GameConfig.WORLD_WIDTH / 2f, GameConfig.WORLD_HEIGHT / 2f);

        // Environment / Fog
        envState = new EnvironmentState();
        player.setHitListener(() -> envState.onPlayerHit()); // stress khi bị đánh

        // Collections
        bugs        = new Array<>();
        projectiles = new Array<>();
        gems        = new Array<>();

        // Stage cho menu upgrade
        upgradeStage = new Stage(new ScreenViewport(), game.batch);
        // Stage cho menu tạm dừng
        pauseStage = new Stage(new ScreenViewport(), game.batch);

        // ── Tạo bản đồ tường và bẫy ─────────────────────────────────────
        walls = new Array<>();
        traps = new Array<>();
        buildMap();
    }

    /** Tạo các tường và bẫy tĩnh trên bản đồ */
    private void buildMap() {
        // Tường biên thế giới
        int w = GameConfig.WORLD_WIDTH, h = GameConfig.WORLD_HEIGHT;
        walls.add(new Rectangle(0,     0,     w,  32)); // đáy
        walls.add(new Rectangle(0,     h-32,  w,  32)); // trên
        walls.add(new Rectangle(0,     0,     32, h));  // trái
        walls.add(new Rectangle(w-32,  0,     32, h));  // phải

        // Một vài tường nội thất giữa bản đồ
        walls.add(new Rectangle(600,  400, 256, 32));
        walls.add(new Rectangle(1200, 700, 32, 200));
        walls.add(new Rectangle(1600, 300, 200, 32));

        // Bẫy (vùng gây sát thương liên tục)
        traps.add(new Rectangle(800,  600, 128, 128));
        traps.add(new Rectangle(1400, 900, 128, 128));
        traps.add(new Rectangle(500,  1100, 96,  96));
    }

    // ─── render ─────────────────────────────────────────────────────────────
    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);

        // ── ESC: toggle PAUSED (chỉ khi đang RUNNING hoặc PAUSED) ───────────
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (currentState == State.RUNNING) {
                showPauseMenu();
            } else if (currentState == State.PAUSED) {
                resumeGame();
            }
        }

        // 1. Logic (chỉ khi RUNNING)
        if (currentState == State.RUNNING) {
            updateGameLogic(delta);
        }

        // 2. Cập nhật Camera theo Player
        float camX = MathUtils.clamp(
                player.getPosition().x + player.getWidth()  / 2f,
                GameConfig.VIEW_WIDTH  / 2f, GameConfig.WORLD_WIDTH  - GameConfig.VIEW_WIDTH  / 2f);
        float camY = MathUtils.clamp(
                player.getPosition().y + player.getHeight() / 2f,
                GameConfig.VIEW_HEIGHT / 2f, GameConfig.WORLD_HEIGHT - GameConfig.VIEW_HEIGHT / 2f);
        camera.position.set(camX, camY, 0);
        camera.update();
        game.batch.setProjectionMatrix(camera.combined);
        game.shapeRenderer.setProjectionMatrix(camera.combined);

        // 3. Vẽ Thế giới
        game.batch.begin();
        drawWorld();
        drawFog();
        drawHudText();
        // Boss announcement (vẽ trên HUD)
        if (bossAnnounceTimer > 0f) {
            bossAnnounceTimer -= delta;
            float alpha = Math.min(1f, bossAnnounceTimer / BOSS_ANNOUNCE_DURATION * 2f);
            game.fontLarge.setColor(1f, 0.2f, 0.2f, alpha);
            String msg = "⚠ BOSS XUẤT HIỆN! ⚠";
            float tx = camera.position.x - GameConfig.VIEW_WIDTH / 2f + 60;
            float ty = camera.position.y + 80;
            game.fontLarge.draw(game.batch, msg, tx, ty);
            game.fontLarge.setColor(Color.WHITE);
        }
        game.batch.end();

        // 4. ShapeRenderer: thanh máu Bug + thanh XP (drawBugHealthBars tự quản lý begin/end)
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBugHealthBars(); // bên trong: end Filled → begin Line → end Line → begin Filled
        drawXpBar();
        game.shapeRenderer.end();

        // 5. UI Upgrade menu
        if (currentState == State.LEVEL_UP) {
            upgradeStage.act(delta);
            upgradeStage.draw();
        }

        // 6. Pause overlay
        if (currentState == State.PAUSED) {
            drawPauseOverlay();
            pauseStage.act(delta);
            pauseStage.draw();
        }

        // 7. Game Over
        if (!player.isAlive()) {
            game.setScreen(new GameOverScreen(game, level));
        }
    }

    // ─── Vẽ thế giới ────────────────────────────────────────────────────────
    private void drawWorld() {
        // Vẽ tường
        Texture wallTex = game.assets.getTexture(Assets.WALL_IMG);
        for (Rectangle wall : walls) {
            for (float wx = wall.x; wx < wall.x + wall.width; wx += 64) {
                for (float wy = wall.y; wy < wall.y + wall.height; wy += 64) {
                    game.batch.draw(wallTex, wx, wy,
                            Math.min(64, wall.x + wall.width  - wx),
                            Math.min(64, wall.y + wall.height - wy));
                }
            }
        }

        // Vẽ bẫy với hiệu ứng PULSE (phình co + màu nhấp nháy)
        Texture trapTex = game.assets.getTexture(Assets.TRAP_IMG);
        float pulse = MathUtils.sin(trapAnimTimer * 4f); // [-1, 1]
        float trapScale = 1f + pulse * 0.06f;            // scale 0.94 → 1.06
        // Màu đổi từ cam → đỏ
        float r = 0.85f + pulse * 0.15f;
        float g = 0.25f - pulse * 0.1f;
        float trapAlpha = 0.85f + pulse * 0.1f;
        game.batch.setColor(r, g, 0f, trapAlpha);
        for (Rectangle trap : traps) {
            float tw = trap.width  * trapScale;
            float th = trap.height * trapScale;
            float tx = trap.x + (trap.width  - tw) / 2f;
            float ty = trap.y + (trap.height - th) / 2f;
            game.batch.draw(trapTex, tx, ty, tw, th);
        }
        game.batch.setColor(com.badlogic.gdx.graphics.Color.WHITE);

        // Vẽ entities
        player.draw(game.batch);
        for (Bug bug : bugs)           bug.draw(game.batch);
        for (ExperienceGem gem : gems) gem.draw(game.batch);
        for (Projectile p : projectiles) p.draw(game.batch);
    }

    // ─── Vẽ Fog of War ──────────────────────────────────────────────────────
    /**
     * Kỹ thuật: vẽ overlay tối toàn màn hình bằng nhiều tam giác (triangle fan)
     * tạo thành một "donut" – lỗ hổng ở giữa hình tròn, tối ở ngoài.
     * Không cần shader, hoạt động trên mọi thiết bị.
     */
    private void drawFog() {
        game.batch.end(); // phải kết thúc batch trước khi dùng ShapeRenderer

        float radius = envState.getCurrentVisionRadius();
        float cx = player.getPosition().x + player.getWidth()  / 2f;
        float cy = player.getPosition().y + player.getHeight() / 2f;
        float far = (GameConfig.WORLD_WIDTH + GameConfig.WORLD_HEIGHT); // đủ lớn để phủ hết

        com.badlogic.gdx.Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        com.badlogic.gdx.Gdx.gl.glBlendFunc(
            com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
            com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        final int SEGMENTS = 64; // độ mịn của vòng tròn tầm nhìn
        double step = 2 * Math.PI / SEGMENTS;

        // Vẽ "donut": mỗi slice là 1 tứ giác (2 tam giác) từ viền vòng tròn ra phạm vi "far"
        for (int i = 0; i < SEGMENTS; i++) {
            double a1 = i       * step;
            double a2 = (i + 1) * step;

            // Điểm trên viền vòng tròn (tầm nhìn)
            float ix1 = cx + radius * (float) Math.cos(a1);
            float iy1 = cy + radius * (float) Math.sin(a1);
            float ix2 = cx + radius * (float) Math.cos(a2);
            float iy2 = cy + radius * (float) Math.sin(a2);

            // Điểm tương ứng ở xa (outer dark ring)
            float ox1 = cx + far * (float) Math.cos(a1);
            float oy1 = cy + far * (float) Math.sin(a1);
            float ox2 = cx + far * (float) Math.cos(a2);
            float oy2 = cy + far * (float) Math.sin(a2);

            // Tam giác 1: inner1, outer1, outer2
            game.shapeRenderer.setColor(0f, 0f, 0f, 0.92f);
            game.shapeRenderer.triangle(ix1, iy1, ox1, oy1, ox2, oy2);
            // Tam giác 2: inner1, outer2, inner2
            game.shapeRenderer.triangle(ix1, iy1, ox2, oy2, ix2, iy2);
        }

        game.shapeRenderer.end();

        // Vẽ fog gradient texture để làm mềm viền (blend từ trong suốt → tối)
        game.batch.begin();
        com.badlogic.gdx.graphics.Texture fogTex = game.assets.getTexture(Assets.FOG_IMG);
        // Texture đã có gradient tối ngoài-sáng trong, kéo giãn đúng bằng radius*2
        float fogSize = radius * 2f;
        game.batch.draw(fogTex, cx - fogSize / 2f, cy - fogSize / 2f, fogSize, fogSize);
        // Không end batch ở đây – caller (render) sẽ end sau khi drawHudText
    }

    // ─── HUD text (trong batch, sau fog) ────────────────────────────────────
    private void drawHudText() {
        // Tọa độ tính theo camera combined — góc trên-trái màn hình
        float hx = camera.position.x - GameConfig.VIEW_WIDTH  / 2f + 10;
        float hy = camera.position.y + GameConfig.VIEW_HEIGHT / 2f - 10;
        game.font.draw(game.batch, "HP: "    + (int) player.getHealth(), hx, hy);
        game.font.draw(game.batch, "Level: " + level,                    hx, hy - 20);
        game.font.draw(game.batch, "Wave: "  + currentWave,              hx, hy - 40);
        int stressPct = (int)(envState.getStress() * 100);
        if (stressPct > 0)
            game.font.draw(game.batch, "Stress: " + stressPct + "%",     hx, hy - 60);
    }

    // ─── Bug health bars + boss attack range ring ────────────────────────────
    private void drawBugHealthBars() {
        // Health bars (Filled)
        for (Bug bug : bugs) {
            if (!bug.isVisibleToPlayer() || bug.getHealthRatio() >= 1f) continue;
            float x = bug.getPosition().x;
            float y = bug.getPosition().y + bug.getHeight() + 4;
            float barW = bug.getWidth();
            game.shapeRenderer.setColor(Color.RED);
            game.shapeRenderer.rect(x, y, barW, 5);
            // Boss: thanh máu vàng; bug thường: xanh lá
            game.shapeRenderer.setColor(bug.isBoss() ? Color.YELLOW : Color.GREEN);
            game.shapeRenderer.rect(x, y, barW * bug.getHealthRatio(), 5);
        }
        game.shapeRenderer.end();

        // Vòng tròn tầm đánh boss (Line renderer)
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        for (Bug bug : bugs) {
            if (!bug.isBoss() || !bug.isVisibleToPlayer()) continue;
            float bcx = bug.getPosition().x + bug.getWidth()  / 2f;
            float bcy = bug.getPosition().y + bug.getHeight() / 2f;
            float ar  = bug.getAttackRange();
            // Vòng đỏ nhấp nháy theo thời gian
            float alpha = 0.45f + 0.35f * MathUtils.sin(trapAnimTimer * 5f);
            game.shapeRenderer.setColor(1f, 0.15f, 0.15f, alpha);
            game.shapeRenderer.circle(bcx, bcy, ar, 48);
            // Vòng trong mờ hơn
            game.shapeRenderer.setColor(1f, 0.4f, 0f, alpha * 0.5f);
            game.shapeRenderer.circle(bcx, bcy, ar * 0.85f, 48);
        }
        game.shapeRenderer.end();

        // Bắt đầu lại Filled để drawXpBar() dùng tiếp
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
    }

    // ─── XP bar (screen-space) ───────────────────────────────────────────────
    private void drawXpBar() {
        float left  = camera.position.x - GameConfig.VIEW_WIDTH  / 2f;
        float top   = camera.position.y + GameConfig.VIEW_HEIGHT / 2f - 10;
        float barW  = GameConfig.VIEW_WIDTH;
        game.shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        game.shapeRenderer.rect(left, top, barW, 8);
        game.shapeRenderer.setColor(0.2f, 0.8f, 0.2f, 1);
        game.shapeRenderer.rect(left, top, barW * (currentXP / xpToNextLevel), 8);
    }

    // ─── updateGameLogic ────────────────────────────────────────────────────
    private void updateGameLogic(float delta) {
        // Animation timers
        trapAnimTimer += delta;

        // Environment / Fog
        envState.update(delta, player.getHealth());

        // Player
        player.update(delta);

        // Va chạm tường → rollback
        for (Rectangle wall : walls) {
            if (player.getBounds().overlaps(wall)) {
                player.rollbackPosition();
                break;
            }
        }

        // Va chạm bẫy → damage per second
        for (Rectangle trap : traps) {
            if (player.getBounds().overlaps(trap)) {
                player.takeDamage(GameConfig.TRAP_DPS * delta);
            }
        }

        // Wave timer
        waveTimer += delta;
        if (waveTimer >= GameConfig.WAVE_DURATION) {
            waveTimer = 0;
            currentWave++;
            bossSpawnedThisWave = false; // reset cờ boss cho wave mới
        }

        // Spawn Bug
        spawnTimer += delta;
        float spawnInterval = Math.max(GameConfig.MIN_SPAWN_INTERVAL, 2f - currentWave * 0.1f);
        if (spawnTimer >= spawnInterval) {
            spawnBug();
            spawnTimer = 0;
        }

        // Bắn đạn
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootAtNearestBug();
            shootTimer = 0;
        }

        // Cập nhật đạn
        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();
            p.update(delta);
            if (!p.isActive()) projIter.remove();
        }

        // Cập nhật Bug & va chạm với Player
        for (Bug bug : bugs) {
            float rangedDmg = bug.update(delta, player.getPosition());

            // Boss tấn công từ xa → gây damage trực tiếp (không cần chạm)
            if (rangedDmg > 0f) {
                player.takeDamage(rangedDmg);
            }

            // Bug thường (hoặc boss đã áp sát): gây damage khi chạm người
            if (bug.isAggro() && !bug.isBoss() && player.getBounds().overlaps(bug.getBounds())) {
                player.takeDamage(10 * delta);
            }
            // Boss vẫn gây damage tiếp xúc khi đứng sát (phòng trường hợp xuyên tầm)
            if (bug.isBoss() && player.getBounds().overlaps(bug.getBounds())) {
                player.takeDamage(20 * delta);
            }
        }

        // Đạn trúng Bug
        checkBulletCollisions();

        // Cập nhật Gem (animation)
        for (ExperienceGem gem : gems) gem.update(delta);

        // Thu thập Gem
        Iterator<ExperienceGem> gemIter = gems.iterator();
        while (gemIter.hasNext()) {
            ExperienceGem gem = gemIter.next();
            if (player.getBounds().overlaps(gem.getBounds())) {
                currentXP += gem.getXpValue();
                gem.collect();
                gemIter.remove();
                if (currentXP >= xpToNextLevel) levelUp();
            }
        }
    }

    // ─── Spawn Bug / Boss ────────────────────────────────────────────────────
    private void spawnBug() {
        // Vị trí spawn ngoài tầm nhìn camera
        float side = MathUtils.random(3);
        float x, y;
        float cx = player.getPosition().x, cy = player.getPosition().y;
        float offset = 700;
        if (side < 1)      { x = cx - offset; y = MathUtils.random(0, GameConfig.WORLD_HEIGHT); }
        else if (side < 2) { x = cx + offset; y = MathUtils.random(0, GameConfig.WORLD_HEIGHT); }
        else if (side < 3) { x = MathUtils.random(0, GameConfig.WORLD_WIDTH); y = cy + offset; }
        else               { x = MathUtils.random(0, GameConfig.WORLD_WIDTH); y = cy - offset; }

        x = MathUtils.clamp(x, 50, GameConfig.WORLD_WIDTH  - 50);
        y = MathUtils.clamp(y, 50, GameConfig.WORLD_HEIGHT - 50);

        // Tốc độ tăng theo wave
        float spd = bugBaseSpeed * (1 + currentWave * GameConfig.WAVE_SPEED_SCALE);
        spd = Math.min(spd, GameConfig.MAX_BUG_SPEED);

       boolean isBossWave = (currentWave % GameConfig.BOSS_WAVE_INTERVAL == 0);

        // ── Wave boss: spawn đúng 1 boss duy nhất ────────────────────────────
        if (isBossWave && !bossSpawnedThisWave) {
            bossSpawnedThisWave = true;
            bossAnnounceTimer   = BOSS_ANNOUNCE_DURATION;
            bugs.add(new Bug(game.assets.getTexture(Assets.BUG_BOSS_IMG),
                    x, y, spd * 0.7f, 96, 96, false));
            return; // chỉ spawn boss, không spawn bug thường ngay lượt này
        }

        // ── Wave bình thường (hoặc boss đã spawn): spawn bug thường ──────────
        boolean stealth = currentWave >= 3 && MathUtils.random() < 0.2f;
        bugs.add(new Bug(game.assets.getTexture(Assets.BUG_IMG),
                x, y, spd, 32, 32, stealth));
    }

    // ─── Bắn đạn ────────────────────────────────────────────────────────────
    private void shootAtNearestBug() {
        Vector2 origin = new Vector2(
                player.getPosition().x + player.getWidth()  / 2f,
                player.getPosition().y + player.getHeight() / 2f);

        float visionRadius = envState.getCurrentVisionRadius();

        // Tìm bug visible gần nhất VÀ nằm trong tầm nhìn của player
        Bug nearest = null;
        float minDist = Float.MAX_VALUE;
        for (Bug bug : bugs) {
            if (!bug.isVisibleToPlayer()) continue;
            float d = origin.dst(bug.getPosition());
            // Chỉ nhắm vào bug nằm trong tầm nhìn (fog of war)
            if (d > visionRadius) continue;
            if (d < minDist) { minDist = d; nearest = bug; }
        }

        // Không có bug trong tầm nhìn → KHÔNG bắn
        if (nearest == null) return;

        Vector2 dir = new Vector2(nearest.getPosition()).sub(origin);
        if (dir.len2() < 0.001f) return;

        projectiles.add(new Projectile(
                game.assets.getTexture(Assets.PROJECTILE_IMG),
                game.assets.getTexture(Assets.PROJECTILE_TRAIL_IMG),
                origin.x, origin.y, dir));
    }

    // ─── Va chạm đạn ────────────────────────────────────────────────────────
    private void checkBulletCollisions() {
        for (Projectile p : projectiles) {
            if (!p.isActive()) continue;
            Iterator<Bug> bugIter = bugs.iterator();
            while (bugIter.hasNext()) {
                Bug b = bugIter.next();
                if (p.getBounds().overlaps(b.getBounds())) {
                    b.takeDamage(25f);
                    p.setInactive();
                    if (b.isDead()) {
                        dropGems(b);
                        bugIter.remove();
                    }
                    break;
                }
            }
        }
    }

    /** Boss drop 3 gem vàng (x3 XP), bug thường drop 1 gem xanh */
    private void dropGems(Bug deadBug) {
        float bx = deadBug.getPosition().x + deadBug.getWidth()  / 2f;
        float by = deadBug.getPosition().y + deadBug.getHeight() / 2f;
        if (deadBug.isBoss()) {
            // Scatter 3 gem boss quanh vị trí chết
            for (int i = 0; i < GameConfig.BOSS_GEM_COUNT; i++) {
                float angle  = MathUtils.random(MathUtils.PI2);
                float offset = MathUtils.random(20f, 60f);
                gems.add(new ExperienceGem(
                        game.assets.getTexture(Assets.BOSS_GEM_IMG),
                        bx + MathUtils.cos(angle) * offset,
                        by + MathUtils.sin(angle) * offset,
                        GameConfig.BOSS_XP_PER_GEM));
            }
        } else {
            gems.add(new ExperienceGem(
                    game.assets.getTexture(Assets.EXP_GEM_IMG),
                    deadBug.getPosition().x, deadBug.getPosition().y));
        }
    }

    // ─── Level Up ───────────────────────────────────────────────────────────
    private void levelUp() {
        level++;
        currentXP     = 0;
        xpToNextLevel *= 1.2f;
        shootInterval  = Math.max(shootInterval * 0.9f, GameConfig.MIN_SHOOT_INTERVAL);
        bugBaseSpeed   = Math.min(bugBaseSpeed + 10f,   GameConfig.MAX_BUG_SPEED);
        showUpgradeMenu();
    }

    // ─── Menu nâng cấp ──────────────────────────────────────────────────────
    private void showUpgradeMenu() {
        currentState = State.LEVEL_UP;
        upgradeStage.clear();
        Gdx.input.setInputProcessor(upgradeStage);

        Table table = new Table();
        table.setFillParent(true);
        upgradeStage.addActor(table);

        String[] upgrades = {
            "Syntax Sugar (Tăng tốc bắn)",
            "Clean Code (Hồi máu)",
            "Legacy Support (Tăng tốc chạy)",
            "Clear Vision (Mở rộng tầm nhìn)"
        };

        for (String upgradeName : upgrades) {
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
            style.font = game.font;
            TextButton btn = new TextButton(upgradeName, style);
            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    applyUpgrade(upgradeName);
                    resumeGame();
                }
            });
            table.add(btn).pad(10).row();
        }
    }

    private void applyUpgrade(String name) {
        if (name.contains("Syntax Sugar")) {
            shootInterval *= 0.7f;
        } else if (name.contains("Clean Code")) {
            player.heal(30);
        } else if (name.contains("Legacy Support")) {
            player.increaseSpeed(50);
        } else if (name.contains("Clear Vision")) {
            // Tăng bán kính tầm nhìn cơ bản 20%
            envState.setBaseVisionRadius(envState.getBaseVisionRadius() * 1.2f);
        }
    }

    private void resumeGame() {
        currentState = State.RUNNING;
        Gdx.input.setInputProcessor(null);
    }

    // ─── Pause menu ──────────────────────────────────────────────────────────
    private void showPauseMenu() {
        currentState = State.PAUSED;
        pauseStage.clear();
        Gdx.input.setInputProcessor(pauseStage);

        Table table = new Table();
        table.setFillParent(true);
        pauseStage.addActor(table);

        // Nút TIẾP TỤC
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font      = game.fontLarge;
        style.fontColor = Color.WHITE;
        style.overFontColor = Color.YELLOW;

        TextButton btnResume  = new TextButton("▶  Tiep tuc  (ESC)", style);
        TextButton btnRestart = new TextButton("↺  Choi lai", style);
        TextButton btnQuit    = new TextButton("✕  Thoat", style);

        btnResume.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { resumeGame(); }
        });
        btnRestart.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) {
                game.setScreen(new PlayScreen(game));
            }
        });
        btnQuit.addListener(new ClickListener() {
            @Override public void clicked(InputEvent e, float x, float y) { Gdx.app.exit(); }
        });

        table.add(btnResume ).padBottom(20).row();
        table.add(btnRestart).padBottom(20).row();
        table.add(btnQuit);
    }

    /** Vẽ lớp phủ tối mờ khi PAUSED (dùng ShapeRenderer tọa độ screen-space) */
    private void drawPauseOverlay() {

        game.shapeRenderer.setProjectionMatrix(pauseStage.getCamera().combined);
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                           com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(0f, 0f, 0f, 0.6f);
        game.shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.shapeRenderer.end();


        game.batch.setProjectionMatrix(pauseStage.getCamera().combined);
        game.batch.begin();
        game.fontLarge.setColor(1f, 1f, 0.2f, 1f);
        float tw = Gdx.graphics.getWidth();
        float th = Gdx.graphics.getHeight();
        game.fontLarge.draw(game.batch, "— TAM DUNG —",
                tw / 2f - 120, th / 2f + 200);
        game.fontLarge.setColor(Color.WHITE);
        game.batch.end();
        // Khôi phục projection matrix về camera world
        game.batch.setProjectionMatrix(camera.combined);
        game.shapeRenderer.setProjectionMatrix(camera.combined);
    }

    // ─── Screen lifecycle ────────────────────────────────────────────────────
    @Override public void resize(int width, int height) {
        if (upgradeStage != null) upgradeStage.getViewport().update(width, height, true);
        if (pauseStage   != null) pauseStage.getViewport().update(width, height, true);
    }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose() {
        if (upgradeStage != null) upgradeStage.dispose();
        if (pauseStage   != null) pauseStage.dispose();
    }
}
