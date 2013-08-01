package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malcom.library.android.utils.MCMUtils;

/**
 * Created by PedroDuran on 25/07/13.
 */
public class MCMCampaignHelper {

    public interface RateMyAppDialogDelegate {

        public void dialogRatePressed(MCMCampaignDTO campaignDTO);
        public void dialogDisablePressed(MCMCampaignDTO campaignDTO);
        public void dialogRemindMeLaterPressed(MCMCampaignDTO campaignDTO);
    }

    /**
     * Method that creates an alert for rate my app campaign
     * @param activity
     * @param campaignDTO
     * @param delegate
     */
    protected static void showRateMyAppDialog(Activity activity, final MCMCampaignDTO campaignDTO, final RateMyAppDialogDelegate delegate) {

        //Default strings
        String title = MCMCampaignDefines.TITLE_DEFAULT;
        String message = MCMCampaignDefines.MESSAGE_DEFAULT;
        String rateButtonText = MCMCampaignDefines.RATE_BUTTON_DEFAULT;
        String remindMeLaterButtonText = MCMCampaignDefines.REMIND_BUTTON_DEFAULT;
        String disableButtonText = MCMCampaignDefines.DISABLE_BUTTON_DEFAULT;

        int idTitle = activity.getResources().getIdentifier(MCMCampaignDefines.RATE_TITLE_ID, "string", activity.getPackageName());
        int idMessage = activity.getResources().getIdentifier(MCMCampaignDefines.RATE_MESSAGE_ID, "string", activity.getPackageName());
        int idRate = activity.getResources().getIdentifier(MCMCampaignDefines.RATE_BUTTON_ID, "string", activity.getPackageName());
        int idRemind = activity.getResources().getIdentifier(MCMCampaignDefines.RATE_REMIND_ID, "string", activity.getPackageName());
        int idDisable = activity.getResources().getIdentifier(MCMCampaignDefines.RATE_DISABLE_ID, "string", activity.getPackageName());

        //Get the localized strings if exists
        if (idTitle != 0) {
            title = activity.getString(idTitle);
        }
        if (idMessage != 0) {
            message = activity.getString(idMessage);
        }
        if (idRate != 0) {
            rateButtonText = activity.getString(idRate);
        }
        if (idRemind != 0) {
            remindMeLaterButtonText = activity.getString(idRemind);
        }
        if (idDisable != 0) {
            disableButtonText = activity.getString(idDisable);
        }

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);

        //Configure alert dialog
        alertDialogBuilder
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(rateButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        delegate.dialogRatePressed(campaignDTO);
                        dialog.dismiss();
                    }
                })
                .setNeutralButton(remindMeLaterButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        delegate.dialogRemindMeLaterPressed(campaignDTO);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton(disableButtonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        delegate.dialogDisablePressed(campaignDTO);
                        dialog.dismiss();
                    }
                });



        //Create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        //Show it
        alertDialog.show();


    }

}
