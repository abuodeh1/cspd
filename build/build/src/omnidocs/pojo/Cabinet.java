package omnidocs.pojo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "Cabinet")
@XmlAccessorType(XmlAccessType.FIELD)
public class Cabinet {

	private String EnableDDTFTS;

	private String LoginUserIndex;

	private String UserPasswordExpire;

	private String AutoPassword;

	private String CreationDateTime;

	private String Privileges;

	private String CabinetName;

	private String FTSDatabasePath;

	private String IsAdmin;

	private String CabinetType;

	private String SecurityLevel;

	private String IsMakerCheckerEnabled;

	private String CabinetLockFlag;

	private String LockByUser;

	private String LastLoginTime;

	private short ImageVolumeIndex;

	private String VersionFlag;
	
	private String LeftLoginAttempts;
	
	private String Services;
	
	private List<Folder> Folders;

    private String UserDBId;

	public String getEnableDDTFTS() {
		return EnableDDTFTS;
	}

	public void setEnableDDTFTS(String EnableDDTFTS) {
		this.EnableDDTFTS = EnableDDTFTS;
	}

	public String getLoginUserIndex() {
		return LoginUserIndex;
	}

	public void setLoginUserIndex(String LoginUserIndex) {
		this.LoginUserIndex = LoginUserIndex;
	}

	public String getUserPasswordExpire() {
		return UserPasswordExpire;
	}

	public void setUserPasswordExpire(String UserPasswordExpire) {
		this.UserPasswordExpire = UserPasswordExpire;
	}

	public String getAutoPassword() {
		return AutoPassword;
	}

	public void setAutoPassword(String AutoPassword) {
		this.AutoPassword = AutoPassword;
	}

	public String getCreationDateTime() {
		return CreationDateTime;
	}

	public void setCreationDateTime(String CreationDateTime) {
		this.CreationDateTime = CreationDateTime;
	}

	public String getPrivileges() {
		return Privileges;
	}

	public void setPrivileges(String Privileges) {
		this.Privileges = Privileges;
	}

	public String getCabinetName() {
		return CabinetName;
	}

	public void setCabinetName(String CabinetName) {
		this.CabinetName = CabinetName;
	}

	public String getFTSDatabasePath() {
		return FTSDatabasePath;
	}

	public void setFTSDatabasePath(String FTSDatabasePath) {
		this.FTSDatabasePath = FTSDatabasePath;
	}

	public String getIsAdmin() {
		return IsAdmin;
	}

	public void setIsAdmin(String IsAdmin) {
		this.IsAdmin = IsAdmin;
	}

	public String getCabinetType() {
		return CabinetType;
	}

	public void setCabinetType(String CabinetType) {
		this.CabinetType = CabinetType;
	}

	public String getSecurityLevel() {
		return SecurityLevel;
	}

	public void setSecurityLevel(String SecurityLevel) {
		this.SecurityLevel = SecurityLevel;
	}

	public String getIsMakerCheckerEnabled() {
		return IsMakerCheckerEnabled;
	}

	public void setIsMakerCheckerEnabled(String IsMakerCheckerEnabled) {
		this.IsMakerCheckerEnabled = IsMakerCheckerEnabled;
	}

	public String getCabinetLockFlag() {
		return CabinetLockFlag;
	}

	public void setCabinetLockFlag(String CabinetLockFlag) {
		this.CabinetLockFlag = CabinetLockFlag;
	}

	public String getLockByUser() {
		return LockByUser;
	}

	public void setLockByUser(String LockByUser) {
		this.LockByUser = LockByUser;
	}

	public String getLastLoginTime() {
		return LastLoginTime;
	}

	public void setLastLoginTime(String LastLoginTime) {
		this.LastLoginTime = LastLoginTime;
	}

	public short getImageVolumeIndex() {
		return ImageVolumeIndex;
	}

	public void setImageVolumeIndex(short ImageVolumeIndex) {
		this.ImageVolumeIndex = ImageVolumeIndex;
	}

	public String getVersionFlag() {
		return VersionFlag;
	}

	public void setVersionFlag(String VersionFlag) {
		this.VersionFlag = VersionFlag;
	}



	public String getLeftLoginAttempts() {
		return LeftLoginAttempts;
	}

	public void setLeftLoginAttempts(String leftLoginAttempts) {
		LeftLoginAttempts = leftLoginAttempts;
	}

	public String getServices() {
		return Services;
	}

	public void setServices(String services) {
		Services = services;
	}

	public List<Folder> getFolders() {
		return Folders;
	}

	public void setFolders(List<Folder> folders) {
		Folders = folders;
	}

	public String getUserDBId() {
		return UserDBId;
	}

	public void setUserDBId(String userDBId) {
		UserDBId = userDBId;
	}
	
	@Override
	public String toString() {
		return "Cabinet [EnableDDTFTS = " + EnableDDTFTS + ", LoginUserIndex = " + LoginUserIndex
				+ ", UserPasswordExpire = " + UserPasswordExpire + ", AutoPassword = " + AutoPassword
				+ ", CreationDateTime = " + CreationDateTime + ", Privileges = " + Privileges + ", CabinetName = "
				+ CabinetName + ", FTSDatabasePath = " + FTSDatabasePath + ", IsAdmin = " + IsAdmin + ", CabinetType = "
				+ CabinetType + ", SecurityLevel = " + SecurityLevel + ", IsMakerCheckerEnabled = "
				+ IsMakerCheckerEnabled + ", CabinetLockFlag = " + CabinetLockFlag + ", LockByUser = " + LockByUser
				+ ", LastLoginTime = " + LastLoginTime + ", ImageVolumeIndex = " + ImageVolumeIndex + ", VersionFlag = "
				+ VersionFlag + "]";
	}
}