package etech.omni.ngo;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class NGOResponse {

	private String originalXML;

	private int Status;

	private String Error;

	public String getOriginalXML() {
		return originalXML;
	}

	public void setOriginalXML(String xmlResponse) {
		originalXML = xmlResponse;
	}

	public int getStatus() {
		return Status;
	}

	public void setStatus(int Status) {
		this.Status = Status;
	}

	public String getError() {
		return Error;
	}

	public void setError(String error) {
		Error = error;
	}

	@Override
	public final String toString() {

		return originalXML;

	}

}
