package etech.omni.ngo;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import omnidocs.pojo.User;

@XmlRootElement(name = "NGOGetUserProperty_Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class NGOGetUserProperty extends NGOResponse {
	
	private User User;

    private String Option;

    public User getUser ()
    {
        return User;
    }

    public void setUser (User User)
    {
        this.User = User;
    }

    public String getOption ()
    {
        return Option;
    }

    public void setOption (String Option)
    {
        this.Option = Option;
    }
    
}