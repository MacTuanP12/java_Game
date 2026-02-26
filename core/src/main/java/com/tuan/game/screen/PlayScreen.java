package com.tuan.game.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
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

import java.util.Iterator;

public class PlayScreen implements Screen {
    private MyGdxGame game;
    private Player player;
    //quái
    private Array<Bug> bugs; // Danh sách kẻ địch
    private float spawnTimer = 0; // Bộ đếm thời gian sinh quái
    // đạn
    private Array<Projectile> projectiles; // Danh sách đạn bắn ra
    private float shootTimer = 0; // Bộ đếm thời gian bắn đạn
    private float shootInterval = 0.5f; // Khoảng thời gian giữa các lần bắn
    //kinh nghiệm
    private Array<ExperienceGem> gems; // Danh sách viên ngọc kinh nghiệm
    private float currentXP =0 ;
    private float xpToNextLevel = 100; // XP cần để lên cấp
    private int level =1;
    public enum State{ RUNNING, LEVEL_UP, GAME_OVER };
    private State currentState = State.RUNNING;
    private Stage upgradeStage; // Stage để hiển thị UI khi lên cấp




    public PlayScreen(MyGdxGame game) {
        this.game = game;
    }


    @Override
    public void show() {
        //Khởi tạo nhân vật
        player = new Player(game.assets.getTexture(com.tuan.game.manager.Assets.PLAYER_IMG), 640, 360);
        bugs = new Array<>();
        projectiles = new Array<>();
        gems = new Array<>();
        upgradeStage = new Stage(new ScreenViewport(), game.batch);
    }

    @Override
    public void render(float delta) {
        // Luôn xóa màn hình và vẽ thế giới trước
        ScreenUtils.clear(0.1f, 0.1f, 0.2f, 1);

        // 1. Chỉ cập nhật LOGIC nếu game đang RUNNING
        if (currentState == State.RUNNING) {
            updateGameLogic(delta);
        }

        // 2. VẼ THẾ GIỚI (Luôn vẽ để người chơi thấy game bị mờ phía sau)
        game.batch.begin();
        player.draw(game.batch);
        for (Bug bug : bugs) bug.draw(game.batch);
        //vẽ chỉ số máu lên góc màn hình
        game.font.draw(game.batch, "Health: " + (int)player.getHealth(), 20, 700);
        for (Projectile p : projectiles) p.draw(game.batch);
        game.batch.end();

        // Vẽ thanh XP
        game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        game.shapeRenderer.setColor(1, 0, 0, 1); // Màu đỏ cho phần nền
        game.shapeRenderer.rect(0, 710, 1280, 10); // Thanh máu nền
        game.shapeRenderer.setColor(0, 1, 0, 1); // Màu xanh cho phần tiến trình
        float progessWidth = (currentXP / xpToNextLevel) * 1280;
        game.shapeRenderer.rect(0, 710, progessWidth, 10); // Thanh XP tiến trình
        game.shapeRenderer.end();

        // 3. VẼ UI NÂNG CẤP (Nếu đang ở trạng thái Level Up)
        if (currentState == State.LEVEL_UP) {
            upgradeStage.act(delta);
            upgradeStage.draw();
        }

        if (player.getHealth() <= 0) {
            game.setScreen(new GameOverScreen(game, level));
        }
    }

    private void updateGameLogic(float delta) {
        player.update(delta);

        // Cập nhật bộ đếm thời gian sinh quái
        spawnTimer += delta;
        if (spawnTimer > 2f) {
            spawnBug();
            spawnTimer = 0;
        }

        // Cập nhật đạn và sinh đạn mới
        shootTimer += delta;
        if (shootTimer >= shootInterval) {
            shootAtNearestBug();
            shootTimer = 0;
        }

        // Xử lý logic đạn (Sử dụng Iterator để xóa đạn an toàn khi đang lặp)
        Iterator<Projectile> projIter = projectiles.iterator();
        while (projIter.hasNext()) {
            Projectile p = projIter.next();
            p.update(delta);
            if (!p.isActive()) projIter.remove();
        }

        // Kiểm tra va chạm giữa Player và từng con Bug
        for (Bug bug : bugs) {
            bug.update(delta, player.getPosition());
            if (player.getBounds().overlaps(bug.getBounds())) {
                player.takeDamage(10 * delta); // Giảm 10 máu mỗi giây khi va chạm
            }
        }

        // Kiểm tra Đạn trúng Bug
        checkBulletCollisions();

        // Kiểm tra Player thu thập Experience Gem
        Iterator<ExperienceGem> gemIter = gems.iterator();
        while (gemIter.hasNext()) {
            ExperienceGem gem = gemIter.next();
            if (player.getBounds().overlaps(gem.getBounds())) {
                currentXP += gem.getXpValue();
                gem.collect();
                gemIter.remove();
                if (currentXP >= xpToNextLevel) {
                    levelUp();
                }
            }
        }

    }
    private void spawnBug() {
        // Sinh quái ở một vị trí ngẫu nhiên ngoài màn hình hoặc ở góc
        float x = MathUtils.random(0, 1280);
        float y = MathUtils.random(0, 720);
        bugs.add(new Bug(game.assets.getTexture(Assets.BUG_IMG), x, y));
    }
    private void shootAtNearestBug() {
        if (bugs.size == 0) return;

        Bug nearestBug = null;
        float minDistance = Float.MAX_VALUE;

        for (Bug bug : bugs) {
            float dist = player.getPosition().dst(bug.getPosition());
            if (dist < minDistance) {
                minDistance = dist;
                nearestBug = bug;
            }
        }

        if (nearestBug != null) {
            Vector2 direction = new Vector2(nearestBug.getPosition()).sub(player.getPosition());
            projectiles.add(new Projectile(game.assets.getTexture(Assets.PROJECTILE_IMG),
                player.getPosition().x, player.getPosition().y, direction));
        }
    }
    private void checkBulletCollisions() {
        for (Projectile p : projectiles) {
            Iterator<Bug> bugIter = bugs.iterator();
            while (bugIter.hasNext()) {
                Bug b = bugIter.next();
                if (p.getBounds().overlaps(b.getBounds())) {
                    // Khi đạn trúng quái, xóa quái và đạn đpoonfg thời sinh ra 1 hạt xp
                    gems.add(new ExperienceGem(game.assets.getTexture(Assets.EXP_GEM_IMG),
                        b.getPosition().x, b.getPosition().y));
                    bugIter.remove(); // Bug chết
                    p.setInactive();  // Đạn biến mất
                    break;
                }
            }
        }
    }
    private void levelUp() {
        level++;
        currentXP = 0;
        xpToNextLevel *= 1.2f; // Tăng độ khó cho cấp sau
        System.out.println("Level Up! Current Level: " + level);
        showUpgradeMenu();
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
    private void showUpgradeMenu() {
        currentState = State.LEVEL_UP;
        upgradeStage.clear();

        // Ép LibGDX nhận chuột cho Menu này
        Gdx.input.setInputProcessor(upgradeStage);

        Table table = new Table();
        table.setFillParent(true);
        upgradeStage.addActor(table);

        // Tạo 3 nút đại diện cho 3 kỹ năng ngẫu nhiên
        String[] upgrades = {"Syntax Sugar (Tăng tốc bắn)", "Clean Code (Hồi máu)", "Legacy Support (Tăng tốc chạy)"};

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
            shootInterval *= 0.7f; // Bắn nhanh hơn 30%
        } else if (name.contains("Clean Code")) {
            player.heal(20); // Hồi 20 máu
        } else if (name.contains("Legacy Support")) {
            player.increaseSpeed(50); // Tăng tốc độ chạy
        }
    }

    private void resumeGame() {
        currentState = State.RUNNING;
        // CỰC KỲ QUAN TRỌNG: Trả lại quyền điều khiển cho game (không còn dùng chuột trong menu)
        Gdx.input.setInputProcessor(null);
    }
}
