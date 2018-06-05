package sample;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name="ApprovedUsers")
 class ApprovedUsers2 {

    @XmlElement(name="User", type=User.class)
    List<User> userList;

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
