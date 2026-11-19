# Google Play Data Safety Responses

**App:** Daily Motivational Quotes (`letshangllc.quoteoftheday`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

Use this when filling the Play Console Data safety form.

---

## Overview

The app shows a daily motivational quote (bundled locally), notifies the user at a chosen time, lets the user share the quote via the **system share sheet** (`ACTION_SEND`) or open Messages with a prefilled `smsto:` intent (`ACTION_SENDTO`), and displays AdMob banner ads after UMP consent.

The app does **not** use `SEND_SMS`, `READ_SMS`, `READ_CONTACTS`, or related restricted permissions. Sharing is user-driven through Android intents; the app never reads the address book or sends SMS silently.

Remote quote APIs / Firebase Realtime Database are **no longer used**. Quotes are bundled in the APK.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| Contacts | No | No | — |
| Phone numbers / SMS | No (app does not access) | No | User may share via system apps they choose |
| Crash / diagnostics | No (no Crashlytics) | No | — |
| App interactions | Yes (AdMob) | Yes (AdMob) | Advertising |
| Advertising ID | Yes (AdMob) | Yes (AdMob) | Advertising |
| Approximate location | Possibly (AdMob) | Yes (AdMob) | Advertising |

Local preferences store only the reminder time and the latest quote text on device.

---

## Permissions

| Permission | Why |
|------------|-----|
| `POST_NOTIFICATIONS` | Daily quote reminder notification (Android 13+). |
| `RECEIVE_BOOT_COMPLETED` | Reschedule the daily alarm after reboot. |
| `INTERNET` / `ACCESS_NETWORK_STATE` / `ACCESS_WIFI_STATE` | AdMob / UMP only (quotes work offline). |

### Explicitly not used (Play policy)

- `SEND_SMS` / `READ_SMS` / `RECEIVE_SMS` / `READ_CALL_LOG` / etc.
- `READ_CONTACTS` / `WRITE_CONTACTS` / `GET_ACCOUNTS`

Quote sharing uses `Intent.ACTION_SEND` (share sheet) and optionally `Intent.ACTION_SENDTO` with `smsto:` so the user confirms in their messaging app — **no SMS or Contacts permission required**.

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
- Direct SMS (`SmsManager`) and Contacts picker / friends list

---

## Data Security

- **In transit:** AdMob/UMP use HTTPS. `android:usesCleartextTraffic="false"`.
- **At rest:** Reminder time and latest quote in SharedPreferences on device.
- **Deletion:** Uninstall clears local data. Contact the Play listing email for other requests.

---

## Children's Privacy

Not directed at children under 13 (COPPA) / under 16 (GDPR).  
UMP uses `setTagForUnderAgeOfConsent(false)`. No child-directed ad serving.

---

## Play Console Form Shortcuts

- **Collects/shares required data types?** Yes (advertising via AdMob)
- **Encrypted in transit?** Yes (for network SDKs)
- **Users can request deletion?** Yes (uninstall / email developer)
- **Ads?** Yes — declare AdMob and advertising ID
- **SMS / Call log / Contacts?** No — do not declare these; app does not request those permissions

---

## Owner TODOs Before Republish

1. Replace TODO / test AdMob App ID and banner unit IDs with real IDs from AdMob Console (publisher `ca-app-pub-8223005482588566`).
2. Complete Data safety form using this doc (ads only; no SMS/Contacts).
3. Review Families / ads policies (app is not designed as a kids app).
4. Provide `keystore.properties` + keystore for release signing (gitignored).
