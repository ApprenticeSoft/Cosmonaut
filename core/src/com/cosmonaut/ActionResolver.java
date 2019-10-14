package com.cosmonaut;

public interface ActionResolver {
	
	public String SKU_REMOVE_ADS = "full_version";
	//(arbitrary) request code for the purchase flow     
	static final int RC_REQUEST = 10001;
	
	public void removeAds();
	public void processPurchases();
	public void showOrLoadInterstital();
	public void LoadInterstital();
	public void showAdsBottom();
	public void showAdsTop();
	public void hideAds();
	public int hauteurBanniere();
	public boolean adsListener();
	public boolean isConnected();
}
