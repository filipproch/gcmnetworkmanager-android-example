package cz.jacktech.gcmnetworkmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;


public class MainActivity extends AppCompatActivity {

    private static final int GPS_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        int errorCheck = api.isGooglePlayServicesAvailable(this);
        if(errorCheck == ConnectionResult.SUCCESS) {
            //google play services available, hooray
        } else if(api.isUserResolvableError(errorCheck)) {
            //GPS_REQUEST_CODE = 1000, and is used in onActivityResult
            api.showErrorDialogFragment(this, errorCheck, GPS_REQUEST_CODE);
            //stop our activity initialization code
            return;
        } else {
            //GPS not available, user cannot resolve this error
            //todo: somehow inform user or fallback to different method
            //stop our activity initialization code
            return;
        }

        initViews();
    }

    private void initViews() {
        Button oneoff = (Button) findViewById(R.id.schedule_oneoff);
        oneoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTaskService.scheduleOneOff(v.getContext());
            }
        });
        Button repeat = (Button) findViewById(R.id.schedule_repeating);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTaskService.scheduleRepeat(v.getContext());
            }
        });
        Button cancelOneoff = (Button) findViewById(R.id.cancel_oneoff);
        cancelOneoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTaskService.cancelOneOff(v.getContext());
            }
        });
        Button cancelRepeat = (Button) findViewById(R.id.cancel_repeat);
        cancelRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTaskService.cancelRepeat(v.getContext());
            }
        });
        Button cancelAll = (Button) findViewById(R.id.cancel_all);
        cancelAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyTaskService.cancelAll(v.getContext());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GPS_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                //GPS successfully updated / enabled, we can continue
                initViews();
            } else {
                //no Google Play, or user denied installing
                //you should probably fallback somehow or inform user
                //here I only exit application
                finish();
            }
        }
    }
}
