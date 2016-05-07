package remindly.remindly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
    public static void login(Activity ac, final loginListener listener){

        LoginManager.getInstance().logOut(); //Log out to re-enable log in //TODO: remove when not demo.

        //Change these permissions to add functionality - https://developers.facebook.com/docs/facebook-login/permissions
        LoginManager.getInstance().logInWithReadPermissions(ac, Arrays.asList("email",
                "user_photos", "public_profile", "user_friends", "user_about_me", "user_birthday",
                "user_work_history", "user_relationship_details"));
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
                                                // On success - save id & name of user running the app.
                                                String jsonresult = String.valueOf(json);
                                                fb_id = json.getString("id");
                                                fb_name = json.getString("name");

                                                Log.d("FB token", AccessToken.getCurrentAccessToken().getToken());

                                                listener.onLoginComplete(true);
                                            } catch (JSONException e) {
                                                listener.onLoginComplete(false);
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }).executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        listener.onLoginComplete(false);
                    }

                    @Override
                    public void onError(FacebookException error) {
                        listener.onLoginComplete(false);
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
        GraphRequest req = new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me/invitable_friends",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        JSONObject res = response.getJSONObject();
                        try {
                            JSONArray all = (JSONArray)(res.get("data"));
                            ArrayList<String> ids = new ArrayList<String>();
                            for(int i=0; i < all.length(); i++){
                                JSONObject friend = all.getJSONObject(i);
                                ids.add(friend.get("id").toString());
                                //http://stackoverflow.com/questions/24417232/facebook-invitable-friends
                            }
                            idListener.onFacebookIdsReceived(ids);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields","id,name,birthday");
        req.setParameters(parameters);
        req.executeAsync();
    }

    // Return dictionary(name => profile pic url) and run callback
    public static void friendPictures(final facebookPhotosListener photosListener){

       /* make the API call */
        GraphRequest req = new GraphRequest(
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
                            }
                            photosListener.onFacebookPhotosReceived(pictures);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
        Bundle parameters = new Bundle();
        parameters.putString("fields","id,name,picture.width(400).height(400)");
        req.setParameters(parameters);
        req.executeAsync();
    }



//Listeners
public interface loginListener{
    public void onLoginComplete(boolean isLoginSuccessfull);
}
public interface facebookIdsListener{
    public void onFacebookIdsReceived(List<String> ids);
}
public interface facebookPhotosListener{
    public void onFacebookPhotosReceived(HashMap<String, String> name_url);
}


}
