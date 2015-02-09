package com.chainsaw.zombieroad;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

public class SplashScreen implements Screen {

    private Texture splash;
    private Game game;
    private Stage stage;
    private Image splashImg;

    public SplashScreen(Game g) {
        this.game = g;
        stage = new Stage();
        stage.clear();
        splash = new Texture(Gdx.files.internal("gfx/splash.png"));
        splashImg = new Image(splash);
        splashImg.addAction(Actions.sequence(Actions.delay(1.0f),
                Actions.fadeOut(0.5f), Actions.run(new Runnable() {
                    public void run() {
                        game.setScreen(new MainScreen(game));
                    }
                })));
        stage.addActor(splashImg);

    }

    @Override
    public void render(float delta) {
        // TODO Auto-generated method stub
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // stage.setViewport(ZombieDrive.WIDTH, ZombieDrive.HEIGHT, true);
    }

    @Override
    public void show() {

    }

    @Override
    public void hide() {
        // TODO Auto-generated method stub

    }

    @Override
    public void pause() {
        // TODO Auto-generated method stub

    }

    @Override
    public void resume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        stage.dispose();
        splash.dispose();
    }

}
