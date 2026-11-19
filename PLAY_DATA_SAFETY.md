# Google Play Data Safety Responses

**App:** Daily Motivational Quotes (`letshangllc.quoteoftheday`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

Use this when filling the Play Console Data safety form.

---

## Overview

The app shows a daily motivational quote (bundled locally), optionally texts that quote to contacts the user adds, shows notifications at a chosen time, and displays AdMob banner ads after UMP consent.

Remote quote APIs / Firebase Realtime Database are **no longer used**. Quotes are bundled in the APK.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| Contacts (names/numbers user selects) | Yes (on device) | No* | App functionality (SMS friends list) |
| Phone numbers (for SMS) | Yes (on device) | Via carrier SMS only | App functionality |
| Crash / diagnostics | No (no Crashlytics) | No | — |
| App interactions | Yes (AdMob) | Yes (AdMob) | Advertising |
| Advertising ID | Yes (AdMob) | Yes (AdMob) | Advertising |
| Approximate location | Possibly (AdMob) | Yes (AdMob) | Advertising |

\* Contact data is stored in app SharedPreferences on device. SMS content is sent only to numbers the user added, via the device SMS APIs / carrier — not to the developer’s servers.

---

## Permissions That Need Play Declarations

| Permission | Why |
|------------|-----|
| `SEND_SMS` | Core feature: send the daily quote to friends the user added. **Restricted permission** — declare SMS use case in Play Console (and complete any declaration form). |
| `READ_CONTACTS` | User picks friends from the address book. Declare Contacts data access. |
| `POST_NOTIFICATIONS` | Daily quote reminder notification (Android 13+). |
| `RECEIVE_BOOT_COMPLETED` | Reschedule the daily alarm after reboot. |
| `INTERNET` / `ACCESS_NETWORK_STATE` / `ACCESS_WIFI_STATE` | AdMob / UMP only (quotes work offline). |

---

## Third-Party SDKs

### Google AdMob (`play-services-ads`)
- **Data:** Advertising ID, app interactions, device info (and possibly approximate location per Google’s policies)
- **Purpose:** Banner ads
- **Shared:** Yes, with Google advertising partners
- **Consent:** UMP form before requesting ads
- **Privacy:** https://policies.google.com/privacy

### Google User Messaging Platform (UMP)
- **Purpose:** GDPR/consent management
- **Privacy:** https://policies.google.com/privacy

### Removed
- Amazon Mobile Ads SDK (HTTP / deprecated)
- Firebase Realtime Database / Volley remote quote fetch
- `quotes.rest` HTTP API

---

## Data Security

- **In transit:** AdMob/UMP use HTTPS. `android:usesCleartextTraffic="false"`.
- **At rest:** Friends list and preferences stored in app SharedPreferences on device.
- **Deletion:** User can remove friends in-app; uninstall clears local data. Contact the Play listing email for other requests.

---

## Children's Privacy

Not directed at children under 13 (COPPA) / under 16 (GDPR).  
UMP uses `setTagForUnderAgeOfConsent(false)`. No child-directed ad serving.

---

## Play Console Form Shortcuts

- **Collects/shares required data types?** Yes (ads; optional contacts/SMS for the friends feature)
- **Encrypted in transit?** Yes (for network SDKs)
- **Users can request deletion?** Yes (in-app remove friends / uninstall / email developer)
- **Ads?** Yes — declare AdMob and advertising ID
- **SMS / Contacts?** Yes — complete sensitive permission declarations

---

## Owner TODOs Before Republish

1. Replace TODO / test AdMob App ID and banner unit IDs with real IDs from AdMob Console (publisher `ca-app-pub-8223005482588566`).
2. Complete Data safety form using this doc.
3. Complete SMS and Contacts permission declarations.
4. Review Families / ads policies (app is not designed as a kids app).
5. Provide `keystore.properties` + keystore for release signing (gitignored).
