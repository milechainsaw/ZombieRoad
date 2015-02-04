package com.chainsaw.zombiedrive;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;

public class MainScreen implements Screen {

    public ArrayList<Zombie> zombies;
    public Game game;
    public Stage stage;
    public Road road;
    public Zombie zombie;
    public Car car;
    private HUD hud;
    private BloodParticleActor particles;
    private Rectangle carRect;
    private Rectangle zombiRect;
    private float spawnTime;

    private boolean menuVisible;
    private Stage menuStage;
    private TextButtonStyle btnStyleMute;
    private Pool<Zombie> pool = new Pool<Zombie>() {

        @Override
        protected Zombie newObject() {
            return new Zombie();
        }
    };

    public MainScreen(Game game) {
        btnStyleMute = new TextButtonStyle();
        btnStyleMute.font = Assets.font;


        //TODO SET GAME LEVEL
        Gameplay.level = 2f;
        Gameplay.setSpawnWrench(false);

        this.game = game;
        zombies = new ArrayList<Zombie>();
        car = new Car();

        hud = new HUD();
        particles = new BloodParticleActor();

        stage = new Stage(new StretchViewport(ZombieDrive.WIDTH, ZombieDrive.HEIGHT));
        road = new Road();

        stage.clear();
        stage.addActor(road);
        stage.addActor(car);
        stage.addActor(hud);
        stage.addActor(particles);

        Gdx.input.setCatchMenuKey(true);
        Gdx.input.setCatchBackKey(false);
        Gdx.input.setInputProcessor(stage);

        // Gameplay.gamePaused = false;

        spawnTime = TimeUtils.nanoTime();
        carRect = new Rectangle();
        zombiRect = new Rectangle();
        if (!Assets.isMuted)
            Assets.ambientMusic.play();
        menuVisible = false;
    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        if (Gameplay.gamePaused) {
            spawnTime = TimeUtils.nanoTime();   // Need this to wait for next spawn after pause
        }


        if (!Gameplay.gamePaused) {
            stage.act(Gdx.graphics.getDeltaTime());
            testCollision();
            playZombies();

            if (Gdx.input.isTouched()) {
                car.move(Gdx.input.getX() * ZombieDrive.ScaleWidht);
            }
            if (Gdx.input.isKeyPressed(Keys.MENU)
                    || Gdx.input.isKeyPressed(Keys.M)) {
                pauseGame();
            }
//            if (Gameplay.exitMileage > Gameplay.exitThreshold - 20
//                    && !Gameplay.spawnExit) {
//                Gameplay.spawnExit = true;
//                Gameplay.setExitX();
//            }

            if (car.wrecked) {
                //
                // TODO GAME OLIVER
                //
                Gameplay.totalMileage += Gameplay.getDistance();
                Gameplay.resetScore();
                game.setScreen(new LoadingScreen(game));

            }

        } else {
            if (menuVisible) {
                menuStage.draw();
                menuStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
            } else {
                showMenu(true);
            }
            if (Gdx.input.isKeyPressed(Keys.BACK)) {
                showMenu(false);
            }

        }

		/*
         * if (menuVisible) {
		 * menuStage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
		 * menuStage.draw(); }
		 */

    }

    private void testCollision() {

        for (int i = 0; i < zombies.size(); i++) {
			/*
			 * First check if Zombie is in hit range
			 */
            Zombie entity = zombies.get(i);
            if ((entity.getY() < car.getY() + car.getHeight())
                    && (!(entity.getY() + entity.getHeight() < car.getY()
                    + (car.getHeight() * 0.9)))) {

                zombiRect.set(entity.getX(), entity.getY(), entity.getWidth(),
                        entity.getHeight());
                carRect.set(car.getX(), car.getY(), car.getWidth(),
                        car.getHeight());

                if (OverlapTester.overlapRectangles(zombiRect, carRect)
                        && (!entity.dead)) {

					/*
					 * Check if we hit the WRENCH, if yes repair the car
					 */
                    if (entity.kindOfZombie == Zombie.ZOMBIE_WRENCH) {
                        car.repair();

                        //TODO Set speedup factor
                        Gameplay.level += 0.2;
                        Gameplay.setSpawnWrench(false);
                        Gameplay.wrenchOnScreen = false;
                        entity.remove();
                        pool.free(entity);
                        zombies.remove(i);

//                    } else if (entity.kindOfZombie == Zombie.ZOMBIE_EXIT) {
//
//                        // END OF LEVEL
//                        //
//                        //
//                        Gameplay.totalMileage += Gameplay.getDistance();
//                        Gameplay.resetScore();
//                        game.setScreen(new LoadingScreen(game));

                    } else {
                        car.hit(entity.hitpoints);
                        Gameplay.score += entity.hitpoints;
                        entity.killZombie();
                        particles.setPosition(car.getX() + car.getWidth() / 2,
                                entity.getY());
                        particles.createBlood();
                    }
					/*
					 * Update the HealthBar when anything is hit.
					 */
                    hud.setHealth(Car.health);

                }
            }

			/*
			 * If Zombie is out of bounds, release it
			 */

            if (entity.oob) {
                if (entity.kindOfZombie == Zombie.ZOMBIE_WRENCH) {
                    Gameplay.setSpawnWrench(true);
                    Gameplay.wrenchOnScreen = false;
                }

//                if (entity.kindOfZombie == Zombie.ZOMBIE_EXIT) {
//                    Gameplay.exitOnScreen = false;
//                    Gameplay.spawnExit = false;
//                    Gameplay.exitMileage = 0;
//                }
                entity.remove();
                pool.free(entity);
                zombies.remove(i);
            }

        }
    }

    private void playZombies() {
        if (TimeUtils.nanoTime() - spawnTime > Gameplay.getSpawnTime()) {
            spawnZombie();
        }
    }

    public void spawnZombie() {
        int zombieType = Gameplay.getZombieType();
        float last_x = MathUtils.random(0, (int) (ZombieDrive.WIDTH - Math
                .ceil(Zombie.getWidthByType(zombieType) * 1.5)));

        Zombie zombie = pool.obtain();
        zombie.init(zombieType, last_x);

        if (Car.health < 30 && !Gameplay.spawnWrench) {
            Gameplay.setSpawnWrench(true);
        }

        Gameplay.setZombieCount();
        if (zombieType == Zombie.ZOMBIE_WRENCH) {
            Gameplay.setSpawnWrench(false);
            Gameplay.wrenchOnScreen = true;
            zombie.setX(Gameplay.getWrenchX());
        }

//        if (zombieType == Zombie.ZOMBIE_EXIT) {
//            Gameplay.exitOnScreen = true;
//            Gameplay.spawnExit = false;
//            zombie.setX(Gameplay.getExitX());
//        }

        zombies.add(zombie);
        stage.addActor(zombie);
        spawnTime = TimeUtils.nanoTime();

    }

    private void showMenu(Boolean show) {
        if (!Assets.isMuted)
            Assets.hit_zombie_wrench.play();

        if (show) {
            Gdx.input.setCatchBackKey(true);
			/*
			 * all logic required to show a menu
			 */
            menuStage = new Stage();
            menuVisible = true;

            Gdx.input.setInputProcessor(menuStage);

            TextButtonStyle btnStyle = new TextButtonStyle();
            btnStyle.font = Assets.font;

            if (Assets.isMuted)
                menuSetMuteColor(Color.RED);
            if (!Assets.isMuted)
                menuSetMuteColor(Color.WHITE);

            Button resumeButton = new TextButton("resume", btnStyle);
            Button muteButton = new TextButton("Mute", btnStyleMute);
            muteButton.setWidth(muteButton.getWidth() + 40);

            Window dialogWindow = new Window("PAUSE", new WindowStyle(
                    Assets.font, Color.RED, null));

            dialogWindow.setHeight(ZombieDrive.HEIGHT / 3);
            dialogWindow.setWidth(ZombieDrive.WIDTH / 2);
            dialogWindow.setPosition(ZombieDrive.WIDTH / 4,
                    ZombieDrive.HEIGHT / 4);

            dialogWindow.align(Align.center);
            dialogWindow.row().fill().expandX();
            dialogWindow.add(resumeButton);
            dialogWindow.add(muteButton);

            resumeButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    showMenu(false);
                }

            });

            muteButton.addListener(new ChangeListener() {

                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (Assets.isMuted) {
                        Assets.unMute();
                        menuSetMuteColor(Color.WHITE);
                    } else {
                        menuSetMuteColor(Color.RED);
                        Assets.mute();
                    }
                }

            });

            menuStage.addActor(dialogWindow);
			/*
			 * End of Menu Logic
			 */

        } else {
            Gdx.input.setCatchBackKey(false);
            Gdx.input.setInputProcessor(stage);
            menuStage.dispose();
            resumeGame();
            menuVisible = false;
        }

    }

    private void menuSetMuteColor(Color color) {
        btnStyleMute.fontColor = color;
    }

    private void pauseGame() {
        Gameplay.gamePaused = true;
    }

    private void resumeGame() {
        Gameplay.gamePaused = false;
    }

    @Override
    public void resize(int width, int height) {
        // TODO Auto-generated method stub

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
        pauseGame();
    }

    @Override
    public void resume() {
        Gdx.app.log("Resume iz MainScreen.java", "p");
    }

    @Override
    public void dispose() {
        stage.dispose();
        Assets.destroy();
    }
}
