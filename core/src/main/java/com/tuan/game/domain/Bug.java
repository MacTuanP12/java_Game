package com.tuan.game.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Bug {
    private Vector2 position;
    private float speed = 100f;
    private Texture texture;
    private final float width = 32, height = 32; // Kích thước quái vật

    public Vector2 getPosition() {
        return position;
    }

    public Bug(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
    }

    // Logic: Đuổi theo tọa độ của Player
    public void update(float delta, Vector2 playerPosition) {
        // Tạo một vector hướng từ Bug đến Player
        Vector2 direction = new Vector2(playerPosition).sub(position).nor();

        // Di chuyển Bug theo hướng đó
        position.add(direction.scl(speed * delta));
    }


    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y, width, height);
    }

    // Getter này dùng để xử lý va chạm sau này
    public Rectangle getBounds() {
        return new Rectangle(position.x, position.y, width, height);
    }
}
