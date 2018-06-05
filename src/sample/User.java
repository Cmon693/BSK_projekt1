package sample;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "User")
public class User
{
    private String Email;
    private String SessionKey;

    public String getEmail() {
        return Email;
    }
    @XmlElement
    public void setEmail(String Email) {
        this.Email = Email;
    }

    public String getSessionKey() {
        return SessionKey;
    }
    @XmlElement
    public void setSessionKey(String SessionKey) {
        this.SessionKey = SessionKey;
    }

}