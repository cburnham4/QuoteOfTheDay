package letshangllc.quoteoftheday;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Share a quote without SMS/Contacts permissions.
 * Uses the system share sheet ({@link Intent#ACTION_SEND}) or opens the default
 * messaging app via {@link Intent#ACTION_SENDTO} {@code smsto:} (user confirms send).
 */
public final class QuoteShareHelper {

    private QuoteShareHelper() {
    }

    public static void shareViaShareSheet(Context context, String quote) {
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        sendIntent.putExtra(Intent.EXTRA_TEXT, quote);
        Intent chooser = Intent.createChooser(sendIntent, context.getString(R.string.share_quote));
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(chooser);
    }

    /**
     * Opens the user's SMS/Messages app with the quote prefilled. No SEND_SMS permission.
     */
    public static void shareViaSmsApp(Context context, String quote) {
        Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.putExtra("sms_body", quote);
        smsIntent.putExtra(Intent.EXTRA_TEXT, quote);
        smsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (smsIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(smsIntent);
        } else {
            // Fallback if no SMS app is available
            shareViaShareSheet(context, quote);
        }
    }
}
