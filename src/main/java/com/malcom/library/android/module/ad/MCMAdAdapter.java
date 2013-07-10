package com.malcom.library.android.module.ad;

import android.app.Activity;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.adapters.AdWhirlAdapter;
import com.malcom.library.android.module.core.MCMCoreAdapter;

/**
 * Ads Module.
 *  
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class MCMAdAdapter implements AdWhirlInterface  {
	
	private static MCMAdAdapter instance = null;
	
	
	// Exists only to defeat instantiation.
	protected MCMAdAdapter() {}
	
	
	public static MCMAdAdapter getInstance() {
	
		if(instance == null) {
			instance = new MCMAdAdapter();	    
		}
		
		return instance;	   
	}
	
	
	/**
	 * Adds the AdWhirl AdView for a non existing Malcom available publicity providers.
	 * 
	 * @param context
	 * @param layoutAd
	 * @param idAdWhirl		Malcom Ads SDK Key
	 * @param eventHandler	Handler for the publicity provider. @See MCMAdEventHandler and
	 * 						documentation.
	 * @param width
	 * @param height
	 */
	public void createAds(Activity context, LinearLayout layoutAd, String idAdWhirl, MCMAdEventHandler eventHandler, int width, int height) {
		
		AdWhirlLayout adWhirlLayout = prepareAdWhirlLayout(context, layoutAd, idAdWhirl, width, height);
		
		//Configure the event handler.
	    if (eventHandler != null) {	    	
	    	eventHandler.adView = adWhirlLayout;	    
	    	adWhirlLayout.setAdWhirlInterface(eventHandler);	    
	    }
        
	    setAdWhirlLayoutIntoAdView(layoutAd,adWhirlLayout);
	}
	
	/**
	 * Adds the AdWhirl AdView for an existing Malcom available publicity providers.
	 * 
	 * @param context
	 * @param layoutAd
	 * @param idAdWhirl		Malcom Ads SDK Key
	 * @param width
	 * @param height
	 */
	public void createAds(Activity context, LinearLayout layoutAd, String idAdWhirl, int width, int height) {
		
		AdWhirlLayout adWhirlLayout = prepareAdWhirlLayout(context, layoutAd, idAdWhirl, width, height);		
		setAdWhirlLayoutIntoAdView(layoutAd,adWhirlLayout);		
	}
	
	
	//AUCILIAR FUNCTIONS
	
	/*
	 * Prepares the AdWhirl layout.
	 * 
	 * @param context
	 * @param layoutAd
	 * @param idAdWhirl
	 * @param width
	 * @param height
	 * @return
	 */
	private AdWhirlLayout prepareAdWhirlLayout(Activity context, LinearLayout layoutAd, String idAdWhirl, int width, int height){
		AdWhirlLayout adWhirlLayout = new AdWhirlLayout(context, idAdWhirl);
		adWhirlLayout.setMaxWidth(width);
	    adWhirlLayout.setMaxHeight(height);
	    
	    if (MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_COMPANY_NAME) != null) {
	    
	    	AdWhirlAdapter.setGoogleAdSenseCompanyName(
	    			MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_COMPANY_NAME));
	    	
	    }
	    if (MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APP_NAME) != null) {
	    
	    	AdWhirlAdapter.setGoogleAdSenseAppName(
	    		MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APP_NAME));
	    	
	    }	    
		
	    return adWhirlLayout;
	}
	
	/*
	 * Sets the AdWhirl view into the Malcom Ad view layout.
	 * 
	 * @param layoutAd
	 * @param adWhirlLayout
	 */
	private void setAdWhirlLayoutIntoAdView(LinearLayout layoutAd, AdWhirlLayout adWhirlLayout){
		RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(
    			LayoutParams.FILL_PARENT,
    			LayoutParams.WRAP_CONTENT);
        
        layoutAd.addView(adWhirlLayout, adWhirlLayoutParams);
        layoutAd.invalidate();
	}
	
	
	
	
	
	/*public MCMAdAdapter(Activity context, LinearLayout layoutAd, String idAdWhirl) {
		
		AdWhirlLayout adWhirlLayout = new AdWhirlLayout(context, idAdWhirl);
        
        RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(
    			LayoutParams.FILL_PARENT,
    			LayoutParams.WRAP_CONTENT);
    			
        layoutAd.addView(adWhirlLayout, adWhirlLayoutParams);
        layoutAd.invalidate();
		
	}*/
	
	/*public void onCreate(Bundle savedInstanceState, LinearLayout layout) {
		
        super.onCreate(savedInstanceState);
        
        AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, "9b60100db43e4bb2bb5eb6e244d03e20");
        
        RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(
    			LayoutParams.FILL_PARENT,
    			LayoutParams.WRAP_CONTENT);
    			
        layout.addView(adWhirlLayout, adWhirlLayoutParams);
        layout.invalidate();
        
	}*/
	
	public void adWhirlGeneric() {
		// TODO Auto-generated method stub
		
	} 

}
