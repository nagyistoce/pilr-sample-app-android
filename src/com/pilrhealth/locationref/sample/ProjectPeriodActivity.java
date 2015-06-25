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
import com.pilrhealth.pilriolib.Period;
import com.pilrhealth.pilriolib.Project;
import com.pilrhealth.pilriolib.api.ApiManager;
import com.pilrhealth.pilriolib.api.AuthCredentials;

public class ProjectPeriodActivity extends Activity {
	private TextView mSynkLink;
	public static ListView sListView;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.project_period);

		mSynkLink = (TextView) findViewById(R.id.syncPerTitle);
		sListView = (ListView) findViewById(R.id.perList);

		mSynkLink.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// Call api to get data
				String mUrl = AuthCredentials.getUrl() + "/api/" + 
						AuthCredentials.getApiVersion() + "/" + 
						Project.getProjectId() + "/instrument/" + 
						InstrumentConfig.getName() + "/participant/" + 
						AuthCredentials.getParticipantId() + "/period/";
				String mKey = "";
				boolean mHeader = true;
				ApiManager apiMan = new ApiManager(ProjectPeriodActivity.this, mKey, mUrl, mHeader);
				apiMan.startRetrieveInAsyncTask(true, new RetrievalCallback(apiMan, sListView));
			}
		});

		List<Period> periods = Project.getPeriods();
		if ((periods != null) && (periods.size() != 0)) {
			ArrayAdapter<?> adapter = new PeriodsItemAdapter(this, android.R.layout.simple_list_item_1, periods);
			sListView.setAdapter(adapter);
		} else {
			Toast.makeText(getApplicationContext(), R.string.sync_project_period_msg, Toast.LENGTH_SHORT).show();
		}
	}
}