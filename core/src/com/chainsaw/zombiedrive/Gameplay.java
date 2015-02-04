package com.chainsaw.zombiedrive;

import com.badlogic.gdx.math.MathUtils;

final public class Gameplay {

    public static boolean spawnWrench;
    public static int score;
    public static int killCount_N;
    public static int killCount_F;
    public static int killCount_W;
    public static int wrenchTime;
    public static float level;
    public static boolean gamePaused = false;
    public static boolean wrenchOnScreen = false;
    public static float totalMileage;
    public static float exitMileage;
    public static boolean exitOnScreen = false;
    public static float carX;
    public static int carHealth;
    static int exitThreshold = 60;
    private static int zombieCount;
    private static int wZombieCount;
    private static int wrenchX;
    private static float mileage;
    private static int exitX;

    Gameplay() {

    }

    public static void resetScore() {
        score = 0;
        killCount_F = 0;
        killCount_N = 0;
        killCount_W = 0;
        zombieCount = 0;
        mileage = 0;
        exitMileage = 0;
//        spawnExit = false;
        exitOnScreen = false;
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


        /*
        Old zombie selection logic

        if (zombieCount < 14) {
            type = Zombie.ZOMBIE_NORMAL;
        } else {
            if (zombieCount < 21) {
                type = Zombie.ZOMBIE_FAT;
            } else {
                type = Zombie.ZOMBIE_FEMALE;
            }
        }

        if (zombieCount > 28) {
            zombieCount = 0;
        }*/

        if (wZombieCount == 0) {
            type = Zombie.ZOMBIE_WRENCH;
            wZombieCount--;
        }

        //TODO EXIT strategy
//        if (Gameplay.exitMileage > exitThreshold && !Gameplay.exitOnScreen) {
//            type = Zombie.ZOMBIE_EXIT;
//        }

        return type;
    }

    public static void addDistance(float delta) {
        mileage += delta;
        exitMileage += delta;
    }

    public static int getDistance() {
        return (int) mileage;
    }

    public static int getExitX() {
        return exitX;
    }

    public static void setExitX() {
        exitX = MathUtils.random(0,
                ZombieDrive.WIDTH - Assets.img_exit_sign.getRegionWidth());
    }

}
