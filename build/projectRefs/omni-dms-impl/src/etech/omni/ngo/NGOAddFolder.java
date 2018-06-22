package etech.omni.ngo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.Folder;

@XmlRootElement(name = "NGOAddFolder_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOAddFolder extends NGOResponse {

	@XmlElement(name = "Folder")
	private Folder Folder;

	private String Option;

	private String LoginUserRights;

	public Folder getFolder() {
		return Folder;
	}

	public void setFolder(Folder Folder) {
		this.Folder = Folder;
	}

	public String getOption() {
		return Option;
	}

	public void setOption(String Option) {
		this.Option = Option;
	}

	public String getLoginUserRights() {
		return LoginUserRights;
	}

	public void setLoginUserRights (String LoginUserRights)
    {
        this.LoginUserRights = LoginUserRights;
    }
}
