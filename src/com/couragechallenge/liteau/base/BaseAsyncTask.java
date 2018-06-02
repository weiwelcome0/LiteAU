package com.couragechallenge.liteau.base;

import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.widget.Toast;

import com.couragechallenge.liteau.bean.RequestParam;
import com.couragechallenge.liteau.bean.RequestResult;
import com.couragechallenge.liteau.itf.ICallBack;
import com.couragechallenge.liteau.itf.IFunction;
import com.couragechallenge.liteau.itf.IMapProcessor;

/**
 * 基础的异步类
 * BaseAsyncTask做了六个部分的扩展：
 * 1,增加定义回调接口ICallBack; 
 * 2,实现了可在异步请求中取消操作; 
 * 3,定义了传入参数为RequestParam结构;
 * 4,可传入IQuery接口，可自定义数据查询实现方式;
 * 6,set方法返回this,可实现链式写法;
 * @param <T>
 * 
 */
public class BaseAsyncTask<T> extends AsyncTask<RequestParam, Object, RequestResult<T>> {
	protected ProgressDialog dialog;
	protected Context context;
	private boolean isShowProgress = true;
	private boolean isCancelable ;
	private ICallBack<RequestResult<T>> callback;
	private IFunction<T> func ;
	private IMapProcessor<T> mapProcessor;

	public BaseAsyncTask(Context context) {
		this(context, true);
	}
	
	public BaseAsyncTask(Context context, boolean isShowProgress) {
		this(context, isShowProgress, null);
	}
	
	public BaseAsyncTask(Context context, ICallBack<RequestResult<T>> callback) {
		this(context, true, callback);
	}
	
	public BaseAsyncTask(Context cxt,IFunction<T> func) {
		this(cxt,func,null);
	}
	
	public BaseAsyncTask(Context cxt,IFunction<T> func,ICallBack<RequestResult<T>> callback) {
		this.context = cxt;
		this.func = func;
		this.callback = callback;
	}
	
	public BaseAsyncTask(Context context, boolean isShowProgress, ICallBack<RequestResult<T>> callback) {
		this.context = context;
		this.isShowProgress = isShowProgress;
		this.callback = callback;
	}

	@Override
	protected void onPreExecute() {
		if(isShowProgress) {
			dialog = new ProgressDialog(context);
			// 设置提示信息
			dialog.setMessage(getProgressTip());
			// 设置是否可以按退回键取消，设置了这个要设置返回监听事件！
			dialog.setCancelable(isCancelable());
			dialog.show();
		}
		
		if(isCancelable()){
			dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					BaseAsyncTask.this.cancel(true);
					dialog.dismiss();
				}
			});
		}
		
		
	}

	@Override
	protected RequestResult<T> doInBackground(RequestParam... params) {
		if(null != func){
			return func.execute();
		}
		RequestResult<Map<String,String>> result = DAOHelper.instance.requestServer(params[0]);
		return RequestResult.convert(result, mapProcessor);
	}
	
	@Override
	protected void onPostExecute(RequestResult<T> result) {
		if(null != dialog) {
			dialog.dismiss();
		}
		if(callback != null){
			callback.call(result);
		}
	}

	@Override
	protected void onCancelled() {
		if(null != dialog) {
			dialog.dismiss();
		}
		Toast.makeText(context, "操作已取消", Toast.LENGTH_LONG).show();
		super.onCancelled();
	}
	
	/**
	 * 获取进度提示
	 * @return
	 */
	protected String getProgressTip() {
		return "正在操作，请稍候..";
	}
	
	public BaseAsyncTask<T> setCallback(ICallBack<RequestResult<T>> callback) {
		this.callback = callback;
		return this;
	}

	public BaseAsyncTask<T> setFunc(IFunction<T> func) {
		this.func = func;
		return this;
	}
	
	public BaseAsyncTask<T> setMapProcessor(IMapProcessor<T> mapProcessor) {
		this.mapProcessor = mapProcessor;
		return this;
	}
	
	public BaseAsyncTask<T> setCancelable(boolean isCancelable){
		this.isCancelable = isCancelable;
		return this;
	}
	
	public boolean isCancelable(){
		return isCancelable;
	}

	public boolean isShowProgress() {
		return isShowProgress;
	}

	public BaseAsyncTask<T> setShowProgress(boolean isShowProgress) {
		this.isShowProgress = isShowProgress;
		return this;
	}
}
