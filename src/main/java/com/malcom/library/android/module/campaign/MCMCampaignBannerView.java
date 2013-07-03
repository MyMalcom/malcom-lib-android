package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.malcom.library.android.MCMDefines;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 05/06/13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class MCMCampaignBannerView extends ImageView {

    private Activity activity;
    private MCMCampaignDTO campaign;
    private MCMCampaignBannerDelegate delegate;

    private boolean imageLoaded = false;

    public MCMCampaignBannerView(Activity activity, MCMCampaignDTO campaign) {
        super(activity.getApplicationContext());
        this.activity = activity;
        this.campaign = campaign;

        //Set the layout params to force the call to onDraw() when parent's addView is called
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        setLayoutParams(layoutParams);

        setScaleType(ScaleType.FIT_XY);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);    //To change body of overridden methods use File | Settings | File Templates.

        if (!imageLoaded){
            Log.d(MCMCampaignDefines.LOG_TAG, "Downloading CampaignImage for: " + campaign.getName());
            //Request for remote image
            new MCMCampaignAsyncTasks.DownloadCampaignImage(this).execute(campaign.getMedia());
        }

    }

    public void notifyBannerDidLoad() {
        if (delegate != null) {
            delegate.bannerDidLoad(campaign);
        }

        // Send Impression Hit event to Malcom
        new MCMCampaignAsyncTasks.NotifyServer(getContext()).execute(MCMCampaignDefines.ATTR_IMPRESSION_HIT, campaign.getCampaignId());
    }

    public void notifyBannerFailLoading(String errorMessage) {
        if (delegate != null) {
            delegate.bannerDidFail(errorMessage);
        }

        Log.e(MCMDefines.LOG_TAG,"There was a problem loading the banner for campaign: "+campaign.getName());
    }

    public void setDelegate(MCMCampaignBannerDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Shows banner image after download finish.
     * @param bitmap   - the downloaded image.
     */
    public void setImageBanner(Bitmap bitmap) {

        notifyBannerDidLoad();

        imageLoaded = true;

        // Config banner image and click actions
        setImageBitmap(bitmap);
        setOnClickListener(new MCMCampaignBannerListener(activity, campaign, delegate));

    }

    public MCMCampaignDTO getCampaign(){
        return campaign;
    }

    public interface MCMCampaignBannerDelegate {

        /**
         * Notified delegate to indicate actions performed by the BannerView of Malcom.
         */
        public void bannerDidLoad(MCMCampaignDTO campaign);

        public void bannerDidFail(String errorMessage);

        public void bannerClosed();

        public void bannerPressed(MCMCampaignDTO campaign);

    }
}
