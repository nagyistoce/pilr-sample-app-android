package com.pilrhealth.locationref.sample;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pilrhealth.android.R;
import com.pilrhealth.pilriolib.InstrumentConfig;
import com.pilrhealth.pilriolib.InstrumentSettings;
import com.pilrhealth.pilriolib.Period;
import com.pilrhealth.pilriolib.Project;
import com.pilrhealth.pilriolib.api.ApiManager;
import com.pilrhealth.pilriolib.api.AuthCredentials;

public class InstrumentSettingsActivity extends Activity {
	private TextView mSynkLink;
	public static ListView sListView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instrument_settings);

		mSynkLink = (TextView) findViewById(R.id.syncSetTitle);
		sListView = (ListView) findViewById(R.id.setList);

		mSynkLink.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				List<Period> periods = Project.getPeriods();
				for (int i = 0; i < periods.size(); i++) {
					// Call api to get data
					String mUrl = AuthCredentials.getUrl() + "/api/" + 
							AuthCredentials.getApiVersion() + "/" + 
							Project.getProjectId() + "/instrument/" + 
							InstrumentConfig.getName() + "/participant/" + 
							AuthCredentials.getParticipantId() + "/period/" + 
							periods.get(i).getPeriodCode() + "/setting";
					String mKey = "";
					boolean mHeader = true;
					ApiManager apiMan = new ApiManager(InstrumentSettingsActivity.this, mKey, mUrl, mHeader);
					apiMan.startRetrieveInAsyncTask(true, new RetrievalCallback(apiMan, sListView));
				}
			}
		});
		List<InstrumentSettings> settings = InstrumentConfig.getInstrumentSettings();
		if ((settings != null) && (settings.size() != 0)) {
			@SuppressWarnings("rawtypes")
			ArrayAdapter adapter = new InstrumentSettingsItemAdapter(this, android.R.layout.simple_list_item_1, settings);
			sListView.setAdapter(adapter);
		} else {
			Toast.makeText(this, R.string.sync_instrument_settings_msg, Toast.LENGTH_SHORT).show();
		}
	}
}