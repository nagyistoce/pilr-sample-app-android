package com.pilrhealth.locationref.sample.location;

import android.content.Context;

import com.pilrhealth.pilriolib.data.Dataset;

public class LocationDataset extends Dataset {
	
	// Configuration information about this data stream.
	private String mStreamId = "pilrhealth:location_ref_app:location";
	private Integer mVersion = 1;
	private boolean mUsesSimpleSave = true;
	

	public LocationDataset(Context ctx) {
		super(ctx);
	}

	@Override
	public String getStreamId() {
		return this.mStreamId;
	}

	@Override
	public Integer getSchemaVersion() {
		return this.mVersion;
	}

	@Override
	public boolean usesSimpleSave() {
		return mUsesSimpleSave;
	}
	
}
