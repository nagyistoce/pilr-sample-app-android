package com.pilrhealth.locationref.sample;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.pilrhealth.android.R;
import com.pilrhealth.pilriolib.InstrumentSettings;

public class InstrumentSettingsItemAdapter extends ArrayAdapter<InstrumentSettings> {

	public InstrumentSettingsItemAdapter(Context context, int resource, List<InstrumentSettings> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		InstrumentSettings iSettings = getItem(position);
		View item = LayoutInflater.from(getContext()).inflate(R.layout.instrument_settings_item, null);
		((TextView) item.findViewById(R.id.instrument)).setText(iSettings.getSettingName());
		((TextView) item.findViewById(R.id.instrumentCode)).setText(iSettings.getSettingCode());
		((TextView) item.findViewById(R.id.periodCode)).setText(iSettings.getPeriodCode());
		((TextView) item.findViewById(R.id.epochCode)).setText(iSettings.getEpochCode());
		((TextView) item.findViewById(R.id.description)).setText(iSettings.getSettingDesc());
		((TextView) item.findViewById(R.id.type)).setText(iSettings.getSettingType());
		((TextView) item.findViewById(R.id.value)).setText(iSettings.getSettingValue());
		return item;
	}
}
