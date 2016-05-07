package remindly.remindly;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import remindly.remindly.FbCommunicator;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FbCommunicator.initialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/Raleway-Light.ttf");

        Button button = (Button) findViewById(R.id.button);

        button.setTypeface(typeface);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Activity ac = MainActivity.this;
                FbCommunicator.login(ac, new FbCommunicator.loginListener() {
                    @Override
                    public void onLoginComplete(boolean isLoginSuccessfull) {
                        Log.v("isLoginSuccessfull: ", Boolean.toString(isLoginSuccessfull));

                        if (isLoginSuccessfull) {
                            startService(new Intent(ac, MymymeService.class));
                            Intent intent_success = new Intent(MainActivity.this, table.class);
                            startActivity(intent_success);
                            finish();
                        }
                    }
                });

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        FbCommunicator.onActivityResult(requestCode, resultCode, data);

    }

}
