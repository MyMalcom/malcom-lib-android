package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.malcom.library.android.module.campaign.MCMCampaignDTO.CampaignPosition;
import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.utils.MCMUtils;
import com.malcom.library.android.utils.ToolBox;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * MCMCampaignAdapter.
 * Campaigns module. Shows a banner in 4 posible positions: bottom, top, full screen, and middle.
 * Redirects to other app in Play Store.
 *
 * @author Malcom
 * @author Pepe - code refactor, added middle banners, new logs.
 */
public class MCMCampaignAdapter implements MCMCampaignBannerView.MCMCampaignBannerDelegate {

    public static int CAMPAIGN_DEFAULT_DURATION = -1;

    private static MCMCampaignAdapter instance = null;
    private ViewGroup bannerLayout;

    private String campaignResource = "";

    private String malcomAppId;

    private MCMCampaignDTO.CampaignType type;

    private Activity activity;

    private int duration;                                   //integer to set the duration of the banner

    private RequestCampaignReceiver receiver;               //callback to execute when the campaign request is done

    private MCMCampaignNotifiedDelegate delegate;

    public interface RequestCampaignReceiver {
        /**
         * @param banners
         */
        public void onReceivedPromotions(List banners);

        public void onRequestFailed(String errorMessage);
    }

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
    public void addBanner(Activity activity, MCMCampaignDTO.CampaignType campaignType, int duration, MCMCampaignNotifiedDelegate delegate) {

        this.activity = activity;
        this.type = campaignType;
        setDuration(duration);
        this.delegate = delegate;

        mHandler.removeCallbacks(mRemoveCampaignBanner);

        makeRequest();

    }

    public void requestBanner(Activity activity, MCMCampaignDTO.CampaignType campaignType, RequestCampaignReceiver receiver) {

        this.activity = activity;
        this.type = campaignType;
        this.receiver = receiver;

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
        this.bannerLayout.setVisibility(View.GONE);

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
            String devideId = URLEncoder.encode(ToolBox.device_getId(this.activity), "UTF-8");

            campaignResource = MCMCampaignDefines.CAMPAIGN_URL.replace(MCMCampaignDefines.APP_ID_TAG, malcomAppId)
                    .replace(MCMCampaignDefines.UDID_TAG, devideId);

            String urlCampaign = malcomBaseUrl + campaignResource;

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
    protected void proccessResponse(ArrayList<MCMCampaignDTO> campaignsArray) {

        if (type == MCMCampaignDTO.CampaignType.IN_APP_CROSS_SELLING ||
                type == MCMCampaignDTO.CampaignType.IN_APP_PROMOTION) {

            ArrayList<MCMCampaignDTO> selectionCampaignsArray = new ArrayList<MCMCampaignDTO>();
            if (type == MCMCampaignDTO.CampaignType.IN_APP_CROSS_SELLING) {
                ArrayList<MCMCampaignDTO> filteredArray = MCMCampaignsUtils.getFilteredCampaigns(campaignsArray, MCMCampaignDTO.CampaignType.IN_APP_CROSS_SELLING);

                //If there is at least one campaign
                if (filteredArray.size()>0) {
                    MCMCampaignDTO selectedCampaign = MCMCampaignsUtils.getCampaignPerWeight(filteredArray);
                    selectionCampaignsArray.add(selectedCampaign);
                }
            } else if (type == MCMCampaignDTO.CampaignType.IN_APP_PROMOTION) {
                selectionCampaignsArray = MCMCampaignsUtils.getFilteredCampaigns(campaignsArray, MCMCampaignDTO.CampaignType.IN_APP_PROMOTION);
            }

            if (selectionCampaignsArray.size()>0) {
                if (receiver == null) {
                        createBanners(selectionCampaignsArray);
                } else {
                    receiver.onReceivedPromotions(createBannersList(activity, selectionCampaignsArray));
                }
            } else {
                notifyCampaignDidFail("There is no campaign to show");
            }
        }
    }

    /**
     * Initiates banner layout depending on position.
     * Launches request to get remote image.
     *
     * @param campaignsArray - the campaigns' data to show.
     */
    protected void createBanners(ArrayList<MCMCampaignDTO> campaignsArray) {

        Log.d(MCMCampaignDefines.LOG_TAG, "createBanners - type: " + type + " campaigns: " + campaignsArray.size());

        if (campaignsArray == null) {
            return;
        }

        // Get layout elements
        try {
            int resBannerLayoutID = activity.getResources().getIdentifier(MCMCampaignDefines.RES_ID_LAYOUT, "id", activity.getPackageName());
            if (resBannerLayoutID==0) {
                throw new Exception("The layout with id = \"campaign_banner_layout\" was not found ");
            }
            bannerLayout = (RelativeLayout) activity.findViewById(resBannerLayoutID);

            if (type == MCMCampaignDTO.CampaignType.IN_APP_CROSS_SELLING) {
                congigureCrossSellingCampaignLayout(campaignsArray.get(0), bannerLayout);
            } else if (type == MCMCampaignDTO.CampaignType.IN_APP_PROMOTION) {
                bannerLayout = createPromotionsLayout(activity, (RelativeLayout) bannerLayout);
            }

            for (MCMCampaignDTO campaign : campaignsArray) {

                //Creates the bannerView with the campaign data
                MCMCampaignBannerView bannerView = new MCMCampaignBannerView(activity, campaign);

                //Calculates the banner height
                float density = activity.getResources().getDisplayMetrics().density;
                int bannerHeight = (int) (MCMCampaignDefines.BANNER_SIZE_HEIGHT * density + 0.5f);
                if (campaign.isFullScreen()) {
                    bannerHeight = ViewGroup.LayoutParams.MATCH_PARENT;
                }

                //Configures the banner view
                bannerView.setLayoutParams(new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        bannerHeight));
                bannerView.setDelegate(this);

                //When a bannerView is added to a view, the remoteimage starts to download
                bannerLayout.addView(bannerView);

                // Config close button (if banner shows on full screen)
                if (campaign.isFullScreen()) {
                    if (RelativeLayout.class.isInstance(bannerLayout))
                        addCloseButton((RelativeLayout) bannerLayout);
                }

                //if duration is not zero the banner will be removed automatically
                if (duration != 0) {
                    mHandler.removeCallbacks(mRemoveCampaignBanner);
                    mHandler.postDelayed(mRemoveCampaignBanner, duration * 1000);
                }
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


    private static void congigureCrossSellingCampaignLayout(MCMCampaignDTO campaign, ViewGroup layout) {

        // Config layout params depending on position
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) layout.getLayoutParams();
//        bannerLayout.setGravity(Gravity.CENTER);
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
    private static void addCloseButton(final RelativeLayout layout) {

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

        //RoundButton
        int radius = MCMUtils.getDPI(layout.getContext(),65);
        RoundRectShape rs = new RoundRectShape(new float[] { radius, radius, radius, radius, radius, radius, radius, radius}, null, null);
        ShapeDrawable sd = new ShapeDrawable(rs);
        sd.getPaint().setColor(Color.argb(MCMCampaignDefines.BACKGROUND_ALPHA, 0, 0, 0));
        closeButton.setBackground(sd);

        //Adds the view
        layout.addView(closeButton);
        layout.setBackgroundColor(Color.argb(MCMCampaignDefines.BACKGROUND_ALPHA, 0, 0, 0));

        //Onclick
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

    public MCMCampaignDTO.CampaignType getType() {
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

    }

    @Override
    public void bannerDidFail() {

    }

    @Override
    public void bannerClosed() {

    }

    @Override
    public void bannerPressed(MCMCampaignDTO campaign) {

    }

}
