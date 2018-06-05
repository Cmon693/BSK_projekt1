package sample;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.security.Key;
import java.util.List;



@XmlRootElement(name="EncryptedFileHeader")
public class EncryptedFileHeader {

    private String Algorithm;
    private String KeySize;
    private String BlockSize;
    private String CipherMode;
    private String IV;
    private String ApprovedUsers;
    //private String SessionKey;

    @XmlElements(@XmlElement(name = "Algorithm"))
    public String getAlgorithm() {
        return Algorithm;
    }

    public void setAlgorithm(String Algorithm) {
        this.Algorithm = Algorithm;
    }

    @XmlElements(@XmlElement(name = "KeySize"))
    public String getKeySize() {
        return KeySize;
    }

    public void setKeySize(String KeySize) {
        this.KeySize = KeySize;
    }

    @XmlElements(@XmlElement(name = "BlockSize"))
    public String getBlockSize() {
        return BlockSize;
    }

    public void setBlockSize(String BlockSize) {
        this.BlockSize = BlockSize;
    }

    @XmlElements(@XmlElement(name = "CipherMode"))
    public String getCipherMode() {
        return CipherMode;
    }

    public void setCipherMode(String CipherMode) {
        this.CipherMode = CipherMode;
    }

    @XmlElements(@XmlElement(name = "IV"))
    public String getIV() {
        return IV;
    }

    public void setIV(String IV) {
        this.IV = IV;
    }

//    @XmlElements(@XmlElement(name = "ApprovedUsers"))
//    public String getApprovedUsers() {
//        return ApprovedUsers;
//    }

//    @XmlElement(name="ApprovedUsers", type=ApprovedUsers.class)
//    List<ApprovedUsers> ApprovedUsersList;
//
//    public void setApprovedUsersList(List<ApprovedUsers> ApprovedUsersList) {
//        this.ApprovedUsersList = ApprovedUsersList;
//    }

    @XmlElement(name="User", type=User.class)
    List<User> userList;

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }



}


