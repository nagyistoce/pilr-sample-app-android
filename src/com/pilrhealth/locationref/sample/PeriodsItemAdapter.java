package com.pilrhealth.locationref.sample;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.pilrhealth.android.R;
import com.pilrhealth.pilriolib.Epoch;
import com.pilrhealth.pilriolib.Period;

public class PeriodsItemAdapter extends ArrayAdapter<Period> {

	public PeriodsItemAdapter(Context context, int resource, List<Period> objects) {
		super(context, resource, objects);
	}

	@Override
	public View getView(int position, View converView, ViewGroup parent) {
		Period period = getItem(position);
		View item = LayoutInflater.from(getContext()).inflate(R.layout.project_period_item, null);
		((TextView) item.findViewById(R.id.period)).setText(period.getPeriodName());
		((TextView) item.findViewById(R.id.periodCode)).setText(period.getPeriodCode());
		TableLayout epochTable = (TableLayout) item.findViewById(R.id.tableLayout);

		List<Epoch> epochsList = period.getEpoch();
		for (int j = 0; j < epochsList.size(); j++) {
			Epoch itemEp = epochsList.get(j);
			LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View epocView = vi.inflate(R.layout.project_epoch_item, null);
			TextView t1 = (TextView) epocView.findViewById(R.id.epoch);
			TextView t2 = (TextView) epocView.findViewById(R.id.startDateEpoch);
			TextView t3 = (TextView) epocView.findViewById(R.id.endDateEpoch);
			t1.setText(itemEp.getEpochName());
			t2.setText(itemEp.getEpochStartDate());
			t3.setText(itemEp.getEpochEndDate());
			TableRow row = new TableRow(getContext());
			row.addView(epocView);
			epochTable.addView(row, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		}
		return item;
	}
}
