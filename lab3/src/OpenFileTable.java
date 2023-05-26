import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author hxq
 * @date 2022/5/10 上午9:24
 */
public class OpenFileTable {
    int length = 16; // 系统打开文件表项的数目

    OpenFileTableItem[] openFileTableItems = new OpenFileTableItem[length];//系统打开文件表集合，长度固定

    public OpenFileTable() {
        for(int i = 0; i < length; ++i) {
            openFileTableItems[i] = new OpenFileTableItem();
        }
    }

    public int getUserOpenNum(String username) {
        int count = 0;
        for(int i = 0; i < openFileTableItems.length; ++i) {
            if(openFileTableItems[i].user.userName .equals( username) && openFileTableItems[i].state == 1) {
                count ++;
            }
        }
        return count;
    }
//

    public void addUserFile(User user, FCB fcb) {

            if(getUserOpenNum(user.userName) > 10) {
                System.out.println("保存文件太多");
                return;
            }
            user.userFD.FCBs.add(fcb);

    }

    public int getFreeItem() {
        // 存在空闲项
        for(int i = 0; i < length; i++) {
            if(openFileTableItems[i].state == 0) {
                return i;
            }
        }
        // 不存在空闲项
        return -1;
    }

    @Override
    public String toString() {
        return "OpenFileTable{" +
                "length=" + length +
                ", openFileTableItems=" + Arrays.toString(openFileTableItems) +
                '}';
    }
}
