package com.couragechallenge.liteau;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class TestActivity extends Activity implements
		 OnClickListener {
	Button btnTest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);
		btnTest = (Button) findViewById(R.id.test_btnTest);
		btnTest.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.test_btnTest:
			onBtnTest();
			break;

		default:
			break;
		}
		
	}

	private void onBtnTest() {
		// TODO Auto-generated method stub
		String msg="测试结果";
		Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
		
	}

}
