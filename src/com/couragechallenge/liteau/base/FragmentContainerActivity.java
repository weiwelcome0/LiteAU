package com.couragechallenge.liteau.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.FrameLayout;
import android.widget.Toast;

/**
 * plugin的Activity 此类应是单例的，所有插件均在此activity中存在
 * 
 */
public class FragmentContainerActivity extends FragmentActivity  {
	private final String TAG = "[FragmentContainerActivity]";
	public static final int CONTAINERID = ViewIdGenerator.generateViewId();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FrameLayout layout = new FrameLayout(this);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
		layout.setLayoutParams(params);
		layout.setId(CONTAINERID);
		setContentView(layout);

		onStartPlugin(getIntent());
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		
		onStartPlugin(getIntent());
	}

	// 启动插件
	private void onStartPlugin(Intent intent) {
		Bundle bundle = intent.getExtras();
		String plugName = bundle.getString("plugName");
		
		android.support.v4.app.FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
		PluginFragment fragment = BeanFactory.createBean(plugName);
		BeanFactory.injectBean(fragment, bundle);
		if(null == fragment){
			Toast.makeText(this, "启动插件失败", Toast.LENGTH_LONG).show();
			return ;
		}
//		Bundle bundle = new Bundle();
//		bundle.putSerializable("", "");
//		fragment.setArguments(bundle);
		trans.add(CONTAINERID, fragment);
		trans.addToBackStack(null);
		trans.commit();
	}
	
}
