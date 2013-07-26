package com.malcom.library.android.module.campaign;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: PedroDuran
 * Date: 09/05/13
 * Time: 15:45
 * To change this template use File | Settings | File Templates.
 */
public class MCMCampaignsLogics {

    /**
     * Method that filter the promotion campaign items from arrayList
     *
     * @param campaignsArray the arrayList with all the MCMCampaignDTO retrieved from server
     * @return ArrayList<MCMCampaignDTO> with promotion campaign items
     */
    public static ArrayList<MCMCampaignDTO> getFilteredCampaigns(ArrayList<MCMCampaignDTO> campaignsArray, MCMCampaignDTO.CampaignType type) {

        Iterator iterator = campaignsArray.iterator();
        ArrayList<MCMCampaignDTO> resultArray = new ArrayList<MCMCampaignDTO>();

        while (iterator.hasNext()) {
            MCMCampaignDTO currentCampaign = (MCMCampaignDTO) iterator.next();
            if (currentCampaign.getType() == type) {
                resultArray.add(currentCampaign);
            }
        }

        return resultArray;
    }

    /**
     * Method that gets randomly weighted a campaign to serve.
     *
     * @return MCMCampaignDTO campaign selected.
     * @since 1.0.1
     */
    public static MCMCampaignDTO getCampaignPerWeight(ArrayList<MCMCampaignDTO> campaignsArray) {
        ArrayList<Integer> weightedArray = new ArrayList<Integer>();

        //generates the array to random weighted selection
        for (int i = 0; i < campaignsArray.size(); i++) {
            MCMCampaignDTO campaignModel = campaignsArray.get(i);
            //adds to the weighted array as ids as weight has
            for (int j = 0; j < campaignModel.getWeight(); j++) {
                weightedArray.add(i);
            }
        }

        //generates random number
        int selection = 0;
        if (weightedArray.size() > 1) {
            selection = new Random().nextInt(weightedArray.size() - 1);
        }

        //gets the random position and gets the id written on it. It will be one of the campaigns
        MCMCampaignDTO selectedCampaignModel = campaignsArray.get(weightedArray.get(selection));

        return selectedCampaignModel;

    }

    private static final String ATTR_TIMES_BEFORE_REMINDING = "TIMES_BEFORE_REMINDING";
    private static final String ATTR_DAYS_UNTIL_PROMT = "DAYS_UNTIL_PROMT";

    private static final String RATE_MY_APP_PREFERENCES = "RateMyAppPreferences";
    private static final String NOT_SHOW_AGAIN = "DontShowAgain";
    private static final String SESSIONS_SINCE_LAST_DIALOG = "SessionsSinceLastDialog";
    private static final String DAYS_SINCE_LAST_DIALOG = "DaysSinceLastDialog";

    public static boolean shouldShowDialog(Context context, MCMCampaignDTO campaignDTO) {
        //By default should show the dialog
        boolean shouldShowDialog = true;

        //Check if the campaign was stored on SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(RATE_MY_APP_PREFERENCES, 0);

        //If the campaign was already there, check the parameters
        //Otherwise should show the dialog
        if (preferences.getBoolean(campaignDTO.getCampaignId(),false)) {

            //Promotion limits
            int sessionLimit = Integer.parseInt(campaignDTO.getClientLimitFeature(ATTR_TIMES_BEFORE_REMINDING));
            int daysLimit = Integer.parseInt(campaignDTO.getClientLimitFeature(ATTR_DAYS_UNTIL_PROMT));

            //Check the client limits and "notshowagain"
            int sessionsSinceLastDialog = preferences.getInt(SESSIONS_SINCE_LAST_DIALOG,0);
            int daysSinceLastDialog = preferences.getInt(DAYS_SINCE_LAST_DIALOG,0);

            boolean notShowAgain = preferences.getBoolean(NOT_SHOW_AGAIN,false);
            boolean notShouldShowDialog = (sessionsSinceLastDialog < sessionLimit) || (daysSinceLastDialog < daysLimit);

            if (notShowAgain || notShouldShowDialog) {
                shouldShowDialog = false;
            }

        }

        return shouldShowDialog;
    }
}
