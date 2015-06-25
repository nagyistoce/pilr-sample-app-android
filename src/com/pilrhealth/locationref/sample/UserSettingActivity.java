package com.pilrhealth.locationref.sample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.pilrhealth.android.R;
import com.pilrhealth.locationref.sample.location.LastLocation;
import com.pilrhealth.locationref.sample.location.LocationDataset;
import com.pilrhealth.locationref.sample.login.CustomDialogBox;
import com.pilrhealth.pilriolib.Epoch;
import com.pilrhealth.pilriolib.InstrumentConfig;
import com.pilrhealth.pilriolib.InstrumentSettings;
import com.pilrhealth.pilriolib.Period;
import com.pilrhealth.pilriolib.Project;
import com.pilrhealth.pilriolib.api.ApiManager;
import com.pilrhealth.pilriolib.api.AuthCredentials;
import com.pilrhealth.pilriolib.data.Dataset;
import com.pilrhealth.pilriolib.data.DsSimpleSaveDB;

public class UserSettingActivity extends PreferenceActivity {

	private String mUrl, mKey, mUri, mUrlBase, mAccessCode, mPartId,
			mProjectCode, mProjectName, mInstrumentSettings, mPeriods;
	private static boolean sFlagStartStop = true;
	private boolean mHeader = false, mFetchSettings = false;
	private SharedPreferences mSp;
	public static Preference sPLogin, sPLogout, sPPartInfo, sPStartStop,
			sPeriod, sSettings, sPUpload, sPUploadLastLoc;
	public static boolean sGPSProvider = true, sNetworkProvider = true;
	public static long sMinTime = 1000;
	public static float sMinDistance = 1;
	// Shared Preferences
	public static final String PREF_LOGIN_SUMM = "login_summ",
			PREF_PART_INFO_SUMM = "part_info_summ",
			PREF_ACCESS_CODE = "access_code",
			PREF_URI = "uri",
			PREF_URL_BASE = "url",
			PREF_PART_ID = "part_id",
			PREF_PROJECT_CODE = "project_code",
			PREF_PROJECT_NAME = "project_name",
			PREF_IS_LOGGED = "is_logged",
			PREF_SETTING_GPS = "pilrhealth:location_ref_app:gps_provider",
			PREF_SETTING_NETWORK = "pilrhealth:location_ref_app:network_provider",
			PREF_SETTING_MIN_TIME = "pilrhealth:location_ref_app:min_time",
			PREF_SETTING_MIN_DIST = "pilrhealth:location_ref_app:min_dist",
			PREF_SETTING_NAME = "name", PREF_SETTING_CODE = "code",
			PREF_PERIOD_CODE = "period_code", PREF_EPOCH_CODE = "epoch_code",
			PREF_SETTING_DESC = "description", PREF_SETTING_TYPE = "type",
			PREF_SETTING_VALUE = "value",
			PREF_INSTRUMENT_SETTINGS = "settings", PREF_PERIODS = "periods",
			PREF_PERIODS_NAME = "name", PREF_PERIODS_CODE = "code",
			PREF_EPOCHS_ST_DT = "start_date", PREF_EPOCHS_NAME = "name",
			PREF_EPOCHS_CODE = "code", PREF_EPOCHS_END_DT = "end_date",
			PREF_TAG_PERIOD_ARRAY = "periodArray",
			PREF_TAG_EPOCH_ARRAY = "epochArray",
			PREF_TAG_SETTING_ARRAY = "settingArray";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.user_settings);
		sPLogin = (Preference) findPreference(getString(R.string.login_key));
		sPLogout = (Preference) findPreference(getString(R.string.logout_key));
		sPPartInfo = (Preference) findPreference(getString(R.string.part_info_key));
		sPeriod = (Preference) findPreference(getString(R.string.period_key));
		sSettings = (Preference) findPreference(getString(R.string.instrument_sett_key));
		sPStartStop = (Preference) findPreference(getString(R.string.start_key));
		sPUpload = (Preference) findPreference(getString(R.string.upload_key));
		sPUploadLastLoc = (Preference) findPreference(getString(R.string.upload_sample_key));
		mSp = PreferenceManager.getDefaultSharedPreferences(this);
		
		// Set Url
		mUrlBase = "https://qa.pilrhealth.com";
		Editor edit = mSp.edit();
		edit.putString(PREF_URL_BASE, mUrlBase);
		edit.commit();
		//LIB INITIALIZATION
		AuthCredentials.setUrl(mUrlBase);
		// Set Instrument name
		//LIB INITIALIZATION
		InstrumentConfig.setName("location_ref_app");

		if ((mSp.getString(PREF_ACCESS_CODE, null) != null)) {
			loadConfiguration();
		}

		// VALIDATE LOGIN
		sPLogin.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				try {
					if (mSp.getString(PREF_ACCESS_CODE, null) == null) {
						// Shows dialog box to enter user and code
						final Dialog loginDial = new Dialog(
								UserSettingActivity.this);
						loginDial.setContentView(R.layout.dialog_preference);
						loginDial.setTitle(R.string.login_label);
						final EditText loginCode = (EditText) loginDial
								.findViewById(R.id.loginCode);
						final EditText code = (EditText) loginDial
								.findViewById(R.id.code);
						Button logBt = (Button) loginDial
								.findViewById(R.id.loginButton);
						Button cancelBt = (Button) loginDial
								.findViewById(R.id.cancelButton);

						logBt.setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								// Perform Authentication
								mUrl = (AuthCredentials.getUrl() + AuthCredentials
										.getWhereClauseLogin());
								mKey = loginCode.getText().toString()
										+ code.getText().toString();
								setHeader(false);
								if (!(mKey.trim().equals(""))) {
									ApiManager apiMan = new ApiManager(
											UserSettingActivity.this, mKey,
											mUrl, isHeader());
									apiMan.startRetrieveInAsyncTask(true,
											new RetrievalCallback(apiMan,
													getListView()));
								} else {
									@SuppressWarnings("unused")
									CustomDialogBox dialog = new CustomDialogBox(
											UserSettingActivity.this,
											R.layout.dialog_failed_login,
											R.string.failed_login_label);
								}
								loginDial.dismiss();
							}
						});

						cancelBt.setOnClickListener(new View.OnClickListener() {
							public void onClick(View view) {
								loginDial.dismiss();
							}
						});
						loginDial.show();
					} else {
						Toast.makeText(UserSettingActivity.this,
								R.string.already_logged_msg, Toast.LENGTH_SHORT)
								.show();
					}
				} catch (Exception ex) {
					Log.d("UserSettingActivity", "Login:" + ex);
					return false;
				}
				return true;
			}
		});

		// Logout service
		sPLogout.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				// Clean Shared preferences if there is user logged in
				if (mSp.getString(PREF_ACCESS_CODE, null) != null) {
					unloadConfiguration();
				} else {
					Toast.makeText(UserSettingActivity.this,
							R.string.not_logged, Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

		// FETCH PARTICIPANT INFO
		sPPartInfo
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						if (mSp.getString(PREF_URI, null) != null) {
							if (mSp.getString(PREF_PART_ID, null) == null) {
								try {
									mUrl = (AuthCredentials.getUrl() + (mSp
											.getString(PREF_URI, null)));
									mKey = "";
									setHeader(true);
									ApiManager apiMan = new ApiManager(
											UserSettingActivity.this, mKey,
											mUrl, isHeader());
									apiMan.startRetrieveInAsyncTask(true,
											new RetrievalCallback(apiMan,
													getListView()));

								} catch (Exception ex) {
									Log.d("UserSettingActivity", "Login:" + ex);
									return false;
								}
							} else {
								Toast.makeText(UserSettingActivity.this,
										R.string.already_have_part_msg,
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(UserSettingActivity.this,
									R.string.not_logged_msg, Toast.LENGTH_SHORT)
									.show();
						}
						return true;
					}
				});

		// FETH PROJECT PERIODS INFO
		sPeriod.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				if ((mSp.getString(PREF_URI, null) != null)
						&& (mSp.getString(PREF_PART_ID, null) != null)) {
					try {
						startActivity(new Intent(
								"android.intent.action.LOCATIONPERIOD"));
					} catch (Exception ex) {
						Log.d("UserSettingActivity", "Periods:" + ex);
						return false;
					}
				} else {
					Toast.makeText(UserSettingActivity.this,
							R.string.not_logged_part_msg, Toast.LENGTH_SHORT)
							.show();
				}
				return true;
			}
		});

		// FETCH INSTRUMENT SETTINGS INFO
		sSettings.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				if ((mSp.getString(PREF_URI, null) != null)
						&& (mSp.getString(PREF_PART_ID, null) != null)
						&& (Project.getPeriods() != null)) {
					try {
						startActivity(new Intent(
								"android.intent.action.LOCATIONINSTRUMENTSET"));
					} catch (Exception ex) {
						Log.d("UserSettingActivity", "Settings:" + ex);
						return false;
					}
				} else {
					Toast.makeText(UserSettingActivity.this,
							R.string.not_logged_part_per_msg,
							Toast.LENGTH_SHORT).show();
				}
				return true;
			}
		});

		// Start and stop recording data
		sPStartStop
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						if ((mSp.getString(PREF_URI, null) != null)
								&& (mSp.getString(PREF_PART_ID, null) != null)
								&& (Project.getPeriods() != null)) {
							if (sFlagStartStop) {
								// 'Start' link in enabled
								try {
									// Show dialogBox with sync advise
									AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
											UserSettingActivity.this);
									alertDialogBuilder
											.setTitle(R.string.sync_msg_title);
									alertDialogBuilder
											.setMessage(R.string.sync_msg);
									alertDialogBuilder
											.setNegativeButton(
													R.string.go_start,
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int id) {
															try {
																mFetchSettings = fetchCurrentSettings();
															} catch (ParseException e) {
																Log.d("UserSettingActivity",
																		"Start-fetchCurrentSettings :"
																				+ e);
																e.printStackTrace();
															}
															if (mFetchSettings) {
																sFlagStartStop = false;
																// Start
																// collecting
																// location
																startService(new Intent(
																		UserSettingActivity.this,
																		com.pilrhealth.locationref.sample.location.LocationService.class));
																// Update link
																// to Stop
																sPStartStop
																		.setSummary(R.string.stop_summ);
																sPStartStop
																		.setTitle(R.string.stop_title);
																Toast.makeText(
																		UserSettingActivity.this,
																		getString(R.string.record_start_msg),
																		Toast.LENGTH_SHORT)
																		.show();
															} else {
																Toast.makeText(
																		UserSettingActivity.this,
																		getString(R.string.period_closed_msg),
																		Toast.LENGTH_SHORT)
																		.show();
															}
														}
													});
									alertDialogBuilder.setPositiveButton(
											R.string.go_sync, null);
									alertDialogBuilder.show();
								} catch (Exception ex) {
									Log.d("UserSettingActivity", "Start:" + ex);
									return false;
								}
							} else {
								// 'Stop' link is enabled
								try {
									sFlagStartStop = true;
									// Stop collecting location and store data
									stopService(new Intent(
											UserSettingActivity.this,
											com.pilrhealth.locationref.sample.location.LocationService.class));
									DsSimpleSaveDB db = new DsSimpleSaveDB(
											UserSettingActivity.this);
									db.open();
									String locDataset = (new LocationDataset(
											UserSettingActivity.this)
											.getStreamId()).toString();
									db.getCursor(locDataset, 1, null);
									db.close();
									// Update link to 'Start'
									sPStartStop.setSummary(R.string.start_summ);
									sPStartStop.setTitle(R.string.start_title);
									Toast.makeText(
											UserSettingActivity.this,
											getString(R.string.record_stop_msg),
											Toast.LENGTH_SHORT).show();
								} catch (Exception ex) {
									Log.d("UserSettingActivity", "Stop:" + ex);
									return false;
								}
							}
						} else {
							Toast.makeText(UserSettingActivity.this,
									R.string.not_logged_part_msg,
									Toast.LENGTH_SHORT).show();
						}
						return true;
					}

				});

		// UPLOAD DATA - LIST
		sPUpload.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				if ((mSp.getString(PREF_URI, null) != null)
						&& (mSp.getString(PREF_PART_ID, null) != null)) {
					try {
						List<Dataset> streams = getDataStreams();
						ApiManager apiMan = new ApiManager(
								UserSettingActivity.this);
						apiMan.startUploadInAsyncTask(true, null, null,
								new RetrievalCallback(apiMan, getListView()),
								streams);
					} catch (Exception ex) {
						Log.d("UserSettingActivity", "Upload:" + ex);
						return false;
					}
				} else {
					Toast.makeText(UserSettingActivity.this,
							R.string.not_logged_part_msg, Toast.LENGTH_SHORT)
							.show();
				}
				return true;
			}
		});

		// UPLOAD DATA - SINGLE LINE
		sPUploadLastLoc
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						if ((mSp.getString(PREF_URI, null) != null)
								&& (mSp.getString(PREF_PART_ID, null) != null)) {
							try {
								mFetchSettings = fetchCurrentSettings();
							} catch (ParseException e) {
								Log.d("UserSettingActivity",
										"Start-fetchCurrentSettings :" + e);
								e.printStackTrace();
							}
							if (mFetchSettings) {
								try {
									List<Dataset> streams = getDataStreams();
									LastLocation loc = new LastLocation();
									loc.getLastLocation(
											UserSettingActivity.this,
											getListView(), streams);
								} catch (Exception ex) {
									Log.d("UserSettingActivity",
											"Upload Last Location:" + ex);
									return false;
								}
							} else {
								Toast.makeText(UserSettingActivity.this,
										getString(R.string.period_closed_msg),
										Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(UserSettingActivity.this,
									R.string.not_logged_part_msg,
									Toast.LENGTH_SHORT).show();
						}
						return true;
					}
				});
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	public boolean isHeader() {
		return mHeader;
	}

	public void setHeader(boolean header) {
		this.mHeader = header;
	}

	public void onResume() {
		super.onResume();
		if ((mSp.getBoolean(PREF_IS_LOGGED, false)) == true) {
			loadConfiguration();
		}
	}

	// Fetch settings values valid for the active epoch
	public boolean fetchCurrentSettings() throws ParseException {
		List<Period> periods = Project.getPeriods();
		// Get current date
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date currentDt = df.parse(df.format(new Date().getTime()));

		for (int i = 0; i < periods.size(); i++) {
			Period period = periods.get(i);
			List<InstrumentSettings> settingsList = InstrumentConfig
					.getInstrumentSettings();
			List<Epoch> epochsList = period.getEpoch();
			for (int j = 0; j < epochsList.size(); j++) {
				Epoch epoch = epochsList.get(j);
				if ((epoch.getEpochStartDate()) != null) {
					Date epochStDt = df.parse(epoch.getEpochStartDate());
					Date epochEndDt = df.parse(epoch.getEpochEndDate());
					// Test if epoch is Active
					if (currentDt.after(epochStDt)
							&& currentDt.before(epochEndDt)) {
						for (int k = 0; k < settingsList.size(); k++) {
							InstrumentSettings setting = settingsList.get(k);
							// Test if setting belongs to the current epoch
							if (epoch.getEpochCode().equals(
									setting.getEpochCode())) {
								if (setting.getSettingCode().equals(
										PREF_SETTING_GPS)) {
									sGPSProvider = (setting.getSettingValue()
											.equals("true")) ? true : false;
								}
								if (setting.getSettingCode().equals(
										PREF_SETTING_NETWORK)) {
									sNetworkProvider = (setting
											.getSettingValue().equals("true")) ? true
											: false;
								}
								if (setting.getSettingCode().equals(
										PREF_SETTING_MIN_TIME)) {
									sMinTime = Long.parseLong(setting
											.getSettingValue());
								}
								if (setting.getSettingCode().equals(
										PREF_SETTING_MIN_DIST)) {
									sMinDistance = Float.parseFloat(setting
											.getSettingValue());
								}
								return true;
							}
						}
					}

				}
			}
		}
		return false;
	}

	/**
	 * Add all the data streams that your application uses. This is required to
	 * automate data uploading to PILR server.
	 * 
	 * @return
	 */
	public List<Dataset> getDataStreams() {
		List<Dataset> streams = new ArrayList<Dataset>();
		streams.add(new LocationDataset(this));

		return streams;
	}

	// Load Shared Preferences
	public void loadConfiguration() {
		String loginSumm = mSp.getString(PREF_LOGIN_SUMM,
				getString(R.string.login_summ));
		String partInfoSumm = mSp.getString(PREF_PART_INFO_SUMM,
				getString(R.string.part_info_summ));

		sPLogin.setSummary(loginSumm);
		sPPartInfo.setSummary(partInfoSumm);
		mAccessCode = mSp.getString(PREF_ACCESS_CODE, null);
		mUri = mSp.getString(PREF_URI, null);
		mPartId = mSp.getString(PREF_PART_ID, null);
		mProjectCode = mSp.getString(PREF_PROJECT_CODE, null);
		mProjectName = mSp.getString(PREF_PROJECT_NAME, null);
		mInstrumentSettings = mSp.getString(PREF_INSTRUMENT_SETTINGS, null);
		mPeriods = mSp.getString(PREF_PERIODS, null);

		if (sFlagStartStop == false) {
			sPStartStop.setSummary(R.string.stop_summ);
			sPStartStop.setTitle(R.string.stop_title);
		}

		AuthCredentials.setAccessCode(mAccessCode);
		AuthCredentials.setUri(mUri);
		AuthCredentials.setParticipantId(mPartId);
		Project.setProjectId(mProjectCode);
		Project.setProjectName(mProjectName);
		if (mInstrumentSettings != null) {
			try {
				JSONObject jObject = new JSONObject(mInstrumentSettings);
				JSONArray settingArray = jObject
						.getJSONArray(PREF_TAG_SETTING_ARRAY);
				List<InstrumentSettings> settings = new ArrayList<InstrumentSettings>();
				for (int i = 0; i < settingArray.length(); i++) {
					JSONObject object = settingArray.getJSONObject(i);
					InstrumentSettings setting = new InstrumentSettings();
					setting.fromJSONObject(object);
					settings.add(setting);
				}
				InstrumentConfig.setInstrumentSettings(settings);
			} catch (JSONException e) {
				Log.d("UserSettingActivity",
						"loadConfiguration - Loading Settings :" + e);
				e.printStackTrace();
			}
		}
		if (mPeriods != null) {
			try {
				JSONObject jObject = new JSONObject(mPeriods);
				JSONArray periodArray = jObject
						.getJSONArray(PREF_TAG_PERIOD_ARRAY);
				List<Period> periods = new ArrayList<Period>();
				for (int i = 0; i < periodArray.length(); i++) {
					JSONObject object = periodArray.getJSONObject(i);
					Period period = new Period();
					period.fromJSONObject(object);
					periods.add(period);

					List<Epoch> epochs = new ArrayList<Epoch>();
					JSONArray epochArray = object
							.getJSONArray(PREF_TAG_EPOCH_ARRAY);
					for (int j = 0; j < epochArray.length(); j++) {
						Epoch epoch = new Epoch();
						JSONObject epochObj = epochArray.getJSONObject(j);
						epoch.fromJSONObject(epochObj);
						epochs.add(epoch);
					}
					period.setEpochs(epochs);
				}
				Project.setPeriods(periods);
			} catch (JSONException e) {
				Log.d("UserSettingActivity",
						"loadConfiguration - Loading Periods :" + e);
				e.printStackTrace();
			}
		}
	}

	public void unloadConfiguration() {
		AuthCredentials.setAccessCode(null);
		AuthCredentials.setUri(null);
		AuthCredentials.setParticipantId(null);
		Project.setProjectId(null);
		Project.setProjectName(null);
		Project.setPeriods(null);
		InstrumentConfig.setInstrumentSettings(null);
		ProjectPeriodActivity.sListView = null;
		InstrumentSettingsActivity.sListView = null;
		mAccessCode = null;
		mUri = null;
		mPartId = null;
		mProjectCode = null;
		mProjectName = null;
		mInstrumentSettings = null;
		mPeriods = null;
		sPLogin.setSummary(getString(R.string.login_summ));
		sPPartInfo.setSummary(getString(R.string.part_info_summ));
		Editor edit = mSp.edit();
		edit.putString(PREF_LOGIN_SUMM, getString(R.string.login_summ));
		edit.putString(PREF_PART_INFO_SUMM, getString(R.string.part_info_summ));
		edit.putString(PREF_ACCESS_CODE, AuthCredentials.getAccessCode());
		edit.putString(PREF_URI, AuthCredentials.getUri());
		edit.putString(PREF_PART_ID, null);
		edit.putBoolean(PREF_IS_LOGGED, false);
		edit.putString(PREF_INSTRUMENT_SETTINGS, null);
		edit.putString(PREF_PERIODS, null);
		edit.putString(PREF_SETTING_CODE, null);
		edit.putString(PREF_SETTING_DESC, null);
		edit.putString(PREF_SETTING_NAME, null);
		edit.putString(PREF_SETTING_TYPE, null);
		edit.putString(PREF_SETTING_VALUE, null);
		edit.putString(PREF_PERIOD_CODE, null);
		edit.putString(PREF_EPOCH_CODE, null);
		edit.commit();
	}
}
