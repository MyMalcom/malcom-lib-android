package com.malcom.library.android.module.campaign;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 09/05/13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
public class MCMCampaignDefines {

    // Request tags
    protected static final String APP_ID_TAG = "%@AppId";
    protected static final String UDID_TAG = "%@Udid";
    protected static final String CAMPAIGN_ID_TAG = "%@CampaignId";
    protected static final String HIT_TYPE_TAG = "%@HitType";

//    private static final String CAMPAIGN_URL = "v2/campaigns/application/"+CAMPAIGN_ID_TAG+"/udid/"+UDID_TAG;
//    private static final String CAMPAIGN_URL = "http://malcom-api-dev.elasticbeanstalk.com/v2/campaigns/application/"+CAMPAIGN_ID_TAG+"/udid/"+UDID_TAG;
    protected static final String CAMPAIGN_HIT_URL = "v1/campaigns/"+CAMPAIGN_ID_TAG+"/hit/"+HIT_TYPE_TAG+"/"+APP_ID_TAG+"/udid/"+UDID_TAG;

    //Dev
    //private static final String CAMPAIGN_URL = "http://malcom-api-dev.elasticbeanstalk.com/v2/campaigns/application/3eb5fdfd-8045-4111-8889-932877366285/udid/1";
    protected static final String CAMPAIGN_URL = "https://dl.dropboxusercontent.com/u/23103432/campaignsV2.json";

    protected static final String LOG_TAG = "MCMCampaign";

    // Service tags
    protected static final String ATTR_CAMPAIGNS_ARRAY = "campaigns";
    protected static final String ATTR_IMPRESSION_HIT = "IMPRESSION";
    protected static final String ATTR_CLICK_HIT = "CLICK";

    // Banner Id's
    protected static final String RES_ID_LAYOUT = "campaign_banner_layout";
    protected static final String RES_ID_IMAGE = "image_campaign_view";

    // Banner Configuration
    public static int DEFAULT_CAMPAIGN_DURATION = 15;
    protected static int SIZE_BANNER = 60;
    protected static int MIDDLE_MARGIN = 30;
    protected static int BACKGROUND_ALPHA = 180; // 0-255
}
