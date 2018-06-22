package omnidocs.pojo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ThumbNail")
@XmlAccessorType(XmlAccessType.FIELD)
public class ThumbNail
{
    private String PageNo;

    private String ImageData;

    public String getPageNo ()
    {
        return PageNo;
    }

    public void setPageNo (String PageNo)
    {
        this.PageNo = PageNo;
    }

    public String getImageData ()
    {
        return ImageData;
    }

    public void setImageData (String ImageData)
    {
        this.ImageData = ImageData;
    }

    @Override
    public String toString()
    {
        return "ThumbNail [PageNo = "+PageNo+", ImageData = "+ImageData+"]";
    }
}