package com.pilrhealth.locationref.sample;

import java.util.List;
import java.util.Map;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.pilrhealth.android.R;
import com.pilrhealth.locationref.sample.login.CustomDialogBox;
import com.pilrhealth.pilriolib.Epoch;
import com.pilrhealth.pilriolib.InstrumentConfig;
import com.pilrhealth.pilriolib.InstrumentSettings;
import com.pilrhealth.pilriolib.Period;
import com.pilrhealth.pilriolib.Project;
import com.pilrhealth.pilriolib.api.ApiManager;
import com.pilrhealth.pilriolib.api.AuthCredentials;
import com.pilrhealth.pilriolib.util.AsyncTaskCallback;

public class RetrievalCallback implements AsyncTaskCallback {
	@SuppressWarnings("unused")
	private ApiManager mApiMan;
	private View mView;

	public RetrievalCallback(ApiManager api, View v) {
		mApiMan = api;
		mView = v;
	}

	public void onStart() {
		// TODO Auto-generated method stub
		Log.d("RetrievalCallback", "onStart ");

	}

	public void onUpdate(Map<?, ?> something) {
		Log.d("RetrievalCallback", "onUpdate ");
		// TODO Auto-generated method stub

	}

	/**
	 * Actions to be done after AsyncTask is finished.
	 * 
	 * @param Map
	 */
	@SuppressWarnings({ "unused" })
	public void onFinish(Map<?, ?> map) {
		if (map.containsValue("login")) {
			// Update view with login information
			SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mView.getContext());
			Editor edit = sp.edit();
			if (map.containsValue("retrieveUnauthorized")) {
				Toast.makeText(mView.getContext(), R.string.unauthorized_msg, Toast.LENGTH_SHORT).show();
			} else {
				if (map.containsValue("retrieveResponseError")) {
					Toast.makeText(mView.getContext(), R.string.response_error_msg, Toast.LENGTH_SHORT).show();
				} else {
					if (map.containsValue("logged")) {
						CustomDialogBox dialog = new CustomDialogBox(mView.getContext(), R.layout.dialog_successful_login, R.string.successful_login_label);
						UserSettingActivity.sPLogin.setSummary(mView.getContext().getString(R.string.successful_login_txt3));
						UserSettingActivity.sPPartInfo.setSummary(mView.getContext().getString(R.string.successful_login_txt4));
						// Persist login information
						edit.putString(UserSettingActivity.PREF_LOGIN_SUMM, mView.getContext().getString(R.string.successful_login_txt3));
						edit.putString(UserSettingActivity.PREF_PART_INFO_SUMM, mView.getContext().getString(R.string.successful_login_txt4));
						edit.putString(UserSettingActivity.PREF_ACCESS_CODE, AuthCredentials.getAccessCode());
						edit.putString(UserSettingActivity.PREF_URI, AuthCredentials.getUri());
						edit.putBoolean(UserSettingActivity.PREF_IS_LOGGED, true);
						edit.commit();
					} else {
						if (map.containsValue("notLogged")) {
							// Display dialog box informing problem with login
							CustomDialogBox dialog = new CustomDialogBox(mView.getContext(), R.layout.dialog_failed_login, R.string.failed_login_label);
							edit.putBoolean(UserSettingActivity.PREF_IS_LOGGED, false);
							edit.commit();
						}
					}
				}
			}
		}

		if (map.containsValue("partInfo")) {
			if (map.containsValue("retrieveUnauthorized")) {
				Toast.makeText(mView.getContext(), R.string.unauthorized_msg, Toast.LENGTH_SHORT).show();
			} else {
				if (map.containsValue("retrieveResponseError")) {
					Toast.makeText(mView.getContext(), R.string.response_error_msg, Toast.LENGTH_SHORT).show();
				} else {
					if (AuthCredentials.getParticipantId() != null) {
						// Fetch periods information
						String mUrl = AuthCredentials.getUrl() + "/api/" + AuthCredentials.getApiVersion() + "/" + Project.getProjectId() + "/instrument/"
								+ InstrumentConfig.getName() + "/participant/" + AuthCredentials.getParticipantId() + "/period/";
						String mKey = "";
						boolean mHeader = true;
						ApiManager apiMan = new ApiManager(mView.getContext(), mKey, mUrl, mHeader);
						apiMan.startRetrieveInAsyncTask(true, new RetrievalCallback(apiMan, mView));

					} else {
						Toast.makeText(mView.getContext(), R.string.problem_participant, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}

		if (map.containsValue("upload")) {
			if (map.containsValue("uploadUnauthorized")) {
				Toast.makeText(mView.getContext(), R.string.unauthorized_msg, Toast.LENGTH_SHORT).show();
			} else {
				if (map.containsValue("uploadResponseError")) {
					Toast.makeText(mView.getContext(), R.string.response_error_msg, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(mView.getContext(), R.string.upload_msg, Toast.LENGTH_SHORT).show();
				}
			}

		}

		if (map.containsValue("uploadFailed")) {
			Toast.makeText(mView.getContext(), R.string.upload_failed_msg, Toast.LENGTH_SHORT).show();
		}

		if (map.containsValue("retrievePeriods")) {
			if (map.containsValue("retrieveUnauthorized")) {
				Toast.makeText(mView.getContext(), R.string.unauthorized_msg, Toast.LENGTH_SHORT).show();
			} else {
				if (map.containsValue("retrieveResponseError")) {
					Toast.makeText(mView.getContext(), R.string.response_error_msg, Toast.LENGTH_SHORT).show();
				} else {
					List<Period> periods = Project.getPeriods();
					if (periods.size() != 0) {
						// Persist periods information
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mView.getContext());
						Editor edit = sp.edit();
						String periodsString = "{\"" + UserSettingActivity.PREF_TAG_PERIOD_ARRAY + "\":[";
						for (int i = 0; i < periods.size(); i++) {
							Period period = periods.get(i);
							periodsString += "{\"" + UserSettingActivity.PREF_PERIODS_CODE + "\":\"" + period.getPeriodCode() + "\",\""
									+ UserSettingActivity.PREF_PERIODS_NAME + "\":\"" + period.getPeriodName() + "\",\""
									+ UserSettingActivity.PREF_TAG_EPOCH_ARRAY + "\":[";
							List<Epoch> epochsList = period.getEpoch();
							// Persist epochs Information
							for (int j = 0; j < epochsList.size(); j++) {
								Epoch epoch = epochsList.get(j);
								periodsString += "{\"" + UserSettingActivity.PREF_EPOCHS_CODE + "\":\"" + epoch.getEpochCode() + "\",\""
										+ UserSettingActivity.PREF_EPOCHS_NAME + "\":\"" + epoch.getEpochName() + "\",\""
										+ UserSettingActivity.PREF_EPOCHS_ST_DT + "\":\"" + epoch.getEpochStartDate() + "\",\""
										+ UserSettingActivity.PREF_EPOCHS_END_DT + "\":\"" + epoch.getEpochEndDate() + "\"}";
								if (j + 1 < epochsList.size()) {
									periodsString += ",";
								} else {
									periodsString += "]}";
								}
							}
							if (i + 1 < periods.size()) {
								periodsString += ",";
							} else {
								periodsString += "]}";
							}
							// Fetch Instrument Settings
							String mKey = "";
							boolean mHeader = true;
							String mUrl = AuthCredentials.getUrl() + "/api/" + AuthCredentials.getApiVersion() + "/" + Project.getProjectId() + "/instrument/"
									+ InstrumentConfig.getName() + "/participant/" + AuthCredentials.getParticipantId() + "/period/" + period.getPeriodCode()
									+ "/setting";
							ApiManager apiMan = new ApiManager(mView.getContext(), mKey, mUrl, mHeader);
							apiMan.startRetrieveInAsyncTask(true, new RetrievalCallback(apiMan, mView));
						}
						edit.putString(UserSettingActivity.PREF_PERIODS, periodsString);
						edit.commit();
						// Open Periods View
						try {
							ArrayAdapter<?> adapter = new PeriodsItemAdapter(mView.getContext(), android.R.layout.simple_list_item_1, periods);
							ProjectPeriodActivity.sListView.setAdapter(adapter);
							Toast.makeText(mView.getContext(), R.string.sync_completed, Toast.LENGTH_SHORT).show();
						} catch (NullPointerException ex) {
							Log.d("Retrieval Callback", "Tried to open Periods View :" + ex);
						}

					} else {
						Toast.makeText(mView.getContext(), R.string.no_project_period_msg, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}

		if (map.containsValue("retrieveInstrumentSettings")) {
			if (map.containsValue("retrieveUnauthorized")) {
				Toast.makeText(mView.getContext(), R.string.unauthorized_msg, Toast.LENGTH_SHORT).show();
			} else {
				if (map.containsValue("retrieveResponseError")) {
					Toast.makeText(mView.getContext(), R.string.response_error_msg, Toast.LENGTH_SHORT).show();
				} else {
					List<InstrumentSettings> iSettings = InstrumentConfig.getInstrumentSettings();
					if (iSettings.size() != 0) {
						// Persist Instrument Settings information
						SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mView.getContext());
						Editor edit = sp.edit();
						String settingsString = "{\"" + UserSettingActivity.PREF_TAG_SETTING_ARRAY + "\":[";
						for (int i = 0; i < iSettings.size(); i++) {
							InstrumentSettings setting = iSettings.get(i);
							settingsString += "{\"" + UserSettingActivity.PREF_SETTING_CODE + "\":\"" + setting.getSettingCode() + "\",\""
									+ UserSettingActivity.PREF_SETTING_NAME + "\":\"" + setting.getSettingName() + "\",\""
									+ UserSettingActivity.PREF_SETTING_DESC + "\":\"" + setting.getSettingDesc() + "\",\""
									+ UserSettingActivity.PREF_SETTING_TYPE + "\":\"" + setting.getSettingType() + "\",\""
									+ UserSettingActivity.PREF_SETTING_VALUE + "\":\"" + setting.getSettingValue() + "\",\""
									+ UserSettingActivity.PREF_PERIOD_CODE + "\":\"" + setting.getPeriodCode() + "\",\"" + UserSettingActivity.PREF_EPOCH_CODE
									+ "\":\"" + setting.getEpochCode() + "\"}";
							if (i + 1 < iSettings.size()) {
								settingsString += ",";
							} else {
								settingsString += "]}";
							}
						}
						edit.putString(UserSettingActivity.PREF_INSTRUMENT_SETTINGS, settingsString);
						// Update View with participant information
						String messagePart = ((mView.getContext().getString(R.string.participant_label)) + AuthCredentials.getParticipantId() + ". "
								+ (mView.getContext().getString(R.string.project_label)) + " " + Project.getProjectId() + " "
								+ (mView.getContext().getString(R.string.project_name_label)) + " " + Project.getProjectName() + ".");
						UserSettingActivity.sPPartInfo.setSummary(messagePart);
						// Persist participant information
						edit.putString(UserSettingActivity.PREF_PART_INFO_SUMM, messagePart);
						edit.putString(UserSettingActivity.PREF_PART_ID, AuthCredentials.getParticipantId());
						edit.putString(UserSettingActivity.PREF_PROJECT_CODE, Project.getProjectId());
						edit.putString(UserSettingActivity.PREF_PROJECT_NAME, Project.getProjectName());
						edit.commit();
						// Open Instrument Setting View
						try {
							ArrayAdapter<?> adapter = new InstrumentSettingsItemAdapter(mView.getContext(), android.R.layout.simple_list_item_1, iSettings);
							InstrumentSettingsActivity.sListView.setAdapter(adapter);
							Toast.makeText(mView.getContext(), R.string.sync_completed, Toast.LENGTH_SHORT).show();
						} catch (NullPointerException ex) {
							Log.d("Retrieval Callback", "Tried to open Periods View :" + ex);
						}
					} else {
						Toast.makeText(mView.getContext(), R.string.no_instrument_settings_msg, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}
}