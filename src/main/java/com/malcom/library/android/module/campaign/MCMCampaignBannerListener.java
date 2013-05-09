package com.malcom.library.android.module.campaign;

import android.app.Activity;
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
    private MCMCampaignModel campaign;
    MCMCampaignNotifiedDelegate delegate;

    public MCMCampaignBannerListener(Activity activity, MCMCampaignModel campaignModel, MCMCampaignNotifiedDelegate delegate) {
        this.activity = activity;
        this.campaign = campaignModel;
        this.delegate = delegate;
    }

    public void onClick(View view) {

        // Notify delegate the click
        delegate.campaignPressed(campaign.getPromotionFeature().getPromotionIdentifier());

        if (campaign.getType() == MCMCampaignModel.CampaignType.IN_APP_CROSS_SELLING) {
            crossSellingClick();
        }
    }

    private void crossSellingClick() {

        // Send Click Hit event to Malcom
        new MCMCampaignAsyncTasks.SendHitClick(activity.getApplicationContext()).execute(MCMCampaignDefines.ATTR_CLICK_HIT, campaign.getCampaignId());

        // Open campaign app in PlayStore
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + campaign.getPromotionFeature().getPromotionIdentifier())));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + campaign.getPromotionFeature().getPromotionIdentifier())));
        }
    }
}
