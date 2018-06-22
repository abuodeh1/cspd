package etech.omni.ngo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.Folder;

@XmlRootElement(name = "NGOGetFolderProperty_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOGetFolderProperty extends NGOResponse {

	private Folder Folder;

	private String Option;

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

}
