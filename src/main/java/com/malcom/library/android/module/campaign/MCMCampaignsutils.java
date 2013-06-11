package com.malcom.library.android.module.campaign;

import android.util.Log;
import com.malcom.library.android.MCMDefines;

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
public class MCMCampaignsUtils {

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

        //Log.d(MCMDefines.LOG_TAG, "campaignObjectsArray : " + campaignsArray.size());
        //generates the array to random weighted selection
        for (int i = 0; i < campaignsArray.size(); i++) {
            MCMCampaignDTO campaignModel = campaignsArray.get(i);
            //Log.d(MCMDefines.LOG_TAG, "campaignModel.weight : " + campaignModel.getWeight());
            //adds to the weighted array as ids as weight has
            for (int j = 0; j < campaignModel.getWeight(); j++) {
                weightedArray.add(i);
            }
        }

        //generates random number
        //Log.d(MCMDefines.LOG_TAG, "Searching number : " + weightedArray.size());
        int selection = 0;
        if (weightedArray.size() > 1) {
            selection = new Random().nextInt(weightedArray.size() - 1);
        }

        //gets the random position and gets the id written on it. It will be one of the campaigns
        MCMCampaignDTO selectedCampaignModel = campaignsArray.get(weightedArray.get(selection));

        return selectedCampaignModel;

    }
}
