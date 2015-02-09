package com.chainsaw.zombiedrive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Mile on 09-Feb-15.
 */
public class ScoreActorMileage extends Actor {

    ScoreActorMileage() {
        if (!Assets.isMuted) Assets.hit_zombie_wrench.play();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setZIndex(99);
        drawScore(batch);
    }

    private void drawScore(Batch batch) {
        //Little zombie Images on the top
        Assets.font.setColor(Color.WHITE);
        Assets.font.draw(batch, "Distance traveled: ", getX(),
                getY() + Assets.font.getXHeight() * 1.5f);
        Assets.font.draw(batch, String.valueOf((int) (Gameplay.totalMileage / 1000)) + " miles", getX(), getY());

    }

}
