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

    protected static final String CAMPAIGN_URL = "v2/campaigns/application/"+APP_ID_TAG+"/udid/"+UDID_TAG;
//    protected static final String CAMPAIGN_URL = "http://malcom-api-dev.elasticbeanstalk.com/v2/campaigns/application/"+CAMPAIGN_ID_TAG+"/udid/"+UDID_TAG;
    protected static final String CAMPAIGN_HIT_URL = "v2/campaigns/" + CAMPAIGN_ID_TAG + "/hit/" + HIT_TYPE_TAG + "/application/" + APP_ID_TAG + "/udid/" + UDID_TAG;

    //Dev
    //protected static final String CAMPAIGN_URL = "http://malcom-api-dev.elasticbeanstalk.com/v2/campaigns/application/3eb5fdfd-8045-4111-8889-932877366285/udid/1";
    //protected static final String CAMPAIGN_URL = "https://dl.dropboxusercontent.com/u/23103432/campaignsV2.json";

    protected static final String LOG_TAG = "MCMCampaign";

    // Service tags
    protected static final String ATTR_CAMPAIGNS_ARRAY = "campaigns";
    public static final String ATTR_IMPRESSION_HIT = "IMPRESSION";
    public static final String ATTR_CLICK_HIT = "CLICK";
    public static final String ATTR_RATE_HIT = "RATE";
    public static final String ATTR_REMIND_HIT = "REMIND_LATER";
    public static final String ATTR_NEVER_HIT = "NEVER_RATE";

    // Banner Id's
    protected static final String RES_ID_LAYOUT = "campaign_banner_layout";

    // Banner Configuration
    public static int DEFAULT_CAMPAIGN_DURATION = 15;
    protected static int BANNER_SIZE_HEIGHT = 60;
    protected static int MIDDLE_MARGIN = 50;
    protected static int CLOSE_BUTTON_SIZE = 40;
    protected static int BACKGROUND_ALPHA = 140; // 0-255

    // RateMyAPP Dialog Configuration
    protected static final int DIALOG_WIDTH = 240;
    protected static final int TEXT_VIEW_MARGIN_LEFT = 10;
    protected static final int TEXT_VIEW_MARGIN_TOP = 0;
    protected static final int TEXT_VIEW_MARGIN_RIGHT = 10;
    protected static final int TEXT_VIEW_MARGIN_BOTTOM = 10;

    // RateMyAPP Dialog Default messages
    protected static final String TITLE_DEFAULT = "Rate My App";
    protected static final String MESSAGE_DEFAULT = "If you enjoy using this app, please take a moment to rate it. Thanks for your support!";
    protected static final String RATE_BUTTON_DEFAULT = "Rate!";
    protected static final String REMIND_BUTTON_DEFAULT = "Remind me later";
    protected static final String DISABLE_BUTTON_DEFAULT = "No, thanks";

    // RateMyAPP Dialog Resources id
    protected static final String RATE_TITLE_ID = "malcom_rate_title";
    protected static final String RATE_MESSAGE_ID = "malcom_rate_message";
    protected static final String RATE_BUTTON_ID = "malcom_rate_button";
    protected static final String RATE_REMIND_ID = "malcom_remind_button";
    protected static final String RATE_DISABLE_ID = "malcom_disable_button";
}
