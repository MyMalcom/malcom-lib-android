
package com.malcom.library.android.module.campaign;

public interface MCMCampaignNotifiedDelegate {

    /**
     * Notified delegate to indicate actions performed by the campaign module of Malcom.
     */
    public void campaignDidFinish();

    public void campaignDidLoad();

    public void campaignDidFail(String errorMessage);

    public void campaignPressed(MCMCampaignDTO campaign);

}
