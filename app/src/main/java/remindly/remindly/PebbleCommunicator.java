package remindly.remindly;

import android.content.Context;
import android.content.Intent;

import com.getpebble.android.kit.PebbleKit;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class PebbleCommunicator {

    public static void inputToPebble(Context context, String title, String notification){
        // Push a notification
        final Intent i = new Intent("com.getpebble.action.SEND_NOTIFICATION");

        final Map data = new HashMap();
        data.put("title", title);
        data.put("body", notification);
        final JSONObject jsonData = new JSONObject(data);
        final String notificationData = new JSONArray().put(jsonData).toString();

        i.putExtra("messageType", "PEBBLE_ALERT");
        i.putExtra("sender", "PebbleKit Android");
        i.putExtra("notificationData", notificationData);
        context.sendBroadcast(i);
    }

    public static boolean isPebbleConnected(Context context){
        boolean isConnected = PebbleKit.isWatchConnected(context);
        return isConnected;
    }
}
