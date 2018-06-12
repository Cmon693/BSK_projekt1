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
    private int KeySize;
    private String BlockSize;
    private String CipherMode;
    private byte[] IV;
    private String ApprovedUsers;
    private String FileExtension;
    private byte[] SessionKey;
    private String Extension;
    private String Login;

    @XmlElements(@XmlElement(name = "Algorithm"))
    public String getAlgorithm() {
        return Algorithm;
    }

    public void setAlgorithm(String Algorithm) {
        this.Algorithm = Algorithm;
    }

    @XmlElements(@XmlElement(name = "KeySize"))
    public int getKeySize() {
        return KeySize;
    }

    public void setKeySize(int KeySize) {
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
    public byte[] getIV() {
        return IV;
    }

    public void setIV(byte[] IV) {
        this.IV = IV;
    }

    @XmlElements(@XmlElement(name = "FileExtension"))
    public String getFileExtension() {
        return FileExtension;
    }

    public void setFileExtension(String FileExtension) {
        this.FileExtension = FileExtension;
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
    public byte[] getSessionKey() {
        return SessionKey;
    }
    @XmlElement
    public void setSessionKey(byte[] SessionKey) {
        this.SessionKey = SessionKey;
    }

    @XmlElements(@XmlElement(name = "Login"))
    public String getLogin() {
        return Login;
    }

    public void setLogin(String Login) {
        this.Login = Login;
    }

    @XmlElement(name="User", type=User.class)
    List<User> userList;

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }



}


