package cspd.core;

import java.sql.Timestamp;

public class GeneralLog implements Log {

	private String logID;
	private String logPriority;
	private String logSeverity;
	private String logMessage;
	private Timestamp logTime;
	
	public GeneralLog(String logID, String logPriority, String logSeverity, String logMessage) {
		super();
		this.logID = logID;
		this.logPriority = logPriority;
		this.logSeverity = logSeverity;
		this.logMessage = logMessage;
	}
	public String getLogID() {
		return logID;
	}
	public void setLogID(String logID) {
		this.logID = logID;
	}
	public String getLogPriority() {
		return logPriority;
	}
	public void setLogPriority(String logPriority) {
		this.logPriority = logPriority;
	}
	public String getLogSeverity() {
		return logSeverity;
	}
	public void setLogSeverity(String logSeverity) {
		this.logSeverity = logSeverity;
	}
	public String getLogMessage() {
		return logMessage;
	}
	public void setLogMessage(String logMessage) {
		this.logMessage = logMessage;
	}
	public Timestamp getLogTime() {
		return logTime;
	}
	public void setLogTime(Timestamp logTime) {
		this.logTime = logTime;
	}
	
	
	
}
