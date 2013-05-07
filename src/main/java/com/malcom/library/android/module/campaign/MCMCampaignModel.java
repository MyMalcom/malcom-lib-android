package com.malcom.library.android.module.campaign;

import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * MCMCampaignModel.
 * Class to store information about a campaign.
 * @author Pepe
 *
 */
public class MCMCampaignModel {

	public static enum BannerPosition { BOTTOM, TOP, FULL_SCREEN, MIDDLE_PORTRAIT, MIDDLE_LANDSCAPE };
	
	private static String ATTR_URL_IMAGE = "media";
	private static String ATTR_ID_APP = "link";
	private static String ATTR_POSITION = "position";
	private static String ATTR_ID_CAMPAIGN = "id";
	private static String ATTR_WEIGHT_CAMPAIGN = "weight";
	private String urlImage;
	private String idApp;
	private String position;
	private String idCampaign;
	private int weight;
	private BannerPosition bannerPosition;
	
	public MCMCampaignModel initWithJSON(JSONObject json) {
		
		try {
			urlImage = (String) json.get(ATTR_URL_IMAGE);
			idApp = (String) json.get(ATTR_ID_APP);
			position = (String) json.getString(ATTR_POSITION);
			idCampaign = (String) json.getString(ATTR_ID_CAMPAIGN);
			weight = json.has(ATTR_WEIGHT_CAMPAIGN)?json.getInt(ATTR_WEIGHT_CAMPAIGN):1;
		} 
		catch (JSONException e) {
			e.printStackTrace();
		}
		
		if (position != null) {
			
			if (position.equals(BannerPosition.BOTTOM.name())) {
				setBannerPosition(BannerPosition.BOTTOM);
			} 
			else if (position.equals(BannerPosition.TOP.name())) {
				setBannerPosition(BannerPosition.TOP);
			}
			else if (position.equals(BannerPosition.FULL_SCREEN.name())) {
				setBannerPosition(BannerPosition.FULL_SCREEN);
			}
			else if (position.equals(BannerPosition.MIDDLE_PORTRAIT.name())) {
				setBannerPosition(BannerPosition.MIDDLE_PORTRAIT);
			}
			else if (position.equals(BannerPosition.MIDDLE_LANDSCAPE.name())) {
				setBannerPosition(BannerPosition.MIDDLE_LANDSCAPE);
			}
			else {
				setBannerPosition(BannerPosition.BOTTOM);
			}
		}
		
		return this;
	}
	
	public String getUrlImage() {
		return urlImage;
	}
	public void setUrlImage(String urlImage) {
		this.urlImage = urlImage;
	}
	public String getIdApp() {
		return idApp;
	}
	public void setIdApp(String idApp) {
		this.idApp = idApp;
	}
	/**
	 * @return the weight
	 */
	public int getWeight() {
		return weight;
	}
	/**
	 * @param weight the weight to set
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	public String getIdCampaign() {
		return idCampaign;
	}
	public void setIdCampaign(String idCampaign) {
		this.idCampaign = idCampaign;
	}

	public BannerPosition getBannerPosition() {
		return bannerPosition;
	}

	public void setBannerPosition(BannerPosition bannerPosition) {
		this.bannerPosition = bannerPosition;
	}
}
