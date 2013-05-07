package com.malcom.library.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.adwhirl.AdWhirlLayout;
import com.adwhirl.AdWhirlLayout.AdWhirlInterface;


/**
 * 
 * @author Malcom Ventures S.L
 * @since  2012
 *
 */
public class AdWhirlAdapter extends Activity implements AdWhirlInterface {

	private ProgressBar mSpinner;
	
	private int layoutAdId;
	private String malcomAdWhirlId;
	
	protected void initialize(int layoutAdId, String malcomAdWhirlId){
		this.layoutAdId = layoutAdId;
		this.malcomAdWhirlId = malcomAdWhirlId;
	}
	
	
	public void adWhirlGeneric() {
		
	}
	
	public void onCreate(Bundle savedInstanceState) {
		
        super.onCreate(savedInstanceState);
        
        /*ScrollView sv = new ScrollView(this);
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.VERTICAL);
        sv.addView(ll);

        TextView tv = new TextView(this);
        tv.setText("Dynamic layouts ftw!");
        ll.addView(tv);

        this.setContentView(sv);*/
        
        //sv.setVisibility(View.GONE);
        
        //setContentView(R.layout.main);
        
        Log.d("AdWhirlAdapter", "onCreate fuera del Activity principal");
        
        
        //Este trozo es literal como lo dice AdWhirl.
        //
        //NOTA: AdWhirl es un proxy de publi. permite configurar las publicidades que se quieran
        //		, cada una que usemos el usuario deberá meter el adapter dentro del package
        //		com.adwhirl.adapters. Por ejemplo, si usamos AdMob, meter el GoogleAdMobAdsAdapter.java.
        LinearLayout layout = (LinearLayout) findViewById(this.layoutAdId);
        AdWhirlLayout adWhirlLayout = new AdWhirlLayout(this, this.malcomAdWhirlId);
        
        //adWhirlLayout.setAdWhirlInterface(this);
        //adWhirlLayout.setMaxWidth((int)(320 * 1));
        //adWhirlLayout.setMaxHeight((int)(480 * 1));

        RelativeLayout.LayoutParams adWhirlLayoutParams = new RelativeLayout.LayoutParams(
        			LayoutParams.FILL_PARENT,
        			LayoutParams.WRAP_CONTENT);
        			layout.addView(adWhirlLayout, adWhirlLayoutParams);
        layout.invalidate();
        //END JAVOC TIP.        
	}
	
	protected void onPause () {
        super.onPause();

        /*
         * Kill application when the root activity is killed.
         */
        Log.d("SALIMOS", "SALIMOS");
    }
	
	protected void onResume () {
        super.onResume();

        /*
         * Kill application when the root activity is killed.
         */
        Log.d("VOLVEMOS", "A ENTRAR");
        //new AlertDialog.Builder(this).setTitle("Mensaje!").setMessage("Volvemos a entrar!!").setNeutralButton("Close", null).show();

    }

}
