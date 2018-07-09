package cspd;

import java.sql.Timestamp;

public class BatchDetails {

	private int id;
	
	private int batchId;
	
	private String fileNumber;
	
	private String name;
	
	private String year;
	
	private int fileStatus;
	
	private String indexName;
	
	private String serialNumber;
	
	private String indexFileNumber;
	
	private String createdBy;
	
	private Timestamp createDate;
	
	private String officeCode;

	private String fileType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getBatchId() {
		return batchId;
	}

	public void setBatchId(int batchId) {
		this.batchId = batchId;
	}

	public String getFileNumber() {
		return fileNumber;
	}

	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public int getFileStatus() {
		return fileStatus;
	}

	public void setFileStatus(int fileStatus) {
		this.fileStatus = fileStatus;
	}

	public String getIndexName() {
		return indexName;
	}

	public void setIndexName(String indexName) {
		this.indexName = indexName;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getIndexFileNumber() {
		return indexFileNumber;
	}

	public void setIndexFileNumber(String indexFileNumber) {
		this.indexFileNumber = indexFileNumber;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	@Override
	public String toString() {
		return "BatchDetails [id=" + id + ", batchId=" + batchId + ", fileNumber=" + fileNumber + ", name=" + name
				+ ", year=" + year + ", fileStatus=" + fileStatus + ", indexName=" + indexName + ", serialNumber="
				+ serialNumber + ", indexFileNumber=" + indexFileNumber + ", createdBy=" + createdBy + ", createDate="
				+ createDate + "]";
	}

	public String getOfficeCode() {
		return officeCode;
	}

	public void setOfficeCode(String officeCode) {
		this.officeCode = officeCode;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	
		
	
}
