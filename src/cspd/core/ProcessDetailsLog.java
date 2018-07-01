package cspd.core;

public class ProcessDetailsLog implements Log {

	private int logID;
	private String documentName;
	private boolean isUploadedToOmniDocs; 
	private boolean isUploadedToDocuware;
	private int timeUploadedToOmniDocs; 
	private int timeUploadedToDocuware;

	public ProcessDetailsLog(int logID, String documentName, boolean isUploadedToOmniDocs) {

		this.logID = logID;
		this.documentName = documentName;
		this.isUploadedToOmniDocs = isUploadedToOmniDocs;
	}
	
	public int getLogID() {
		return logID;
	}
	public void setLogID(int logID) {
		this.logID = logID;
	}
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
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
