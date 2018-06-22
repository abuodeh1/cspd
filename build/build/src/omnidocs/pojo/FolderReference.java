package omnidocs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Reference")
@XmlAccessorType(XmlAccessType.FIELD)
public class FolderReference {

	private String Rights;

	private String FolderIndex;

	private String FileDatetime;

	private String FolderName;

	public String getRights() {
		return Rights;
	}

	public void setRights(String Rights) {
		this.Rights = Rights;
	}

	public String getFolderIndex() {
		return FolderIndex;
	}

	public void setFolderIndex(String FolderIndex) {
		this.FolderIndex = FolderIndex;
	}

	public String getFileDatetime() {
		return FileDatetime;
	}

	public void setFileDatetime(String FileDatetime) {
		this.FileDatetime = FileDatetime;
	}

	public String getFolderName() {
		return FolderName;
	}

	public void setFolderName(String FolderName) {
		this.FolderName = FolderName;
	}

	@Override
	public String toString() {
		return "FolderReference [Rights = " + Rights + ", FolderIndex = " + FolderIndex + ", FileDatetime = " + FileDatetime
				+ ", FolderName = " + FolderName + "]";
	}
}