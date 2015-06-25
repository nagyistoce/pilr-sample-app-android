package com.pilrhealth.locationref.sample.location;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.pilrhealth.locationref.sample.RetrievalCallback;
import com.pilrhealth.pilriolib.api.ApiManager;
import com.pilrhealth.pilriolib.api.AuthCredentials;
import com.pilrhealth.pilriolib.data.Dataset;
import com.pilrhealth.pilriolib.data.DatasetRecord;

public class LastLocation extends Activity {

	public void getLastLocation(Context context, ListView view, List<Dataset> stream) {
		LocationManager mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		Criteria criteria = new Criteria();
		String provider = mLocationManager.getBestProvider(criteria, false);
		Location location = mLocationManager.getLastKnownLocation(provider);
		if (location != null) {
			try {
				LocationDsRecord record = new LocationDsRecord();
				record.setDateCreated(new Date());
				record.setParticipant(AuthCredentials.getParticipantId());
				ArrayList<DatasetRecord> dsRecord = new ArrayList<DatasetRecord>();
				dsRecord.add(record);

				ApiManager apiMan = new ApiManager(context);
				apiMan.startUploadInAsyncTask(true, (record.toJSONObject(location)), dsRecord, new RetrievalCallback(apiMan, view), stream);

			} catch (Exception e) {
				Log.d("LastLocation", "Error storing location data:" + e);
			}
		} else {
			Toast.makeText(context, "no location", Toast.LENGTH_SHORT).show();
		}
	}
}
