/**
 * @author hxq
 * @date 2022/5/10 上午9:20
 */
public class User {
    public String userName; // 用户名
//    public int userOpenNum; // 用户打开的文件数
//    public int userSaveNum; // 用户保存的文件数
    public UserFD userFD; // 用户文件目录

    public User(String userName) {
        this.userName = userName;
        this.userFD = new UserFD();
//        this.userOpenNum = 0;
//        this.userSaveNum = 0;
    }

    public User() {

    }

    public int getSeveNum() {
        return userFD.FCBs.size();
    }





    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
//                ", userOpenNum=" + userOpenNum +
//                ", userSaveNum=" + userSaveNum +
                ", userFD=" + userFD +
                '}';
    }
}
