package com.easyparking;

import android.content.Intent;

public class DetailActivity extends BaseActivity {
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		//my spots got returns from add process.
		//showErrorMessage("is this happen in myspotActivity"+resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
	}
}
