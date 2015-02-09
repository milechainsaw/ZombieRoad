package com.chainsaw.zombiedrive;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
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
    private SmokeAndExplosionParticles smoke;

    private float mLastX = 0;
    private boolean movingLeft;
    private boolean movingRight;

    public Car() {
        health = 10; // TODO adjust this!
        carImg = Assets.img_car_H;
        headlightsImg = Assets.img_headlights;
        wrecked = false;
        setPosition((ZombieDrive.WIDTH / 2) - (width / 2),
                ZombieDrive.HEIGHT / 7);
        Gameplay.carX = getX();
        setWidth(width);
        setHeight(height);

        smoking = false;
        smoke = new SmokeAndExplosionParticles();
        smoke.x = (int) (getX() + width / 2);
        smoke.y = (int) (getY() + height - 20);
        //smoke.createSmoke();
    }


    public void moveLeft() {
        skid();
        movingRight = false;
        movingLeft = true;
        this.setRotation(2f);
    }

    public void moveRight() {
        skid();
        movingLeft = false;
        movingRight = true;
        this.setRotation(-2);
    }

    public void stop() {
        movingLeft = false;
        movingRight = false;
        setRotation(0);
    }

    private void skid() {
        if (!Assets.isMuted)
            switch (MathUtils.random(0, 10)) {
                case 0:
                case 1:
                case 2:
                    Assets.skid_1.play(0.1f);
                    break;
                case 3:
                case 4:
                case 5:
                    Assets.skid_2.play(0.1f);
                    break;
                default:
                    break;

            }


    }


    @Override
    public void act(float delta) {
        if (this.getX() < 0) {
            this.setX(0);
        }
        if (this.getX() > ZombieDrive.WIDTH - width) {
            this.setX(ZombieDrive.WIDTH - width);
        }

        if (movingRight) {
            this.setX((float) (this.getX() + (ZombieDrive.GAME_SPEED
                    * Gameplay.level / 2)));
        }
        if (movingLeft) {
            this.setX((float) (this.getX() - (ZombieDrive.GAME_SPEED
                    * Gameplay.level / 2)));
        }

        smoke.x = (int) (getX() + width / 2);
        Gameplay.carX = getX();

        if (getX() > mLastX + 10 || getX() < mLastX - 10) {
            if (MathUtils.random(0, 10) > 5) {
                Assets.skid_1.play(0.1f);
            } else {
                Assets.skid_2.play(0.1f);
            }
        } else {
            setRotation(0f);
        }
        mLastX = getX();
        super.act(delta);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (!wrecked) {
            setZIndex(10);
            batch.setColor(1f, 1f, 1f, 1f);
            batch.draw(this.headlightsImg, getX()
                    - (headlightsImg.getRegionWidth() / 2) + (width / 2), getY()
                    - headlights_height + height);
            setZIndex(11);
            setColor(1f, 1f, 1f, 1f);
            batch.draw(this.carImg, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        } else {
            batch.setColor(1f, 1f, 1f, 1f);
            setZIndex(14);
            batch.draw(Assets.dark, 0, 0, ZombieDrive.WIDTH, ZombieDrive.HEIGHT);
        }
//        if (Gameplay.gamePaused && !wrecked) {
//            batch.draw(Assets.dark, 0, 0, ZombieDrive.WIDTH, ZombieDrive.HEIGHT);
//        }

        if (smoking) smoke.draw(batch);

    }


    public void hit(int hitpoints) {
        if (!wrecked) {
            health -= hitpoints;

            if (health < 0 || health == 0) {
                smoke.y = (int) (getY() + height / 2);
                if (!Assets.isMuted)
                    Assets.explode.play(0.4f);
                smoke.explode();
                wrecked = true;
            }

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
