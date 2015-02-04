package com.chainsaw.zombiedrive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Array;

public class SmokeParticles {
    public int x;
    public int y;
    ParticleEffectPool smokeEffectPool;
    Array<PooledEffect> effects = new Array<PooledEffect>();


    public SmokeParticles() {
        ParticleEffect smoke = new ParticleEffect();
        smoke.load(Gdx.files.internal("gfx/smoke.p"),
                Gdx.files.internal("gfx"));
        smokeEffectPool = new ParticleEffectPool(smoke, 1, 3);


    }

    public void createSmoke() {
        PooledEffect effect = smokeEffectPool.obtain();
        effect.setPosition(this.x, this.y);
        effects.add(effect);
    }

    public void stopSmoke() {
        effects.clear();
    }

    public void draw(Batch batch) {
        for (int i = effects.size - 1; i >= 0; i--) {
            PooledEffect effect = effects.get(i);
            effect.setPosition(this.x, this.y);
            effect.draw(batch, Gdx.graphics.getDeltaTime());
            if (effect.isComplete()) {
                effect.free();
                effects.removeIndex(i);

            }
        }

    }

}
