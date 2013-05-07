package com.malcom.library.android.module.campaign;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.malcom.library.android.exceptions.ApplicationConfigurationNotFoundException;
import com.malcom.library.android.module.campaign.MCMCampaignModel.CampaignPosition;
import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.utils.HttpDateUtils;
import com.malcom.library.android.utils.MalcomHttpOperations;
import com.malcom.library.android.utils.ToolBox;
import com.malcom.library.android.utils.ToolBox.HTTP_METHOD;
import com.malcom.library.android.utils.encoding.DigestUtils;
import com.malcom.library.android.utils.encoding.base64.Base64;

/**
 * MCMCampaignAdapter.
 * Campaigns module. Shows a banner in 4 posible positions: bottom, top, full screen, and middle.
 * Redirects to other app in Play Store.
 * @author Malcom
 * @author Pepe - code refactor, added middle banners, new logs.
 *
 */
public class MCMCampaignAdapter {

    private static final String campaignURL = "v2/campaigns/application/%@AppId/udid/%@Udid";
    //private static final String campaignURL = "http://malcom-api-dev.elasticbeanstalk.com/v2/campaigns/application/%@AppId/udid/%@Udid";
	
	private static final String LOG_TAG = "MCMCampaign";
	private static final String ATTR_CAMPAIGNS_ARRAY = "campaigns";
	private static final String ATTR_IMPRESSION_HIT = "IMPRESSION";
	private static final String ATTR_CLICK_HIT = "CLICK";
	private static final String RES_ID_LAYOUT = "campaign_banner_layout";
	private static final String RES_ID_IMAGE = "image_campaign_view";
	private static int SIZE_BANNER = 60;
	private static int MIDDLE_MARGIN = 30;
	private static int BACKGROUND_ALPHA = 180; // 0-255
	private static int DEFAULT_CAMPAIGN_DURATION = 15;

	private static MCMCampaignAdapter instance = null;
	private RelativeLayout bannerLayout;
	private ImageView bannerImageView;
	private String campaignResource = "";
	
	private String appSecretKey;
	private String malcomAppId;
	
	
	private ArrayList<MCMCampaignModel> campaignObjectsArray;
	private MCMCampaignModel campaignObjectSelected;
	private Activity context;
	private float density = 1;
	private int resBannerLayoutID;
	private int resImageLayoutID;
	
	private int duration; //integer to set the duration of the banner


	public MCMCampaignNotifiedDelegate delegate;
	
    private Handler mHandler = new Handler();

    
	// Exists only to defeat instantiation.
	protected MCMCampaignAdapter() {}
	
	/**
	 * Gets the singleton instance of this class.
	 * @return instance of MCMCampaignAdapter.
	 */
	public static MCMCampaignAdapter getInstance() {
	
		if (instance == null) {
			instance = new MCMCampaignAdapter();	    
		}
		
		return instance;	   
	}
	
	/**
	 * Adds a campaign banner to an activity.
	 * @param context - your activity.
	 */
	public void addBanner(Activity context) {
		
		addBanner(context, null);
	}

	/**
	 * Adds a campaign banner to an activity.
	 * @param context - your activity.
	 * @param delegate - This is the delegate.	 
	 * 
	 */
	public void addBanner(Activity context, MCMCampaignNotifiedDelegate delegate) {

		this.context = context;
		this.delegate = delegate;
		
        mHandler.removeCallbacks(mRemoveCampaignBanner);

        //sets the default time duration
    	this.duration = DEFAULT_CAMPAIGN_DURATION;

		this.density = context.getResources().getDisplayMetrics().density;
		try {
			// Create URL to get campaigns of my app
			String malcomBaseUrl = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL);

			malcomAppId = URLEncoder.encode(MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID), "UTF-8");
			String devideId = URLEncoder.encode(ToolBox.device_getId(context), "UTF-8");

            campaignResource = campaignURL.replace("%@AppId",malcomAppId).replace("%@Udid",devideId);

//			String urlCampaign = malcomBaseUrl + campaignResource;
            String urlCampaign = campaignResource;

            // Launch request to get campaigns data
            new DownloadCampaignFile().execute(urlCampaign);
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Method that removes the banner and notifies it to delegate if delegate is set.
	 * @since 2.0.2
	 */	
	public void removeCurrentBanner(Activity context){
		
		// Get layout elements
		resBannerLayoutID = context.getResources().getIdentifier(RES_ID_LAYOUT, "id", context.getPackageName());
		bannerLayout = (RelativeLayout)context.findViewById(resBannerLayoutID);
	    resImageLayoutID = context.getResources().getIdentifier(RES_ID_IMAGE, "id", context.getPackageName());
		bannerImageView = (ImageView)context.findViewById(resImageLayoutID);
		this.bannerLayout.setVisibility(View.GONE);
		
		if(delegate!=null){
			delegate.campaignDidFinish();
		}
	}
	
	
	/**
	 * Method that removes the banner and notifies it to delegate if delegate is set when time is over (duration)
	 * @since 2.0.2
	 */	
	public void finishCampaign(){
		
		bannerImageView = (ImageView)context.findViewById(resImageLayoutID);
		this.bannerLayout.setVisibility(View.GONE);
		
		if(delegate!=null){
			delegate.campaignDidFinish();
		}
	}
	
	
	/**
	 * Method that gets randomly weighted a campaign to serve.
	 * @return MCMCampaignModel campaign selected.
	 * @since 2.0.2
	 */
	private MCMCampaignModel getCampaignPerWeight(){
		ArrayList<Integer> weightedArray = new ArrayList<Integer>();
		
		Log.d(LOG_TAG, "campaignObjectsArray : " + campaignObjectsArray.size());
		//generates the array to random weighted selection
		for (int i = 0; i < campaignObjectsArray.size(); i++) {
			MCMCampaignModel campaignModel = campaignObjectsArray.get(i);
			Log.d(LOG_TAG, "campaignModel.weight : " + campaignModel.getWeight());
	        //adds to the weighted array as ids as weight has
	        for(int j=0; j<campaignModel.getWeight();j++){
	            weightedArray.add(i);
	        }
		}
		
		//generates random number
		Log.d(LOG_TAG, "Searching number : " + weightedArray.size());
		int selection = 0;
		if (weightedArray.size()>1) {
			selection = new Random().nextInt(weightedArray.size()-1);
		}

	    //gets the random position and gets the id written on it. It will be one of the campaigns
	    MCMCampaignModel selectedCampaignModel = campaignObjectsArray.get(weightedArray.get(selection));
	    
	    return selectedCampaignModel;
		
	}

	/**
	 * Initiates banner layout depending on position.
	 * Launches request to get remote image.
	 * @param campaign - the campaign data to show.
	 */
	private void createBanner(MCMCampaignModel campaign) {

		if (campaign == null) {
			return;
		}
		
		// Get layout elements
		try {
			resBannerLayoutID = context.getResources().getIdentifier(RES_ID_LAYOUT, "id", context.getPackageName());
			bannerLayout = (RelativeLayout)context.findViewById(resBannerLayoutID);
		    resImageLayoutID = context.getResources().getIdentifier(RES_ID_IMAGE, "id", context.getPackageName());
			bannerImageView = (ImageView)context.findViewById(resImageLayoutID);

		    // Config layout params depending on position
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) bannerLayout.getLayoutParams();
		    bannerLayout.setGravity(Gravity.CENTER);
		    if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.BOTTOM) {
		    
		        params.height = dp(SIZE_BANNER);
		        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		    }
		    else if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.TOP) {
		    	
		    	params.height = dp(SIZE_BANNER);
		        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
		    }
		    else if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.MIDDLE_LANDSCAPE ||
		    		campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.MIDDLE_PORTRAIT) {

		    	int margin = dp(MIDDLE_MARGIN);
		    	bannerImageView.setPadding(margin, margin, margin, margin);
		    }
		    else if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.FULL_SCREEN) {
		    	
		    	// mantain params of full screen
		    }
		    
		    // Apply params to banner layout
		    bannerLayout.setLayoutParams(params);
		    
		    // Launch request to get image bitmap and add it to banner layout
		    new DownloadCampaignImage().execute();
		} catch (Exception e) {
			// TODO: handle exception
			Log.d(LOG_TAG, "Create banner error: Attends to load the layout " + RES_ID_LAYOUT);
			
		}
		
	}
	
	/**
	 * Shows banner image after download finish.
	 * Configures close button if needed, and adds click events for banner and close button.
	 * @param bitmap - the image downloaded.
	 * @param campaign - the campaign object that is showed.
	 */
	private void setImageBanner(Bitmap bitmap, final MCMCampaignModel campaign) {
		
		// Config banner image and click actions
		bannerImageView.setImageBitmap(bitmap);
		bannerImageView.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View view) {
				  
				// Send Click Hit event to Malcom
				new SendHitClick().execute(ATTR_CLICK_HIT);
				
				// Open campaign app in PlayStore
				try {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + campaign.getPromotionFeature().getPromotionIdentifier())));
				} 
				catch (android.content.ActivityNotFoundException anfe) {
					context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + campaign.getPromotionFeature().getPromotionIdentifier())));
				}
			}
		});
		
		// Config close button (if banner position is middle or full screen)
		if (campaign.getMediaFeature().getCampaignPosition() != CampaignPosition.BOTTOM  && campaign.getMediaFeature().getCampaignPosition() != CampaignPosition.TOP) {
		
			Button closeButton = new Button(context);
			closeButton.setText("X");
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.ALIGN_RIGHT, resImageLayoutID);
			params.addRule(RelativeLayout.ALIGN_TOP, resImageLayoutID);
			closeButton.setLayoutParams(params);
			bannerLayout.addView(closeButton);
			bannerLayout.setBackgroundColor(Color.argb(BACKGROUND_ALPHA, 0, 0, 0));
			closeButton.setOnClickListener(new View.OnClickListener() {
				
				public void onClick(View view) {
		        	  
					// Hide banner
					bannerLayout.setVisibility(View.GONE);
				}
			});
		}
		
		// Send Impression Hit event to Malcom
		new SendHitClick().execute(ATTR_IMPRESSION_HIT);
	}
	
	/**
	 * Converts pixels in density pixels.
	 * @param pixels - mdpi pixels.
	 * @return dpi value for current device.
	 */
	private int dp(int pixels) {
		
		float result = pixels * density;
		return Math.round(result);
	}

	/**
	 * DownloadCampaignFile.
	 * Async request campaigns data, and parse it to models.
	 *
	 */
	private class DownloadCampaignFile extends AsyncTask<String, Float, Integer>{
    	
  	 	protected void onPreExecute() {}

        protected Integer doInBackground(String ...valores) {
        	
			campaignObjectsArray = new ArrayList<MCMCampaignModel>();
       	 
        	try {
        		
        		// Execute request to get JSON
				JSONObject objectJSON = getJSONfromURL(valores[0]);
				
				if (objectJSON != null) {

                    Log.d(LOG_TAG,"Received Campaigns JSON: "+objectJSON);
					
					// Parse JSON to obtain campaign data
					JSONArray campaignArray = (JSONArray) objectJSON.get(ATTR_CAMPAIGNS_ARRAY);
					
					if (campaignArray != null && campaignArray.length() > 0) {
		
						for (int i=0; i<campaignArray.length(); i++) {
							JSONObject campaignJSON = campaignArray.getJSONObject(i);
							campaignObjectsArray.add(new MCMCampaignModel(campaignJSON));
						}
						
					} else {
						if(delegate!=null){
							delegate.campaignDidFinish();
						}
					}
				}else{
					if(delegate!=null){
						delegate.campaignDidFailed();
					}

				}
						
			} catch (ApplicationConfigurationNotFoundException e) {
				e.printStackTrace();
				if(delegate!=null){
					delegate.campaignDidFailed();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				if(delegate!=null){
					delegate.campaignDidFailed();
				}
			}
        	
       	 	return 0;
        }

        protected void onPostExecute(Integer bytes) {

            Log.d(LOG_TAG,"CampaignObjectsArray size: "+campaignObjectsArray.size());
        	
        	// After receiving campaign data, prepare banner
        	if (campaignObjectsArray.size()>0) {
        		campaignObjectSelected = getCampaignPerWeight();
            	createBanner(campaignObjectSelected);
			}
        }
	}
	
	/**
	 * DownloadCampaignImage.
	 * Async request banner image.
	 *
	 */
	private class DownloadCampaignImage extends AsyncTask<Void, Float, Integer>{
    	
		private Bitmap bitmap;
		
		protected void onPreExecute() {}

		protected Integer doInBackground(Void ...valores) {        	 
     	 
			try {
				URL imageUrl = new URL(campaignObjectSelected.getMediaFeature().getMedia());
				InputStream imageImputStream = (InputStream) imageUrl.getContent();
				bitmap = BitmapFactory.decodeStream(imageImputStream);
				
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
     	 	return 0;
 	 	} 

		protected void onPostExecute(Integer bytes) {
			
			// After downloading image, show the banner
			setImageBanner(bitmap, campaignObjectSelected);
			
			
		    mHandler.removeCallbacks(mRemoveCampaignBanner);
		    
		    //if duration is not zero it will not finish it automatically 
		    if(duration != 0){
		    	mHandler.postDelayed(mRemoveCampaignBanner, duration*1000);	
		    }
		    
		       
		       
			if(delegate!=null){
				delegate.campaignDidLoad();
			}
		}
	}
	
	  private Runnable mRemoveCampaignBanner = new Runnable() {
          public void run() {
             mHandler.removeCallbacks(mRemoveCampaignBanner);
             finishCampaign();
          }
        };
        
	/**
	 * SendHitClick
	 * Async request to notify impressions and clicks to Malcom.
	 * Needs the event type (CLICK or IMPRESSION) in first param.
	 *
	 */
	private class SendHitClick extends AsyncTask<String, Float, Integer> {
    	
		private Bitmap bitmap;
		
		protected void onPreExecute() {}

		protected Integer doInBackground(String ...valores) {        	 
      	 
			String url;
			try {
				
				// Create URL for campaign hit
				String malcomUrl 		= MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL);
				String campaignsPath 	= "v1/campaigns/";
				String idCampaign		= campaignObjectSelected.getCampaignId();
				String hitPath 			= "/hit/";
				String hitType 			= valores[0]; // CLICK or IMPRESSION
				String applicationPath 	= "/application/";
				String appId 			= MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID);
				String udidPath 		= "/udid/";
				String devideId 		= URLEncoder.encode(ToolBox.device_getId(context), "UTF-8");
				
				String svcUrl = campaignsPath + idCampaign + hitPath + hitType + applicationPath + appId + udidPath + devideId;
				url = malcomUrl + svcUrl;
				
				String secretKey = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);

				// Send hit to Malcom
				MalcomHttpOperations.sendRequestToMalcom(url, svcUrl, "", appId, secretKey, HTTP_METHOD.GET);
				
			
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
			};
      	 
			return 0;
		}

		protected void onPostExecute(Integer bytes) {}
	}
	
	/**
	 * Sends a GET request, obtains a response, and converts it to a JSON object.
	 * @param url - the destination of request
	 * @return - the response JSON object
	 * @throws ApplicationConfigurationNotFoundException
	 */
	private JSONObject getJSONfromURL(String url) throws ApplicationConfigurationNotFoundException{
    	
		String result = "";
    	JSONObject jObject = null;
    	try{
    		// Send request to Malcom and log it
    		Log.d(LOG_TAG, ">>> getJSONfromURL: " + url);
    		
    		// TODO: Refactor this by extracting the headers build to a static helper class. This class should be used by all the methods connecting with Malcom Server
    		
    		String malcomDate = HttpDateUtils.formatDate(new Date());
    		String verb = "GET";
    		String contentType = "application/json";
    		String md5 = ToolBox.md5_calculateMD5("");
    		
    		Map<String, String> headersData = new HashMap<String, String>();
    		headersData.put("Content-Type", "application/json");
    		headersData.put("content-md5", md5);
    		headersData.put("x-mcm-date", malcomDate);
    	
    		String dataToSign = ToolBox.deliveries_getDataToSign(ToolBox.getCanonicalizedMalcomHeaders(headersData), contentType, null, verb, campaignResource, md5);
            String secretKey = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);
    		String password = DigestUtils.calculateRFC2104HMAC(dataToSign, secretKey);
    		
    		// Complete headers with authorization
    		headersData.put("Authorization", "basic " + new String(Base64.encode(malcomAppId + ":" + password).getBytes()));
    		
    		// End of refactor
    		
    		
    		result = ToolBox.net_httpclient_doAction(HTTP_METHOD.GET, url, "", headersData);
    		Log.d(LOG_TAG, "<<< getJSONfromURL result: " + result);
    		    	    
    	    // Try parse the string to a JSON object
    		jObject = new JSONObject(result);
    	        	    
    	}catch(ApplicationConfigurationNotFoundException e){
    		throw e;
    	}catch(Exception e){    		
    	    Log.e(LOG_TAG, "<<< getJSONfromURL ERROR: " + e.toString() + " - " + e.getMessage());
    	}
    	
    	return jObject;    	
    }
	
	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	
}
