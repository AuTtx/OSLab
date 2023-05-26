import java.util.HashMap;
import java.util.Map;

/**
 * @author hxq
 * @date 2022/5/10 上午9:19
 */
public class MasterFD {
    public Map<User, UserFD> masterFDItems = new HashMap<>(); //主目录项

    // 根据用户名得到用户，存在则返回用户，失败则返回null
    public User getUserByName(String username) {

        for(User user : masterFDItems.keySet()) {
            if(user.userName .equals( username) ) {
                return user;
            }
        }
        return null;
    }

    public void addUser(User user, UserFD userFD) {
        if(masterFDItems.keySet().size()>8) {
            System.out.println("用户数量太多");
            return;
        }

        masterFDItems.put(user,userFD);
    }


    @Override
    public String toString() {
        return "MasterFD{" +
                "masterFDTable=" + masterFDItems +
                '}';
    }
}
