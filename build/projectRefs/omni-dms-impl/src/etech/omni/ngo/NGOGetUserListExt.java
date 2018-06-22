package etech.omni.ngo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.User;

@XmlRootElement(name = "NGOGetUserListExt_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOGetUserListExt extends NGOResponse {

	@XmlElementWrapper(name="Users")
	@XmlElement(name = "User")
	private List<User> Users;

	private String TotalNoOfRecords;

	private String Option;

	private String NoOfRecordsFetched;

	public List<User> getUsers() {
		return Users;
	}

	public void setUsers(List<User> Users) {
		this.Users = Users;
	}

	public String getTotalNoOfRecords() {
		return TotalNoOfRecords;
	}

	public void setTotalNoOfRecords(String TotalNoOfRecords) {
		this.TotalNoOfRecords = TotalNoOfRecords;
	}

	public String getOption() {
		return Option;
	}

	public void setOption(String Option) {
		this.Option = Option;
	}

	public String getNoOfRecordsFetched() {
		return NoOfRecordsFetched;
	}

	public void setNoOfRecordsFetched(String NoOfRecordsFetched) {
		this.NoOfRecordsFetched = NoOfRecordsFetched;
	}

}
