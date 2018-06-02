package com.couragechallenge.liteau.base;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.couragechallenge.liteau.bean.RequestParam;
import com.couragechallenge.liteau.bean.RequestResult;
import com.couragechallenge.liteau.itf.IStatus;

public class UpdateService extends Service {
	
	Object upload = new Object();
	AtomicBoolean isWait = new AtomicBoolean(false);
	public static final UpdateService instance = new UpdateService();


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	public void uploadMedia(){
		if(isWait.get()){
			synchronized (upload) {
				upload.notify();
			}
			return;
		}
		
		Runnable r = new Runnable() {			
			@Override
			public void run() {
				try{
					Logger.i("--uploadMedia run:"+DateUtil.getNow()+",threadName"+Thread.currentThread().getName());
					List<Map<String,String>> list = getUploadList();
					int size = null == list?0:list.size();
					if(size <= 0){
						synchronized(upload){
							isWait.compareAndSet(false, true);
							upload.wait();
						}
						return ;
					}
					
					RequestParam rp = new RequestParam();
					rp.functionName = RequestParam.FUNC_UPLOAD;
					
					for(Map<String,String> mediaMap : list){
						rp.paramMap = mediaMap;
						if(isFileNotFound(mediaMap.get("spath"))){
							mediaMap.put("istatus", ""+IStatus.STATUS_ERROR);
						}else{
							RequestResult<Map<String,String>> rr = DAOHelper.instance.requestServer(rp);
							if(rr.getRetCode() == RequestResult.ERROR_NET){
								return;
							}else if(rr.getRetCode() == RequestResult.OK){
								mediaMap.put("istatus", ""+IStatus.STATUS_OK);
							}else{
								mediaMap.put("istatus", ""+IStatus.STATUS_FAIL);
							}
						}
						
						
						String sql = " update bill_tmedia set istatus=? where sid_bill=? and scode=?";
						
						DBHelper.update(sql, new String[]{mediaMap.get("istatus"),mediaMap.get("sid_bill"),mediaMap.get("scode")});
						
					}
					
				}catch(Exception e){
					Logger.e("--uploadMedia, run exception;"+e);
				}				
			}

			private boolean isFileNotFound(String path) {				
				return !new File(path).exists();
			}
		};	
		
//		MainApp.SCHEDULEDPOOL.scheduleWithFixedDelay(r, 30, 20, TimeUnit.SECONDS);
	}

	private List<Map<String, String>> getUploadList() {
		String sql = "select m.* from bill_tmedia m where sid_bill is not null and m.istatus in(2,3) order by m.tts desc";
		return DBHelper.query(sql);
	}

//	public void updateDic() {
//		for(final Entry<String,BaseContentValueConverter> entry:Factory.instance.getMapConverter().entrySet() ){
//			Runnable r = new Runnable(){
//
//				@Override
//				public void run() {
//					RequestParam rp = new RequestParam();
//					rp.functionName = "dao";
//					rp.paramMap.put("stype", entry.getKey());
//					RequestResult<Map<String,String>> rr = DAOHelper.instance.requestServer(rp);
//					if(rr.getErrCode() == RequestResult.OK){
//						BaseContentValueConverter converter = entry.getValue();
//						DBHelper.delete(converter.getTableName(), null);
//						DBHelper.insert(converter.getTableName(), rr.getDataList(), converter);
//					}
//				}
//				
//			};
//			MainApp.FIEXDPOOL.execute(r);
//		}
//	}
	
}
