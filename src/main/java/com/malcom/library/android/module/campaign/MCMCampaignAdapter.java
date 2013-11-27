package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.malcom.library.android.MCMDefines;
import com.malcom.library.android.module.campaign.MCMCampaignDTO.CampaignPosition;
import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.utils.LocationUtils;
import com.malcom.library.android.utils.MCMUtils;
import com.malcom.library.android.utils.ToolBox;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.malcom.library.android.module.campaign.MCMCampaignDTO.CampaignType;

/**
 * Handles most of campaigns logic.
 */
public class MCMCampaignAdapter implements MCMCampaignBannerView.MCMCampaignBannerDelegate {

	// TODO: This class is messy and problematic. We must refactor or rebuild it from scratch.

	// For example, it's a singleton with (a lot of) state, so it's not thread-safe.
	// In the meanwhile, to mitigate that, I've added the type argument in {@link #getInstance(CampaignType)} to return a
	// different instance for each campaign type. That way, apps can use different campaign types at the same time with less risk.

	// Also, it uses Handlers instead of AsyncTasks tasks. That makes the class very difficult to use from background threads.

	public static int CAMPAIGN_DEFAULT_DURATION = -1;

    private static Map<CampaignType, MCMCampaignAdapter> instances = new HashMap<CampaignType, MCMCampaignAdapter>();

    private ViewGroup bannerLayout;

    private String campaignResource = "";

    private String malcomAppId;

    private CampaignType type;

    private Activity activity;

    private int duration;                                   //integer to set the duration of the banner

    private RequestCampaignReceiver receiver;               //callback to execute when the campaign request is done

    private MCMCampaignNotifiedDelegate delegate;

    private Integer loadingImageResId;

    public interface RequestCampaignReceiver {
        /**
         * @param banners
         */
        public void onReceivedPromotions(List<MCMCampaignBannerView> banners);

        public void onRequestFailed(String errorMessage);
    }

    private Handler mHandler = new Handler();

    // Exists only to defeat instantiation.
    protected MCMCampaignAdapter() {
    }

    /**
     * Gets the singleton instance of this class for the given campaign type.
     *
     * @return instance of MCMCampaignAdapter.
     */
    public static synchronized MCMCampaignAdapter getInstance(CampaignType type) {

        if (!instances.containsKey(type)) {
            instances.put(type, new MCMCampaignAdapter());
        }

        return instances.get(type);
    }

    /**
     * Adds a campaign banner to an activity.
     *
     * @param activity     - your activity.
     * @param campaignType - the type of campaign requested
     * @param duration     - the duration to show the campaign banner in seconds
     * @param delegate     - This is the delegate.
     */
    public void addBanner(Activity activity, CampaignType campaignType, int duration, MCMCampaignNotifiedDelegate delegate, Integer loadingImgResId) {

        this.activity = activity;
        this.type = campaignType;
        setDuration(duration);
        this.delegate = delegate;
        this.loadingImageResId = loadingImgResId;

        mHandler.removeCallbacks(mRemoveCampaignBanner);

        makeRequest();

    }

    public void requestBanner(Activity activity, CampaignType campaignType, RequestCampaignReceiver receiver) {

        this.activity = activity;
        this.type = campaignType;
        this.receiver = receiver;

        makeRequest();
    }

    public void addRateAlert(Activity activity, MCMCampaignNotifiedDelegate delegate) {
        this.activity = activity;
        this.type = CampaignType.IN_APP_RATE_MY_APP;
        this.delegate = delegate;

        makeRequest();
    }

    /**
     * Method that removes the banner and notifies it to delegate if delegate is set.
     *
     * @since 1.0.1
     */
    public void removeCurrentBanner(Activity activity) {

        // Get layout elements
        int resBannerLayoutID = activity.getResources().getIdentifier(MCMCampaignDefines.RES_ID_LAYOUT, "id", activity.getPackageName());
        bannerLayout = (RelativeLayout) activity.findViewById(resBannerLayoutID);
        if (bannerLayout!=null) {
            bannerLayout.setVisibility(View.GONE);
        }

        notifyCampaignDidFinish();
    }


    /**
     * Method that removes the banner and notifies it to delegate if delegate is set when time is over (duration)
     *
     * @since 1.0.1
     */
    public void finishCampaign() {

        this.bannerLayout.setVisibility(View.GONE);

        notifyCampaignDidFinish();
    }

    private void makeRequest() {

        try {
            // Create URL to get campaigns of my app
            String malcomBaseUrl = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL);

            malcomAppId = URLEncoder.encode(MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID), "UTF-8");
            String devideId = URLEncoder.encode(ToolBox.device_getId(activity.getApplicationContext()), "UTF-8");

            campaignResource = MCMCampaignDefines.CAMPAIGN_URL.replace(MCMCampaignDefines.APP_ID_TAG, malcomAppId)
                    .replace(MCMCampaignDefines.UDID_TAG, devideId);

            String urlCampaign = malcomBaseUrl + campaignResource;

            //Add the location to the URL
            Location lastKnownLocation = LocationUtils.getLocation(activity.getApplicationContext());
            if (lastKnownLocation!=null) {
                urlCampaign = urlCampaign+"?lat="+lastKnownLocation.getLatitude()
                        +"&lng="+lastKnownLocation.getLongitude();
            }

            // Launch request to get campaigns data
            new MCMCampaignAsyncTasks.DownloadCampaignFile(type, this).execute(urlCampaign);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            notifyCampaignDidFail(e.getMessage());
        }
    }

    /**
     * Method that filters the elements for current campaign type and calls the needed operation after the filtered
     *
     * @param campaignsArray the campaigns collected from the server's response
     */
    protected void processResponse(ArrayList<MCMCampaignDTO> campaignsArray) {

        //Gets the campaigns for the current type
        MCMCampaignDTO selectedCampaign = null;
        ArrayList<MCMCampaignDTO> filteredArray = MCMCampaignsLogics.getFilteredCampaigns(campaignsArray, type);
        if(type == CampaignType.IN_APP_CROSS_SELLING) {
            ArrayList<MCMCampaignDTO> externalCampaignsArray = MCMCampaignsLogics.getFilteredCampaigns(campaignsArray, CampaignType.IN_APP_EXTERNAL_URL);
            filteredArray.addAll(externalCampaignsArray);
        }

        //If there is at least one campaign
        if (filteredArray.size() > 0) {
            selectedCampaign = MCMCampaignsLogics.getCampaignPerWeight(filteredArray);
        }

        if (selectedCampaign != null) {

            if (type == CampaignType.IN_APP_CROSS_SELLING || type == CampaignType.IN_APP_EXTERNAL_URL || type == CampaignType.IN_APP_PROMOTION) {
                if (receiver == null) {
                    createBanner(selectedCampaign);
                } else {
                    receiver.onReceivedPromotions(createBannersList(activity, filteredArray));
                }
            } else if (type == CampaignType.IN_APP_RATE_MY_APP) {

                //Show the dialog if it's necessary
                if (MCMCampaignsLogics.shouldShowDialog(activity.getApplicationContext(),selectedCampaign)) {
                    createRateDialog(selectedCampaign);
                }
                //Update the session number
                MCMCampaignsLogics.updateRateDialogSession(activity.getApplicationContext(),selectedCampaign);
            }
        } else {
            notifyCampaignDidFail("There is no campaign to show");
        }
    }

    /**
     * Initiates banner layout depending on position.
     * Launches request to get remote image.
     *
     * @param campaign - the campaign data to show.
     */
    protected void createBanner(MCMCampaignDTO campaign) {

        Log.d(MCMCampaignDefines.LOG_TAG, "createBanner - type: " + type + " campaign: " + campaign);

        // Get layout elements
        try {
            int resBannerLayoutID = activity.getResources().getIdentifier(MCMCampaignDefines.RES_ID_LAYOUT, "id", activity.getPackageName());
            if (resBannerLayoutID==0) {
                throw new Exception("The layout with id = \"" + MCMCampaignDefines.RES_ID_LAYOUT + "\" was not found ");
            }
            bannerLayout = (ViewGroup) activity.findViewById(resBannerLayoutID);

            //Configures the layout
            configureCampaignLayout(campaign, bannerLayout);

            //Creates the bannerView with the campaign data
            MCMCampaignBannerView bannerView = new MCMCampaignBannerView(activity, campaign, loadingImageResId);

            //Calculates the banner height
            float density = activity.getResources().getDisplayMetrics().density;
            int bannerHeight = (int) (MCMCampaignDefines.BANNER_SIZE_HEIGHT * density + 0.5f);
            if (campaign.isFullScreen()) {
                bannerHeight = ViewGroup.LayoutParams.MATCH_PARENT;
            }

            //Configures the banner view
            bannerView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,bannerHeight));
            bannerView.setDelegate(this);

            //When a bannerView is added to a view, the remoteimage starts to download
            bannerLayout.addView(bannerView);

            // Config close button (if banner shows on full screen)
            if (campaign.isFullScreen()) {
                if (bannerLayout instanceof RelativeLayout)
                    addCloseButton((RelativeLayout) bannerLayout);
				else
					Log.w(MCMCampaignDefines.LOG_TAG, MCMCampaignDefines.RES_ID_LAYOUT + " is not a RelativeLayout so close button won't be added");
            }

            //if duration is not zero the banner will be removed automatically
            if (duration != 0) {
                mHandler.removeCallbacks(mRemoveCampaignBanner);
                mHandler.postDelayed(mRemoveCampaignBanner, duration * 1000);
            }

        } catch (Exception e) {
            e.printStackTrace();

            Log.d(MCMCampaignDefines.LOG_TAG, "Create banner error: Attends to load the layout " + MCMCampaignDefines.RES_ID_LAYOUT);
            notifyCampaignDidFail(e.getMessage());

        }

    }

    protected static List<MCMCampaignBannerView> createBannersList(Activity activity, ArrayList<MCMCampaignDTO> campaignsArray) {

        ArrayList<MCMCampaignBannerView> bannersList = new ArrayList<MCMCampaignBannerView>();

        Iterator campaignsIterator = campaignsArray.iterator();

        while (campaignsIterator.hasNext()) {
            // Launch request to get image bitmap and add it to banner layout
//                new MCMCampaignAsyncTasks.DownloadCampaignImage(this).execute((MCMCampaignDTO) campaignsIterator.next());
            MCMCampaignBannerView bannerView = new MCMCampaignBannerView(activity, (MCMCampaignDTO) campaignsIterator.next());
            bannersList.add(bannerView);
        }

        return bannersList;
    }

    private void createRateDialog(MCMCampaignDTO campaignDTO) {

        //Only notify when the dialog will be shown
        new MCMCampaignAsyncTasks.NotifyServer(activity.getApplicationContext()).execute(MCMCampaignDefines.ATTR_IMPRESSION_HIT,campaignDTO.getCampaignId());
        notifyCampaignDidLoad();

        MCMCampaignHelper.showRateMyAppDialog(activity, campaignDTO, new MCMCampaignHelper.RateMyAppDialogDelegate() {
            @Override
            public void dialogRatePressed(MCMCampaignDTO campaignDTO) {

                //Open the market
                Uri uri = Uri.parse("market://details?id=" + activity.getApplication().getPackageName());
                activity.startActivity(new Intent(Intent.ACTION_VIEW,uri));

                //Update the control parameters
                MCMCampaignsLogics.updateRateDialogDontShowAgain(activity.getApplicationContext());
                new MCMCampaignAsyncTasks.NotifyServer(activity.getApplicationContext()).execute(MCMCampaignDefines.ATTR_RATE_HIT,campaignDTO.getCampaignId());

                notifyCampaignDidFinish();
            }

            @Override
            public void dialogDisablePressed(MCMCampaignDTO campaignDTO) {

                //Update the control parameters
                MCMCampaignsLogics.updateRateDialogDontShowAgain(activity.getApplicationContext());
                new MCMCampaignAsyncTasks.NotifyServer(activity.getApplicationContext()).execute(MCMCampaignDefines.ATTR_NEVER_HIT,campaignDTO.getCampaignId());

                notifyCampaignDidFinish();
            }

            @Override
            public void dialogRemindMeLaterPressed(MCMCampaignDTO campaignDTO) {

                //Update the control parameters
                MCMCampaignsLogics.updateRateDialogDate(activity.getApplicationContext());
                new MCMCampaignAsyncTasks.NotifyServer(activity.getApplicationContext()).execute(MCMCampaignDefines.ATTR_REMIND_HIT,campaignDTO.getCampaignId());

                notifyCampaignDidFinish();
            }
        });
    }

    /**
     * Method that notifies the delegate that the campaign was loaded
     */
    protected void notifyCampaignDidLoad() {
        if (delegate != null) {
            delegate.campaignDidLoad();
        }
    }

    /**
     * Method that notifies the delegate that the campaign finished
     */
    protected void notifyCampaignDidFinish() {
        if (delegate != null) {
            delegate.campaignDidFinish();
        }
    }

    /**
     * Method that notifies the delegate that the campaign failed
     *
     * @param errorMessage the message of the error
     */
    protected void notifyCampaignDidFail(String errorMessage) {
        if (delegate != null) {
            delegate.campaignDidFail(errorMessage);
        }

        if (receiver != null) {
            receiver.onRequestFailed(errorMessage);
        }
    }

    private static LinearLayout createPromotionsLayout(Activity activity, RelativeLayout layout) {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        //Create the ScrollView to can add more banners
        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(android.R.color.transparent);
        scroll.setLayoutParams(layoutParams);

        LinearLayout resultantLayout = new LinearLayout(activity);
        resultantLayout.setLayoutParams(layoutParams);
        resultantLayout.setOrientation(LinearLayout.VERTICAL);

        //Add the views in hierarchy
        scroll.addView(resultantLayout);
        layout.addView(scroll);

        return resultantLayout;
    }


    private static void configureCampaignLayout(MCMCampaignDTO campaign, ViewGroup layout) {

		if ( !(layout.getLayoutParams() instanceof RelativeLayout.LayoutParams) ) {
			throw new IllegalArgumentException(MCMCampaignDefines.RES_ID_LAYOUT + " is not inside a RelativeLayout so it can't be placed");
		}

        // Config layout params depending on position
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();

        if (campaign.getCampaignPosition() == CampaignPosition.BOTTOM) {

            params.height = MCMUtils.getDPI(layout.getContext(), MCMCampaignDefines.BANNER_SIZE_HEIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (campaign.getCampaignPosition() == CampaignPosition.TOP) {

            params.height = MCMUtils.getDPI(layout.getContext(), MCMCampaignDefines.BANNER_SIZE_HEIGHT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else if (campaign.getCampaignPosition() == CampaignPosition.MIDDLE_LANDSCAPE ||
                campaign.getCampaignPosition() == CampaignPosition.MIDDLE_PORTRAIT) {

            int margin = MCMUtils.getDPI(layout.getContext(), MCMCampaignDefines.MIDDLE_MARGIN);
            layout.setPadding(margin, margin, margin, margin);
        } else if (campaign.getCampaignPosition() == CampaignPosition.FULL_SCREEN) {

            // mantain params of full screen
        }

        // Apply params to banner layout
        layout.setLayoutParams(params);
    }

    /**
     * Method that adds a close button to the layout and hide its when clicked
     *
     * @param layout RelativeLayout where the button will be added
     */
    private void addCloseButton(final RelativeLayout layout) {

        Button closeButton = new Button(layout.getContext());
        closeButton.setText("X");

        //Sets layout params
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                MCMUtils.getDPI(layout.getContext(),MCMCampaignDefines.CLOSE_BUTTON_SIZE),
                MCMUtils.getDPI(layout.getContext(),MCMCampaignDefines.CLOSE_BUTTON_SIZE));
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, layout.getId());
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP, layout.getId());
        //Margins
        int margin = MCMUtils.getDPI(layout.getContext(),10);
        params.setMargins(margin, margin, margin, margin);
        closeButton.setLayoutParams(params);

        //Adds the view
        layout.addView(closeButton);
        layout.setBackgroundColor(Color.argb(MCMCampaignDefines.BACKGROUND_ALPHA, 0, 0, 0));

        //Onclick
        closeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                // Hide banner
                layout.setVisibility(View.GONE);
                MCMCampaignAdapter.this.notifyCampaignDidFinish();
            }
        });
    }

    private Runnable mRemoveCampaignBanner = new Runnable() {
        public void run() {
            mHandler.removeCallbacks(mRemoveCampaignBanner);
            finishCampaign();
        }
    };

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        if (duration < 0) {
            //sets the default time duration
            this.duration = MCMCampaignDefines.DEFAULT_CAMPAIGN_DURATION;
        } else {
            //set the duration parameter
            this.duration = duration;
        }
    }

    public CampaignType getType() {
        return type;
    }

    public String getCampaignResource() {
        return campaignResource;
    }

    public String getMalcomAppId() {
        return malcomAppId;
    }

    @Override
    public void bannerDidLoad(MCMCampaignDTO campaign) {

        if (delegate != null) {
            delegate.campaignDidLoad();
        }

    }

    @Override
    public void bannerDidFail(String errorMessage) {

        notifyCampaignDidFail(errorMessage);

    }

    @Override
    public void bannerClosed() {

        notifyCampaignDidFinish();

    }

    @Override
    public void bannerPressed(MCMCampaignDTO campaign) {
        Log.d(MCMDefines.LOG_TAG,"Banner pressed: "+campaign.getName());

        if (delegate != null) {
            delegate.campaignPressed(campaign);
        }

    }

}
