package omnidocs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Document")
@XmlAccessorType(XmlAccessType.FIELD)
public class Document {
	
	private String FTSDocumentIndex;

	private String CreatedByAppName;

	private String FinalizedFlag;

	private String AnnotationFlag;

	private String CreationDateTime;

	private String UsefulInfo;

	private String ISIndex;

	private String Author;

	private String ODMADocumentIndex;

	private String LoginUserRights;

	private String OriginalFolderLocation;

	private String FiledByUser;

	private String OriginalFolderIndex;

	private int DocumentSize;

	private String CheckoutBy;

	private String LinkDocFlag;

	private String DocumentIndex;

	private int NoOfPages = 0;

	private String VersionFlag;

	private String ExpiryDateTime;

	private char DocumentType;

	private String DocStatus;

	private String DocumentLock;

	private String DocOrderNo;

	private String FTSFlag = "Y";

	private String GlobalIndexes;

	private String Owner;

	private String EnableLog = "Y";

	private String RevisedDateTime;

	private String ThumbNailFlag;

	private String DocumentVersionNo;

	private ThumbNail ThumbNail;

	private String CreatedByApp;

	private String Comment;

	private String FinalizedDateTime;

	private String ParentFolderIndex;

	private String Location;

	private String AccessDateTime;

	private String[] content;

	private String ReferenceFlag;

	private String TextISIndex;

	private String DocumentName;

	private String PullPrintFlag;

	private String CheckoutStatus;

	private DataDefinition DataDefinition;

	private String FiledDateTime;

	private String OwnerIndex;

	public String getFTSDocumentIndex() {
		return FTSDocumentIndex;
	}

	public void setFTSDocumentIndex(String FTSDocumentIndex) {
		this.FTSDocumentIndex = FTSDocumentIndex;
	}

	public String getCreatedByAppName() {
		return CreatedByAppName;
	}

	public void setCreatedByAppName(String CreatedByAppName) {
		this.CreatedByAppName = CreatedByAppName;
	}

	public String getFinalizedFlag() {
		return FinalizedFlag;
	}

	public void setFinalizedFlag(String FinalizedFlag) {
		this.FinalizedFlag = FinalizedFlag;
	}

	public String getAnnotationFlag() {
		return AnnotationFlag;
	}

	public void setAnnotationFlag(String AnnotationFlag) {
		this.AnnotationFlag = AnnotationFlag;
	}

	public String getCreationDateTime() {
		return CreationDateTime;
	}

	public void setCreationDateTime(String CreationDateTime) {
		this.CreationDateTime = CreationDateTime;
	}

	public String getUsefulInfo() {
		return UsefulInfo;
	}

	public void setUsefulInfo(String UsefulInfo) {
		this.UsefulInfo = UsefulInfo;
	}

	public String getISIndex() {
		return ISIndex;
	}

	public void setISIndex(String ISIndex) {
		this.ISIndex = ISIndex;
	}

	public String getAuthor() {
		return Author;
	}

	public void setAuthor(String Author) {
		this.Author = Author;
	}

	public String getODMADocumentIndex() {
		return ODMADocumentIndex;
	}

	public void setODMADocumentIndex(String ODMADocumentIndex) {
		this.ODMADocumentIndex = ODMADocumentIndex;
	}

	public String getLoginUserRights() {
		return LoginUserRights;
	}

	public void setLoginUserRights(String LoginUserRights) {
		this.LoginUserRights = LoginUserRights;
	}

	public String getOriginalFolderLocation() {
		return OriginalFolderLocation;
	}

	public void setOriginalFolderLocation(String OriginalFolderLocation) {
		this.OriginalFolderLocation = OriginalFolderLocation;
	}

	public String getFiledByUser() {
		return FiledByUser;
	}

	public void setFiledByUser(String FiledByUser) {
		this.FiledByUser = FiledByUser;
	}

	public String getOriginalFolderIndex() {
		return OriginalFolderIndex;
	}

	public void setOriginalFolderIndex(String OriginalFolderIndex) {
		this.OriginalFolderIndex = OriginalFolderIndex;
	}

	public int getDocumentSize() {
		return DocumentSize;
	}

	public void setDocumentSize(int DocumentSize) {
		this.DocumentSize = DocumentSize;
	}

	public String getCheckoutBy() {
		return CheckoutBy;
	}

	public void setCheckoutBy(String CheckoutBy) {
		this.CheckoutBy = CheckoutBy;
	}

	public String getLinkDocFlag() {
		return LinkDocFlag;
	}

	public void setLinkDocFlag(String LinkDocFlag) {
		this.LinkDocFlag = LinkDocFlag;
	}

	public String getDocumentIndex() {
		return DocumentIndex;
	}

	public void setDocumentIndex(String DocumentIndex) {
		this.DocumentIndex = DocumentIndex;
	}

	public int getNoOfPages() {
		return NoOfPages;
	}

	public void setNoOfPages(int NoOfPages) {
		this.NoOfPages = NoOfPages;
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

	public char getDocumentType() {
		return DocumentType;
	}

	public void setDocumentType(char DocumentType) {
		this.DocumentType = DocumentType;
	}

	public String getDocStatus() {
		return DocStatus;
	}

	public void setDocStatus(String DocStatus) {
		this.DocStatus = DocStatus;
	}

	public String getDocumentLock() {
		return DocumentLock;
	}

	public void setDocumentLock(String DocumentLock) {
		this.DocumentLock = DocumentLock;
	}

	public String getDocOrderNo() {
		return DocOrderNo;
	}

	public void setDocOrderNo(String DocOrderNo) {
		this.DocOrderNo = DocOrderNo;
	}

	public String getFTSFlag() {
		return FTSFlag;
	}

	public void setFTSFlag(String FTSFlag) {
		this.FTSFlag = FTSFlag;
	}

	public String getGlobalIndexes() {
		return GlobalIndexes;
	}

	public void setGlobalIndexes(String GlobalIndexes) {
		this.GlobalIndexes = GlobalIndexes;
	}

	public String getOwner() {
		return Owner;
	}

	public void setOwner(String Owner) {
		this.Owner = Owner;
	}

	public String getEnableLog() {
		return EnableLog;
	}

	public void setEnableLog(String EnableLog) {
		this.EnableLog = EnableLog;
	}

	public String getRevisedDateTime() {
		return RevisedDateTime;
	}

	public void setRevisedDateTime(String RevisedDateTime) {
		this.RevisedDateTime = RevisedDateTime;
	}

	public String getThumbNailFlag() {
		return ThumbNailFlag;
	}

	public void setThumbNailFlag(String ThumbNailFlag) {
		this.ThumbNailFlag = ThumbNailFlag;
	}

	public String getDocumentVersionNo() {
		return DocumentVersionNo;
	}

	public void setDocumentVersionNo(String DocumentVersionNo) {
		this.DocumentVersionNo = DocumentVersionNo;
	}

	public ThumbNail getThumbNail() {
		return ThumbNail;
	}

	public void setThumbNail(ThumbNail ThumbNail) {
		this.ThumbNail = ThumbNail;
	}

	public String getCreatedByApp() {
		return CreatedByApp;
	}

	public void setCreatedByApp(String CreatedByApp) {
		this.CreatedByApp = CreatedByApp;
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

	public String[] getContent() {
		return content;
	}

	public void setContent(String[] content) {
		this.content = content;
	}

	public String getReferenceFlag() {
		return ReferenceFlag;
	}

	public void setReferenceFlag(String ReferenceFlag) {
		this.ReferenceFlag = ReferenceFlag;
	}

	public String getTextISIndex() {
		return TextISIndex;
	}

	public void setTextISIndex(String TextISIndex) {
		this.TextISIndex = TextISIndex;
	}

	public String getDocumentName() {
		return DocumentName;
	}

	public void setDocumentName(String DocumentName) {
		this.DocumentName = DocumentName;
	}

	public String getPullPrintFlag() {
		return PullPrintFlag;
	}

	public void setPullPrintFlag(String PullPrintFlag) {
		this.PullPrintFlag = PullPrintFlag;
	}

	public String getCheckoutStatus() {
		return CheckoutStatus;
	}

	public void setCheckoutStatus(String CheckoutStatus) {
		this.CheckoutStatus = CheckoutStatus;
	}

	public DataDefinition getDataDefinition() {
		return DataDefinition;
	}

	public void setDataDefinition(DataDefinition DataDefinition) {
		this.DataDefinition = DataDefinition;
	}

	public String getFiledDateTime() {
		return FiledDateTime;
	}

	public void setFiledDateTime(String FiledDateTime) {
		this.FiledDateTime = FiledDateTime;
	}

	public String getOwnerIndex() {
		return OwnerIndex;
	}

	public void setOwnerIndex(String OwnerIndex) {
		this.OwnerIndex = OwnerIndex;
	}

	@Override
	public String toString() {
		return "Document [FTSDocumentIndex = " + FTSDocumentIndex + ", CreatedByAppName = " + CreatedByAppName
				+ ", FinalizedFlag = " + FinalizedFlag + ", AnnotationFlag = " + AnnotationFlag
				+ ", CreationDateTime = " + CreationDateTime + ", UsefulInfo = " + UsefulInfo + ", ISIndex = " + ISIndex
				+ ", Author = " + Author + ", ODMADocumentIndex = " + ODMADocumentIndex + ", LoginUserRights = "
				+ LoginUserRights + ", OriginalFolderLocation = " + OriginalFolderLocation + ", FiledByUser = "
				+ FiledByUser + ", OriginalFolderIndex = " + OriginalFolderIndex + ", DocumentSize = " + DocumentSize
				+ ", CheckoutBy = " + CheckoutBy + ", LinkDocFlag = " + LinkDocFlag + ", DocumentIndex = "
				+ DocumentIndex + ", NoOfPages = " + NoOfPages + ", VersionFlag = " + VersionFlag
				+ ", ExpiryDateTime = " + ExpiryDateTime + ", DocumentType = " + DocumentType + ", DocStatus = "
				+ DocStatus + ", DocumentLock = " + DocumentLock + ", DocOrderNo = " + DocOrderNo + ", FTSFlag = "
				+ FTSFlag + ", GlobalIndexes = " + GlobalIndexes + ", Owner = " + Owner + ", EnableLog = " + EnableLog
				+ ", RevisedDateTime = " + RevisedDateTime + ", ThumbNailFlag = " + ThumbNailFlag
				+ ", DocumentVersionNo = " + DocumentVersionNo + ", ThumbNail = " + ThumbNail + ", CreatedByApp = "
				+ CreatedByApp + ", Comment = " + Comment + ", FinalizedDateTime = " + FinalizedDateTime
				+ ", ParentFolderIndex = " + ParentFolderIndex + ", Location = " + Location + ", AccessDateTime = "
				+ AccessDateTime + ", content = " + content + ", ReferenceFlag = " + ReferenceFlag + ", TextISIndex = "
				+ TextISIndex + ", DocumentName = " + DocumentName + ", PullPrintFlag = " + PullPrintFlag
				+ ", CheckoutStatus = " + CheckoutStatus + ", DataDefinition = " + DataDefinition + ", FiledDateTime = "
				+ FiledDateTime + ", OwnerIndex = " + OwnerIndex + "]";
	}
}