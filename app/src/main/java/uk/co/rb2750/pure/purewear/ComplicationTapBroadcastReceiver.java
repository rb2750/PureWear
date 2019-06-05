package uk.co.rb2750.pure.purewear;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class ComplicationTapBroadcastReceiver extends BroadcastReceiver
{
    private static final String EXTRA_PROVIDER_COMPONENT =
            "uk.co.rb2750.pure.PROVIDER_COMPONENT";
    private static final String EXTRA_COMPLICATION_ID =
            "uk.co.rb2750.pure.COMPLICATION_ID";
    private static final String LAST_UPDATE =
            "uk.co.rb2750.pure.LAST_UPDATE";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Bundle extras = intent.getExtras();
        System.out.println(extras.getLong(LAST_UPDATE));
        Toast.makeText(context, "Last Update: " + TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - extras.getLong(LAST_UPDATE)) + " mins ago", Toast.LENGTH_SHORT).show();
    }

    static PendingIntent getToggleIntent(
            Context context, ComponentName provider, int complicationId, long lastUpdate)
    {
        Intent intent = new Intent(context, ComplicationTapBroadcastReceiver.class);
        intent.putExtra(EXTRA_PROVIDER_COMPONENT, provider);
        intent.putExtra(EXTRA_COMPLICATION_ID, complicationId);
        intent.putExtra(LAST_UPDATE, lastUpdate);
        return PendingIntent.getBroadcast(
                context, complicationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
