package cspd.core;

import java.util.ArrayList;
import java.util.List;

public class OpexFolderReport {
	
	private String folderName;
	
	private String failedReason;
	
	private boolean isDocumentLevel = false;
	
	private int totalDocuments;
	
	private List<DocumentReport> failedDocuments = new ArrayList<>();

	public String getFolderName() {
		return folderName;
	}

	public void setFolderName(String folderName) {
		this.folderName = folderName;
	}

	public String getFailedReason() {
		return failedReason;
	}

	public void setFailedReason(String failedReason) {
		this.failedReason = failedReason;
	}

	public int getTotalDocuments() {
		return totalDocuments;
	}

	public void setTotalDocuments(int totalDocuments) {
		this.totalDocuments = totalDocuments;
	}

	public List<DocumentReport> getFailedDocuments() {
		return failedDocuments;
	}

	public void setFailedDocuments(List<DocumentReport> failedDocuments) {
		this.failedDocuments = failedDocuments;
	}

	public boolean isDocumentLevel() {
		return isDocumentLevel;
	}

	public void setDocumentLevel(boolean isDocumentLevel) {
		this.isDocumentLevel = isDocumentLevel;
	}
	

}
