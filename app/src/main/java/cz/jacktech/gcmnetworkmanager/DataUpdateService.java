package cz.jacktech.gcmnetworkmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.PeriodicTask;
import com.google.android.gms.gcm.Task;
import com.google.android.gms.gcm.TaskParams;

public class DataUpdateService extends GcmTaskService {

    private static final String TAG = DataUpdateService.class.getSimpleName();

    public static final String GCM_ONEOFF_TAG = "oneoff|[0,0]";
    public static final String GCM_REPEAT_TAG = "repeat|[7200,1800]";

    @Override
    public void onInitializeTasks() {
	//called when app is updated to a new version, reinstalled etc.
	//you have to schedule your repeating tasks again
        super.onInitializeTasks();
        scheduleRepeat(this);
    }

    @Override
    public int onRunTask(TaskParams taskParams) {
        //do some stuff (mostly network) - executed in background thread (async)

        Handler h = new Handler(getMainLooper());
        if(taskParams.getTag().equals(GCM_ONEOFF_TAG)) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DataUpdateService.this, "ONEOFF executed", Toast.LENGTH_SHORT).show();
                }
            });
        } else if(taskParams.getTag().equals(GCM_REPEAT_TAG)) {
            h.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(DataUpdateService.this, "REPEATING executed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    public static void scheduleOneOff(Context context) {
	//in this method, single OneOff task is scheduled (the target service that will be called is DataUpdateService.class)
        try {
            OneoffTask oneoff = new OneoffTask.Builder()
		    //specify target service - must extend GcmTaskService
                    .setService(DataUpdateService.class)
		    //tag that is unique to this task (can be used to cancel task)
                    .setTag(GCM_ONEOFF_TAG)
		    //executed between 0 - 10s from now
                    .setExecutionWindow(30, 60)
		    //set required network state, this line is optional
		    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
		    //request that charging must be connected, this line is optional
                    .setRequiresCharging(true)
		    //if another task with same tag is already scheduled, replace it with this task
                    .setUpdateCurrent(true)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(oneoff);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void scheduleRepeat(Context context) {
	//in this method, single Repeating task is scheduled (the target service that will be called is DataUpdateService.class)
        try {
            PeriodicTask periodic = new PeriodicTask.Builder()
		    //specify target service - must extend GcmTaskService
                    .setService(DataUpdateService.class)
		    //repeat every 7200 seconds
                    .setPeriod(60)
		    //specify how much earlier the task can be executed (in seconds)
                    .setFlex(30)
		    //tag that is unique to this task (can be used to cancel task)
                    .setTag(GCM_REPEAT_TAG)
		    //whether the task persists after device reboot
                    .setPersisted(true)
		    //if another task with same tag is already scheduled, replace it with this task
                    .setUpdateCurrent(true)
		    //set required network state, this line is optional
                    .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)
		    //request that charging must be connected, this line is optional
                    .setRequiresCharging(true)
                    .build();
            GcmNetworkManager.getInstance(context).schedule(periodic);
        } catch (Exception e) {
            Log.e(TAG, "scheduling failed");
            e.printStackTrace();
        }
    }

    public static void cancelOneOff(Context context) {
        GcmNetworkManager.getInstance(context).cancelTask(GCM_ONEOFF_TAG, DataUpdateService.class);
    }

    public static void cancelRepeat(Context context) {
        GcmNetworkManager.getInstance(context).cancelTask(GCM_REPEAT_TAG, DataUpdateService.class);
    }
}
