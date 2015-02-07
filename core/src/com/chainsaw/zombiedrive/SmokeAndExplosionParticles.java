package com.chainsaw.zombiedrive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.utils.Array;

public class SmokeAndExplosionParticles {
    public int x;
    public int y;
    ParticleEffectPool smokeEffectPool;
    ParticleEffectPool explosionPool;
    Array<PooledEffect> effects = new Array<PooledEffect>();


    public SmokeAndExplosionParticles() {
        ParticleEffect smoke = new ParticleEffect();
        smoke.load(Gdx.files.internal("gfx/smoke.p"),
                Gdx.files.internal("gfx"));
        smokeEffectPool = new ParticleEffectPool(smoke, 2, 4);

        ParticleEffect explosion = new ParticleEffect();
        explosion.load(Gdx.files.internal("gfx/explode.p"), Gdx.files.internal("gfx"));
        explosionPool = new ParticleEffectPool(explosion, 1, 1);
    }

    public void createSmoke() {
        PooledEffect effect = smokeEffectPool.obtain();
        effect.setPosition(this.x, this.y);
        effects.add(effect);
    }

    public void explode() {
        effects.clear();
        PooledEffect effect = explosionPool.obtain();
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
