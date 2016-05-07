package remindly.remindly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;


public class FbCommunicator {
    // Creating Facebook CallbackManager Value
    public static CallbackManager callbackmanager;
    private static String fb_id;
    private static String fb_name;

    // Must call initialize before using this class!
    public static void initialize(Context ctx){
        FacebookSdk.sdkInitialize(ctx);
        callbackmanager = CallbackManager.Factory.create();
    }

    //
    public static void login(Activity ac){

        //Change these permissions to add functionality - https://developers.facebook.com/docs/facebook-login/permissions
        LoginManager.getInstance().logInWithReadPermissions(ac, Arrays.asList("email",
                "user_photos", "public_profile", "user_friends", "user_about_me"));
        LoginManager.getInstance().registerCallback(callbackmanager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        GraphRequest.newMeRequest(
                                loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject json, GraphResponse response) {
                                        if (response.getError() != null) {
                                            // handle error
                                        } else {
                                            try {
                                                // On success - svae id & name of user tunning the app.
                                                String jsonresult = String.valueOf(json);
                                                fb_id = json.getString("id");
                                                fb_name = json.getString("name");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }

                                }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onError(FacebookException error) {
                    }
                });
    }

    // Necessary: This actually perfomrs whats on the callbackmanager
    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackmanager.onActivityResult(requestCode, resultCode, data);
    }

    // return List of friend ids and run callback
    public static void friendsIds(final facebookIdsListener idListener){
        /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/invitable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject res = response.getJSONObject();
                        Log.d("FB FRIENDS: ", res.toString());
                        try {
                            JSONArray all = (JSONArray)(res.get("data"));
                            ArrayList<String> ids = new ArrayList<String>();
                            for(int i=0; i < all.length(); i++){
                                JSONObject friend = all.getJSONObject(i);
                                ids.add(friend.get("id").toString());
                                //http://stackoverflow.com/questions/24417232/facebook-invitable-friends
                                Log.d("FB friend ID: ", friend.get("id").toString());
                            }
                            idListener.onFacebookIdsReceived(ids);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

    // Return dictionary(name => profile pic url) and run callback
    public static void friendPictures(final facebookPhotosListener photosListener) throws JSONException {

       /* make the API call */
        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/invitable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject res = response.getJSONObject();
                        try {

                            JSONArray all = (JSONArray)(res.get("data"));
                            HashMap<String, String> pictures = new HashMap<String, String>();
                            for(int i=0; i < all.length(); i++){
                                JSONObject friend = all.getJSONObject(i);
                                JSONObject pic = (JSONObject)friend.get("picture");
                                JSONObject picData = (JSONObject)pic.get("data");
                                pictures.put(friend.get("name").toString(), picData.get("url").toString());
                                        //http://stackoverflow.com/questions/24417232/facebook-invitable-friends
                                        Log.d("FB friend ID: ", friend.get("id").toString());
                            }
                            photosListener.onFacebookPhotosReceived(pictures);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();
    }

//Listener
public interface facebookIdsListener{
    public void onFacebookIdsReceived(List<String> ids);
}
public interface facebookPhotosListener{
    public void onFacebookPhotosReceived(HashMap<String, String> name_url);
}

}
