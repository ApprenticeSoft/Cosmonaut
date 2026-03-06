package com.cosmonaut.client;

import com.cosmonaut.ActionResolver;

public class ActionResolverHtml implements ActionResolver {

    @Override
    public void removeAds() {
        // No-op on web.
    }

    @Override
    public void processPurchases() {
        // No-op on web.
    }

    @Override
    public void showOrLoadInterstital() {
        // No-op on web.
    }

    @Override
    public void LoadInterstital() {
        // No-op on web.
    }

    @Override
    public void showAdsBottom() {
        // No-op on web.
    }

    @Override
    public void showAdsTop() {
        // No-op on web.
    }

    @Override
    public void hideAds() {
        // No-op on web.
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
