package omnidocs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "User")
@XmlAccessorType(XmlAccessType.FIELD)
public class User {

	private String DeletedDateTime;

	private String SuperiorIndex;

	private String CreationDateTime;

	private String UserTrash;

	private String Privileges;

	private String UserLock;

	private String Account;

	private String Comment;

	private String UserAlive;

	private String PersonalName;

	private String SuperiorFlag;

	private String Name;

	private String PasswordNeverExpire;

	private String UserInbox;

	private String UserSentItem;

	private String ExpiryDateTime;

	private String ParentGroupIndex;

	private String UserIndex;

	public String getDeletedDateTime() {
		return DeletedDateTime;
	}

	public void setDeletedDateTime(String DeletedDateTime) {
		this.DeletedDateTime = DeletedDateTime;
	}

	public String getSuperiorIndex() {
		return SuperiorIndex;
	}

	public void setSuperiorIndex(String SuperiorIndex) {
		this.SuperiorIndex = SuperiorIndex;
	}

	public String getCreationDateTime() {
		return CreationDateTime;
	}

	public void setCreationDateTime(String CreationDateTime) {
		this.CreationDateTime = CreationDateTime;
	}

	public String getUserTrash() {
		return UserTrash;
	}

	public void setUserTrash(String UserTrash) {
		this.UserTrash = UserTrash;
	}

	public String getPrivileges() {
		return Privileges;
	}

	public void setPrivileges(String Privileges) {
		this.Privileges = Privileges;
	}

	public String getUserLock() {
		return UserLock;
	}

	public void setUserLock(String UserLock) {
		this.UserLock = UserLock;
	}

	public String getAccount() {
		return Account;
	}

	public void setAccount(String Account) {
		this.Account = Account;
	}

	public String getComment() {
		return Comment;
	}

	public void setComment(String Comment) {
		this.Comment = Comment;
	}

	public String getUserAlive() {
		return UserAlive;
	}

	public void setUserAlive(String UserAlive) {
		this.UserAlive = UserAlive;
	}

	public String getPersonalName() {
		return PersonalName;
	}

	public void setPersonalName(String PersonalName) {
		this.PersonalName = PersonalName;
	}

	public String getSuperiorFlag() {
		return SuperiorFlag;
	}

	public void setSuperiorFlag(String SuperiorFlag) {
		this.SuperiorFlag = SuperiorFlag;
	}

	public String getName() {
		return Name;
	}

	public void setName(String Name) {
		this.Name = Name;
	}

	public String getPasswordNeverExpire() {
		return PasswordNeverExpire;
	}

	public void setPasswordNeverExpire(String PasswordNeverExpire) {
		this.PasswordNeverExpire = PasswordNeverExpire;
	}

	public String getUserInbox() {
		return UserInbox;
	}

	public void setUserInbox(String UserInbox) {
		this.UserInbox = UserInbox;
	}

	public String getUserSentItem() {
		return UserSentItem;
	}

	public void setUserSentItem(String UserSentItem) {
		this.UserSentItem = UserSentItem;
	}

	public String getExpiryDateTime() {
		return ExpiryDateTime;
	}

	public void setExpiryDateTime(String ExpiryDateTime) {
		this.ExpiryDateTime = ExpiryDateTime;
	}

	public String getParentGroupIndex() {
		return ParentGroupIndex;
	}

	public void setParentGroupIndex(String ParentGroupIndex) {
		this.ParentGroupIndex = ParentGroupIndex;
	}

	public String getUserIndex() {
		return UserIndex;
	}

	public void setUserIndex(String UserIndex) {
		this.UserIndex = UserIndex;
	}

	@Override
	public String toString() {
		return "ClassPojo [DeletedDateTime = " + DeletedDateTime + ", SuperiorIndex = " + SuperiorIndex
				+ ", CreationDateTime = " + CreationDateTime + ", UserTrash = " + UserTrash + ", Privileges = "
				+ Privileges + ", UserLock = " + UserLock + ", Account = " + Account + ", Comment = " + Comment
				+ ", UserAlive = " + UserAlive + ", PersonalName = " + PersonalName + ", SuperiorFlag = " + SuperiorFlag
				+ ", Name = " + Name + ", PasswordNeverExpire = " + PasswordNeverExpire + ", UserInbox = " + UserInbox
				+ ", UserSentItem = " + UserSentItem + ", ExpiryDateTime = " + ExpiryDateTime + ", ParentGroupIndex = "
				+ ParentGroupIndex + ", UserIndex = " + UserIndex + "]";
	}
}
