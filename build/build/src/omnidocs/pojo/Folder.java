package omnidocs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Folder")
@XmlAccessorType(XmlAccessType.FIELD)
public class Folder {

	private String ACLMoreFlag;

	private String FinalizedFlag;

	private String CreationDateTime;

	private String LoginUserRights;

	private String NoOfSubFolders;

	private String FolderLock;

	private String FolderIndex;

	private String ImageVolumeIndex;

	private String VersionFlag;

	private String ExpiryDateTime;

	private String ACL;

	private String Owner;

	private String RevisedDateTime;

	private String DeletedDateTime;

	private String Comment;

	private String FinalizedDateTime;

	private String ParentFolderIndex;

	private String Location;

	private String AccessDateTime;

	private String AccessType;

	private String LockByUser;

	private String FinalizedBy;

	private DataDefinition DataDefinition;

	private String FolderName;

	private String FolderType;

	private String OwnerIndex;

	private String NoOfDocuments;

	public String getACLMoreFlag() {
		return ACLMoreFlag;
	}

	public void setACLMoreFlag(String ACLMoreFlag) {
		this.ACLMoreFlag = ACLMoreFlag;
	}

	public String getFinalizedFlag() {
		return FinalizedFlag;
	}

	public void setFinalizedFlag(String FinalizedFlag) {
		this.FinalizedFlag = FinalizedFlag;
	}

	public String getCreationDateTime() {
		return CreationDateTime;
	}

	public void setCreationDateTime(String CreationDateTime) {
		this.CreationDateTime = CreationDateTime;
	}

	public String getLoginUserRights() {
		return LoginUserRights;
	}

	public void setLoginUserRights(String LoginUserRights) {
		this.LoginUserRights = LoginUserRights;
	}

	public String getNoOfSubFolders() {
		return NoOfSubFolders;
	}

	public void setNoOfSubFolders(String NoOfSubFolders) {
		this.NoOfSubFolders = NoOfSubFolders;
	}

	public String getFolderLock() {
		return FolderLock;
	}

	public void setFolderLock(String FolderLock) {
		this.FolderLock = FolderLock;
	}

	public String getFolderIndex() {
		return FolderIndex;
	}

	public void setFolderIndex(String FolderIndex) {
		this.FolderIndex = FolderIndex;
	}

	public String getImageVolumeIndex() {
		return ImageVolumeIndex;
	}

	public void setImageVolumeIndex(String ImageVolumeIndex) {
		this.ImageVolumeIndex = ImageVolumeIndex;
	}

	public String getVersionFlag() {
		return VersionFlag;
	}

	public void setVersionFlag(String VersionFlag) {
		this.VersionFlag = VersionFlag;
	}

	public String getExpiryDateTime() {
		return ExpiryDateTime;
	}

	public void setExpiryDateTime(String ExpiryDateTime) {
		this.ExpiryDateTime = ExpiryDateTime;
	}

	public String getACL() {
		return ACL;
	}

	public void setACL(String ACL) {
		this.ACL = ACL;
	}

	public String getOwner() {
		return Owner;
	}

	public void setOwner(String Owner) {
		this.Owner = Owner;
	}

	public String getRevisedDateTime() {
		return RevisedDateTime;
	}

	public void setRevisedDateTime(String RevisedDateTime) {
		this.RevisedDateTime = RevisedDateTime;
	}

	public String getDeletedDateTime() {
		return DeletedDateTime;
	}

	public void setDeletedDateTime(String DeletedDateTime) {
		this.DeletedDateTime = DeletedDateTime;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String Comment) {
		this.Comment = Comment;
	}

	public String getFinalizedDateTime() {
		return FinalizedDateTime;
	}

	public void setFinalizedDateTime(String FinalizedDateTime) {
		this.FinalizedDateTime = FinalizedDateTime;
	}

	public String getParentFolderIndex() {
		return ParentFolderIndex;
	}

	public void setParentFolderIndex(String ParentFolderIndex) {
		this.ParentFolderIndex = ParentFolderIndex;
	}

	public String getLocation() {
		return Location;
	}

	public void setLocation(String Location) {
		this.Location = Location;
	}

	public String getAccessDateTime() {
		return AccessDateTime;
	}

	public void setAccessDateTime(String AccessDateTime) {
		this.AccessDateTime = AccessDateTime;
	}

	public String getAccessType() {
		return AccessType;
	}

	public void setAccessType(String AccessType) {
		this.AccessType = AccessType;
	}

	public String getLockByUser() {
		return LockByUser;
	}

	public void setLockByUser(String LockByUser) {
		this.LockByUser = LockByUser;
	}

	public String getFinalizedBy() {
		return FinalizedBy;
	}

	public void setFinalizedBy (String FinalizedBy)
    {
        this.FinalizedBy = FinalizedBy;
    }

	public DataDefinition getDataDefinition() {
		return DataDefinition;
	}

	public void setDataDefinition(DataDefinition DataDefinition) {
		this.DataDefinition = DataDefinition;
	}

	public String getFolderName() {
		return FolderName;
	}

	public void setFolderName(String FolderName) {
		this.FolderName = FolderName;
	}

	public String getFolderType() {
		return FolderType;
	}

	public void setFolderType(String FolderType) {
		this.FolderType = FolderType;
	}

	public String getOwnerIndex() {
		return OwnerIndex;
	}

	public void setOwnerIndex(String OwnerIndex) {
		this.OwnerIndex = OwnerIndex;
	}

	public String getNoOfDocuments() {
		return NoOfDocuments;
	}

	public void setNoOfDocuments(String NoOfDocuments) {
		this.NoOfDocuments = NoOfDocuments;
	}

	@Override
	public String toString() {
		return "Folder [ACLMoreFlag = " + ACLMoreFlag + ", FinalizedFlag = " + FinalizedFlag + ", CreationDateTime = "
				+ CreationDateTime + ", LoginUserRights = " + LoginUserRights + ", NoOfSubFolders = " + NoOfSubFolders
				+ ", FolderLock = " + FolderLock + ", FolderIndex = " + FolderIndex + ", ImageVolumeIndex = "
				+ ImageVolumeIndex + ", VersionFlag = " + VersionFlag + ", ExpiryDateTime = " + ExpiryDateTime
				+ ", ACL = " + ACL + ", Owner = " + Owner + ", RevisedDateTime = " + RevisedDateTime
				+ ", DeletedDateTime = " + DeletedDateTime + ", Comment = " + Comment + ", FinalizedDateTime = "
				+ FinalizedDateTime + ", ParentFolderIndex = " + ParentFolderIndex + ", Location = " + Location
				+ ", AccessDateTime = " + AccessDateTime + ", AccessType = " + AccessType + ", LockByUser = "
				+ LockByUser + ", FinalizedBy = " + FinalizedBy + ", DataDefinition = " + DataDefinition
				+ ", FolderName = " + FolderName + ", FolderType = " + FolderType + ", OwnerIndex = " + OwnerIndex
				+ ", NoOfDocuments = " + NoOfDocuments + "]";
	}
}