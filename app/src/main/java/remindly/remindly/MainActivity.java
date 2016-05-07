package remindly.remindly;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {


    LoginManager lm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FbCommunicator.initialize(getApplicationContext());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

//        Settings.getApplicationSignature(Context);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FbCommunicator.onActivityResult(requestCode, resultCode, data);

    }

    public void connectToFb(View v){
        FbCommunicator.login(this);
    }


    public void printFbFriendsDetails(View v){
        FbCommunicator.friendsIds(new FbCommunicator.facebookIdsListener() {
            @Override
            public void onFacebookIdsReceived(List<String> ids) {
                for (int i = 0; i < ids.size(); i++) {
                    String id = ids.get(i);
                    Log.d("FB FRIEND ID: ", id);
                }
            }
        });
    }
    public void printFbFriendsPhotoUrls(View v) throws JSONException {
        FbCommunicator.friendPictures(new FbCommunicator.facebookPhotosListener() {
            @Override
            public void onFacebookPhotosReceived(HashMap<String, String> name_url) {
                Iterator it = name_url.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry)it.next();
                    Log.d("FB FRIEND: ", pair.getKey() + ", image: " + pair.getValue());
                    it.remove(); // avoids a ConcurrentModificationException
                }
            }
        });
    }

    }
