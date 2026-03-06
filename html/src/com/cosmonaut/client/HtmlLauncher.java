package com.cosmonaut.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.cosmonaut.MyGdxGame;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Window;

public class HtmlLauncher extends GwtApplication {
    private static final float LANDSCAPE_ASPECT = 16f / 9f;

    @Override
    public GwtApplicationConfiguration getConfig() {
        int browserWidth = Math.max(1, Window.getClientWidth());
        int browserHeight = Math.max(1, Window.getClientHeight());
        int targetWidth = browserWidth;
        int targetHeight = Math.min(browserHeight, Math.round(browserWidth / LANDSCAPE_ASPECT));

        GwtApplicationConfiguration config = new GwtApplicationConfiguration(targetWidth, targetHeight);
        config.padHorizontal = 0;
        config.padVertical = 0;
        config.useDebugGL = false;
        return config;
    }

    @Override
    public void onModuleLoad() {
        super.onModuleLoad();
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                applyResponsiveSize();
            }
        });
        applyResponsiveSize();
    }

    private void applyResponsiveSize() {
        int browserWidth = Math.max(1, Window.getClientWidth());
        int browserHeight = Math.max(1, Window.getClientHeight());
        int targetWidth = browserWidth;
        int targetHeight = Math.min(browserHeight, Math.round(browserWidth / LANDSCAPE_ASPECT));

        if (getRootPanel() != null) {
            getRootPanel().setWidth(targetWidth + "px");
            getRootPanel().setHeight(targetHeight + "px");
        }

        if (Gdx.graphics != null) {
            Gdx.graphics.setWindowedMode(targetWidth, targetHeight);
        }
    }

    @Override
    public ApplicationListener createApplicationListener() {
        return new MyGdxGame(new ActionResolverHtml());
    }
}
