package com.chainsaw.zombieroad;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.badlogic.gdx.utils.TimeUtils;

public class Zombie extends Actor implements Poolable {

    public static final int ZOMBIE_NORMAL = 0;
    public static final int ZOMBIE_FEMALE = 1;
    public static final int ZOMBIE_FAT = 2;
    public static final int ZOMBIE_WRENCH = 3;
    public static final int hitpointsFat = 10;
    public static final int hitpointsFemale = 15;
    public static final int hitpointsNormal = 5;
    public int kindOfZombie;
    public int hitpoints;
    public Sound hitSound;
    public TextureRegion zombieImg;
    public boolean oob;
    public boolean dead;
    public Vector2 position = new Vector2();
    public long spawnTime;
    private float speed;
    // Vars for shadow
    private float shadowAngle;
    private float shadowAlpha;
    private double angle;
    private double k;
    //
    private double j;

    public Zombie() {

    }

    public static float getWidthByType(int type) {
        float w;

        switch (type) {
            case ZOMBIE_FAT:
                w = Assets.img_zombie_fat.getRegionWidth();
                break;
            case ZOMBIE_FEMALE:
                w = Assets.img_zombie_female.getRegionWidth();
                break;
            case ZOMBIE_NORMAL:
                w = Assets.img_zombie_normal.getRegionWidth();
                break;
            case ZOMBIE_WRENCH:
                w = Assets.img_zombie_wrench.getRegionWidth();
                break;
            default:
                w = Assets.img_zombie_normal.getRegionWidth();

        }

        return w;
    }

    public void init(int zombieType, float x) {
        dead = false;
        oob = false;

        setType(zombieType);
        setPosition(x, ZombieRoad.HEIGHT);
        this.spawnTime = TimeUtils.nanoTime();
        position.x = x;
    }

    @Override
    public void act(float delta) {
        speed = ZombieRoad.HEIGHT
                / (float) (25 / (ZombieRoad.GAME_SPEED * Gameplay.level));
        this.setY(this.getY() - (speed * delta));

        if (getY() < 20) {
            this.oob = true;
            this.remove();
        }
        position.y = getY();

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        if (getY() > 0) {
            if (!dead && kindOfZombie != Zombie.ZOMBIE_WRENCH && !Gameplay.carWrecked) {
                if (getY() > ZombieRoad.HEIGHT / 7 + Car.height) {
                    setZIndex(2);
                    calcShadowParams(getX(), getY());
                    batch.setColor(1, 1, 1, shadowAlpha);
                    batch.draw(Assets.img_zombie_shadow, getX(), getY(), 0, 0,
                            1, 1, Assets.img_zombie_shadow.getRegionWidth(),
                            Assets.img_zombie_shadow.getRegionHeight(),
                            shadowAngle);
                }
            }
            setZIndex(3);
            batch.setColor(1, 1, 1, 1);
            batch.draw(this.zombieImg, getX(), getY());
        }

    }

    private void calcShadowParams(float x, float y) {
        j = (int) x - (int) Gameplay.carX;
        k = (int) ((ZombieRoad.HEIGHT / 7) + Car.height) - (int) y;
        angle = Math.atan(j / k);
        shadowAngle = (float) Math.toDegrees(angle);

        if (Math.abs(shadowAngle) > 0 && Math.abs(shadowAngle) < 45) {
            shadowAlpha = Interpolation.exp5Out
                    .apply(1 - Math.abs(shadowAngle) / 45);
        } else {
            shadowAlpha = 0;
            if (shadowAngle == 0)
                shadowAlpha = 1;
        }

    }

    public int getType() {
        return kindOfZombie;
    }

    public void setType(int zombieType) {

        kindOfZombie = zombieType;

        switch (zombieType) {
            case ZOMBIE_FAT:
                hitSound = Assets.hit_zombie_fat;
                zombieImg = Assets.img_zombie_fat;
                hitpoints = hitpointsFat;
                setWidth(Assets.img_zombie_fat.getRegionWidth());
                setHeight(Assets.img_zombie_fat.getRegionHeight());
                break;
            case ZOMBIE_FEMALE:
                hitSound = Assets.hit_zombie_female;
                hitpoints = hitpointsFemale;
                zombieImg = Assets.img_zombie_female;
                setWidth(Assets.img_zombie_female.getRegionWidth());
                setHeight(Assets.img_zombie_female.getRegionHeight());
                break;
            case ZOMBIE_NORMAL:
                hitSound = Assets.hit_zombie_normal;
                hitpoints = hitpointsNormal;
                zombieImg = Assets.img_zombie_normal;
                setWidth(Assets.img_zombie_normal.getRegionWidth());
                setHeight(Assets.img_zombie_normal.getRegionHeight());
                break;
            case ZOMBIE_WRENCH:
                hitSound = Assets.hit_zombie_wrench;
                zombieImg = Assets.img_zombie_wrench;
                hitpoints = 0;
                setWidth(Assets.img_zombie_wrench.getRegionWidth());
                setHeight(Assets.img_zombie_wrench.getRegionHeight());
                break;
//            case ZOMBIE_EXIT:
//                hitSound = Assets.hit_zombie_wrench;
//                zombieImg = Assets.img_exit_sign;
//                hitpoints = 0;
//                setWidth(Assets.img_exit_sign.getRegionWidth());
//                setHeight(Assets.img_exit_sign.getRegionHeight());
//                break;

        }
    }

    public void killZombie() {
        if (!dead && !Assets.isMuted) {
            hitSound.play(0.3f);
        }
        zombieImg = Assets.img_blood;
        if (!Gameplay.gamePaused) {
            switch (kindOfZombie) {
                case ZOMBIE_FAT:
                    Gameplay.killCount_F++;
                    break;
                case ZOMBIE_FEMALE:
                    Gameplay.killCount_W++;
                    break;
                case ZOMBIE_NORMAL:
                    Gameplay.killCount_N++;
                    break;
            }

            dead = true;
        }
    }

    @Override
    public void reset() {
        dead = false;

    }

}
