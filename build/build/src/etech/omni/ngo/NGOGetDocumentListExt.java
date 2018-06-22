package etech.omni.ngo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.Document;
import omnidocs.pojo.FolderReference;

@XmlRootElement(name = "NGOGetDocumentListExt_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOGetDocumentListExt extends NGOResponse {

	@XmlElementWrapper(name = "FolderReferences")
	@XmlElement(name = "Reference")
	private List<FolderReference> FolderReferences;

	private String TotalNoOfRecords;

	private String NoOfReferenceFetched;

	private String Option;

	private String NoOfRecordsFetched;

	private String TotalNoOfReference;

	@XmlElementWrapper(name = "Documents")
	@XmlElement(name = "Document")
	private List<Document> Documents;

	public List<FolderReference> getFolderReferences() {
		return FolderReferences;
	}

	public void setFolderReferences(List<FolderReference> FolderReferences) {
		this.FolderReferences = FolderReferences;
	}

	public String getTotalNoOfRecords() {
		return TotalNoOfRecords;
	}

	public void setTotalNoOfRecords(String TotalNoOfRecords) {
		this.TotalNoOfRecords = TotalNoOfRecords;
	}

	public String getNoOfReferenceFetched() {
		return NoOfReferenceFetched;
	}

	public void setNoOfReferenceFetched(String NoOfReferenceFetched) {
		this.NoOfReferenceFetched = NoOfReferenceFetched;
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

	public String getTotalNoOfReference() {
		return TotalNoOfReference;
	}

	public void setTotalNoOfReference(String TotalNoOfReference) {
		this.TotalNoOfReference = TotalNoOfReference;
	}

	public List<Document> getDocuments() {
		return Documents;
	}

	public void setDocuments(List<Document> Documents) {
		this.Documents = Documents;
	}

}
