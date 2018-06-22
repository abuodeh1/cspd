package etech.omni.ngo;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.CabinetInfo;

@XmlRootElement(name = "NGOGetListOfCabinets_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOGetListOfCabinets extends NGOResponse {

	private String Encoding;

	private String Option;
	
	@XmlElementWrapper(name="Cabinets")
	@XmlElement(name = "CabinetStructure")
	private List<CabinetInfo> CabinetInfos;

	public String getEncoding() {
		return Encoding;
	}

	public void setEncoding(String Encoding) {
		this.Encoding = Encoding;
	}

	public String getOption() {
		return Option;
	}

	public void setOption(String Option) {
		this.Option = Option;
	}

	public List<CabinetInfo> getCabinets() {
		return CabinetInfos;
	}

	public void setCabinets(List<CabinetInfo> cabinetInfos) {
		this.CabinetInfos = CabinetInfos;
	}

}
