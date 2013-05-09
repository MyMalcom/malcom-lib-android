package com.malcom.library.android.module.campaign;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import com.malcom.library.android.MCMDefines;
import com.malcom.library.android.exceptions.ApplicationConfigurationNotFoundException;
import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.utils.HttpDateUtils;
import com.malcom.library.android.utils.MalcomHttpOperations;
import com.malcom.library.android.utils.ToolBox;
import com.malcom.library.android.utils.encoding.DigestUtils;
import com.malcom.library.android.utils.encoding.base64.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 09/05/13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
public class MCMCampaignAsyncTasks {

    /**
     * DownloadCampaignFile.
     * Async request campaigns data, and parse it to models.
     */
    protected static class DownloadCampaignFile extends AsyncTask<String, Void, ArrayList<MCMCampaignModel>> {

        private MCMCampaignModel.CampaignType campaignType;
        private MCMCampaignAdapter campaignAdapter;

        public DownloadCampaignFile(MCMCampaignModel.CampaignType campaignType, MCMCampaignAdapter campaignAdapter) {
            this.campaignType = campaignType;
            this.campaignAdapter = campaignAdapter;

        }

        protected void onPreExecute() {
        }

        protected ArrayList<MCMCampaignModel> doInBackground(String... valores) {

            ArrayList<MCMCampaignModel> campaignsArray = new ArrayList<MCMCampaignModel>();

            try {
                // Execute request to get JSON
                JSONObject objectJSON = getJSONfromURL(valores[0]);

                if (objectJSON != null) {

                    Log.d(MCMDefines.LOG_TAG, "Received Campaigns JSON: " + objectJSON);

                    // Parse JSON to obtain campaign data
                    JSONArray campaignArray = (JSONArray) objectJSON.get(MCMCampaignDefines.ATTR_CAMPAIGNS_ARRAY);

                    if (campaignArray != null && campaignArray.length() > 0) {

                        for (int i = 0; i < campaignArray.length(); i++) {
                            JSONObject campaignJSON = campaignArray.getJSONObject(i);
                            campaignsArray.add(new MCMCampaignModel(campaignJSON));
                        }

                    } else {
                        if (campaignAdapter.delegate != null)
                            campaignAdapter.delegate.campaignDidFinish();
                    }
                } else {
                    notifyDelegateFailed();
                }

            } catch (ApplicationConfigurationNotFoundException e) {
                e.printStackTrace();
                notifyDelegateFailed();
            } catch (JSONException e) {
                e.printStackTrace();
                notifyDelegateFailed();
            }

            return campaignsArray;
        }

        protected void onPostExecute(ArrayList<MCMCampaignModel> campaignsArray) {

            // After receiving campaign data, prepare banner
            if (campaignsArray.size() > 0) {
                if (campaignAdapter.getType() == MCMCampaignModel.CampaignType.IN_APP_CROSS_SELLING ||
                        campaignAdapter.getType() == MCMCampaignModel.CampaignType.IN_APP_PROMOTION) {

                    ArrayList<MCMCampaignModel> selectionCampaignsArray = new ArrayList<MCMCampaignModel>();
                    if (campaignAdapter.getType() == MCMCampaignModel.CampaignType.IN_APP_CROSS_SELLING) {
                        ArrayList<MCMCampaignModel> filteredArray = MCMCampaignsUtils.getFilteredCampaigns(campaignsArray, MCMCampaignModel.CampaignType.IN_APP_CROSS_SELLING);
                        MCMCampaignModel selectedCampaign = MCMCampaignsUtils.getCampaignPerWeight(filteredArray);
                        selectionCampaignsArray.add(selectedCampaign);
                    } else if (campaignAdapter.getType() == MCMCampaignModel.CampaignType.IN_APP_PROMOTION) {
                        selectionCampaignsArray = MCMCampaignsUtils.getFilteredCampaigns(campaignsArray, MCMCampaignModel.CampaignType.IN_APP_PROMOTION);
                    }
                    campaignAdapter.createBanner(selectionCampaignsArray);
                }
            }
        }

        private void notifyDelegateFailed() {
            if (campaignAdapter.delegate != null) {
                campaignAdapter.delegate.campaignDidFailed();
            }
        }

        /**
         * Sends a GET request, obtains a response, and converts it to a JSON object.
         *
         * @param url - the destination of request
         * @return - the response JSON object
         * @throws ApplicationConfigurationNotFoundException
         *
         */
        private JSONObject getJSONfromURL(String url) throws ApplicationConfigurationNotFoundException {

            String result = "";
            JSONObject jObject = null;
            try {
                // Send request to Malcom and log it
                Log.d(MCMDefines.LOG_TAG, ">>> getJSONfromURL: " + url);

                // TODO: Refactor this by extracting the headers build to a static helper class. This class should be used by all the methods connecting with Malcom Server

                String malcomDate = HttpDateUtils.formatDate(new Date());
                String verb = "GET";
                String contentType = "application/json";
                String md5 = ToolBox.md5_calculateMD5("");

                Map<String, String> headersData = new HashMap<String, String>();
                headersData.put("Content-Type", "application/json");
                headersData.put("content-md5", md5);
                headersData.put("x-mcm-date", malcomDate);

                String dataToSign = ToolBox.deliveries_getDataToSign(ToolBox.getCanonicalizedMalcomHeaders(headersData),
                        contentType, null, verb, campaignAdapter.getCampaignResource(), md5);
                String secretKey = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);
                String password = DigestUtils.calculateRFC2104HMAC(dataToSign, secretKey);

                // Complete headers with authorization
                headersData.put("Authorization", "basic " + new String(Base64.encode(campaignAdapter.getMalcomAppId() + ":" + password).getBytes()));

                // End of refactor


                result = ToolBox.net_httpclient_doAction(ToolBox.HTTP_METHOD.GET, url, "", headersData);
                Log.d(MCMCampaignDefines.LOG_TAG, "<<< getJSONfromURL result: " + result);

                // Try parse the string to a JSON object
                jObject = new JSONObject(result);

            } catch (ApplicationConfigurationNotFoundException e) {
                throw e;
            } catch (Exception e) {
                Log.e(MCMCampaignDefines.LOG_TAG, "<<< getJSONfromURL ERROR: " + e.toString() + " - " + e.getMessage());
            }

            return jObject;
        }
    }

    /**
     * DownloadCampaignImage.
     * Async request banner image.
     */
    protected static class DownloadCampaignImage extends AsyncTask<MCMCampaignModel, Void, Bitmap> {

        private MCMCampaignModel campaignModel;
        private MCMCampaignAdapter campaignAdapter;

        public DownloadCampaignImage(MCMCampaignAdapter campaignAdapter) {
            this.campaignAdapter = campaignAdapter;

        }

        protected Bitmap doInBackground(MCMCampaignModel... campaignModels) {
            campaignModel = campaignModels[0];
            Log.d(MCMCampaignDefines.LOG_TAG, "Downloading CampaignImage for: " + campaignModel.getName());
            Bitmap bitmap = null;

            try {
                URL imageUrl = new URL(campaignModel.getMediaFeature().getMedia());
                InputStream imageImputStream = (InputStream) imageUrl.getContent();
                bitmap = BitmapFactory.decodeStream(imageImputStream);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap bitmap) {

            // After downloading image, show the banner
            campaignAdapter.setImageBanner(bitmap, campaignModel);

            if (campaignAdapter.delegate != null) {
                campaignAdapter.delegate.campaignDidLoad();
            }
        }
    }


    /**
     * SendHitClick
     * Async request to notify impressions and clicks to Malcom.
     * Needs the event type (CLICK or IMPRESSION) in first param.
     */
    public static class SendHitClick extends AsyncTask<String, Float, Integer> {

        private Bitmap bitmap;
        private Context context;

        public SendHitClick(Context context) {
            this.context = context;
        }

        protected Integer doInBackground(String... valores) {

            String url;
            try {

                // Get the connection params
                String malcomBaseUrl = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL);
                String appId = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID);
                String encodedMalcomAppId = URLEncoder.encode(appId, "UTF-8");
                String devideId = URLEncoder.encode(ToolBox.device_getId(context), "UTF-8");
                String secretKey = MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);

                // Get the service url
                String campaignHitService = MCMCampaignDefines.CAMPAIGN_HIT_URL
                        .replace(MCMCampaignDefines.HIT_TYPE_TAG, valores[0])  // CLICK or IMPRESSION
                        .replace(MCMCampaignDefines.CAMPAIGN_ID_TAG, valores[1])
                        .replace(MCMCampaignDefines.APP_ID_TAG, encodedMalcomAppId)
                        .replace(MCMCampaignDefines.UDID_TAG, devideId);

                url = malcomBaseUrl + campaignHitService;

                // Send hit to Malcom
                MalcomHttpOperations.sendRequestToMalcom(url, campaignHitService, "", appId, secretKey, ToolBox.HTTP_METHOD.GET);


            } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            ;

            return 0;
        }

        protected void onPostExecute(Integer bytes) {
        }
    }
}
