package com.chainsaw.zombiedrive;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.ArrayList;

public class MainScreen implements Screen {

    public ArrayList<Zombie> zombies;
    public Game game;
    public Stage stage;
    public Road road;
    public Car car;
    //buttons
    Button muteButton;
    Button playButton;
    Button leftButton;
    Button rightButton;
    private HUD hud;
    private BloodParticleActor particles;
    private Rectangle carRect;
    private Rectangle zombiRect;
    private float spawnTime;
    private TextButtonStyle btnStyleMute;
    private Pool<Zombie> pool = new Pool<Zombie>() {

        @Override
        protected Zombie newObject() {
            return new Zombie();
        }
    };
    private float buttonsAlpha = 0.4f;
    private boolean mTouchToReset;


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

        spawnTime = TimeUtils.nanoTime();
        carRect = new Rectangle();
        zombiRect = new Rectangle();

        Assets.ambientMusic.play();
        Assets.engineSound.play();
        setUpMuteButton(stage);
        setUpPlayPause(stage);
        setUpSteering(stage);

    }

    @Override
    public void render(float delta) {
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();

        if (Gameplay.gamePaused) {
            spawnTime = TimeUtils.nanoTime();   // Need this to wait for next spawn after pause
        }

        if (Gameplay.gameOver) {
            stage.act(Gdx.graphics.getDeltaTime());
        }

        if (mTouchToReset && Gdx.input.isTouched()) {
            newGame();
        }

        if (!Gameplay.gamePaused) {
            stage.act(Gdx.graphics.getDeltaTime());
            testCollision();
            playZombies();


            if (car.wrecked) {
                //
                // TODO GAME OLIVER
                //
                // hud.remove();
                removeAllButtons();
                Gameplay.totalMileage += Gameplay.getDistance();
                hud.addAction(Actions.sequence(Actions.delay(3f), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        if (!Gameplay.gameOver) drawScore();
                        Gameplay.gameOver = true;
                        Gameplay.gamePaused = true;
                    }
                })));
            }
        }

    }

    private void drawScore() {

        final ScoreActorNormal scoreActorNormal = new ScoreActorNormal();

        int y = ZombieDrive.HEIGHT / 2 + ZombieDrive.HEIGHT / 4;
        scoreActorNormal.setPosition(70, y);

        final float delay = 1f;
        scoreActorNormal.addAction(Actions.sequence(Actions.delay(delay), Actions.run(new Runnable() {
            @Override
            public void run() {
                final ScoreActorFat scoreActorFat = new ScoreActorFat();
                scoreActorFat.setPosition(70, scoreActorNormal.getY() - Assets.img_zombie_female.getRegionHeight() * 1.2f);
                stage.addActor(scoreActorFat);
                scoreActorFat.addAction(Actions.sequence(Actions.delay(delay), Actions.run(new Runnable() {
                    @Override
                    public void run() {
                        final ScoreActorWoman scoreActorWoman = new ScoreActorWoman();
                        scoreActorWoman.setPosition(70, scoreActorFat.getY() - Assets.img_zombie_female.getRegionHeight() * 1.2f);
                        scoreActorWoman.addAction(Actions.sequence(Actions.delay(delay), Actions.run(new Runnable() {
                            @Override
                            public void run() {
                                ScoreActorMileage scoreActorMileage = new ScoreActorMileage();
                                scoreActorMileage.setPosition(70, scoreActorWoman.getY() - Assets.img_zombie_female.getRegionHeight() * 1.2f);
                                stage.addActor(scoreActorMileage);
                                mTouchToReset = true;
                            }
                        })));
                        stage.addActor(scoreActorWoman);
                    }
                })));

            }
        })));
        stage.addActor(scoreActorNormal);
    }

    private void testCollision() {
        if (!car.wrecked) {
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
                            hud.setMessage("Faster!");
                            hud.drawMessageText = true;
                            car.repair();

                            //TODO Set speedup factor
                            Gameplay.level += 0.2;
                            Gameplay.setSpawnWrench(false);
                            Gameplay.wrenchOnScreen = false;
                            entity.remove();
                            pool.free(entity);
                            zombies.remove(i);


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


    private void setUpMuteButton(Stage stage) {
        ImageButton.ImageButtonStyle muteStyle = new ImageButton.ImageButtonStyle();
        muteStyle.checked = Assets.img_sound_off;
        muteStyle.up = Assets.img_sound_on;

        muteButton = new Button(muteStyle) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                setColor(1, 1, 1, buttonsAlpha);
                setZIndex(99);
                super.draw(batch, parentAlpha);
            }
        };

        if (Assets.isMuted) {
            Assets.unMute();
            muteButton.setChecked(false);
        } else {
            muteButton.setChecked(true);
            Assets.mute();
        }


        muteButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Assets.isMuted) {
                    Assets.unMute();
                    muteButton.setChecked(false);
                } else {
                    muteButton.setChecked(true);
                    Assets.mute();
                }
                super.touchUp(event, x, y, pointer, button);
            }
        });

        muteButton.setWidth(80);
        muteButton.setHeight(80);
        muteButton.setX(ZombieDrive.WIDTH - 80);
        muteButton.setY(ZombieDrive.HEIGHT - 80);

        stage.addActor(muteButton);
    }

    private void setUpPlayPause(Stage stage) {
        ImageButton.ImageButtonStyle playStyle = new ImageButton.ImageButtonStyle();
        playStyle.checked = Assets.img_play;
        playStyle.up = Assets.img_pause;

        playButton = new Button(playStyle) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                setZIndex(99);
                setColor(1, 1, 1, buttonsAlpha);
                super.draw(batch, parentAlpha);
            }
        };


        playButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (Gameplay.gamePaused) {
                    resumeGame();
                    playButton.setChecked(false);
                } else {
                    pauseGame();
                    playButton.setChecked(true);
                }

                if (Gameplay.gameOver) {
                    newGame();
                }

                super.touchUp(event, x, y, pointer, button);
            }
        });

        playButton.setWidth(80);
        playButton.setHeight(80);
        playButton.setX(ZombieDrive.WIDTH - 160);
        playButton.setY(ZombieDrive.HEIGHT - 80);

        stage.addActor(playButton);
    }

    private void newGame() {
        //TODO new Game!!!!
        Gameplay.resetScore();
        game.setScreen(new LoadingScreen(game));
    }

    private void setUpSteering(Stage stage) {
        leftButton = new ImageButton(Assets.img_left) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                setZIndex(99);
                setColor(1, 1, 1, buttonsAlpha);
                super.draw(batch, parentAlpha);
            }
        };


        leftButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                car.moveLeft();
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                car.stop();
                super.touchUp(event, x, y, pointer, button);
            }
        });
        leftButton.setWidth(100);
        leftButton.setHeight(100);
        leftButton.setX(5);
        leftButton.setY(ZombieDrive.HEIGHT / 12);
        stage.addActor(leftButton);

        rightButton = new ImageButton(Assets.img_right) {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                setZIndex(99);
                setColor(1, 1, 1, buttonsAlpha);
                super.draw(batch, parentAlpha);
            }
        };
        rightButton.addListener(new ClickListener() {
                                    @Override
                                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                                        car.moveRight();
                                        return super.touchDown(event, x, y, pointer, button);
                                    }

                                    @Override
                                    public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                                        car.stop();
                                        super.touchUp(event, x, y, pointer, button);
                                    }
                                }
        );
        rightButton.setWidth(100);
        rightButton.setHeight(100);
        rightButton.setX(ZombieDrive.WIDTH - rightButton.getWidth() - 5);
        rightButton.setY(ZombieDrive.HEIGHT / 12);
        stage.addActor(rightButton);
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

    private void removeAllButtons() {
        playButton.remove();
        leftButton.remove();
        rightButton.remove();
    }

}
