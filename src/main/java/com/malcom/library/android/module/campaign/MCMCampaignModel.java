package com.malcom.library.android.module.campaign;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * MCMCampaignModel.
 * Class to store information about a campaign.
 *
 */
public class MCMCampaignModel {
    public static enum CampaignType { IN_APP_PROMOTION, IN_APP_RATE_MY_APP, IN_APP_CROSS_SELLING };
    public static enum CampaignPosition { BOTTOM, TOP, FULL_SCREEN, MIDDLE_PORTRAIT, MIDDLE_LANDSCAPE };

    //JSON tags
    private static final String ATTR_CAMPAIGN_ID = "id";
    private static final String ATTR_NAME = "name";
    private static final String ATTR_START = "start";
    private static final String ATTR_END = "end";
    private static final String ATTR_CREATED_ON = "createdOn";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_MEDIA_FEATURE = "mediaFeature";
    private static final String ATTR_PROMOTION_FEATURE = "promotionFeature";
    private static final String ATTR_CLIENT_LIMIT_FEATURE = "clientLimitFeature";
    private static final String ATTR_WEIGHT_CAMPAIGN = "weight";

    //Attributes
    private String campaignId;
    private String name;
    private String start;
    private String end;
    private String createdOn;
    private CampaignType type;
    private MCMCampaignMediaFeatureModel mediaFeature;
    private MCMCampaignPromotionFeatureModel promotionFeature;
    private MCMCampaignClientLimitFeatureModel clientLimitFeature;
    private int weight;
	
	public MCMCampaignModel(JSONObject json) {

        String typeJSON = null;
		
		try {
            campaignId = (String) json.get(ATTR_CAMPAIGN_ID).toString();
            name = (String) json.get(ATTR_NAME);
			start = (String) json.getString(ATTR_START);
			end = (String) json.getString(ATTR_END);
            createdOn = (String) json.getString(ATTR_CREATED_ON);
            typeJSON = (String) json.getString(ATTR_TYPE);
            if (json.has(ATTR_MEDIA_FEATURE))
                mediaFeature = new MCMCampaignMediaFeatureModel(json.getJSONObject(ATTR_MEDIA_FEATURE));
            if (json.has(ATTR_PROMOTION_FEATURE))
                promotionFeature = new MCMCampaignPromotionFeatureModel(json.getJSONObject(ATTR_PROMOTION_FEATURE));
            if (json.has(ATTR_CLIENT_LIMIT_FEATURE))
                clientLimitFeature = new MCMCampaignClientLimitFeatureModel(json.getJSONObject(ATTR_CLIENT_LIMIT_FEATURE));
			weight = json.has(ATTR_WEIGHT_CAMPAIGN)?json.getInt(ATTR_WEIGHT_CAMPAIGN):1;
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (typeJSON != null) {
			
			if (typeJSON.equalsIgnoreCase(CampaignType.IN_APP_PROMOTION.name())) {
				type = CampaignType.IN_APP_PROMOTION;
			} 
			else if (typeJSON.equalsIgnoreCase(CampaignType.IN_APP_RATE_MY_APP.name())) {
                type = CampaignType.IN_APP_RATE_MY_APP;
            }
            else if (typeJSON.equalsIgnoreCase(CampaignType.IN_APP_CROSS_SELLING.name())) {
                type = CampaignType.IN_APP_CROSS_SELLING;
            }
			else {
                type = CampaignType.IN_APP_PROMOTION;
			}
		}
	}

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public CampaignType getType() {
        return type;
    }

    public void setType(CampaignType type) {
        this.type = type;
    }

    public MCMCampaignMediaFeatureModel getMediaFeature() {
        return mediaFeature;
    }

    public void setMediaFeature(MCMCampaignMediaFeatureModel mediaFeature) {
        this.mediaFeature = mediaFeature;
    }

    public MCMCampaignPromotionFeatureModel getPromotionFeature() {
        return promotionFeature;
    }

    public void setPromotionFeature(MCMCampaignPromotionFeatureModel promotionFeature) {
        this.promotionFeature = promotionFeature;
    }

    public MCMCampaignClientLimitFeatureModel getClientLimitFeature() {
        return clientLimitFeature;
    }

    public void setClientLimitFeature(MCMCampaignClientLimitFeatureModel clientLimitFeature) {
        this.clientLimitFeature = clientLimitFeature;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    /**
     * MCMCampaignMediaFeatureModel
     * Class to store information about a campaign's media feature.
     */
    public class MCMCampaignMediaFeatureModel{

        //JSON tags
        private static final String ATTR_MEDIA = "media";
        private static final String ATTR_CAMPAIGN_POSITION = "position";

        //Attributes
        private String media;
        private CampaignPosition campaignPosition;

        public MCMCampaignMediaFeatureModel(JSONObject json){

            String position = null;
            try {
                if (json.has(ATTR_MEDIA))
                    media = (String) json.get(ATTR_MEDIA);
                if (json.has(ATTR_CAMPAIGN_POSITION))
                    position = (String) json.getString(ATTR_CAMPAIGN_POSITION);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (position != null) {

                if (position.equals(CampaignPosition.BOTTOM.name())) {
                    campaignPosition = CampaignPosition.BOTTOM;
                }
                else if (position.equals(CampaignPosition.TOP.name())) {
                    campaignPosition = CampaignPosition.TOP;
                }
                else if (position.equals(CampaignPosition.FULL_SCREEN.name())) {
                    campaignPosition = CampaignPosition.FULL_SCREEN;
                }
                else if (position.equals(CampaignPosition.MIDDLE_PORTRAIT.name())) {
                    campaignPosition = CampaignPosition.MIDDLE_PORTRAIT;
                }
                else if (position.equals(CampaignPosition.MIDDLE_LANDSCAPE.name())) {
                    campaignPosition = CampaignPosition.MIDDLE_LANDSCAPE;
                }
                else {
                    //if there is no position, set Bottom by default
                    campaignPosition = CampaignPosition.BOTTOM;
                }
            } else {
                //if there is no position, set Bottom by default
                campaignPosition = CampaignPosition.BOTTOM;
            }
        }

        public String getMedia() {
            return media;
        }

        public void setMedia(String media) {
            this.media = media;
        }

        public CampaignPosition getCampaignPosition() {
            return campaignPosition;
        }

        public void setCampaignPosition(CampaignPosition campaignPosition) {
            this.campaignPosition = campaignPosition;
        }
    }

    /**
     * MCMCampaignPromotionFeatureModel
     * Class to store information about a campaign's promotion feature.
     */
    public class MCMCampaignPromotionFeatureModel{

        //JSON tags
        private static final String ATTR_PROMOTION_TYPE = "promotionType";
        private static final String ATTR_PROMOTION_IDENTIFIER = "promotionIdentifier";

        //Attributes
        private String promotionType;
        private String promotionIdentifier;

        public MCMCampaignPromotionFeatureModel(JSONObject json){
            try {
                promotionType = (String) json.get(ATTR_PROMOTION_TYPE);
                promotionIdentifier = (String) json.getString(ATTR_PROMOTION_IDENTIFIER);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getPromotionType() {
            return promotionType;
        }

        public void setPromotionType(String promotionType) {
            this.promotionType = promotionType;
        }

        public String getPromotionIdentifier() {
            return promotionIdentifier;
        }

        public void setPromotionIdentifier(String promotionIdentifier) {
            this.promotionIdentifier = promotionIdentifier;
        }
    }

    /**
     * MCMCampaignClientLimitFeatureModel
     * Class to store information about a campaign's client limit feature.
     */
    public class MCMCampaignClientLimitFeatureModel{

        //JSON tags
        private static final String ATTR_CLIENT_LIMIT_TYPE = "clientLimitType";
        private static final String ATTR_LIMIT_VALUE = "limitValue";

        //Attributes
        private String clientLimitType;
        private String limitValue;

        public MCMCampaignClientLimitFeatureModel(JSONObject json){
            try {
                clientLimitType = (String) json.get(ATTR_CLIENT_LIMIT_TYPE);
                limitValue = (String) json.getString(ATTR_LIMIT_VALUE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public String getClientLimitType() {
            return clientLimitType;
        }

        public void setClientLimitType(String clientLimitType) {
            this.clientLimitType = clientLimitType;
        }

        public String getLimitValue() {
            return limitValue;
        }

        public void setLimitValue(String limitValue) {
            this.limitValue = limitValue;
        }
    }
}
