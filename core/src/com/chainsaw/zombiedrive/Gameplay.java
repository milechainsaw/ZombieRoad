package com.chainsaw.zombiedrive;

import com.badlogic.gdx.math.MathUtils;

final public class Gameplay {

    public static boolean spawnWrench;
    public static int score;
    public static int killCount_N;
    public static int killCount_F;
    public static int killCount_W;
    public static float level;
    public static boolean gamePaused = false;
    public static boolean wrenchOnScreen = false;
    public static float totalMileage;
    public static float carX;
    public static boolean gameOver;
    public static boolean carWrecked = false;
    private static int zombieCount;
    private static int wZombieCount;
    private static int wrenchX;
    private static float mileage;
    private static int exitX;

    Gameplay() {

    }

    public static void resetScore() {
        gameOver = false;
        gamePaused = false;
        carWrecked = false;
        score = 0;
        killCount_F = 0;
        killCount_N = 0;
        killCount_W = 0;
        zombieCount = 0;
        mileage = 0;
        totalMileage = 0;
    }

    public static int getWrenchX() {
        return wrenchX;
    }

    public static void setWrenchX() {
        wrenchX = MathUtils.random(0, ZombieDrive.WIDTH
                - Assets.img_zombie_wrench.getRegionWidth());
    }

    public static void setSpawnWrench(boolean setWrench) {
        spawnWrench = setWrench;
        if (spawnWrench) {
            if (wZombieCount == -1)
                wZombieCount = getWrenchSeparation();
            setWrenchX();
        } else {
            wZombieCount = -1;
        }

    }

    public static void setZombieCount() {
        zombieCount++;
        if (spawnWrench) {
            wZombieCount--;
        }
    }

    public static float getSpawnTime() {
        return MathUtils.random(1000000000, Integer.MAX_VALUE) / level;
    }

    public static int getWrenchSeparation() {
        return Math.round(level * 5);
    }

    public static int getZombieType() {
        int type = Zombie.ZOMBIE_NORMAL;

        if (zombieCount > MathUtils.random(15, 20) / level) {
            type = Zombie.ZOMBIE_FAT;
        }
        if (zombieCount > MathUtils.random(20, 25) / level) {
            type = Zombie.ZOMBIE_FEMALE;
        }
        if (zombieCount > MathUtils.random(25, 30) / level) {
            zombieCount = 0;
        }

        if (wZombieCount == 0) {
            type = Zombie.ZOMBIE_WRENCH;
            wZombieCount--;
        }

        return type;
    }

    public static void addDistance(float delta) {
        mileage += delta;
    }

    public static int getDistance() {
        return (int) mileage;
    }


}
