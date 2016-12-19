package letshangllc.quoteoftheday;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdSize;


/**
 * Created by cvburnha on 10/26/2015.
 */
public class AdsHelper implements AdListener{
    String amazon_id;
    String admob_id;

    Activity activity;
    View view;
    private boolean amazonAdEnabled;

    Handler handler = new Handler();

    private ViewGroup adViewContainer;
    private com.amazon.device.ads.AdLayout amazonAdView;
    private com.google.android.gms.ads.AdView admobAdView;

    public AdsHelper(View view, String admob_id, Activity activity){
        this.admob_id = admob_id;
        this.amazon_id = activity.getResources().getString(R.string.amazon_ad_id);
        this.activity = activity;
        this.view =view;
    }

    public void runAds(){
        this.setUpAds();
        handler.post(runnable);
    }
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            refreshAd(); // display the data
            handler.postDelayed(this, activity.getResources().getInteger(R.integer.ad_refresh_rate));
        }
    };

    private void setUpAds(){
        AdRegistration.setAppKey(amazon_id);
        amazonAdView = new com.amazon.device.ads.AdLayout(activity, AdSize.SIZE_320x50);
        amazonAdView.setListener(this);
        //AdRegistration.enableTesting(true);
        admobAdView = new com.google.android.gms.ads.AdView(activity);
        admobAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
        admobAdView.setAdUnitId(admob_id);

        // Initialize view container
        adViewContainer = (ViewGroup) view.findViewById(R.id.ad_layout);
        amazonAdEnabled = true;
        adViewContainer.addView(amazonAdView);

        amazonAdView.loadAd(new com.amazon.device.ads.AdTargetingOptions());
    }


    public void refreshAd()
    {
        amazonAdView.loadAd(new com.amazon.device.ads.AdTargetingOptions());
    }

    @Override
    public void onAdLoaded(Ad ad, AdProperties adProperties) {
        if (!amazonAdEnabled)
        {
            amazonAdEnabled = true;
            adViewContainer.removeView(admobAdView);
            adViewContainer.addView(amazonAdView);
        }
    }

    @Override
    public void onAdFailedToLoad(Ad ad, AdError adError) {
        // Call AdMob SDK for backfill
        Log.e("ADERROR", adError.getCode() +": "+ adError.getMessage() );

        if (amazonAdEnabled)
        {
            amazonAdEnabled = false;
            adViewContainer.removeView(amazonAdView);
            adViewContainer.addView(admobAdView);
        }
//        AdRequest.Builder.addTestDevice("04CD51A7A1F806B7F55CADD6A3B84E92");
        admobAdView.loadAd((new com.google.android.gms.ads.AdRequest.Builder()).build());
    }

    @Override
    public void onAdExpanded(Ad ad) {

    }

    @Override
    public void onAdCollapsed(Ad ad) {

    }

    @Override
    public void onAdDismissed(Ad ad) {

    }

    public void onDestroy()
    {
        handler.removeCallbacks(runnable);
        this.amazonAdView.destroy();

    }

    public void onPause(){
        handler.removeCallbacks(runnable);
        this.amazonAdView.destroy();
    }

    public void onResume(){
        this.setUpAds();
    }
}
