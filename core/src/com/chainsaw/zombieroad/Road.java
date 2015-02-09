package com.chainsaw.zombieroad;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Road extends Actor {

    public Sprite screenBackground;
    private Texture roadImg;
    private float speed;

    public Road() {
        screenBackground = new Sprite();
        roadImg = Assets.img_road;
        screenBackground.setSize(ZombieRoad.WIDTH, ZombieRoad.HEIGHT);
        roadImg.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
        screenBackground.setRegion(roadImg);
    }

    @Override
    public void act(float delta) {
        speed = (ZombieRoad.HEIGHT / (float) (25 / (ZombieRoad.GAME_SPEED * Gameplay.level)))
                * delta / roadImg.getHeight();
        screenBackground.scroll(0, -speed);
        if (!Gameplay.gamePaused)
            Gameplay.addDistance(speed);
    }

    @Override
    public void draw(Batch batch, float delta) {
        setZIndex(0);
        batch.disableBlending();
        batch.setColor(1f, 1f, 1f, 1f);
        screenBackground.draw(batch);
        batch.enableBlending();
    }

}
