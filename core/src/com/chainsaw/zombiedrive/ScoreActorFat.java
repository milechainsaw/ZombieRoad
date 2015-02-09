package com.chainsaw.zombiedrive;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by Mile on 09-Feb-15.
 */
public class ScoreActorFat extends Actor {

    ScoreActorFat() {
        if (!Assets.isMuted) Assets.hit_zombie_wrench.play();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setZIndex(99);
        drawScore(batch);
        // super.draw(batch, parentAlpha);
    }


    private void drawScore(Batch batch) {
        //Little zombie Images on the top
        Assets.font.setColor(Color.WHITE);
        batch.draw(Assets.img_zombie_fat, getX(), getY());
        Assets.font.draw(batch, "x " + String.valueOf(Gameplay.killCount_F), getX() + Assets.img_zombie_fat.getRegionWidth() * 2,
                getY() + Assets.font.getXHeight() * 1.5f);

//

    }

}
