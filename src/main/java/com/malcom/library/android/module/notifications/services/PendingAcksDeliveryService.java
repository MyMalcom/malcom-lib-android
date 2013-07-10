package com.malcom.library.android.module.notifications.services;

import java.io.File;
import java.io.FilenameFilter;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.malcom.library.android.module.core.MCMCoreAdapter;
import com.malcom.library.android.module.notifications.MCMNotificationModule;
import com.malcom.library.android.utils.HttpDateUtils;
import com.malcom.library.android.utils.ToolBox;
import com.malcom.library.android.utils.ToolBox.HTTP_METHOD;
import com.malcom.library.android.utils.encoding.DigestUtils;
import com.malcom.library.android.utils.encoding.base64.Base64;

/**
 * Service that delivers the pending acks to malcom server.
 * 
 * By using a service for this, we avoid the system to destroys 
 * the application if more resources are needed. Operation of 
 * this kind (deliveries) should always be done by using a service.
 * 
 * We use the IntentService because the advantages explained here: 
 * http://developer.android.com/guide/components/services.html
 * 
 * @author Malcom Ventures S.L
 * @since  2012
 *
 */
public class PendingAcksDeliveryService extends IntentService {
	
	
	
	public PendingAcksDeliveryService() {
		super("PendingAcksDeliveryService");
	}
	
	public PendingAcksDeliveryService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		String[] pendingAcks = listCachedAcks();
		if(pendingAcks!=null && pendingAcks.length>0){
			if(ToolBox.network_haveNetworkConnection(getApplicationContext())){
				sendCachedAcks(pendingAcks);
			}else{
				Log.i(MCMNotificationModule.TAG,"PendingAcksDeliveryService: no network connection, skipping pending ack.");
			}
		}
	}

	
	
	// AUXILIAR FUNCTIONS ----------------------------------------------------------------------------------------------
	
	private String[] listCachedAcks(){
		String filePath = getApplicationContext().getFilesDir().getAbsolutePath();//returns current directory.
		File appInternalDir = new File(filePath);
		String[] pendingBeacons = appInternalDir.list(new FilenameFilter(){
			public boolean accept(File arg0, String name) {
				return name.startsWith(MCMNotificationModule.CACHED_ACK_FILE_PREFIX);			
			}});
		
		return pendingBeacons;
	}
	
	private void sendCachedAcks(String[] pendingAcks){
		Log.i(MCMNotificationModule.TAG, "PendingAcksDeliveryService: Pending acks to send: " + pendingAcks.length);
		for(String ack:pendingAcks){			
			try {
				byte[] ackBytes = ToolBox.storage_readDataFromInternalStorage(getApplicationContext(), ack);
				if(ackBytes!=null && ackBytes.length>0){
					sendToMalcom(new String(ackBytes));
				}
				ToolBox.storage_deleteDataFromInternalStorage(getApplicationContext(), ack);
			} catch (Exception e) {
				Log.e(MCMNotificationModule.TAG,"PendingAcksDeliveryService: Error sending pending ack ("+e.getMessage()+")",e);
			}
		}
	}
	
	private void sendToMalcom(String ackData) throws Exception{
		
		String theJSON = ackData;
		Log.d(MCMNotificationModule.TAG, "PendingAcksDeliveryService: "+theJSON);
		
		URL url = null;
		try {
			String malcomDate = HttpDateUtils.formatDate(new Date());
			
			url = new URL(MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_BASEURL) + MCMNotificationModule.notification_ack);
						  
			//	Prepare required data for headers, these headers are requested by Malcom beacon API.
			String headers = "x-mcm-date:" + malcomDate+"\n";
			String md5 = ToolBox.md5_calculateMD5(theJSON);
			String password = ToolBox.deliveries_getDataToSign(headers, "application/json", "", "POST", MCMNotificationModule.notification_ack, md5);
			password = DigestUtils.calculateRFC2104HMAC(password, MCMCoreAdapter.PROPERTIES_MALCOM_APPSECRETKEY);
			
			Map<String, String> headersData = new HashMap<String, String>();
			headersData.put("Authorization", "basic " + new String(Base64.encode(new String(MCMCoreAdapter.getInstance().coreGetProperty(MCMCoreAdapter.PROPERTIES_MALCOM_APPID) + ":" + password).getBytes())));
			headersData.put("Content-Type", "application/json");
			headersData.put("content-md5", md5);
			headersData.put("x-mcm-date", malcomDate);			
			ToolBox.net_httpclient_doAction(HTTP_METHOD.POST, url.toString(), theJSON, headersData);

		} catch (Exception e) {
			Log.e(MCMNotificationModule.TAG, "PendingAcksDeliveryService: Error sending ack data to Malcom service url '"+url.toString()+"': "+e.getMessage(),e);
			throw e;
		}
	}
	
}
