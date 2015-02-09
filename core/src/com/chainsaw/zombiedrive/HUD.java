package com.chainsaw.zombiedrive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.TimeUtils;

public class HUD extends Actor {

    private static int maxValue = ZombieDrive.WIDTH;
    private static int height = ZombieDrive.HEIGHT / 25;
    private final int wrenchY = ZombieDrive.HEIGHT
            - Assets.img_zombie_wrench.getRegionHeight();

    boolean drawMessageText = false;
    private long messageStartTime = 0;
    private String mMessage = "";

    private Texture hudTexture;
    private Pixmap pixmapBar;
    private int health;
    private float alphaBlinkerTemp;
    private int sign = 1;


    public HUD() {
        pixmapBar = new Pixmap(maxValue, height, Pixmap.Format.RGBA8888);
        hudTexture = new Texture(pixmapBar);
        alphaBlinkerTemp = 1f;

        health = Car.health;
        prepareTexture();

    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.setZIndex(13);
        if (!Gameplay.gameOver) {
            if (Gameplay.spawnWrench && !Gameplay.wrenchOnScreen) {
                batch.setColor(1, 1, 1, alphaBlinker());
                batch.draw(Assets.img_zombie_wrench, Gameplay.getWrenchX(), wrenchY);
            }

            if (drawMessageText) {
                drawMessageText(batch);
            }

            batch.setColor(1, 1, 1, 1);
            this.setZIndex(12);
            batch.draw(hudTexture, 0, 0);
        }
    }

    private void drawMessageText(Batch batch) {
        if (messageStartTime == 0)
            messageStartTime = TimeUtils.nanoTime();
        if ((TimeUtils.nanoTime() - messageStartTime) < 5000000000l) {
            Assets.font.setColor(1f, 0.2f, 0.2f, 1f);
            BitmapFont.TextBounds bounds = Assets.font.getBounds(mMessage);
            Assets.font.draw(batch, mMessage, (ZombieDrive.WIDTH / 2) - (bounds.width / 2), (ZombieDrive.HEIGHT / 2) - (bounds.height));
        } else {
            messageStartTime = 0;
            drawMessageText = false;
        }
    }

    void setMessage(String text) {
        mMessage = text;
    }

    private void prepareTexture() {
        // Background Color
        pixmapBar.setColor(1, 1, 1, 0f);
        pixmapBar.fill();
        // Bar Color

        pixmapBar.setColor(getColor(health));
        pixmapBar.fillRectangle(0, 0, getFillAmount(), height);
        hudTexture.draw(pixmapBar, 0, 0);

    }

    public void setHealth(int h) {
        health = h;
        prepareTexture();
    }

    private int getFillAmount() {
        return (int) (maxValue * (Float.valueOf(health) / 100));
    }

    private Color getColor(int health) {
        if (health < Car.DAMAGE_HALF)
            return Color.RED;
        if (health < Car.DAMAGE_NONE)
            return Color.ORANGE;

        return Color.GREEN;
    }

    private float alphaBlinker() {
        if (Gameplay.gamePaused)
            return alphaBlinkerTemp;
        if (alphaBlinkerTemp <= 0.01f || alphaBlinkerTemp >= 1f)
            sign *= -1;
        alphaBlinkerTemp += sign * 0.02f;

        return alphaBlinkerTemp;

    }

}
