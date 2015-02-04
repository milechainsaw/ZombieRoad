package com.chainsaw.zombiedrive;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Car extends Actor {

    public static final int DAMAGE_NONE = 60;
    public static final int DAMAGE_HALF = 30;

    public static Rectangle position;

    public static float width = Assets.img_car_H.getRegionWidth();
    public static float height = Assets.img_car_H.getRegionHeight();
    public static int health;
    static int headlights_height = 370;
    public Boolean wrecked;
    private TextureRegion carImg;
    private TextureRegion headlightsImg;
    private Boolean smoking;
    private SmokeParticles smoke;

    public Car() {
        health = 100;
        carImg = Assets.img_car_H;
        headlightsImg = Assets.img_headlights;
        wrecked = false;
        setPosition((ZombieDrive.WIDTH / 2) - (width / 2),
                ZombieDrive.HEIGHT / 7);
        Gameplay.carX = getX();
        setWidth(width);
        setHeight(height);

        smoking = false;
        smoke = new SmokeParticles();
        smoke.x = (int) (getX() + width / 2);
        smoke.y = (int) (getY() + height - 20);
        //smoke.createSmoke();
    }

    public void move(float x) {
        if (this.getX() < 0) {
            this.setX(0);
        }
        if (this.getX() > ZombieDrive.WIDTH - width) {
            this.setX(ZombieDrive.WIDTH - width);
        }

        if (this.getX() > (x - width / 2) + 10
                || this.getX() < (x - width / 2) - 10) {
            if (this.getX() < (x - width / 2)) {
                this.setX((float) (this.getX() + (ZombieDrive.GAME_SPEED
                        * Gameplay.level / 2)));
            } else {
                this.setX((float) (this.getX() - (ZombieDrive.GAME_SPEED
                        * Gameplay.level / 2)));
            }
        }
        smoke.x = (int) (getX() + width / 2);
        Gameplay.carX = getX();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setZIndex(9);
        batch.draw(this.headlightsImg, getX()
                - (headlightsImg.getRegionWidth() / 2) + (width / 2), getY()
                - headlights_height + height);
        setZIndex(10);
        batch.draw(this.carImg, getX(), getY());
        if (smoking) smoke.draw(batch);


    }


    public void hit(int hitpoints) {
        health -= hitpoints;

        if (health < 0 || health == 0)
            wrecked = true;

        if (health < DAMAGE_HALF) {
            carImg = Assets.img_car_L;
            if (!smoking) {
                smoke.createSmoke();
                smoking = true;
            }

        } else if (health < DAMAGE_NONE) {
            carImg = Assets.img_car_M;
            if (smoking) {
                smoke.stopSmoke();
                smoking = false;
            }
        } else {
            carImg = Assets.img_car_H;
            if (smoking) {
                smoke.stopSmoke();
                smoking = false;
            }
        }

    }

    public void repair() {
        if (!Assets.isMuted)
            Assets.hit_zombie_wrench.play();
        health = 100;
        this.hit(0);
    }

    public void update() {

    }


}
