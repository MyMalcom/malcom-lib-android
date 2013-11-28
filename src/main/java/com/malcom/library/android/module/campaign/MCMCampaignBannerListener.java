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

    private Activity activity;
    private MCMCampaignDTO campaign;
    MCMCampaignBannerView.MCMCampaignBannerDelegate delegate;

    public MCMCampaignBannerListener(Activity activity, MCMCampaignDTO campaignModel, MCMCampaignBannerView.MCMCampaignBannerDelegate delegate) {
        this.activity = activity;
        this.campaign = campaignModel;
        this.delegate = delegate;
    }

    public void onClick(View view) {

        // Notify delegate the click
        if (delegate != null) {
            delegate.bannerPressed(campaign);
        }

        if (campaign.getType() == MCMCampaignDTO.CampaignType.IN_APP_CROSS_SELLING) {
            crossSellingClick();
        }

        if (campaign.getType() == MCMCampaignDTO.CampaignType.IN_APP_EXTERNAL_URL) {
            externalURLClick();
        }

        // Send Click Hit event to Malcom
        new MCMCampaignAsyncTasks.NotifyServer(activity.getApplicationContext()).execute(MCMCampaignDefines.ATTR_CLICK_HIT, campaign.getCampaignId());

    }

    private void crossSellingClick() {
        // Open campaign app in PlayStore
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + campaign.getPromotionIdentifier())));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + campaign.getPromotionIdentifier())));
        }
    }

    private void externalURLClick() {
        activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(campaign.getExternalPromotionURL())));
    }
}
