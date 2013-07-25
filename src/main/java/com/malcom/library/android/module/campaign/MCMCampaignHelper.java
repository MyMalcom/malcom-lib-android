package com.malcom.library.android.module.campaign;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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

        String title = "RateMyApp";
        String message = "If you enjoy using this app, please take a moment to rate it. Thanks for your support!";
        String rateButtonText = "Rate!";
        String remindMeLaterButtonText = "Remind me later";
        String disableButtonText = "No, thanks";

        //TODO: Pedro: Probar con el context para mostrar el alert
        final Dialog dialog = new Dialog(activity);
        dialog.setTitle(title);

        LinearLayout ll = new LinearLayout(activity);
        ll.setOrientation(LinearLayout.VERTICAL);

        TextView tv = new TextView(activity);
        tv.setText(message);
        tv.setWidth(240);
        tv.setPadding(4, 0, 4, 10);
        ll.addView(tv);

        Button b1 = new Button(activity);
        b1.setText(rateButtonText);
        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                delegate.dialogRatePressed(campaignDTO);
                dialog.dismiss();
            }
        });
        ll.addView(b1);

        Button b2 = new Button(activity);
        b2.setText(remindMeLaterButtonText);
        b2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                delegate.dialogRatePressed(campaignDTO);
                dialog.dismiss();
            }
        });
        ll.addView(b2);

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
