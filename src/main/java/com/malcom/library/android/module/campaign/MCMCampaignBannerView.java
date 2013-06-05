package com.malcom.library.android.module.campaign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 05/06/13
 * Time: 10:09
 * To change this template use File | Settings | File Templates.
 */
public class MCMCampaignBannerView extends View {

    private ImageView imageView;
    private MCMCampaignDTO campaign;
    private MCMCampaignBannerDelegate delegate;

    public MCMCampaignBannerView(Context context, MCMCampaignDTO campaign) {
        super(context);
        this.campaign = campaign;

        imageView = new ImageView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        imageView.setLayoutParams(layoutParams);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);    //To change body of overridden methods use File | Settings | File Templates.

        Log.d("Pedro","Llamamos al onDraw de la vista");
        Log.d(MCMCampaignDefines.LOG_TAG, "Downloading CampaignImage for: " + campaign.getName());
        //Request for remote image
        new MCMCampaignAsyncTasks.DownloadCampaignImage(this).execute(campaign.getMedia());

    }

    public void notifyBannerDidLoad() {
        if (delegate != null) {
            delegate.bannerDidLoad(campaign);
        }

        // Send Impression Hit event to Malcom
        new MCMCampaignAsyncTasks.NotifyServer(getContext()).execute(MCMCampaignDefines.ATTR_IMPRESSION_HIT, campaign.getCampaignId());
    }

    public void setDelegate(MCMCampaignBannerDelegate delegate) {
        this.delegate = delegate;
    }

    public void setImageBanner(Bitmap bitmap) {

        notifyBannerDidLoad();

        // Config banner image and click actions
        imageView.setImageBitmap(bitmap);
        imageView.setOnClickListener(new MCMCampaignBannerListener(getContext(), campaign, delegate));
    }

    public interface MCMCampaignBannerDelegate {

        /**
         * Notified delegate to indicate actions performed by the BannerView of Malcom.
         */
        public void bannerDidLoad(MCMCampaignDTO campaign);

        public void bannerDidFail();

        public void bannerClosed();

        public void bannerPressed(MCMCampaignDTO campaign);

    }
}
