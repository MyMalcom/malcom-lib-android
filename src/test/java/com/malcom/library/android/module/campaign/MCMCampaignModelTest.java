package com.malcom.library.android.module.campaign;

import com.malcom.library.android.module.core.MCMCoreAdapter;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

/**
 * Test class for MCMCampaignDTO.
 */
class MCMCampaignModelTest {// extends TestCase {

    private String campaignsURL = "http://api.mymalcom.com/v2/campaigns/application/cef7c5ed-7bda-462a-a554-e8c32c607b27/udid/xvQ995mMZDd8cvRFNnTrpA%3D%3D";

    @Test
    public void testCampaignModelHydrate() {

        String responseJSON = "{\"campaigns\":[{\"createdOn\":1370445584000,\"id\":33,\"enabled\":true,\"customParamsFeature\":{\"properties\":{\"codigo\":\"12345\",\"provincia\":\"28\"}},\"descriptionFeature\":{\"promotionDescription\":\"Prueba de promoción\"},\"promotionFeature\":{\"promotionType\":\"APPLICATION\",\"promotionIdentifier\":\"296739784\"},\"start\":1370390400000,\"name\":\"Prueba de promoción\",\"state\":\"ACTIVE\",\"type\":\"IN_APP_PROMOTION\",\"mediaFeature\":{\"position\":\"TOP\",\"media\":\"https:\\/\\/s3.amazonaws.com\\/assets.tebas.mymalcom.com\\/inappcampaign\\/f504632e-bce5-4fab-9e05-daf0a95417a5\\/image\"},\"end\":1375228800000,\"serverOrderFeature\":{\"weight\":5}}]}";

        try {
            JSONObject objectJSON = new JSONObject(responseJSON);
            JSONArray campaignArray = (JSONArray) objectJSON.get(MCMCampaignDefines.ATTR_CAMPAIGNS_ARRAY);

            MCMCampaignDTO campaignDTO = new MCMCampaignDTO(campaignArray.getJSONObject(0));

            Assert.assertEquals(campaignDTO.getCampaignId(), "33");
            Assert.assertEquals(campaignDTO.getName(), "Prueba de promoción");
            Assert.assertEquals(campaignDTO.getCampaignDescription(), "Prueba de promoción");
            Assert.assertEquals(campaignDTO.getStart(), "1370390400000");
            Assert.assertEquals(campaignDTO.getEnd(), "1375228800000");
            Assert.assertEquals(campaignDTO.getCreatedOn(), "1370445584000");
            Assert.assertEquals(campaignDTO.getCampaignDescription(), "Prueba de promoción");
            Assert.assertEquals(campaignDTO.getType(), MCMCampaignDTO.CampaignType.IN_APP_PROMOTION);
            Assert.assertEquals(campaignDTO.getMedia(), "1375228800000");
            Assert.assertEquals(campaignDTO.getCampaignPosition(), MCMCampaignDTO.CampaignPosition.TOP);
            Assert.assertEquals(campaignDTO.getPromotionType(), "APPLICATION");
            Assert.assertEquals(campaignDTO.getPromotionIdentifier(), "296739784");
//            Assert.assertEquals(campaignDTO.getClientLimitType(), "1370445584000");
            Assert.assertEquals(campaignDTO.getLimitValue(), "1370390400000");
            Assert.assertEquals(campaignDTO.getWeight(), "5");
            Assert.assertEquals(campaignDTO.getCustomParam("provincia"), "28");
            Assert.assertEquals(campaignDTO.getCustomParam("codigo"), "12345");

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}