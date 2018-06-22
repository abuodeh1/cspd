package etech.omni.ngo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.Document;

@XmlRootElement(name = "NGOAddDocument_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOAddDocument extends NGOResponse {

	private Document Document;

	private String Option;

	private String MainGroupIndex;

	public Document getDocument() {
		return Document;
	}

	public void setDocument(Document Document) {
		this.Document = Document;
	}

	public String getOption() {
		return Option;
	}

	public void setOption(String Option) {
		this.Option = Option;
	}

	public String getMainGroupIndex() {
		return MainGroupIndex;
	}

	public void setMainGroupIndex(String MainGroupIndex) {
		this.MainGroupIndex = MainGroupIndex;
	}

}