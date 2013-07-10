package com.malcom.library.android.module.ad;

import android.util.Log;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;
import com.adwhirl.AdWhirlTargeting;

/**
 * Ads Module.
 *  
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class MCMAdEventHandler implements AdWhirlInterface  {
	
	public AdWhirlLayout adView;

    public MCMAdEventHandler() {
        
    }

    public void adWhirlGeneric() {
        // nothing to be done: Generic notifications should also be 
        // configurable in the AdWhirl web interface, but I could't find them
    }


}
