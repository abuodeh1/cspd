package omnidocs.pojo;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "DataDefinition")
@XmlAccessorType(XmlAccessType.FIELD)
public class DataDefinition {
	
    private String DataDefIndex;
    
    @XmlJavaTypeAdapter(OFieldAdapter.class)
	@XmlElement(name = "Fields")
    private Map<String, ? extends Field> fields = new HashMap<>();
	
    private String DataDefName;

    public String getDataDefIndex ()
    {
        return DataDefIndex;
    }

    public void setDataDefIndex (String DataDefIndex)
    {
        this.DataDefIndex = DataDefIndex;
    }

    public Map<String, Field> getFields() {
		//return (Map<String, OField>) super.getFields();
		return (Map<String, Field>) fields;
	}

	public void setFields(Map<String, ? extends Field> fields) {
		//super.setFields(fields);
		this.fields = fields;
	}

    public String getDataDefName ()
    {
        return DataDefName;
    }

    public void setDataDefName (String DataDefName)
    {
        this.DataDefName = DataDefName;
    }

}