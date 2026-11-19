package letshangllc.quoteoftheday;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AdMob banners with UMP consent. Amazon Mobile Ads removed (HTTP / deprecated SDK).
 */
public class AdsHelper {
    private static final String TAG = "AdsHelper";

    private final String admobId;
    private final Activity activity;
    private final View view;

    private ViewGroup adViewContainer;
    private AdView admobAdView;
    private ConsentInformation consentInformation;
    private static final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);

    public AdsHelper(View view, String admobId, Activity activity) {
        this.admobId = admobId;
        this.activity = activity;
        this.view = view;
    }

    /** Starts UMP consent then loads an adaptive banner when allowed. */
    public void setUpAds() {
        ConsentRequestParameters.Builder paramsBuilder = new ConsentRequestParameters.Builder()
                .setTagForUnderAgeOfConsent(false);

        if (BuildConfig.DEBUG) {
            ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
                    .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
                    .addTestDeviceHashedId(AdRequest.DEVICE_ID_EMULATOR)
                    .build();
            paramsBuilder.setConsentDebugSettings(debugSettings);
        }

        ConsentRequestParameters params = paramsBuilder.build();

        consentInformation = UserMessagingPlatform.getConsentInformation(activity);
        consentInformation.requestConsentInfoUpdate(
                activity,
                params,
                () -> UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                        activity,
                        loadAndShowError -> {
                            if (loadAndShowError != null) {
                                Log.w(TAG, String.format("Consent form error: %d: %s",
                                        loadAndShowError.getErrorCode(),
                                        loadAndShowError.getMessage()));
                            }
                            if (consentInformation.canRequestAds()) {
                                initializeMobileAds();
                            }
                        }
                ),
                requestConsentError -> {
                    Log.w(TAG, String.format("Consent request error: %d: %s",
                            requestConsentError.getErrorCode(),
                            requestConsentError.getMessage()));
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAds();
                    }
                }
        );

        if (consentInformation.canRequestAds()) {
            initializeMobileAds();
        }
    }

    /** Legacy entry point used by activities. */
    public void runAds() {
        setUpAds();
    }

    private void initializeMobileAds() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            activity.runOnUiThread(this::loadBannerAd);
            return;
        }

        new Thread(() -> MobileAds.initialize(activity, initializationStatus ->
                activity.runOnUiThread(this::loadBannerAd)
        )).start();
    }

    private void loadBannerAd() {
        adViewContainer = view.findViewById(R.id.ad_layout);
        if (adViewContainer == null) {
            Log.e(TAG, "Ad container view not found");
            return;
        }

        if (admobId == null || admobId.startsWith("TODO_")) {
            Log.w(TAG, "Ad unit ID is a TODO placeholder; skipping ad load");
            return;
        }

        admobAdView = new AdView(activity);
        admobAdView.setAdUnitId(admobId);
        admobAdView.setAdSize(getAdaptiveAdSize());

        adViewContainer.removeAllViews();
        adViewContainer.addView(admobAdView);

        AdRequest adRequest = new AdRequest.Builder().build();
        admobAdView.loadAd(adRequest);
    }

    private AdSize getAdaptiveAdSize() {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int adWidthPixels = displayMetrics.widthPixels;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = activity.getWindowManager().getCurrentWindowMetrics();
            adWidthPixels = windowMetrics.getBounds().width();
        }

        int adWidth = (int) (adWidthPixels / displayMetrics.density);
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth);
    }

    public void onPause() {
        if (admobAdView != null) {
            admobAdView.pause();
        }
    }

    public void onResume() {
        if (admobAdView != null) {
            admobAdView.resume();
        }
    }

    public void onDestroy() {
        if (admobAdView != null) {
            admobAdView.destroy();
            admobAdView = null;
        }
    }
}
