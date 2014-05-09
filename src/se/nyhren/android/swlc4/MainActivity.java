package se.nyhren.android.swlc4;

import se.nyhren.android.swlc4.store.DBX;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

/** RadioGroup.check() fires OnCheckedChangeListener event
 * */
public class MainActivity extends Activity {

	private Button mLinkButton;
	private Button runBtn;
	private Button showLatestBtn;
	private Button mUnlinkButton;
	private Switch scheduleOnOff;
	private RadioGroup rg;
	private DBX dbx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbx = new DBX(getApplicationContext());
		mLinkButton = (Button) findViewById(R.id.btnDbxConnect);
		mUnlinkButton = (Button) findViewById(R.id.btnDbxUnlink);
		runBtn = (Button) findViewById(R.id.btnRun);
		showLatestBtn = (Button) findViewById(R.id.btnShowLatest);
		scheduleOnOff = (Switch) findViewById(R.id.scheduleOnOff);
		rg = (RadioGroup) findViewById(R.id.radioGroup1);
		mLinkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dbx.connect((Activity) MainActivity.this);
			}
		});
		mUnlinkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dbx.unlink();
				setConnect();
			}
		});
		runBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				BootScheduler.run(getApplicationContext());
			}
		});
		showLatestBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this, Dialog.class);
				startActivity(intent);
			}
		});
		scheduleOnOff.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean on = ((Switch) v).isChecked();
				Log.d("TAG", "scheduling " + on);
				SharedPreferences preferences = PreferenceManager
						.getDefaultSharedPreferences(getApplicationContext());
				SharedPreferences.Editor editor = preferences.edit();
				if (on) {
					editor.putBoolean("scheduleOnOff", true);
					// default to first radiobutton if previously unset
					if (-1 == preferences.getInt("pollFreq", -1)) {
						editor.putInt("pollFreq", 0);
					}
				} else {
					editor.putBoolean("scheduleOnOff", false);
					editor.putInt("pollFreq", -1);
				}
				editor.commit();
				setScheduleVC();
			}
		});
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				Log.d("TAG", "checked changed on radio " + checkedId);
				/*
				 * SharedPreferences preferences =
				 * PreferenceManager.getDefaultSharedPreferences
				 * (getApplicationContext()); SharedPreferences.Editor editor =
				 * preferences.edit(); if (checkedId == R.id.radio0) {
				 * editor.putInt("pollFreq", 0); } else if (checkedId ==
				 * R.id.radio1) { editor.putInt("pollFreq", 1); } else if
				 * (checkedId == R.id.radio2) { editor.putInt("pollFreq", 2); }
				 * editor.commit(); setScheduleVC();
				 */
			}
		});

		for (int i = 0; i < rg.getChildCount(); i++) {
			((RadioButton) rg.getChildAt(i)).setOnClickListener(rInRgListener);
		}

		if (dbx.isConnected()) {
			setConnected();
		} else {
			setConnect();
		}
		setScheduleVC(true);
	}

	private OnClickListener rInRgListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			int radioId = ((RadioButton) v).getId();
			Log.d("TAG", "radio clicked " + radioId);
			SharedPreferences preferences = PreferenceManager
					.getDefaultSharedPreferences(getApplicationContext());
			SharedPreferences.Editor editor = preferences.edit();
			if (radioId == R.id.radio0) {
				editor.putInt("pollFreq", 0);
			} else if (radioId == R.id.radio1) {
				editor.putInt("pollFreq", 1);
			} else if (radioId == R.id.radio2) {
				editor.putInt("pollFreq", 2);
			}
			editor.commit();
			setScheduleVC();
		}
	};
	
	private void setScheduleVC() { setScheduleVC(false); }

	private void setScheduleVC(boolean isOnCreate) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(getApplicationContext());
		int pf = prefs.getInt("pollFreq", -1);
		boolean on = prefs.getBoolean("scheduleOnOff", false);
		Log.d("TAG", "prefs " + on + " " + pf);
		// set view
		scheduleOnOff.setChecked(on);
		for (int i = 0; i < rg.getChildCount(); i++) {
			if (i == pf)
				((RadioButton) rg.getChildAt(i)).setChecked(true);
			((RadioButton) rg.getChildAt(i)).setEnabled(on);
		}
		if (-1 == pf) {
			for (int i = 0; i < rg.getChildCount(); i++) {
				((RadioButton) rg.getChildAt(i)).setChecked(false);
			}
		}
		// set control
		if (on && !isOnCreate) {
			// explicitly unschedule any previous ones
			BootScheduler.unschedule(getApplicationContext());
			BootScheduler.schedule(getApplicationContext(), pf);
			Log.d("TAG", "scheduling on " + pf);
		} else if (!isOnCreate) {
			BootScheduler.unschedule(getApplicationContext());
			Log.d("TAG", "unschedule");
		}
	}

	private void setConnected() {
		runBtn.setVisibility(View.VISIBLE);
		mUnlinkButton.setVisibility(View.VISIBLE);
		showLatestBtn.setVisibility(View.VISIBLE);
		mLinkButton.setVisibility(View.GONE);
		scheduleOnOff.setVisibility(View.VISIBLE);
		rg.setVisibility(View.VISIBLE);
	}

	private void setConnect() {
		runBtn.setVisibility(View.GONE);
		mUnlinkButton.setVisibility(View.GONE);
		showLatestBtn.setVisibility(View.GONE);
		mLinkButton.setVisibility(View.VISIBLE);
		scheduleOnOff.setVisibility(View.GONE);
		rg.setVisibility(View.GONE);
	}


	/*	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}*/

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == DBX.REQUEST_LINK_TO_DBX) {
			setConnected();
		} else {
			super.onActivityResult(requestCode, resultCode, data);
		}
	}

}
