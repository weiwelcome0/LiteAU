package com.couragechallenge.liteau.base;

import java.io.Serializable;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PluginFragment extends Fragment {
	public static String TAG = "PluginFragment";
	public OnFragmentResult onFragResult;
	
	public PluginFragment(){
		this(null);
	}
	
	public PluginFragment(OnFragmentResult onFragResult){
		this.onFragResult = onFragResult;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Logger.d("[onAttach]--Activity"+activity);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.d("[onCreate]--");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Logger.d( "[onCreateView]--");
		return null;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		Logger.d( "[onActivityCreated]--");
	}
	
	@Override
	public void onStart() {
		super.onStart();
		Logger.d( "[onStart]--");
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.d( "[onResume]--");
		
	}
	
	@Override
	public void onPause(){
		super.onPause();
		Logger.d( "[onPause]--");
	}
	
	@Override
	public void onStop(){
		super.onStop();
		Logger.d( "[onStop]--");
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Logger.d( "[onDestroyView]--");
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		Logger.d( "[onDestroy]--");
	}
	
	@Override
	public void onDetach(){
		super.onDetach();
		Logger.d( "[onDetach]--");
	}
	

	public OnFragmentResult getOnFragResult() {
		return onFragResult;
	}

	public void setOnFragResult(OnFragmentResult onFragResult) {
		this.onFragResult = onFragResult;
	}

	/**
	 * Fragment返回数据接口,作用类似onActivityResult
	 *  
	 */
	public interface OnFragmentResult extends Serializable{
		
		public void onFragmentResult(Bundle bundle);
		
	}
}