package com.cosmonaut.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Input.Peripheral;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.Utils.GameConstants;

public class LoadingScreen implements Screen {

    final MyGdxGame game;
    OrthographicCamera camera;
    private Texture textureLogo;
    private Texture barTexture;
    private Texture knobTexture;
    private Texture knobBeforeTexture;
    private Image imageLogo;
    private Stage stage;

    private ProgressBar progressBar;
    private ProgressBarStyle progressBarStyle;
    private NinePatchDrawable ninePatchKnob, ninePatchKnobBefore, ninePatchBar;

    public LoadingScreen(final MyGdxGame game) {
        this.game = game;

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        textureLogo = loadWebTexture("Images/Logo.jpg");
        imageLogo = new Image(textureLogo);
        imageLogo.setWidth(Gdx.graphics.getWidth());
        imageLogo.setHeight(textureLogo.getHeight() * imageLogo.getWidth() / textureLogo.getWidth());
        imageLogo.setX(Gdx.graphics.getWidth() / 2f - imageLogo.getWidth() / 2f);
        imageLogo.setY(Gdx.graphics.getHeight() / 2f - imageLogo.getHeight() / 2f);
        if(Gdx.app.getType() == com.badlogic.gdx.Application.ApplicationType.WebGL)
            stage = new Stage(new com.badlogic.gdx.utils.viewport.ScreenViewport(), game.batch);
        else
            stage = new Stage();

        barTexture = loadWebTexture("Images/Bar.png");
        knobTexture = loadWebTexture("Images/Knob.png");
        knobBeforeTexture = loadWebTexture("Images/KnobBefore.png");
        ninePatchBar = new NinePatchDrawable(new NinePatch(barTexture, 7, 7, 7, 7));
        ninePatchKnob = new NinePatchDrawable(new NinePatch(knobTexture, 1, 7, 9, 9));
        ninePatchKnobBefore = new NinePatchDrawable(new NinePatch(knobBeforeTexture, 1, 1, 9, 9));

        progressBarStyle = new ProgressBarStyle(ninePatchBar, ninePatchKnob);
        progressBarStyle.knobBefore = ninePatchKnobBefore;
        progressBarStyle.knobBefore.setLeftWidth(0);
        progressBarStyle.knobBefore.setRightWidth(0);
        progressBarStyle.background.setLeftWidth(2);
        progressBarStyle.background.setRightWidth(0);

        progressBar = new ProgressBar(0, 100, .1f, false, progressBarStyle);
        progressBar.setWidth(Gdx.graphics.getWidth() / 3f);
        progressBar.setHeight(3 * Gdx.graphics.getHeight() / 100f);
        progressBar.setX(Gdx.graphics.getWidth() / 2f - progressBar.getWidth() / 2f);
        progressBar.setY(Gdx.graphics.getHeight() / 5f);

        queueAssets(game.assets);
        registerFallbackFonts();

        stage.addActor(imageLogo);
        imageLogo.addAction(Actions.sequence(Actions.alpha(0), Actions.fadeIn(0.1f), Actions.delay(1.5f)));
        stage.addActor(progressBar);
    }

    private void queueAssets(AssetManager assets) {
        assets.load("Sounds/Alarm.ogg", Sound.class);
        assets.load("Sounds/Piston_Motor.ogg", Sound.class);
        assets.load("Sounds/Piston_Bang.ogg", Sound.class);
        assets.load("Sounds/Electrocution.ogg", Sound.class);
        assets.load("Sounds/Jetpack.ogg", Sound.class);
        assets.load("Sounds/Impact.ogg", Sound.class);
        assets.load("Sounds/Door.ogg", Sound.class);
        assets.load("Sounds/Fuel Refill.ogg", Sound.class);
        assets.load("Sounds/Oxygen Refill.ogg", Sound.class);
        assets.load("Sounds/Button On.ogg", Sound.class);
        assets.load("Sounds/Button Off.ogg", Sound.class);
        assets.load("Sounds/Exit.ogg", Sound.class);
        assets.load("Sounds/Upgrade.ogg", Sound.class);
        assets.load("Sounds/Gas Leak.ogg", Sound.class);
        assets.load("Sounds/Background.ogg", Music.class);

        assets.load("Images/Stars.jpg", Texture.class);
        assets.load("Images/Barre.png", Texture.class);

        assets.load("Images/" + GameConstants.SCREEN_RESOLUTION + "/Images.pack", TextureAtlas.class);
        assets.load("Images/Animations/Leak_Animation.pack", TextureAtlas.class);
        assets.load("Images/Animations/Exit_Animation.pack", TextureAtlas.class);
        assets.load("Images/Animations/Exit_End_Animation.pack", TextureAtlas.class);
        assets.load("Images/Animations/Upgrade_Animation.pack", TextureAtlas.class);
        assets.load("Images/Animations/Fleche_Animation.pack", TextureAtlas.class);

        if (GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_QWERTY
                || GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_KEYBOARD_CONTROLS_AZERTY
                || GameConstants.GAME_CONTROLS == GameConstants.DESKTOP_GAMEPAD_CONTROLS) {
            assets.load("Images/Animations/Tom_Animation_HD.pack", TextureAtlas.class);
            assets.load("Images/Desktop_Controls.pack", TextureAtlas.class);
        } else {
            assets.load("Images/Animations/Tom_Animation.pack", TextureAtlas.class);
            assets.load("Images/Animations/Controls_Animation.pack", TextureAtlas.class);

            if (GameConstants.SELECTED_LEVEL == 1 && !game.levelHandler.isLevelUnlocked(2)) {
                assets.load("Images/Animations/Rotation_Control_Animation.pack", TextureAtlas.class);
                assets.load("Images/Animations/Jetpack_Control_Animation.pack", TextureAtlas.class);
            }
        }

        assets.setLoader(TiledMap.class, new TmxMapLoader(new com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver()));
    }

    private void registerFallbackFonts() {
        float width = Gdx.graphics.getWidth();
        boolean mobileClient = Gdx.input.isPeripheralAvailable(Peripheral.MultitouchScreen);
        float buttonFontScaleBoost = mobileClient ? 1.28f : 1.58f;
        float uiFontScaleBoost = mobileClient ? 1.22f : 1.45f;

        registerFallbackFont("fontMenu.ttf", 0.04f * width);
        registerFallbackFont("fontTable.ttf", 0.024f * width * buttonFontScaleBoost);
        registerFallbackFont("fontHUD.ttf", 0.013f * width * uiFontScaleBoost);
        registerFallbackFont("fontUpgrade.ttf", 0.019f * width * uiFontScaleBoost);
        registerFallbackFont("fontDialogue.ttf", Math.max(0.019f * width * uiFontScaleBoost, 20f));
        registerFallbackFont("fontOption.ttf", 0.027f * width * uiFontScaleBoost);
        registerFallbackFont("fontCosmonaut.ttf", 0.1f * width);
        registerFallbackFont("fontCredit.ttf", 0.05f * width);
    }

    private void registerFallbackFont(String fontKey, float targetSizePx) {
        BitmapFont font = new BitmapFont();
        float baseLineHeight = Math.max(1f, font.getLineHeight());
        float scale = Math.max(0.75f, targetSizePx / baseLineHeight);
        font.getData().setScale(scale);
        font.setUseIntegerPositions(false);
        for (TextureRegion region : font.getRegions()) {
            region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
        }
        game.registerRuntimeFont(fontKey, font);
    }

    private Texture loadWebTexture(String internalPath) {
        Texture texture = new Texture(Gdx.files.internal(internalPath), false);
        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
        return texture;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        game.batch.setProjectionMatrix(camera.combined);

        stage.act(delta);
        stage.draw();

        progressBar.setValue(100 * game.assets.getProgress());

        if (game.assets.update()) {
            dispose();
            ((Game) Gdx.app.getApplicationListener()).setScreen(new HomeScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        textureLogo.dispose();
        barTexture.dispose();
        knobTexture.dispose();
        knobBeforeTexture.dispose();
    }
}
