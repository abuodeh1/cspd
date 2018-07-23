package cspd.core;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ProcessLog implements Log {

	private int logID;
	private Timestamp logTimestamp;
	private String batchIdentifier;
	private String machineName;
	private Timestamp startTime;
	private Timestamp endTime;
	private int numberOfDocuments; 
	private boolean isUploadedToOmniDocs; 
	private boolean isUploadedToDocuware;
	private int timeUploadedToOmniDocs; 
	private int timeUploadedToDocuware;
	
	public ProcessLog(String batchIdentifier, int numberOfDocuments, String machineName, String startTime, String endTime) {

		this.batchIdentifier = batchIdentifier;
		this.machineName = machineName;
		try {//2018-06-25 12:13:13
			
			Timestamp current  = new Timestamp( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) ).getTime() );
			
			this.startTime = (startTime != null && startTime.length() != 0? new Timestamp( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(startTime).getTime() ): current );
			this.endTime = (startTime != null && endTime.length() != 0? new Timestamp( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endTime).getTime() ) : current);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
		this.numberOfDocuments = numberOfDocuments;
		this.isUploadedToOmniDocs = isUploadedToOmniDocs;
		this.isUploadedToDocuware = isUploadedToDocuware;
		this.timeUploadedToOmniDocs = timeUploadedToOmniDocs;
		this.timeUploadedToDocuware = timeUploadedToDocuware;
	}
	
	public int getLogID() {
		return logID;
	}
	public void setLogID(int logID) {
		this.logID = logID;
	}
	public Timestamp getLogTimestamp() {
		return logTimestamp;
	}
	public void setLogTimestamp(Timestamp logTimestamp) {
		this.logTimestamp = logTimestamp;
	}
	public String getBatchIdentifier() {
		return batchIdentifier;
	}
	public void setBatchIdentifier(String batchIdentifier) {
		this.batchIdentifier = batchIdentifier;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public int getNumberOfDocuments() {
		return numberOfDocuments;
	}
	public void setNumberOfDocuments(int numberOfDocuments) {
		this.numberOfDocuments = numberOfDocuments;
	}
	public boolean isUploadedToOmniDocs() {
		return isUploadedToOmniDocs;
	}
	public void setUploadedToOmniDocs(boolean isUploadedToOmniDocs) {
		this.isUploadedToOmniDocs = isUploadedToOmniDocs;
	}
	public boolean isUploadedToDocuware() {
		return isUploadedToDocuware;
	}
	public void setUploadedToDocuware(boolean isUploadedToDocuware) {
		this.isUploadedToDocuware = isUploadedToDocuware;
	}
	public int getTimeUploadedToOmniDocs() {
		return timeUploadedToOmniDocs;
	}
	public void setTimeUploadedToOmniDocs(int timeUploadedToOmniDocs) {
		this.timeUploadedToOmniDocs = timeUploadedToOmniDocs;
	}
	public int getTimeUploadedToDocuware() {
		return timeUploadedToDocuware;
	}
	public void setTimeUploadedToDocuware(int timeUploadedToDocuware) {
		this.timeUploadedToDocuware = timeUploadedToDocuware;
	}

}
