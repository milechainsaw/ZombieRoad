package com.chainsaw.zombieroad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.StretchViewport;

public class LoadingScreen implements Screen {


    private final Image taptoplaytext;
    private Game game;
    private Stage stage;
    private Image logo;
    private Image loadingFrame;
    private Image loadingBarHidden;
    private Image screenBg;
    private Image loadingBg;
    private Image zombie;
    private LoadingBar loadingBar;
    private float startX, endX;
    private float percent;
    private boolean tapInfoDisplayed = false;

    public LoadingScreen(Game g) {
        this.game = g;
        ZombieRoad.manager.load("data/loading.pack", TextureAtlas.class);
        ZombieRoad.manager.load("gfx/zombie.png", Texture.class);
        ZombieRoad.manager.load("gfx/taptoplay.png", Texture.class);
        ZombieRoad.manager.finishLoading();

        stage = new Stage(new StretchViewport(ZombieRoad.WIDTH * 2, ZombieRoad.HEIGHT * 2));

        TextureAtlas atlas = ZombieRoad.manager.get("data/loading.pack",
                TextureAtlas.class);
        zombie = new Image(ZombieRoad.manager.get("gfx/zombie.png", Texture.class));
        taptoplaytext = new Image(ZombieRoad.manager.get("gfx/taptoplay.png", Texture.class));

        logo = new Image(atlas.findRegion("libgdx-logo"));
        loadingFrame = new Image(atlas.findRegion("loading-frame"));
        loadingBarHidden = new Image(atlas.findRegion("loading-bar-hidden"));
        screenBg = new Image(atlas.findRegion("screen-bg"));
        loadingBg = new Image(atlas.findRegion("loading-frame-bg"));

        Animation anim = new Animation(0.05f,
                atlas.findRegions("loading-bar-anim"));
        anim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
        loadingBar = new LoadingBar(anim);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (ZombieRoad.manager.update()) {
            Assets.assign();
            displayTapInfo();
            if (Gdx.input.isTouched()) {
                game.setScreen(new MainScreen(game));
            }
        }

        percent = Interpolation.linear.apply(percent,
                ZombieRoad.manager.getProgress(), 0.1f);

        loadingBarHidden.setX(startX + endX * percent);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setWidth(450 - 450 * percent);
        loadingBg.invalidate();

        stage.act();
        stage.draw();

    }

    private void displayTapInfo() {
        if (!tapInfoDisplayed) {
            stage.addActor(taptoplaytext);
            stage.addActor(new ExplainerText(50, ZombieRoad.HEIGHT / 2));
        }
        tapInfoDisplayed = true;
    }

    @Override
    public void resize(int width, int height) {
        // stage.setViewport(ZombieDrive.WIDTH, ZombieDrive.HEIGHT, true);
    }

    @Override
    public void show() {
        screenBg.setSize(ZombieRoad.WIDTH * 2, ZombieRoad.HEIGHT * 2);
        logo.setX(10);
        logo.setY(10);
        logo.setScale(0.5f);
        loadingFrame.setX((stage.getWidth() - loadingFrame.getWidth()) / 2);
        loadingFrame.setY(((stage.getHeight() - loadingFrame.getHeight()) / 2) + 100);

        zombie.setSize(ZombieRoad.WIDTH / 2, ZombieRoad.HEIGHT / 2);
        zombie.setPosition(stage.getWidth() / 2 - zombie.getWidth() / 2f, 450 + stage.getHeight() / 2 - zombie.getHeight() / 2);

        taptoplaytext.setWidth(taptoplaytext.getWidth() * 2);
        taptoplaytext.setHeight(taptoplaytext.getHeight() * 2);
        taptoplaytext.setPosition(stage.getWidth() / 2 - taptoplaytext.getWidth() / 2, (stage.getHeight() / 2 - taptoplaytext.getHeight() * 2f) + 150);

        loadingBar.setX(loadingFrame.getX() + 15);
        loadingBar.setY(loadingFrame.getY() + 5);

        loadingBarHidden.setX(loadingBar.getX() + 35);
        loadingBarHidden.setY(loadingBar.getY() - 3);

        startX = loadingBarHidden.getX();
        endX = 440;

        loadingBg.setSize(450, 50);
        loadingBg.setX(loadingBarHidden.getX() + 30);
        loadingBg.setY(loadingBarHidden.getY() + 3);

        stage.addActor(screenBg);
        stage.addActor(loadingBar);
        stage.addActor(loadingBg);
        stage.addActor(loadingBarHidden);
        stage.addActor(loadingFrame);
        stage.addActor(logo);
        stage.addActor(zombie);

        Assets.queue();

    }

    @Override
    public void hide() {
        ZombieRoad.manager.unload("data/loading.pack");

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private class ExplainerText extends Actor {
        String explainerText = "Run over the Zombies \nuntil your damage is critical.\nRepair and repeat.";
        BitmapFont.TextBounds bounds;

        ExplainerText(float x, float y) {
            setX(x);
            setY(y);
            Assets.font.setScale(2);
            bounds = Assets.font.getBounds(explainerText);
        }

        @Override
        public void draw(Batch batch, float parentAlpha) {
            Assets.font.drawMultiLine(batch, explainerText, getX(), getY() + bounds.height);
            super.draw(batch, parentAlpha);
        }
    }
}
