package no.nordicsemi.android.nrftoolbox.hts;

import no.nordicsemi.android.log.Logger;
import no.nordicsemi.android.nrftoolbox.FeaturesActivity;
import no.nordicsemi.android.nrftoolbox.R;
import no.nordicsemi.android.nrftoolbox.profile.BleManager;
import no.nordicsemi.android.nrftoolbox.profile.BleProfileService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

public class HTSService extends BleProfileService implements HTSManagerCallbacks {
	public static final String BROADCAST_HTS_MEASUREMENT = "no.nordicsemi.android.nrftoolbox.hts.BROADCAST_HTS_MEASUREMENT";
	public static final String EXTRA_TEMPERATURE = "no.nordicsemi.android.nrftoolbox.hts.EXTRA_TEMPERATURE";

	private final static String ACTION_DISCONNECT = "no.nordicsemi.android.nrftoolbox.hts.ACTION_DISCONNECT";

	private final static int NOTIFICATION_ID = 267;
	private final static int OPEN_ACTIVITY_REQ = 0;
	private final static int DISCONNECT_REQ = 1;

	private HTSManager mManager;
	private boolean mBinded;

	private final LocalBinder mBinder = new RSCBinder();

	/**
	 * This local binder is an interface for the binded activity to operate with the HTS sensor
	 */
	public class RSCBinder extends LocalBinder {
		// empty
	}

	@Override
	protected LocalBinder getBinder() {
		return mBinder;
	}

	@Override
	protected BleManager<HTSManagerCallbacks> initializeManager() {
		return mManager = new HTSManager();
	}

	@Override
	public void onCreate() {
		super.onCreate();

		final IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_DISCONNECT);
		registerReceiver(mDisconnectActionBroadcastReceiver, filter);
	}

	@Override
	public void onDestroy() {
		// when user has disconnected from the sensor, we have to cancel the notification that we've created some milliseconds before using unbindService
		cancelNotification();
		unregisterReceiver(mDisconnectActionBroadcastReceiver);

		super.onDestroy();
	}

	@Override
	public IBinder onBind(final Intent intent) {
		mBinded = true;
		return super.onBind(intent);
	}

	@Override
	public void onRebind(final Intent intent) {
		mBinded = true;
		// when the activity rebinds to the service, remove the notification
		cancelNotification();

		// read the battery level when back in the Activity
		if (isConnected())
			mManager.readBatteryLevel();
	}

	@Override
	public boolean onUnbind(final Intent intent) {
		mBinded = false;
		// when the activity closes we need to show the notification that user is connected to the sensor  
		createNotifcation(R.string.hts_notification_connected_message, 0);
		return super.onUnbind(intent);
	}

	@Override
	public void onHTValueReceived(final double value) {
		final Intent broadcast = new Intent(BROADCAST_HTS_MEASUREMENT);
		broadcast.putExtra(EXTRA_TEMPERATURE, value);
		LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast);
	}

	/**
	 * Creates the notification
	 * 
	 * @param messageResIdthe
	 *            message resource id. The message must have one String parameter,<br />
	 *            f.e. <code>&lt;string name="name"&gt;%s is connected&lt;/string&gt;</code>
	 * @param defaults
	 *            signals that will be used to notify the user
	 */
	private void createNotifcation(final int messageResId, final int defaults) {
		final Intent parentIntent = new Intent(this, FeaturesActivity.class);
		parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		final Intent targetIntent = new Intent(this, HTSActivity.class);

		final Intent disconnect = new Intent(ACTION_DISCONNECT);
		final PendingIntent disconnectAction = PendingIntent.getBroadcast(this, DISCONNECT_REQ, disconnect, PendingIntent.FLAG_UPDATE_CURRENT);

		// both activities above have launchMode="singleTask" in the AndoridManifest.xml file, so if the task is already running, it will be resumed
		final PendingIntent pendingIntent = PendingIntent.getActivities(this, OPEN_ACTIVITY_REQ, new Intent[] { parentIntent, targetIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
		final Notification.Builder builder = new Notification.Builder(this).setContentIntent(pendingIntent);
		builder.setContentTitle(getString(R.string.app_name)).setContentText(getString(messageResId, getDeviceName()));
		builder.setSmallIcon(R.drawable.ic_stat_notify_hts);
		builder.setShowWhen(defaults != 0).setDefaults(defaults).setAutoCancel(true).setOngoing(true);
		builder.addAction(R.drawable.ic_action_bluetooth, getString(R.string.hts_notification_action_disconnect), disconnectAction);

		final Notification notification = builder.build();
		final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		nm.notify(NOTIFICATION_ID, notification);
	}

	/**
	 * Cancels the existing notification. If there is no active notification this method does nothing
	 */
	private void cancelNotification() {
		final NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		nm.cancel(NOTIFICATION_ID);
	}

	/**
	 * This broadcast receiver listens for {@link #ACTION_DISCONNECT} that may be fired by pressing Disconnect action button on the notification.
	 */
	private BroadcastReceiver mDisconnectActionBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			Logger.i(getLogSession(), "[HTS] Disconnect action pressed");
			if (isConnected())
				getBinder().disconnect();
			else
				stopSelf();
		};
	};

}
