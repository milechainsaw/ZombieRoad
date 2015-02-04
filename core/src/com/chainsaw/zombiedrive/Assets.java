package com.chainsaw.zombiedrive;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class Assets {

    public static TextureRegionDrawable img_sound_on;
    public static TextureRegionDrawable img_sound_off;

    public static TextureRegion img_zombie_fat;
    public static TextureRegion img_zombie_female;
    public static TextureRegion img_zombie_normal;
    public static TextureRegion img_zombie_wrench;
    public static TextureRegion img_zombie_shadow;

    public static TextureRegion img_car_H;
    public static TextureRegion img_car_M;
    public static TextureRegion img_car_L;
    public static TextureRegion img_headlights;

    public static TextureRegion img_blood;

    public static Texture img_road;

    public static TextureRegion img_exit_sign;

    public static Texture img_mainmenu;
    public static Sound hit_zombie_fat;
    public static Sound hit_zombie_female;
    public static Sound hit_zombie_normal;
    public static Sound hit_zombie_wrench;
    public static BitmapFont font;
    public static Music ambientMusic;
    public static Boolean isMuted;
    static TextureAtlas mainTextures;

    private static TextureRegion findRegion(String region) {
        return new TextureRegion(mainTextures.findRegion(region));
    }

    public static void queue() {
        ZombieDrive.manager.load("gfx/MainScreen.atlas", TextureAtlas.class);
        ZombieDrive.manager.load("gfx/font.fnt", BitmapFont.class);
        ZombieDrive.manager.load("sfx/hit_1.wav", Sound.class);
        ZombieDrive.manager.load("sfx/hit_2.wav", Sound.class);
        ZombieDrive.manager.load("sfx/hit_3.wav", Sound.class);
        ZombieDrive.manager.load("sfx/hit_wrench.wav", Sound.class);
        ZombieDrive.manager.load("sfx/ambient.mp3", Music.class);
        ZombieDrive.manager.load("gfx/sound.png", Texture.class);
        ZombieDrive.manager.load("gfx/mute.png", Texture.class);
        ZombieDrive.manager.load("gfx/road.png", Texture.class);

    }

    public static void assign() {
        mainTextures = ZombieDrive.manager.get("gfx/MainScreen.atlas",
                TextureAtlas.class);

        img_zombie_fat = findRegion("zombie_F");
        img_zombie_female = findRegion("zombie_W");
        img_zombie_normal = findRegion("zombie_N");
        img_zombie_wrench = findRegion("wrench");
        img_zombie_shadow = findRegion("zombie_shadow");
        img_car_H = findRegion("car_Q");
        img_car_M = findRegion("car_W");
        img_car_L = findRegion("car_E");
        img_headlights = findRegion("headlights");
        img_blood = findRegion("blood");
        img_road = ZombieDrive.manager.get("gfx/road.png", Texture.class);
        img_exit_sign = findRegion("exit_sign");

        img_sound_off = new TextureRegionDrawable(new TextureRegion(ZombieDrive.manager.get("gfx/mute.png", Texture.class)));

        img_sound_on = new TextureRegionDrawable(new TextureRegion(ZombieDrive.manager.get("gfx/sound.png", Texture.class)));

        font = ZombieDrive.manager.get("gfx/font.fnt", BitmapFont.class);

        hit_zombie_fat = ZombieDrive.manager.get("sfx/hit_1.wav", Sound.class);
        hit_zombie_female = ZombieDrive.manager.get("sfx/hit_2.wav",
                Sound.class);
        hit_zombie_normal = ZombieDrive.manager.get("sfx/hit_3.wav",
                Sound.class);
        hit_zombie_wrench = ZombieDrive.manager.get("sfx/hit_wrench.wav",
                Sound.class);

        ambientMusic = ZombieDrive.manager.get("sfx/ambient.mp3", Music.class);

        ambientMusic.setLooping(true);
        ambientMusic.setVolume(0.5f);

        isMuted = false;

    }

    public static void mute() {
        ambientMusic.setVolume(0);
        isMuted = true;
    }

    public static void unMute() {
        ambientMusic.setVolume(0.5f);
        isMuted = false;
    }

    public static void destroy() {
        ZombieDrive.manager.dispose();
    }

}
