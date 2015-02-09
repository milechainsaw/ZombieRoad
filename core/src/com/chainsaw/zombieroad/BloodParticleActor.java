package com.chainsaw.zombieroad;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;

public class BloodParticleActor extends Actor {
    ParticleEffectPool bloodEffectPool;
    Array<PooledEffect> effects = new Array<PooledEffect>();


    public BloodParticleActor() {
        ParticleEffect splash = new ParticleEffect();
        splash.load(Gdx.files.internal("gfx/splash.p"),
                Gdx.files.internal("gfx"));
        bloodEffectPool = new ParticleEffectPool(splash, 1, 3);


    }

    public void createBlood() {
        PooledEffect effect = bloodEffectPool.obtain();
        effect.setPosition(this.getX(), this.getY());
        effects.add(effect);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        setZIndex(22);
        for (int i = effects.size - 1; i >= 0; i--) {
            PooledEffect effect = effects.get(i);
            effect.draw(batch, Gdx.graphics.getDeltaTime());
            if (effect.isComplete()) {
                effect.free();
                effects.removeIndex(i);

            }
        }

    }

}
