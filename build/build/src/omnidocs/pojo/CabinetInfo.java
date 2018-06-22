package omnidocs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "CabinetStructure")
@XmlAccessorType(XmlAccessType.FIELD)
public class CabinetInfo {

	private String CabinetName;

	private String DriverURL;

	private String Comment;

	private String CabinetType;

	private String DatabaseType;

	public String getCabinetName() {
		return CabinetName;
	}

	public void setCabinetName(String CabinetName) {
		this.CabinetName = CabinetName;
	}

	public String getDriverURL() {
		return DriverURL;
	}

	public void setDriverURL(String DriverURL) {
		this.DriverURL = DriverURL;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String Comment) {
		this.Comment = Comment;
	}

	public String getCabinetType() {
		return CabinetType;
	}

	public void setCabinetType(String CabinetType) {
		this.CabinetType = CabinetType;
	}

	public String getDatabaseType() {
		return DatabaseType;
	}

	public void setDatabaseType(String DatabaseType) {
		this.DatabaseType = DatabaseType;
	}

	@Override
	public String toString() {
		return "ClassPojo [CabinetName = " + CabinetName + ", DriverURL = " + DriverURL + ", Comment = " + Comment
				+ ", CabinetType = " + CabinetType + ", DatabaseType = " + DatabaseType + "]";
	}
}