package com.chainsaw.zombiedrive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class HUD extends Actor {

    private static int maxValue = ZombieDrive.WIDTH;
    private static int height = ZombieDrive.HEIGHT / 15;
    private final int wrenchY = ZombieDrive.HEIGHT
            - Assets.img_zombie_wrench.getRegionHeight();
    private final int exitY = ZombieDrive.HEIGHT
            - Assets.img_exit_sign.getRegionHeight();
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

    public static void postSlowMessage() {

    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        this.setZIndex(13);

        if (Gameplay.spawnWrench && !Gameplay.wrenchOnScreen) {
            batch.setColor(1, 1, 1, alphaBlinker());
            batch.draw(Assets.img_zombie_wrench, Gameplay.getWrenchX(), wrenchY);
        }

        if (Gameplay.spawnExit && !Gameplay.exitOnScreen) {
            batch.setColor(1, 1, 1, alphaBlinker());
            batch.draw(Assets.img_exit_sign, Gameplay.getExitX(), exitY);
        }

        batch.setColor(1, 1, 1, 1);
        // drawScore(batch);
        this.setZIndex(12);
        batch.draw(hudTexture, 0, 0);

    }

    // private void drawScore(SpriteBatch batch) {

    //
    // Little zombie Images on the top
    //

    // Assets.font.setColor(Color.WHITE);
    // batch.draw(Assets.img_zombie_normal, 5, ZombieDrive.HEIGHT -
    // iconHeight);
    // Assets.font.draw(batch, "x " + String.valueOf(Gameplay.killCount_N)
    // + String.valueOf(Gdx.graphics.getFramesPerSecond()), scorePosX,
    // ZombieDrive.HEIGHT - iconHeight + scoreHeight);
    //
    // batch.draw(Assets.img_zombie_fat, 5, ZombieDrive.HEIGHT
    // - (iconHeight * 2));
    // Assets.font.draw(batch, "x " + String.valueOf(Gameplay.killCount_F),
    // scorePosX, ZombieDrive.HEIGHT - (iconHeight * 2) + scoreHeight);
    //
    // batch.draw(Assets.img_zombie_female, 5, ZombieDrive.HEIGHT
    // - (iconHeight * 3));
    // Assets.font.draw(batch, "x " + String.valueOf(Gameplay.killCount_W),
    // scorePosX, ZombieDrive.HEIGHT - (iconHeight * 3) + scoreHeight);
    //
    // Assets.font.draw(batch, prepActionString(health), 10, height
    // + Assets.font.getLineHeight());

    // }

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

        if (alphaBlinkerTemp <= 0.01f || alphaBlinkerTemp >= 1f)
            sign *= -1;
        alphaBlinkerTemp += sign * 0.02f;

        return alphaBlinkerTemp;

    }

    public void destroy() {
        pixmapBar.dispose();
        hudTexture.dispose();
    }
}