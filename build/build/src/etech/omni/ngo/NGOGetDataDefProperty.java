package etech.omni.ngo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.DataDefinition;

@XmlRootElement(name = "NGOGetDataDefProperty_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOGetDataDefProperty extends NGOResponse {

	private String Option;

	private DataDefinition DataDefinition;

	public String getOption() {
		return Option;
	}

	public void setOption(String Option) {
		this.Option = Option;
	}

	public DataDefinition getDataDefinition() {
		return DataDefinition;
	}

	public void setDataDefinition(DataDefinition DataDefinition) {
		this.DataDefinition = DataDefinition;
	}

}
