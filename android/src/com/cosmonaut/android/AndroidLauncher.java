package com.cosmonaut.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.FrameLayout.LayoutParams;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.cosmonaut.Data;
import com.cosmonaut.MyGdxGame;
import com.cosmonaut.android.util.IabHelper;
import com.cosmonaut.android.util.IabResult;
import com.cosmonaut.android.util.Inventory;
import com.cosmonaut.android.util.Purchase;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.cosmonaut.ActionResolver;

public class AndroidLauncher extends AndroidApplication implements ActionResolver {
	
	IabHelper mHelper;
    boolean mAdsRemoved = false, connected = false;
	
    FrameLayout fLayout;
	AdView admobView, adView;
	protected View gameView;
	private InterstitialAd interstitialAd;
	ConnectivityManager connManager;
    private NetworkInfo info;
	
	private static final String BANNER_ID = "ca-app-pub-7775582829834874/6369963942";
	private static final String INTERSTITIAL_ID = "ca-app-pub-7775582829834874/4753629945";
    
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.r = 8;
		config.g = 8;
		config.b = 8;
		config.a = 8;
		System.out.println("Soundpool size : " + config.maxSimultaneousSounds);
		config.maxSimultaneousSounds = 32;
		System.out.println("Soundpool size : " + config.maxSimultaneousSounds);
	   
		/*
		 * Achat dans l'application
		 */
		System.out.println("************************In App Purchase************************");
	    String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA1IPRxr4v+kcJ883OQJkNEWfBxMD4QKEnx1Kub6HRe3X1Rg6OO+2ghSDKkkMbxHBP+1QRLhyG/jHYUxnyzpfHDuxUolipaybGD9Q7dSfjXSyKIjRcxRbctz+neEmD31+vgbwYacVj3nWsrIgmqmD3t7WacIV1nDiYeQnbd5xImUnL9qzD9ko/CqS5uKsv29hl0dQDic3ilnM0Ihhv5BhSigHQF1A61268rvk7kRmsHs5WarvrxpeTyOA9gVqy18lQPinL5k5/WDkQHZb/dxfy2KFunLPYXy6eHyH62K0GbDdC4KhkvlBBo7o1uvx+6ecDvDzKYvkqVuW1ErpXJA5R+QIDAQAB";		
		// compute your public key and store it in base64EncodedPublicKey
		mHelper = new IabHelper(this, base64EncodedPublicKey);
		
		mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
			   public void onIabSetupFinished(IabResult result) {
			      if (!result.isSuccess()) {
			         // Oh noes, there was a problem.
			         Log.d("IAB", "Problem setting up In-app Billing: " + result);
			      }
			      // Hooray, IAB is fully set up!
			      Log.d("IAB", "Billing Success: " + result);
			      System.out.println("IAB: Billing Success: " + result);
			      
			      processPurchases();
			   }
		});
		System.out.println("************************In App Purchase************************");
		/*
		 * Creation du Layout pour pouvoir afficher les publicités
		 */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);    
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
		
		/*
		 * Mode plein écran
		 * In KITKAT (4.4) and next releases, hide the virtual buttons
		 */
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	        hideVirtualButtons();
	    }
	    else{ //Si version plus vieille que 4.4
			config.hideStatusBar = true;
			config.useImmersiveMode = true;
	    }

		fLayout = new FrameLayout(this);
		FrameLayout.LayoutParams fParams = 
		new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
		                			FrameLayout.LayoutParams.MATCH_PARENT);
		fLayout.setLayoutParams(fParams);
		
		admobView = createAdView();
		View gameView = createGameView(config);

		fLayout.addView(gameView);
		fLayout.addView(admobView);
		setContentView(fLayout);
		startAdvertising(admobView);
		
		interstitialAd = new InterstitialAd(this);
		interstitialAd.setAdUnitId(INTERSTITIAL_ID);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
			}
			@Override
			public void onAdClosed() {
			}
		});
		showOrLoadInterstital();
		
		/*
		 * Gestion des connections WIFI/3G
		 */
		connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		info = connManager.getActiveNetworkInfo();	
	}
	
	private AdView createAdView() {
		adView = new AdView(this);
		adView.setAdSize(AdSize.SMART_BANNER);
		adView.setAdUnitId(BANNER_ID);
		adView.setId(12345); // this is an arbitrary id, allows for relative positioning in createGameView()
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			adView.setLayoutParams(params);
			//adView.setBackgroundColor(Color.BLACK);
			return adView;
	}
	
	private View createGameView(AndroidApplicationConfiguration config) {
		gameView = initializeForView(new MyGdxGame(this), config);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
			params.addRule(RelativeLayout.BELOW, adView.getId());
			gameView.setLayoutParams(params);
			
			//Pour éviter que la publicité s'affiche par défaut (ajouté le 22-01-2017)
			adView.setVisibility(View.GONE);
			return gameView;
	}

	public void startAdvertising(AdView adView) {
		AdRequest adRequest = new AdRequest.Builder().build();
		adView.loadAd(adRequest);
	}
	
	public void showOrLoadInterstital() {
		try {
			runOnUiThread(new Runnable() {
				public void run() {
					if (info != null && info.isConnected()){
						if (interstitialAd.isLoaded()) {
							interstitialAd.show();
	
						} else {
							AdRequest interstitialRequest = new AdRequest.Builder().build();
							interstitialAd.loadAd(interstitialRequest);
						}
					}
				}
			});
		} catch (Exception e) {
		}
	 }
	
	public void LoadInterstital() {
			try {
				runOnUiThread(new Runnable() {
					public void run() {
						if (info != null && info.isConnected()){
							if (!interstitialAd.isLoaded()) {
								AdRequest interstitialRequest = new AdRequest.Builder().addTestDevice("1E55D4A762FAE18E36A0BC83CBF3FA2B").build();
								interstitialAd.loadAd(interstitialRequest);
							}
						}
					}
				});
			} catch (Exception e) {
			}
	}
	
	public void showAdsBottom() {
        runOnUiThread(new Runnable() {
                @Override
                public void run() {               	  
              	  if (info != null && info.isConnected()){
              		  adView.setVisibility(View.VISIBLE); 	
                  	  
                  	  adView.setLayoutParams(new FrameLayout.LayoutParams(
                                FrameLayout.LayoutParams.MATCH_PARENT, 
                                FrameLayout.LayoutParams.WRAP_CONTENT, 
                                Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM));
                     }
                }
        	});
	}

	public void showAdsTop() {
		runOnUiThread(new Runnable() {
	    	@Override
	    	public void run() {
	    		if (info != null && info.isConnected()){
	    			adView.setVisibility(View.VISIBLE);
	                	  
	    			adView.setLayoutParams(new FrameLayout.LayoutParams(
	    					FrameLayout.LayoutParams.MATCH_PARENT, 
	    					FrameLayout.LayoutParams.WRAP_CONTENT, 
	    					Gravity.CENTER_HORIZONTAL | Gravity.TOP));
	    		}
	    	}
	    });
	}
	  
	public void hideAds() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (info != null && info.isConnected()){
					adView.setVisibility(View.GONE);
				} 
			}
		});
	}
	  
	public int hauteurBanniere(){
		if (info != null && info.isConnected()){
			return adView.getHeight();
		}
		  else return 0;		  
	}
	
	//Mode plein écran
	@TargetApi(19)
	private void hideVirtualButtons() {
	    getWindow().getDecorView().setSystemUiVisibility(
	              View.SYSTEM_UI_FLAG_LAYOUT_STABLE
	            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
	            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
	            | View.SYSTEM_UI_FLAG_FULLSCREEN
	            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}
	
	//Réactiver le mode plein écran quand on revient à l'application après l'avoir quitter
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
	    super.onWindowFocusChanged(hasFocus);
	    if (hasFocus) {
	        // In KITKAT (4.4) and next releases, hide the virtual buttons
	        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
	            hideVirtualButtons();
	        }
	    }
	}
	
	public void removeAds(){
        System.out.println("*******************************removeAds()*******************************");
		 mHelper.launchPurchaseFlow(this, SKU_REMOVE_ADS, RC_REQUEST,
			     mPurchaseFinishedListener, "HANDLE_PAYLOADS");
	    System.out.println("*******************************removeAds()*******************************");
	}
	
	public boolean adsListener(){
		return mAdsRemoved;
	}
	
	public boolean isConnected(){
		if (info != null && info.isConnected())
			connected = true;
		else connected = false;
		
		return connected;
	}
	
	// Callback for when a purchase is finished
	IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
		public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
			System.out.println("Purchase Finished Listener");
			if ( purchase == null) return;
			Log.d("IAB", "Purchase finished: " + result + ", purchase: " + purchase);
			System.out.println("IAB: Purchase finished: " + result + ", purchase: " + purchase);
          
			// if we were disposed of in the meantime, quit.
			if (mHelper == null) return;
			System.out.println("mHelper: " + mHelper);

			if (result.isFailure()) {
				//complain("Error purchasing: " + result);
				//setWaitScreen(false);
				System.out.println("Error purchasing: " + result);
				return;
			}
			/*
			 * Test consume purchase
			 */
			/*
			else if(purchase.getSku().equals(SKU_REMOVE_ADS)){
				System.out.println("*****************************Test consume purchase*****************************");
				System.out.println("purchase : " + purchase);
				ConsumePurchase(purchase);
				System.out.println("purchase : " + purchase);
				System.out.println("*****************************Test consume purchase*****************************");
			}
			*/
//	            if (!verifyDeveloperPayload(purchase)) {
//	                //complain("Error purchasing. Authenticity verification failed.");
//	                //setWaitScreen(false);
//	                return;
//	            }

           Log.d("IAB", "Purchase successful.");
           System.out.println("IAB: Purchase successful.");

           if (purchase.getSku().equals(SKU_REMOVE_ADS)) {
               // bought the premium upgrade!
               Log.d("IAB", "Purchase is premium upgrade. Congratulating user.");
               System.out.println("IAB: Purchase is premium upgrade. Congratulating user.");

               // Do what you want here maybe call your game to do some update
               //
               //Maybe set a flag to indicate that ads shouldn't show anymore
               mAdsRemoved = true;
               Data.setFullVersion(true);
               System.out.println("Remove ads : " + mAdsRemoved);

           }
		}
	};

	public void processPurchases(){
		System.out.println("Process purchases");
		mHelper.queryInventoryAsync(mGotInventoryListener);
	}
   	
   	// Listener that's called when we finish querying the items and subscriptions we own
   	IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
   	    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
   	        Log.d("IAB", "Query inventory finished.");
   	        System.out.println("IAB: Query inventory finished.");

	        System.out.println("mHelper = " + mHelper);
   	        // Have we been disposed of in the meantime? If so, quit.
   	        if (mHelper == null) return;
   	        
   	        // Is it a failure?
   	        if (result.isFailure()) {
   	            // handle failure here
   	        	System.out.println("Failure !");
   	            return;
   	        }

   	        // Do we have the premium upgrade?
   	        Purchase removeAdPurchase = inventory.getPurchase(SKU_REMOVE_ADS);
   	        mAdsRemoved = (removeAdPurchase != null);
   	        System.out.println("mAdsRemoved = " + mAdsRemoved + "*****************");
   	        System.out.println("removeAdPurchase = " + removeAdPurchase + "*****************");
   	        //System.out.println("inventory.getSkuDetails(SKU_REMOVE_ADS).getPrice() = " + inventory.getSkuDetails(SKU_REMOVE_ADS).getPrice() + "*****************");
   	        

   	        System.out.println("************************TEST CONSUME**************************");
	        System.out.println("inventory.hasPurchase(SKU_REMOVE_ADS) : " + inventory.hasPurchase(SKU_REMOVE_ADS));
   	        if(inventory.hasPurchase(SKU_REMOVE_ADS)){
   	        	System.out.println("Test inventory.hasPurchase(SKU_REMOVE_ADS) réussi !!!!!!!");
   	        	Data.setFullVersion(true);
   	   	        //ConsumePurchase(inventory.getPurchase(SKU_REMOVE_ADS));
   	        }
   	        System.out.println("************************TEST CONSUME**************************");

   	    }
   	};
   	
   	@Override
   	public void onActivityResult(int request, int response, Intent data) {
   	    super.onActivityResult(request, response, data);

   	    if (mHelper != null) {
   	        // Pass on the activity result to the helper for handling
   	        if (mHelper.handleActivityResult(request, response, data)) {
   	            Log.d("IAB", "onActivityResult handled by IABUtil.");
   	        }
   	    }
   	}
   	
   	public void ConsumePurchase(Purchase purchase){
   		mHelper.consumeAsync(purchase, mConsumeFinishedListener);
   	}
   	
   	IabHelper.OnConsumeFinishedListener mConsumeFinishedListener = new IabHelper.OnConsumeFinishedListener() {
   		public void onConsumeFinished(Purchase purchase, IabResult result) {
   			// if we were disposed of in the meantime, quit.
   	        if (mHelper == null) return;
   	        
   			if (result.isSuccess()) {
   				// provision the in-app purchase to the user
   				// (for example, credit 50 gold coins to player's character)
   				System.out.println("Success !!!!!");
   			}
   			else {
   				// handle error
   				System.out.println("Error !!!!!!");
   			}
   			
   			if(purchase.getSku().equals(SKU_REMOVE_ADS)){
                mAdsRemoved = false;
                Data.setFullVersion(false);
                System.out.println("Remove ads : " + mAdsRemoved);
   			}
   		}
   	};

	@Override
	public void onDestroy() {
	   super.onDestroy();
	   if (mHelper != null) mHelper.dispose();
	   mHelper = null;
	}
		
}

