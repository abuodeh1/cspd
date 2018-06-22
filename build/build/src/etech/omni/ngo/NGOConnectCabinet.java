package etech.omni.ngo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.Cabinet;
import omnidocs.pojo.Folder;

@XmlRootElement(name = "NGOConnectCabinet_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOConnectCabinet extends NGOResponse {

	private Cabinet Cabinet;

	private String LeftLoginAttempts;

	private String Services;

	private String Option;

	@XmlElementWrapper(name = "Folders")
	@XmlElement(name = "Folder")
	private List<Folder> Folders;

	private String UserDBId;

	public Cabinet getCabinet() {
		return Cabinet;
	}

	public void setCabinet(Cabinet Cabinet) {
		this.Cabinet = Cabinet;
	}

	public String getLeftLoginAttempts() {
		return LeftLoginAttempts;
	}

	public void setLeftLoginAttempts(String LeftLoginAttempts) {
		this.LeftLoginAttempts = LeftLoginAttempts;
	}

	public String getServices() {
		return Services;
	}

	public void setServices(String Services) {
		this.Services = Services;
	}

	public String getOption() {
		return Option;
	}

	public void setOption(String Option) {
		this.Option = Option;
	}

	public List<Folder> getFolders() {
		return Folders;
	}

	public void setFolders(List<Folder> Folders) {
		this.Folders = Folders;
	}

	public String getUserDBId() {
		return UserDBId;
	}

	public void setUserDBId(String UserDBId) {
		this.UserDBId = UserDBId;
	}

}
