package com.pilrhealth.locationref.sample.login;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;

import com.pilrhealth.android.R;

public class CustomDialogBox {

	public CustomDialogBox(Context context, int layout, int title) {
		final Dialog dialog = new Dialog(context);
		dialog.setContentView(layout);
		dialog.setTitle(title);
		Button okBt = (Button) dialog.findViewById(R.id.okButton);

		okBt.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}
}