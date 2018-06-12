package sample;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "User")
public class User
{
    private String Email;
    private byte[] SessionKey;

    public String getEmail() {
        return Email;
    }
    @XmlElement
    public void setEmail(String Email) {
        this.Email = Email;
    }

    public byte[] getSessionKey() {
        return SessionKey;
    }
    @XmlElement
    public void setSessionKey(byte[] SessionKey) {
        this.SessionKey = SessionKey;
    }

}