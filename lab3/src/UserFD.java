import java.util.ArrayList;
import java.util.List;

/**
 * @author hxq
 * @date 2022/5/10 上午9:20
 */
public class UserFD {
    public List<FCB> FCBs = new ArrayList<>(); //用户文件目录项集合

    // 根据文件名找文件，如果没找到返回null
    public FCB getFileByName(String filename) {
        for(FCB fcb : FCBs) {
            if(fcb.fileName.equals(filename)) {
                return fcb;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return "UserFD{" +
                "userFDTable=" + FCBs +
                '}';
    }
}
