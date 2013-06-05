package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 09/05/13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class MCMCampaignBannerListener implements View.OnClickListener {

    private Context context;
    private MCMCampaignDTO campaign;
    MCMCampaignBannerView.MCMCampaignBannerDelegate delegate;

    public MCMCampaignBannerListener(Context context, MCMCampaignDTO campaignModel, MCMCampaignBannerView.MCMCampaignBannerDelegate delegate) {
        this.context = context;
        this.campaign = campaignModel;
        this.delegate = delegate;
    }

    public void onClick(View view) {

        // Notify delegate the click
        if (delegate != null) {
            delegate.bannerPressed(campaign);
        }

        // Send Click Hit event to Malcom
        new MCMCampaignAsyncTasks.NotifyServer(context).execute(MCMCampaignDefines.ATTR_CLICK_HIT, campaign.getCampaignId());
    }

    public class MCMCampaignBannerCrossSellingListener extends MCMCampaignBannerListener {

        private Activity activity;

        public MCMCampaignBannerCrossSellingListener(Activity activity, MCMCampaignDTO campaignModel, MCMCampaignBannerView.MCMCampaignBannerDelegate delegate) {
            super(activity.getApplicationContext(),campaignModel,delegate);
            this.activity = activity;
        }

        public void onClick(View view) {
            super.onClick(view);

            crossSellingClick();
        }

        private void crossSellingClick() {
            // Open campaign app in PlayStore
            try {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + campaign.getPromotionIdentifier())));
            } catch (android.content.ActivityNotFoundException anfe) {
                activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + campaign.getPromotionIdentifier())));
            }
        }

    }
}
