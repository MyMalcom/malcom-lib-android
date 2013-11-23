#Campaigns

Malcom campaigns let you display different kinds of messages and banners in your app and measure the response from your users.

##Banners

Some campaigns display a banner in your app. You can let Malcom draw the banner or you can draw it yourself. If you let Malcom draw it, you need to add the following layout inside the layout of the activity where you want to display the banner.

```xml
<RelativeLayout
    android:id="@+id/campaign_banner_layout"
    android:layout_width="match_parent"
    android:layout_height="300dp">
</RelativeLayout>
```

##Promotion campaigns

Promotion campaigns are designed to display a custom promotion banner inside your app. They let you measure **impressions** (how many times it is displayed) and **clicks** (how many times users have clicked on it).

Call this method to add a promotion campaign to your activity:

```java
MalcomLib.addCampaignPromotion(this);
```

That method will look for a promotion campaign and display a banner for it (in the layout for the banner, the `campaign_banner_layout` we explained before).

There is another version of that method that lets you control more parameters:

```java
MalcomLib.addCampaignPromotion(this, 20, placeholderImageId, delegate);
```

The second parameter are the seconds that the campaign banner will stay on screen (use 0 if you want it to stay forever). The third parameter (optional) is the placeholder image that will be displayed while the remote banner image is being downloaded. The last parameter (optional) is the `MCMCampaignNotifiedDelegate` that will be notified with the campaign actions.

In case you want to handle campaigns other way, you can request the campaigns and display them the way you want. In that case you don't need to include the `campaign_banner_layout` in your activity. In order to do that, call the following method and perform the desired actions in the receiver:

```java
MalcomLib.requestCampaignsPromotion(activity, receiver)
```

##Cross-selling campaigns

Cross selling campaigns are similar to promotion campaigns. The difference is that the banner is meant to promote another app. Besides **impressions** and **clicks** it also lets you meassure **downloads** (how many users have downloaded the promoted app).

To add a cross-selling campaign call this method from your activity:

```java
MalcomLib.addCampaignCrossSelling(this);
```

That method will look for a cross selling campaign and display a banner for it (in the the `campaign_banner_layout`). Like with promotion campaigns, there's a version of that method with more paremeters (the behaviour is analogous):

```java   
MalcomLib.addCampaignCrossSelling(this, 20, placeholderImageId, delegate);
```

You can also call the following method in case you want to handle campaigns other way:

```java
MalcomLib.requestCampaignsCrossSelling(activity, receiver)
```

##Rate my app campaign

The rate my app campaign is different from the others. It displays a dialog that asks the user to rate the app. The user can decide to **rate it now**, **later** or **never**. You can measure how many times users have chosen each option.

To display the rate my app dialog, call this method from your activity:

```java
MalcomLib.addCampaignRateMyApp(this, delegate);
```

The second parameter (optional) is the `MCMCampaignNotifiedDelegate` that will be notified with the campaign actions.

In order to customize the dialog messages, add these entries to your `strings.xml`:

```xml
<string name="malcom_rate_title">Rate this app</string>
<string name="malcom_rate_message">Please support us and rate our app!</string>
<string name="malcom_rate_button">OK</string>
<string name="malcom_remind_button">Maybe later</string>
<string name="malcom_disable_button">Never</string>
```

From Malcom web you can configure other settings like the number of sessions/days before displaying the rate-my-app dialog again.
