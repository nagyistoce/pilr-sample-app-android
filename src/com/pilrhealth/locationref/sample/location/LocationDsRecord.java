package com.pilrhealth.locationref.sample.location;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.location.Location;
import android.util.Log;

import com.pilrhealth.pilriolib.data.DatasetRecord;
import com.pilrhealth.pilriolib.util.ISODateHelper;

public class LocationDsRecord extends DatasetRecord {

	public Location location;
	// JSON Node names
	private static String LATITUDE = "lat";
	private static String LONGITUDE = "lon";
	private static String PROVIDER = "provider";
	private static String SPEED = "speed";
	private static String TIME = "time";
	private static String ALTITUDE = "alt";
	private static String ACCURACY = "acc";
	private static String BEARING = "bearing";

	/*
	 * Build JSONObject for location queue
	 * 
	 * @see com.pilrhealth.android.pilrcorelib.util.JSONable#toJSONObject()
	 */
	public JSONObject toJSONObject() throws JSONException {
		return toJSONObject(location);
	}

	/*
	 * Build JSONObject for last location
	 * 
	 * @see com.pilrhealth.android.pilrcorelib.util.JSONable#toJSONObject()
	 */
	public JSONObject toJSONObject(Location location) throws JSONException {
		JSONObject obj = new JSONObject();
		// For LastLocation the time will be cached, not actual time
		obj.put(TIME, ISODateHelper.toString(new Date(location.getTime())));
		obj.put(LATITUDE, location.getLatitude());
		obj.put(LONGITUDE, location.getLongitude());
		obj.put(PROVIDER, location.getProvider());
		obj.put(ACCURACY, location.getAccuracy());
		obj.put(BEARING, location.getBearing());
		obj.put(ALTITUDE, location.getAltitude());
		obj.put(SPEED, location.getSpeed());
		Log.d("JSON obj", "obj" + obj);
		return obj;
	}

	public void fromJSONObject(JSONObject src) throws JSONException {
		// TODO Set the location object values from the JSON

	}

}
