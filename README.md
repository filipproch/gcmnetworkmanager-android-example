# gcmnetworkmanager-android-example
An example for GCMNetworkManager introduced on I/O 2015

Use the code below to schedule a OneOff task from your Activity, Fragment, anywhere
```
DataUpdateService.scheduleOneOff(context);
```
And the code below to schedule repeating task that persists after reboot
```
DataUpdateService.scheduleRepeat(context);
```
