package com.tuan.game.domain;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Projectile {
    private Vector2 position;
    private Vector2 velocity;
    private float speed = 500f;
    private Texture texture;
    private boolean active = true;
    private float width = 16, height = 16;

    public Projectile(Texture texture, float x, float y, Vector2 direction) {
        this.texture = texture;
        this.position = new Vector2(x, y);
        // hướng bay được chuẩn hóa và nhân với tốc độ
        this.velocity = new Vector2(direction).nor().scl(speed);
    }
    public void update(float delta) {
        if (active) {
            position.add(velocity.x * delta, velocity.y * delta);
        }
        //vô hiệu hóa khi ra khỏi màn hình
        if(position.x < 0 || position.x > 1280 || position.y < 0 || position.y > 720){
            active = false;
        }
    }
    public void draw(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, position.x, position.y, width, height);
        }
    }
    public Rectangle getBounds(){
        return new Rectangle(position.x, position.y, width, height);
    }
    public boolean isActive() {
        return active;
    }
    public void setInactive() {
        active = false;
    }
}
