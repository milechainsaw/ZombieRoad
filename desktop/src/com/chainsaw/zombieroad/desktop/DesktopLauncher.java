package com.chainsaw.zombieroad.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.chainsaw.zombieroad.ZombieRoad;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Zombie Drive";
        cfg.width = 480;
        cfg.height = 800;

        new LwjglApplication(new ZombieRoad(), cfg);
    }
}
