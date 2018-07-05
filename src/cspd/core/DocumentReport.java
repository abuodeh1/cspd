package cspd.core;

public class DocumentReport {

	public DocumentReport(String documentName, String failedReason) {
		this.documentName = documentName;
		this.failedReason = failedReason;
	}

	String documentName;
	String failedReason;
	
	public String getDocumentName() {
		return documentName;
	}
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}
	public String getFailedReason() {
		return failedReason;
	}
	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}
	
	
	
}