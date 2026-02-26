package com.tuan.game.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ExperienceGem {
    private Vector2 position;
    private Texture texture;
    private float xpValue = 10; // Mỗi viên ngọc kinh nghiệm có giá trị XP cố định
    private boolean active = true; // Biến để kiểm tra xem viên ngọc có còn tồn tại hay không
    private float width = 16, height = 16; // Kích thước viên ngọc

    public ExperienceGem(Texture texture, float x, float y) {
        this.texture = texture;
        this.position = new Vector2(x, y);
    }
    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
    public Vector2 getPosition() {
        return position;
    }
    public float getXpValue() {
        return xpValue;
    }
    public boolean isActive() {
        return active;
    }
    public void collect(){
        active = false; // Khi được thu thập, viên ngọc sẽ không còn tồn tại
    }
    public Rectangle getBounds(){
        return new Rectangle(position.x, position.y, width, height);
    }

}
