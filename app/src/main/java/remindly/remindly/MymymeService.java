package remindly.remindly;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.orcam.orcam_sdk.API.Interfaces.ConnectedListener;
import com.orcam.orcam_sdk.API.Interfaces.CreatePersonWithImagesListener;
import com.orcam.orcam_sdk.API.Model.MyMeError;
import com.orcam.orcam_sdk.API.Model.MyMeFace;
import com.orcam.orcam_sdk.API.Model.MyMePerson;
import com.orcam.orcam_sdk.API.MyMe;
import com.orcam.orcam_sdk.API.MyMeCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by omer on 06/05/2016.
 */

public class MymymeService extends Service {
    private MyMe myMe;
    SharedPreferences sp;
    private final String SP_NAME = "PREF";
    @Override
    public int onStartCommand(Intent intent, int flags, int startid){
        MyMeCallback myMeCallback = initMyMeCallback(); //(1)
        myMe = MyMe.getInstance(this,myMeCallback);     //(2)
        myMe.quickConnect(new ConnectedListener() {
            @Override
            public void OnSuccess(String deviceId) {
                Log.d("MymymeService", "Connected to " + deviceId);
            }

            @Override
            public void OnError(MyMeError myMeError) {
                Log.d("MymymeService", "Failed to connect");
                //TODO:add error handaling
            }
        });

        sp = getSharedPreferences(SP_NAME, 0);
        return super.onStartCommand(intent, flags, startid);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    private MyMeCallback initMyMeCallback() {
        return new MyMeCallback() {


            @Override
            public void faceDetected(MyMeFace face, MyMePerson person, float probability, long time) {
                { //(1)
                    Log.d("MyMe","Face Detected");
                    if(person == null)//(2)
                    {
                        Log.d("MyMe","Unknown Person Detected");
                    }
                    else
                    {
                        Log.d("MyMe",person.getPersonID()+ " Detected at "+ probability +" certainty"); //(3)
                        String res = sp.getString(person.getPersonID(), "failed");
                        if(res.equals("failed")){
                            Log.d("MymyMeService", "Failed DB extract person");
                        }
                        else{
                            if(res.length() > 0){
                                String[] data = res.split(" ");
                                Log.d("MymyMeService", "Success DB extract person" + res);
                                PebbleCommunicator.inputToPebble(getApplicationContext(), data[0], data[1]);
                            }else{
                                Log.d("MymyMeService", "extracted - But no data on person");
                            }
                        }
                    }
                }
            }

            @Override
            public void newPersonCreated(MyMePerson person, long time) {//(1)

                Log.d("MyMe", "New Person Created. ID: " + person.getPersonID()); //(2)
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(person.getPersonID(), "is an-asshole");
                editor.apply();
            }
        };
    }
}
