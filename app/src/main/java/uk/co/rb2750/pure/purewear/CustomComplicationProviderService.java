package uk.co.rb2750.pure.purewear;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.SharedPreferences;
import android.support.wearable.complications.ComplicationData;
import android.support.wearable.complications.ComplicationManager;
import android.support.wearable.complications.ComplicationProviderService;
import android.support.wearable.complications.ComplicationText;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Example watch face complication data provider provides a number that can be incremented on tap.
 */
public class CustomComplicationProviderService extends ComplicationProviderService
{

    private static final String TAG = "ComplicationProvider";

    @Override
    public void onComplicationActivated(
            int complicationId, int dataType, ComplicationManager complicationManager)
    {
        Log.d(TAG, "onComplicationActivated(): " + complicationId);
    }

    @Override
    public void onComplicationUpdate(
            final int complicationId, final int dataType, final ComplicationManager complicationManager)
    {
        Log.d(TAG, "onComplicationUpdate() id: " + complicationId);
//        ComponentName thisProvider = new ComponentName(this, getClass());
        Thread thread = new Thread(new Runnable()
        {

            @Override
            public void run()
            {
                try
                {
                    URL url = new URL("http://rb2750.co.uk:3500/");

                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    try
                    {
                        InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                        byte[] contents = new byte[1024];

                        int bytesRead;
                        String text = "";
                        while ((bytesRead = in.read(contents)) != -1)
                        {
                            text += new String(contents, 0, bytesRead);
                        }

                        ComplicationData complicationData = null;

                        ComponentName thisProvider = new ComponentName(CustomComplicationProviderService.this, getClass());
                        PendingIntent complicationPendingIntent =
                                ComplicationTapBroadcastReceiver.getToggleIntent(
                                        CustomComplicationProviderService.this, thisProvider, complicationId, System.currentTimeMillis());

                        switch (dataType)
                        {
                            case ComplicationData.TYPE_SHORT_TEXT:
                                complicationData =
                                        new ComplicationData.Builder(ComplicationData.TYPE_SHORT_TEXT)
                                                .setShortTitle(ComplicationText.plainText("In Gym"))
                                                .setShortText(ComplicationText.plainText(text.trim()))
                                                .setTapAction(complicationPendingIntent)
                                                .build();
                                break;
                            default:
                                if (Log.isLoggable(TAG, Log.WARN))
                                {
                                    Log.w(TAG, "Unexpected complication type " + dataType);
                                }
                        }

                        if (complicationData != null)
                        {
//                                lastUpdate = System.currentTimeMillis();
                            complicationManager.updateComplicationData(complicationId, complicationData);
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        complicationManager.noUpdateRequired(complicationId);
                    }
                    finally
                    {
                        urlConnection.disconnect();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
    }

    @Override
    public void onComplicationDeactivated(int complicationId)
    {
        Log.d(TAG, "onComplicationDeactivated(): " + complicationId);
    }
}