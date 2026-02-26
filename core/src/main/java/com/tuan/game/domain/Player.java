package com.tuan.game.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private float health = 100f;
    private float speed = 200f; // pixels per second
    private Texture texture;
    private float width = 64;
    private float height = 64;
    private boolean isAlive = true;

   public Player(Texture texture, float x, float y){
       this.texture = texture;
       this.position = new Vector2(x, y);
       this.health = 100;
   }
   public void takeDamage(float amount){
       if(!isAlive){
           return;
       }
         health -= amount;
            if(health <= 0){
                health = 0;
                isAlive = false;
                System.out.println("Player has died!/n" +
                    "Game Over!");
            }
   }
   public float getHealth() {
       return health;
   }
   public boolean isAlive(){
       return isAlive; // fixed: was calling itself recursively
   }

   public void heal(float amount) {
       health = Math.min(100f, health + amount);
   }

   public void increaseSpeed(float amount) {
       speed += amount;
   }
   public void update(float deltalTime){
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
       batch.draw(texture, position.x, position.y);

   }
   public Vector2 getPosition() {
       return position;
   }

    public Rectangle getBounds() {
        // Trả về một hình chữ nhật tại vị trí hiện tại của vật thể
        return new Rectangle(position.x, position.y, width, height);
    }


}
