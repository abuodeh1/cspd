package etech.omni.ngo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "NGOGetIDFromName_Output")
@XmlAccessorType(XmlAccessType.FIELD)

public class NGOGetIDFromName extends NGOResponse{

	private String ObjectIndex;

	private String Option;

	public String getObjectIndex() {
		return ObjectIndex;
	}

	public void setObjectIndex(String ObjectIndex) {
		this.ObjectIndex = ObjectIndex;
	}

	public String getOption() {
		return Option;
	}

	public void setOption(String Option) {
		this.Option = Option;
	}
}
