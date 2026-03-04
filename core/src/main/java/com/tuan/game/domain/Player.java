package com.tuan.game.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private Vector2 previousPosition; // Dùng để rollback khi va chạm tường
    private float health = 100f;
    private float speed = 200f; // pixels per second
    private Texture texture;
    private float width = 64;
    private float height = 64;
    private boolean isAlive = true;
    private float visionRadius = 1.0f; // Tỉ lệ tầm nhìn (có thể nâng cấp)

    // Listener để thông báo cho EnvironmentState khi bị đánh
    public interface HitListener { void onHit(); }
    private HitListener hitListener;

    public void setHitListener(HitListener l) { this.hitListener = l; }

   public Player(Texture texture, float x, float y){
       this.texture = texture;
       this.position = new Vector2(x, y);
       this.previousPosition = new Vector2(x, y);
       this.health = 100;
   }
   public void takeDamage(float amount){
       if(!isAlive){
           return;
       }
         health -= amount;
         if (hitListener != null) hitListener.onHit();
            if(health <= 0){
                health = 0;
                isAlive = false;
            }
   }
   public float getHealth()  { return health; }
   public boolean isAlive()  { return isAlive; }
   public float getWidth()   { return width; }
   public float getHeight()  { return height; }

   public void heal(float amount) {
       health = Math.min(100f, health + amount);
   }

   public void increaseSpeed(float amount) {
       speed += amount;
   }

    /** Lưu vị trí trước khi di chuyển để rollback nếu va tường */
    public void savePreviousPosition() {
        previousPosition.set(position);
    }

    /** Quay về vị trí trước (gọi khi phát hiện va chạm tường) */
    public void rollbackPosition() {
        position.set(previousPosition);
    }

   public void update(float deltalTime){
       savePreviousPosition();
       if(Gdx.input.isKeyPressed(Input.Keys.W)){
           position.y += speed * deltalTime;
       }
         if(Gdx.input.isKeyPressed(Input.Keys.S)){
              position.y -= speed * deltalTime;
         }
            if(Gdx.input.isKeyPressed(Input.Keys.A)){
                position.x -= speed * deltalTime;
            }
                if(Gdx.input.isKeyPressed(Input.Keys.D)){
                    position.x += speed * deltalTime;
                }

   }
   public void draw(SpriteBatch batch){
       batch.draw(texture, position.x, position.y, width, height);

   }
   public Vector2 getPosition() { return position; }

    public Rectangle getBounds() {
        // Trả về một hình chữ nhật tại vị trí hiện tại của vật thể
        return new Rectangle(position.x, position.y, width, height);
    }
    public float getVisionRadius()          { return visionRadius; }
    public void  setVisionRadius(float s)   { this.visionRadius = s; }
}
