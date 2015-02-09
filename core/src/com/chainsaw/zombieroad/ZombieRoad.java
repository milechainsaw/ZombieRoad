package com.chainsaw.zombieroad;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

public class ZombieRoad extends Game implements ApplicationListener {

    public static int WIDTH;
    public static int HEIGHT;
    public static int GAME_SPEED = 5;

    public static float ScaleWidht;
    public static float ScaleHeight;

    public static AssetManager manager;
    public static float wrenchVolume = 0.5f;


    @Override
    public void create() {
        HEIGHT = 800;
        WIDTH = 480;
        float aspR = (float) HEIGHT / (float) WIDTH;

        float relativeAspR = (float) Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

        aspR = aspR / relativeAspR;
        WIDTH = (int) ((float) WIDTH * aspR);

        ScaleWidht = ((float) WIDTH) / Gdx.graphics.getWidth();
        ScaleHeight = ((float) HEIGHT) / Gdx.graphics.getHeight();

        manager = new AssetManager();

        Gdx.gl20.glFlush();
        this.setScreen(new LoadingScreen(this));

    }

    @Override
    public void dispose() {

    }


    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
        if (this.getScreen() instanceof MainScreen) {
            this.getScreen().pause();
        }
    }

    @Override
    public void resume() {

    }

}
