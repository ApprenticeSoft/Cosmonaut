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
    private final boolean mobileClient = isLikelyMobileClient();

    private int[] computeTargetSize(int browserWidth, int browserHeight){
        if(mobileClient){
            return new int[]{Math.max(1, browserWidth), Math.max(1, browserHeight)};
        }

        float browserAspect = browserWidth / (float) browserHeight;
        int targetWidth;
        int targetHeight;

        if(browserAspect > LANDSCAPE_ASPECT){
            targetHeight = browserHeight;
            targetWidth = Math.round(targetHeight * LANDSCAPE_ASPECT);
        }
        else{
            targetWidth = browserWidth;
            targetHeight = Math.round(targetWidth / LANDSCAPE_ASPECT);
        }
        return new int[]{Math.max(1, targetWidth), Math.max(1, targetHeight)};
    }

    private native boolean isLikelyMobileClient() /*-{
        var ua = ($wnd.navigator && $wnd.navigator.userAgent) ? $wnd.navigator.userAgent : "";
        var touchPoints = ($wnd.navigator && typeof $wnd.navigator.maxTouchPoints === "number") ? $wnd.navigator.maxTouchPoints : 0;
        var mobileUA = /Android|iPhone|iPad|iPod|Mobile|Silk|Kindle|Opera Mini/i.test(ua);
        return mobileUA || touchPoints > 1;
    }-*/;

    private native int getVisualViewportWidth() /*-{
        var vv = $wnd.visualViewport;
        if (vv && vv.width) return Math.max(1, Math.round(vv.width));
        return Math.max(1, $wnd.innerWidth || $doc.documentElement.clientWidth || 1);
    }-*/;

    private native int getVisualViewportHeight() /*-{
        var vv = $wnd.visualViewport;
        if (vv && vv.height) return Math.max(1, Math.round(vv.height));
        return Math.max(1, $wnd.innerHeight || $doc.documentElement.clientHeight || 1);
    }-*/;

    private int getBrowserWidth() {
        if (mobileClient) {
            return getVisualViewportWidth();
        }
        return Math.max(1, Window.getClientWidth());
    }

    private int getBrowserHeight() {
        if (mobileClient) {
            return getVisualViewportHeight();
        }
        return Math.max(1, Window.getClientHeight());
    }

    @Override
    public GwtApplicationConfiguration getConfig() {
        int browserWidth = getBrowserWidth();
        int browserHeight = getBrowserHeight();
        int[] size = computeTargetSize(browserWidth, browserHeight);
        int targetWidth = size[0];
        int targetHeight = size[1];

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
        int browserWidth = getBrowserWidth();
        int browserHeight = getBrowserHeight();
        int[] size = computeTargetSize(browserWidth, browserHeight);
        int targetWidth = size[0];
        int targetHeight = size[1];

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
