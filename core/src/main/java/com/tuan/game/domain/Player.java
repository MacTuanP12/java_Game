package com.tuan.game.domain;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Vector2 position;
    private float health;
    private float speed = 200f; // pixels per second
    private Texture texture;

   public Player(Texture texture, float x, float y){
       this.texture = texture;
       this.position = new Vector2(x, y);
       this.health = 100;
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


}
