# GcmNetworkManager example
### NOTICE: information based on GPS v8.3.0

**An example for GCMNetworkManager introduced on I/O 2015
I wrote a nice blogpost about using this API, check it out http://jacktech24.blogspot.cz/2015/08/working-with-gcmnetworkmanager-on.html**

### If you wan't nice library using this API together with Job Scheduler and Alarm Manager, try this one https://github.com/evernote/android-job

Use the code below together with the example provided to schedule a OneOff task from your Activity, Fragment, anywhere
```java
MyTaskService.scheduleOneOff(context);
```
And the code below to schedule repeating task that persists after reboot
```java
MyTaskService.scheduleRepeat(context);
```

Use
```bash
user@machine:~$ adb shell dumpsys activity service GcmService --endpoints MyTaskService
```
to check for scheduled tasks from your PC (replace MyTaskService with your service class name)

More info and documentation : https://developers.google.com/cloud-messaging/network-manager

## FAQ

### Do I need to use GCM (Google Cloud Messaging) to use this?
No, you don't have to care about GCM when working with this API. But if you have in dependencies only
parts of Google Play services library (to save space and methods), you must have:
```groovy
compile 'com.google.android.gms:play-services-gcm:{insert latest version here}'
```

### What's in the background of GCMNetworkManager
It differs on what Android version it is used. For pre-Lollipop versions, an Google proprietary solution is used. On Lollipop and onwards, JobScheduler API is used.

### Does it require Google Play services?
Yes it does, there must be (as for now) installed Google Play services to make this work (even on Lollipop+). Before you start being angry about this, you must think about it for a moment. The way this feature works is that there is a service running which have all tasks for all apps registered and calls them when time comes. There is no such thing on pre-Lollipop versions and so Google implemented this. This way, Google can also fix bugs in the API etc. much faster, just by updating GPS.
Use something like this (this is taken from this example app, found in MainActivity)
```java
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
```
to check whether GPS are installed, if not, use another solution

### Can I use it only for network related stuff?
This API is built for it, but you can use it whatever you want. You just need to remember there are some limitations.
1. your task is given wakelock for only 3 minutes, after that your task is expected to be timed-out -> use this API for only short network tasks
2. there are some settings related to network and charging, if you are not dependent on network, don't forget to set ``` setRequiredNetwork(Task.NETWORK_STATE_ANY) ``` because default is ```Task.NETWORK_STATE_CONNECTED```
3. the time during which your task is executed is approximate, although the service is trying to execute the task in specified interval, sometimes it's executed after the interval so don't forget it

### Persistent tasks upon reboots
You can make your tasks persistent upon reboots by setting ```setPersisted(true)``` but don't forget to add ```RECEIVE_BOOT_COMPLETED``` permission to your manifest

### What to return from onRunTask? Differences
So first, what can you return there. You have three options: ```GcmNetworkManager.RESULT_FAILURE```, ```GcmNetworkManager.RESULT_SUCCESS``` and ```GcmNetworkManager.RESULT_RESCHEDULE```

```java GcmNetworkManager.RESULT_FAILURE``` : means that your task failed and you don't want it to get rescheduled, if your task is OneOff, then it won't get rescheduled and nothing more happens, for a Repeating task, it's the same behaviour, all ongoing tasks (to be repeated) are kept in queque<br/>
```java GcmNetworkManager.RESULT_SUCCESS``` : means your task completed succesfully, it won't be rescheduled (because why), behaviour is the same as for ```GcmNetworkManager.RESULT_FAILURE```<br/>
```java GcmNetworkManager.RESULT_RESCHEDULE``` : mean your task failed, reschedules your task, so it will be executed again when conditions are met<br/>


### Can I pass something (eg. Bundle) using TaskParams?
~~No you can't. You have to find yourself a different way to do it. Either store it in ``` SharedPreferences ``` which you will access from this service or if the data you need are large, store them in DB and load them in your service.~~

**No longer true, since Google Play services 7.8.0, you can pass Bundle using .setExtras(Bundle) to your task.**

### Can I get the queque of the tasks from in-app?
Again, for now, you can't. There is no API for getting list of your scheduled tasks. Only option that comes to my mind now is to execute the command mentioned above and parse the data from it. Otherwise, we have to wait for Google to implement it.

### My tasks are removed every time I reinstall the app, what can I do?
There is a method in ```GcmTaskService```  you have to override called ```onInitializeTasks```. This method is called each time your package is removed or updated (taken from docs). Unfortunatelly, there is again no way to simple say: "I want to reschedule all my previous tasks". At the moment this method is called, they are all already canceled. You have to figure out somehow (maybe store it in configuration or whatever) which task you want to reschedule and reschedule them here. **IMPORTANT, this method is executed on UI thread**

### Task persists even after I clear my app data / force stop it
Yeah of course, that's because your app is not the one who is managing them, that's Google Play services. You have to check in your service/task whether your app is configured/setup and you can eventually remove all the tasks here. There is no other way to do it for now.

### Can I set interval to (0, 1) in OneOff task to execute it immediatelly?
You can, but it won't be executed immediatelly, it can take up to 1 minute or more to get the task executed. A better way would be to start the service directly.

### Is it possible to control `RESULT_RESCHEDULE` count, delay?
For now, not, but there are some possibilites for you. By default, if you return `RESULT_RESCHEDULE` from your `onRunTask`, your task is executed again as soon as conditions (charging, internet available) you defined for it are met.
* one option, if you need only to limit the count of reschedules is to store somewhere the number of reschedules, and when it reaches your defined limit, return `RESULT_FAILURE` instead of `RESULT_RESCHEDULE`
* second one, if you need more control would be to return `RESULT_FAILURE` and schedule an OneOff task, make it recognizable by some special tag and again count how many times have you scheduled it somewhere, this way you will be able to control count and delay between executions of the task
