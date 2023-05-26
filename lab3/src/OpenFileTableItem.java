import java.io.*;

/**
 * @author hxq
 * @date 2022/5/10 上午9:27
 */
public class OpenFileTableItem {
    public User user = null; //用户
    public FCB fcb = new FCB();
    public int state = 0; //状态，0为空，1为满
    public int readPointer = 0;//文件读指针
    public int writePointer = fcb.fileLeng-1;
    public byte[] file = new byte[10]; // memory mapped file
    public int dirty=0; // dirty bit, 0 : clean, 1 : dirty

    public OpenFileTableItem(User user, FCB fcb) throws IOException{

            this.user = user;
            this.fcb = fcb;
            this.state = 1;
            this.file = new byte[fcb.fileLeng];
            this.writePointer = 0;
            this.readPointer = 0;
            // read the file and write the memory mapper file
        if(fcb.fileLeng != 0) {
            BufferedReader br = new BufferedReader(new FileReader(fcb.file));
            int b = -1;
            file = new byte[fcb.fileLeng];
            while((b = br.read()) != -1){
                file[writePointer++]=(byte)b;
            }
            System.out.println(new String(file));


            br.close();
        }

    }

    public OpenFileTableItem(){

    }


    @Override
    public String toString() {
        String str = "";
        if(user == null) {
            return "";
        }
        if (fcb.fileLeng == 0) {
            str = "OpenFileTableItem{" +
                    "user=" + user +
                    ", fcb=" + fcb +
                    ", state=" + state +
                    ", filePointer=" + readPointer +
                    ", dirty=" + dirty +
                    '}';

        } else {
            str = "OpenFileTableItem{" +
                    "user=" + user +
                    ", fcb=" + fcb +
                    ", state=" + state +
                    ", filePointer=" + readPointer +
                    ", file='" + new String(file) + '\'' +
                    ", dirty=" + dirty +
                    '}';
        }

        return str;
    }
}
