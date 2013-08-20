package com.malcom.library.android.module.campaign;

import android.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.loopj.android.image.SmartImageTask;
import com.loopj.android.image.SmartImageView;
import com.malcom.library.android.MCMDefines;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 05/06/13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class MCMCampaignBannerView extends SmartImageView {

    private Activity activity;
    private MCMCampaignDTO campaign;
    private MCMCampaignBannerDelegate delegate;

    private Integer loadingImageResId;

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

        setScaleType(ImageView.ScaleType.FIT_XY);

    }

    public MCMCampaignBannerView(Activity activity, MCMCampaignDTO campaign, Integer loadingImageResId) {
        this(activity,campaign);

        this.loadingImageResId = loadingImageResId;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);    //To change body of overridden methods use File | Settings | File Templates.

        if (!imageLoaded){
            Log.d(MCMCampaignDefines.LOG_TAG, "Downloading CampaignImage for: " + campaign.getName());
            //Set the remote image
            this.setImageUrl(campaign.getMedia(), null, loadingImageResId, new SmartImageTask.OnCompleteListener() {
                @Override
                public void onComplete() {

                    notifyBannerDidLoad();

                    imageLoaded = true;

                    // Config banner click actions
                    setOnClickListener(new MCMCampaignBannerListener(activity, campaign, delegate));

                }
            });
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
