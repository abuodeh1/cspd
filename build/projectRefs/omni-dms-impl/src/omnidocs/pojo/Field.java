package omnidocs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Field")
@XmlAccessorType(XmlAccessType.FIELD)
public class Field {
	
	private String IndexType;

    private String IndexFlag;

    private String UsefulInfoFlag;

    private String IndexAttribute;

    private String IndexId;

    private String IndexName;

    private String UsefulInfoSize;

    private String IndexLength;

    private String IndexValue;

    public String getIndexType ()
    {
        return IndexType;
    }

    public void setIndexType (String IndexType)
    {
        this.IndexType = IndexType;
    }

    public String getIndexFlag ()
    {
        return IndexFlag;
    }

    public void setIndexFlag (String IndexFlag)
    {
        this.IndexFlag = IndexFlag;
    }

    public String getUsefulInfoFlag ()
    {
        return UsefulInfoFlag;
    }

    public void setUsefulInfoFlag (String UsefulInfoFlag)
    {
        this.UsefulInfoFlag = UsefulInfoFlag;
    }

    public String getIndexAttribute ()
    {
        return IndexAttribute;
    }

    public void setIndexAttribute (String IndexAttribute)
    {
        this.IndexAttribute = IndexAttribute;
    }

    public String getIndexId ()
    {
        return IndexId;
    }

    public void setIndexId (String IndexId)
    {
        this.IndexId = IndexId;
    }

    public String getIndexName ()
    {
        return IndexName;
    }

    public void setIndexName (String IndexName)
    {
        this.IndexName = IndexName;
    }

    public String getUsefulInfoSize ()
    {
        return UsefulInfoSize;
    }

    public void setUsefulInfoSize (String UsefulInfoSize)
    {
        this.UsefulInfoSize = UsefulInfoSize;
    }

    public String getIndexLength ()
    {
        return IndexLength;
    }

    public void setIndexLength (String IndexLength)
    {
        this.IndexLength = IndexLength;
    }

    public String getIndexValue ()
    {
        return IndexValue;
    }

    public void setIndexValue (String IndexValue)
    {
        this.IndexValue = IndexValue;
    }

    @Override
    public String toString()
    {
        return "Field [IndexType = "+IndexType+", IndexFlag = "+IndexFlag+", UsefulInfoFlag = "+UsefulInfoFlag+", IndexAttribute = "+IndexAttribute+", IndexId = "+IndexId+", IndexName = "+IndexName+", UsefulInfoSize = "+UsefulInfoSize+", IndexLength = "+IndexLength+", IndexValue = "+IndexValue+"]";
    }
}