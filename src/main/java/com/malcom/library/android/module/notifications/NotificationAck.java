package com.malcom.library.android.module.notifications;


/**
 * Malcom Notification ACK.
 * 
 * @author Malcom Ventures, S.L.
 * @since  2012
 *
 */
public class NotificationAck {

	//Required
	private Long id;
	private Long segmentId;
	private String applicationCode;
    private String udid;
    private String environment;
    private String created;
    private String ackDate;
        
    
   
    
    public NotificationAck(){}


    
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
		
	public Long getSegmentId() {
		return segmentId;
	}
	public void setSegmentId(Long segmentId) {
		this.segmentId = segmentId;
	}

	public String getApplicationCode() {
		return applicationCode;
	}
	public void setApplicationCode(String applicationCode) {
		this.applicationCode = applicationCode;
	}

	public String getUdid() {
		return udid;
	}
	public void setUdid(String udid) {
		this.udid = udid;
	}

	public String getEnvironment() {
		return environment;
	}
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}

	public String getAckDate() {
		return ackDate;
	}
	public void setAckDate(String ackDate) {
		this.ackDate = ackDate;
	}	
	
    
}
