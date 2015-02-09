package com.chainsaw.zombieroad;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Mile on 09-Feb-15.
 */
public class ScoreActorFat extends Actor {

    ScoreActorFat() {
        if (!Assets.isMuted) Assets.hit_zombie_wrench.play(ZombieRoad.wrenchVolume);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setZIndex(99);
        drawScore(batch);
    }


    private void drawScore(Batch batch) {
        Assets.font.setColor(Color.WHITE);
        batch.setColor(1, 1, 1, 1);
        batch.draw(Assets.img_zombie_fat, getX(), getY());
        Assets.font.setScale(1);
        Assets.font.draw(batch, "x " + String.valueOf(Gameplay.killCount_F), getX() + Assets.img_zombie_fat.getRegionWidth() * 2,
                getY() + Assets.font.getXHeight() * 1.5f);

//

    }

}
