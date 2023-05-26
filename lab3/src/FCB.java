import java.io.File;

/**
 * @author hxq
 * @date 2022/5/10 上午9:22
 */
public class FCB {
    public String fileName = ""; //文件名
    public enum Permission{ //保护字段枚举
        R,//读
        W,//写
        X,//执行
        RW // 读写
    }
    public Permission permission = Permission.R;//保护字段
    public int fileLeng = 0;//长度
    public File file = new File(fileName);//disk file

    public FCB(String fileName, Permission permission, int fileLeng,File file) {
        this.fileName = fileName;
        this.permission = permission;
        this.fileLeng = fileLeng;
        this.file = file;
    }

    public FCB(){}

    @Override

    public String toString() {
        return fileName+"{" +
                "fileName='" + fileName + '\'' +
                ", permission=" + permission +
                ", fileLeng=" + fileLeng +
                ", file=" + file.getName() +
                '}';
    }
}
