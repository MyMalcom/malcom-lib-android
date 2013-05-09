package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.malcom.library.android.module.campaign.MCMCampaignModel.CampaignPosition;
import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.utils.MCMUtils;
import com.malcom.library.android.utils.ToolBox;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * MCMCampaignAdapter.
 * Campaigns module. Shows a banner in 4 posible positions: bottom, top, full screen, and middle.
 * Redirects to other app in Play Store.
 *
 * @author Malcom
 * @author Pepe - code refactor, added middle banners, new logs.
 */
public class MCMCampaignAdapter {

    public static int CAMPAIGN_DEFAULT_DURATION = -1;

    private static MCMCampaignAdapter instance = null;
    private ViewGroup bannerLayout;

    private String campaignResource = "";

    private String malcomAppId;

    private MCMCampaignModel.CampaignType type;

    private Activity activity;
    private float density = 1;
    private int resBannerLayoutID;
    private int resImageLayoutID;

    private int duration; //integer to set the duration of the banner


    public MCMCampaignNotifiedDelegate delegate;

    private Handler mHandler = new Handler();

    // Exists only to defeat instantiation.
    protected MCMCampaignAdapter() {
    }

    /**
     * Gets the singleton instance of this class.
     *
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
     *
     * @param activity     - your activity.
     * @param campaignType - the type of campaign requested
     * @param duration     - the duration to show the campaign banner in seconds
     * @param delegate     - This is the delegate.
     */
    public void addBanner(Activity activity, MCMCampaignModel.CampaignType campaignType, int duration, MCMCampaignNotifiedDelegate delegate) {

        this.activity = activity;
        this.type = campaignType;
        setDuration(duration);
        this.delegate = delegate;

        mHandler.removeCallbacks(mRemoveCampaignBanner);


        this.density = this.activity.getResources().getDisplayMetrics().density;
        try {
            // Create URL to get campaigns of my app
            String malcomBaseUrl = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL);

            malcomAppId = URLEncoder.encode(MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID), "UTF-8");
            String devideId = URLEncoder.encode(ToolBox.device_getId(this.activity), "UTF-8");

            campaignResource = MCMCampaignDefines.CAMPAIGN_URL.replace(MCMCampaignDefines.APP_ID_TAG, malcomAppId)
                    .replace(MCMCampaignDefines.UDID_TAG, devideId);

//			String urlCampaign = malcomBaseUrl + campaignResource;
            String urlCampaign = campaignResource;

            // Launch request to get campaigns data
            new MCMCampaignAsyncTasks.DownloadCampaignFile(type, this).execute(urlCampaign);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that removes the banner and notifies it to delegate if delegate is set.
     *
     * @since 1.0.1
     */
    public void removeCurrentBanner(Activity activity) {

        // Get layout elements
        resBannerLayoutID = activity.getResources().getIdentifier(MCMCampaignDefines.RES_ID_LAYOUT, "id", activity.getPackageName());
        bannerLayout = (RelativeLayout) activity.findViewById(resBannerLayoutID);
        this.bannerLayout.setVisibility(View.GONE);

        if (delegate != null) {
            delegate.campaignDidFinish();
        }
    }


    /**
     * Method that removes the banner and notifies it to delegate if delegate is set when time is over (duration)
     *
     * @since 1.0.1
     */
    public void finishCampaign() {

        this.bannerLayout.setVisibility(View.GONE);

        if (delegate != null) {
            delegate.campaignDidFinish();
        }
    }

    /**
     * Initiates banner layout depending on position.
     * Launches request to get remote image.
     *
     * @param campaignsArray - the campaigns' data to show.
     */
    protected void createBanner(ArrayList<MCMCampaignModel> campaignsArray) {

        Log.d(MCMCampaignDefines.LOG_TAG, "createBanner - type: " + type + " campaigns: " + campaignsArray.size());

        if (campaignsArray == null) {
            return;
        }

        // Get layout elements
        try {
            resBannerLayoutID = activity.getResources().getIdentifier(MCMCampaignDefines.RES_ID_LAYOUT, "id", activity.getPackageName());
            bannerLayout = (RelativeLayout) activity.findViewById(resBannerLayoutID);
            resImageLayoutID = activity.getResources().getIdentifier(MCMCampaignDefines.RES_ID_IMAGE, "id", activity.getPackageName());

            if (type == MCMCampaignModel.CampaignType.IN_APP_CROSS_SELLING) {
                congigureCrossSellingCampaignLayout(campaignsArray.get(0), bannerLayout);
            } else if (type == MCMCampaignModel.CampaignType.IN_APP_PROMOTION) {
                bannerLayout = createPromotionsLayout(activity, (RelativeLayout) bannerLayout);
            }

            Iterator campaignsIterator = campaignsArray.iterator();

            while (campaignsIterator.hasNext()) {
                // Launch request to get image bitmap and add it to banner layout
                new MCMCampaignAsyncTasks.DownloadCampaignImage(this).execute((MCMCampaignModel) campaignsIterator.next());
            }

        } catch (Exception e) {

            Log.d(MCMCampaignDefines.LOG_TAG, "Create banner error: Attends to load the layout " + MCMCampaignDefines.RES_ID_LAYOUT);

        }

    }

    private static LinearLayout createPromotionsLayout(Activity activity, RelativeLayout layout) {

        //Create the ScrollView to can add more banners
        ScrollView scroll = new ScrollView(activity);
        scroll.setBackgroundColor(android.R.color.transparent);
        scroll.setLayoutParams(layout.getLayoutParams());

        LinearLayout resultantLayout = new LinearLayout(activity);
        resultantLayout.setOrientation(LinearLayout.VERTICAL);

        //Add the views in hierarchy
        scroll.addView(resultantLayout);
        layout.addView(scroll);

        return resultantLayout;
    }


    private static void congigureCrossSellingCampaignLayout(MCMCampaignModel campaign, ViewGroup layout) {

        // Config layout params depending on position
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
//        bannerLayout.setGravity(Gravity.CENTER);
        if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.BOTTOM) {

            params.height = MCMUtils.getDPI(layout.getContext(), MCMCampaignDefines.SIZE_BANNER);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.TOP) {

            params.height = MCMUtils.getDPI(layout.getContext(), MCMCampaignDefines.SIZE_BANNER);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        } else if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.MIDDLE_LANDSCAPE ||
                campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.MIDDLE_PORTRAIT) {

            int margin = MCMUtils.getDPI(layout.getContext(), MCMCampaignDefines.MIDDLE_MARGIN);
            layout.setPadding(margin, margin, margin, margin);
        } else if (campaign.getMediaFeature().getCampaignPosition() == CampaignPosition.FULL_SCREEN) {

            // mantain params of full screen
        }

        // Apply params to banner layout
        layout.setLayoutParams(params);
    }

    /**
     * Shows banner image after download finish.
     * Configures close button if needed, and adds click events for banner and close button.
     *
     * @param bitmap   - the downloaded image.
     * @param campaign - the campaign object that is showed.
     */
    protected void setImageBanner(Bitmap bitmap, final MCMCampaignModel campaign) {

        Log.d(MCMCampaignDefines.LOG_TAG, "setImageBanner campaign: " + campaign.getName());

        ImageView bannerImageView = new ImageView(activity.getApplicationContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        bannerImageView.setLayoutParams(layoutParams);

        bannerLayout.addView(bannerImageView);

        // Config banner image and click actions
        bannerImageView.setImageBitmap(bitmap);
        bannerImageView.setOnClickListener(new MCMCampaignBannerListener(activity, campaign, delegate));

        // Config close button (if banner position is middle or full screen)
        if (campaign.getMediaFeature().getCampaignPosition() != CampaignPosition.BOTTOM &&
                campaign.getMediaFeature().getCampaignPosition() != CampaignPosition.TOP) {
            if (RelativeLayout.class.isInstance(bannerLayout))
                addCloseButton((RelativeLayout) bannerLayout);
        }

        // Send Impression Hit event to Malcom
        new MCMCampaignAsyncTasks.SendHitClick(activity.getApplicationContext()).execute(MCMCampaignDefines.ATTR_IMPRESSION_HIT, campaign.getCampaignId());

        //if duration is not zero the banner will be removed automatically
        if (duration != 0) {
            mHandler.removeCallbacks(mRemoveCampaignBanner);
            mHandler.postDelayed(mRemoveCampaignBanner, duration * 1000);
        }
    }

    /**
     * Method that adds a close button to the layout and hide its when clicked
     *
     * @param layout RelativeLayout where the button will be added
     */
    private static void addCloseButton(final RelativeLayout layout) {
        Button closeButton = new Button(layout.getContext());
        closeButton.setText("X");
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_RIGHT, layout.getId());
        params.addRule(RelativeLayout.ALIGN_TOP, layout.getId());
        closeButton.setLayoutParams(params);
        layout.addView(closeButton);
        layout.setBackgroundColor(Color.argb(MCMCampaignDefines.BACKGROUND_ALPHA, 0, 0, 0));
        closeButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {

                // Hide banner
                layout.setVisibility(View.GONE);
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

    public MCMCampaignModel.CampaignType getType() {
        return type;
    }

    public String getCampaignResource() {
        return campaignResource;
    }

    public String getMalcomAppId() {
        return malcomAppId;
    }

}
