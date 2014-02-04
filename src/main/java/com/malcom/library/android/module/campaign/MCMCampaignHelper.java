package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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

    protected interface RateMyAppDialogDelegate {

        public void dialogRatePressed(MCMCampaignDTO campaignDTO);
        public void dialogDisablePressed(MCMCampaignDTO campaignDTO);
        public void dialogRemindMeLaterPressed(MCMCampaignDTO campaignDTO);
    }

    /**
     * Mtehod that creates an alert for rate my app campaign
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

        Context ctx = activity.getApplicationContext();

        final Dialog dialog = new Dialog(activity);
        dialog.setTitle(title);

        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.VERTICAL);

        //Message TextView
        TextView tv = new TextView(activity);
        tv.setText(message);
        tv.setTextColor(Color.GRAY);
        tv.setWidth(MCMUtils.getDPI(ctx,MCMCampaignDefines.DIALOG_WIDTH));
        tv.setPadding(MCMUtils.getDPI(ctx,MCMCampaignDefines.TEXT_VIEW_MARGIN_LEFT),
                MCMUtils.getDPI(ctx,MCMCampaignDefines.TEXT_VIEW_MARGIN_TOP),
                MCMUtils.getDPI(ctx,MCMCampaignDefines.TEXT_VIEW_MARGIN_RIGHT),
                MCMUtils.getDPI(ctx,MCMCampaignDefines.TEXT_VIEW_MARGIN_BOTTOM));
        ll.addView(tv);

        //Rate Button
        Button b1 = new Button(activity);
        b1.setText(rateButtonText);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                delegate.dialogRatePressed(campaignDTO);
                dialog.dismiss();
            }
        });
        ll.addView(b1);

        //Remind Button
        Button b2 = new Button(activity);
        b2.setText(remindMeLaterButtonText);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                delegate.dialogRemindMeLaterPressed(campaignDTO);
                dialog.dismiss();
            }
        });
        ll.addView(b2);

        //Disable Button
        Button b3 = new Button(activity);
        b3.setText(disableButtonText);
        b3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                delegate.dialogDisablePressed(campaignDTO);
                dialog.dismiss();
            }
        });
        ll.addView(b3);

        dialog.setContentView(ll);
        dialog.show();
    }

}
