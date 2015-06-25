package com.pilrhealth.locationref.sample.location;

import java.util.Date;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.pilrhealth.android.R;
import com.pilrhealth.locationref.sample.UserSettingActivity;
import com.pilrhealth.pilriolib.api.AuthCredentials;

public class LocationService extends Service {

	private LocationManager mLocationManager;
	private LocationListener mLocationListener;
	private LocationDataset mLocationDs;

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		//Start service foreground
		Notification notification = new Notification(R.drawable.location, getString(R.string.record_msg), System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, UserSettingActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, getString(R.string.record_msg), getString(R.string.app_name), pendingIntent);
		notification.flags|=Notification.FLAG_NO_CLEAR;
		startForeground(1234, notification);

		mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		mLocationListener = new MyLocationListener();
		if (UserSettingActivity.sGPSProvider) {
			mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, UserSettingActivity.sMinTime, UserSettingActivity.sMinDistance, mLocationListener);
		}
		if (UserSettingActivity.sNetworkProvider) {
			mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,  UserSettingActivity.sMinTime, UserSettingActivity.sMinDistance, mLocationListener);
		}
		mLocationDs = new LocationDataset(this);

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// unregister the listeners
		mLocationManager.removeUpdates(mLocationListener);
		Log.d("LocationService", "onDestroy");
		//stop foreground
		stopForeground(true);
	}

	private class MyLocationListener implements LocationListener {
		public void onLocationChanged(Location arg0) {
			try {
				LocationDsRecord record = new LocationDsRecord();
				record.location = arg0;
				record.setDateCreated(new Date());
				record.setParticipant(AuthCredentials.getParticipantId());
				mLocationDs.stashRecord(record);
				mLocationDs.saveStashedRecordsAndFlush();
			} catch (Exception e) {
				Log.d("LocationService", "MyLocationListener:" + e);
			}
		}

		public void onProviderDisabled(String arg0) {
			// Do nothing
		}

		public void onProviderEnabled(String arg0) {
			// Do nothing
		}

		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
			// Do nothing
		}

	}

}
