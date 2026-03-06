package com.cosmonaut.desktop;

import com.cosmonaut.ActionResolver;

public class ActionResolverDesktop implements ActionResolver {

    @Override
    public void removeAds() {
        // No-op on desktop.
    }

    @Override
    public void processPurchases() {
        // No-op on desktop.
    }

    @Override
    public void showOrLoadInterstital() {
        // No-op on desktop.
    }

    @Override
    public void LoadInterstital() {
        // No-op on desktop.
    }

    @Override
    public void showAdsBottom() {
        // No-op on desktop.
    }

    @Override
    public void showAdsTop() {
        // No-op on desktop.
    }

    @Override
    public void hideAds() {
        // No-op on desktop.
    }

    @Override
    public int hauteurBanniere() {
        return 0;
    }

    @Override
    public boolean adsListener() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return true;
    }
}
