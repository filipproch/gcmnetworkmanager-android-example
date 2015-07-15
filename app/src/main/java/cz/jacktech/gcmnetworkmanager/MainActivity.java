package cz.jacktech.gcmnetworkmanager;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button oneoff = (Button) findViewById(R.id.schedule_oneoff);
        oneoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUpdateService.scheduleOneOff(v.getContext());
            }
        });
        Button repeat = (Button) findViewById(R.id.schedule_repeating);
        repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUpdateService.scheduleRepeat(v.getContext());
            }
        });
        Button cancelOneoff = (Button) findViewById(R.id.cancel_oneoff);
        cancelOneoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUpdateService.cancelOneOff(v.getContext());
            }
        });
        Button cancelRepeat = (Button) findViewById(R.id.cancel_repeat);
        cancelRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataUpdateService.cancelRepeat(v.getContext());
            }
        });
    }

}
