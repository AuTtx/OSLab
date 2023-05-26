import java.io.*;
import java.util.Arrays;


/**
 * @author hxq
 * @date 2022/5/10 上午9:26
 */
public class FileSystem {
    MasterFD masterFD = new MasterFD(); //主目录
    OpenFileTable openFileTable = new OpenFileTable(); //系统打开文件目录


    /**
     * 列文件目录
     * @param username 用户名
     */
    void dir(String username) {
        // 用户名不存在，直接返回
        User user = masterFD.getUserByName(username);
        if(user== null) {
            System.out.println("用户不存在");
            return ;
        }

        StringBuilder sb = new StringBuilder();
        // 显示该用户文件目录的内容，包含文件名、文件长度，保护字段。
        for(FCB fcb : masterFD.masterFDItems.get(user).FCBs) {
            sb.append(fcb);
        }

        if (sb.length() == 0) {

            System.out.println("没有文件");
        } else {
            System.out.println(sb.toString());
        }
    }

    /**
     * 打开文件, 异常情况返回-1
     * @param username 用户名
     * @param fileName 文件名
     * @return
     */
    int open(String username, String fileName) throws IOException{
        // 查找用户
        User user = masterFD.getUserByName(username);
        if(user ==null) {
            System.out.println("用户不存在");
            return -1;
        }
        // 查找文件
        FCB fcb = masterFD.masterFDItems.get(user).getFileByName(fileName);
        if(fcb == null ) {
            System.out.println("文件不存在");
            return -1;
        }
        int i = -1;// 文件描述符
        boolean flag = false;// 文件是否已经打开
        // 重复打开：该文件存在于系统打开文件表中
        // 文件读写指针不变
        for(int count = 0; count < openFileTable.openFileTableItems.length; ++count) {
            if(openFileTable.openFileTableItems[count].fcb.fileName .equals( fileName) && openFileTable.openFileTableItems[count].state == 1) {

                i = count;
                flag = true;
                String file = "/Users/autt/Desktop/OSLab-master/lab3"+username+"/"+fileName;
                System.out.println("重复打开,文件描述符:"+i+",文件:"+file);
                return i;
            }
        }
        // 申请一个空闲的系统打开文件表项
         i = openFileTable.getFreeItem();
        if(i == -1) {
            System.out.println("打开文件过多");
            return -1;
        }
        //将文件目录复制到系统打开文件表项中并初始化文件读写指针
        openFileTable.openFileTableItems[i] =   new OpenFileTableItem(user,fcb);
        OpenFileTableItem o = openFileTable.openFileTableItems[i];

        String file = "/Users/autt/Desktop/OSLab-master/lab3"+o.user.userName+"/"+o.fcb.fileName;
        System.out.println("打开成功,文件描述符:"+i+",文件:"+file);

        System.out.println("文件打开目录:"+openFileTable);


        // 返回文件描述符
        return i;
    }

    /**
     * 关闭文件
     * @param descriptor 文件描述符
     */
    void close(int descriptor) throws IOException{
        if(descriptor < 0 || descriptor > 15) {
            System.out.println("文件描述符不存在");
            return;
        }
        //将文件描述符所指系统打开文件表项释放，使其成为空闲表项
        OpenFileTableItem o = openFileTable.openFileTableItems[descriptor];
        o.state = 0;

        if(o.user!=null) {
            String file = "/Users/autt/Desktop/OSLab-master/lab3"+o.user.userName+"/"+o.fcb.fileName;
            // if dirty bit = 1, write back
            if(o.dirty == 1) {
                System.out.println("写回");
                FileWriter fileWriter = new FileWriter("/Users/autt/Desktop/OSLab-master/lab3"+o.user.userName+"/"+o.fcb.fileName);
                fileWriter.write("");
                fileWriter.flush();
                if(!Arrays.equals(o.file, new byte[0])) {
                    fileWriter.write(new String(o.file));
                    System.out.println(o.file);
                    fileWriter.flush();
                    fileWriter.close();
                }

            }



            System.out.println("关闭成功,文件描述符:"+descriptor+",文件:"+new String(file));
            System.out.println("文件打开目录:"+openFileTable);

        } else { // 关闭没有打开的文件
            System.out.println("关闭成功");
            System.out.println("文件打开目录:"+openFileTable);
        }

    }

    /**
     * 创建文件，异常返回-1
     * @param username 用户名
     * @param filename 文件名
     * @param permission 文件权限
     * @return 文件描述符
     * @throws IOException
     */
    int create(String username, String filename, FCB.Permission permission) throws IOException {
         //判断用户是否存在，若不存在显示错误信息并返回；
         User user = masterFD.getUserByName(username);
         if(user == null) {
             System.out.println("用户不存在");
             return -1;
         }

//
         FCB fcb = user.userFD.getFileByName(filename);
         if(fcb == null && user.getSeveNum() >10) {
             System.out.println("创建文件已达最大数目");
             return -1;
         }

         // 已有该文件，覆盖
         if(fcb!=null) {
             int i = -1; // 文件描述符
             boolean flag = false;// 已有的文件是否再系统打开文件表里面
             System.out.println("已有该文件，覆盖");

             // 在系统打开文件表里，覆盖文件，返回原有文件描述符（注意！文件读写指针要改变）
             for(int count = 0; count < openFileTable.openFileTableItems.length; ++count) {
                 if(openFileTable.openFileTableItems[count].fcb.fileName .equals( filename) && openFileTable.openFileTableItems[count].state == 1) {
                     i = count;
                     flag = true;
                     break;
                 }
             }
             if(!flag) {
                 //申请一个空闲的系统打开文件表项
                  i = openFileTable.getFreeItem();
                 if(i == -1) {
                     System.out.println("打开文件过多");
                     return -1;
                 }
             }


             fcb.fileLeng=0;


             fcb.permission = permission;
             //将文件目录复制到系统打开文件表项中并初始化文件读写指针
             openFileTable.openFileTableItems[i]=   new OpenFileTableItem(user,fcb);
             OpenFileTableItem o =openFileTable.openFileTableItems[i];
             o.file = new byte[0];
             o.dirty = 1;
             System.out.println("创建成功，文件描述符："+i);
             System.out.println("fcb："+fcb);
             System.out.println("文件打开目录:"+openFileTable);

             // 返回文件描述符
             return i;


         } else {//若没有找到指定文件,新建文件
             FCB.Permission permission1 = permission;
             File file = new File("/Users/autt/Desktop/OSLab-master/lab3"+username+"/"+filename);
             file.createNewFile();
             FCB fcb1 = new FCB(filename,permission1,0,file);


             //申请一个空闲的系统打开文件表项
             int i = openFileTable.getFreeItem();
             if(i == -1) {
                 System.out.println("打开文件过多");

             }
             //将文件目录复制到系统打开文件表项中并初始化文件读写指针
             openFileTable.openFileTableItems[i] =   new OpenFileTableItem(user,fcb1);

             // 在用户文件目录中加入该文件
             masterFD.masterFDItems.get(user).FCBs.add(fcb1);
             System.out.println("创建成功，文件描述符："+i);
             System.out.println("fcb："+fcb1);
             System.out.println("文件打开目录:"+openFileTable);


             // 返回文件描述符
             return i;


         }


    }


    /**
     * 删除文件
     * @param username 用户名
     * @param filename 文件名
     */
    void delete(String username, String filename) {
        User user = masterFD.getUserByName(username);
        if (user == null) {
            System.out.println("用户不存在");
            return;
        }
        FCB fcb = user.userFD.getFileByName(filename);
        if(fcb == null) {
            System.out.println("文件不存在");
            return;
        }

        //if open file table contains this file, release

        for(int i = 0; i < openFileTable.length; ++i) {
            if(openFileTable.openFileTableItems[i].fcb.equals(fcb)) {
                System.out.println("在系统打开文件表中删除");
                openFileTable.openFileTableItems[i].state = 0;
            }
        }

        // 用户目录中删去
        masterFD.masterFDItems.get(user).FCBs.remove(fcb);
        fcb.file.delete();
        System.out.println("删除成功，fcb:"+fcb);


    }


    /**
     * 读文件
     * @param descriptor 文件描述符
     * @param cache 缓存
     * @param size 大小
     */
    void read(int descriptor, byte[] cache, int size) {
        //判断用户文件描述符是否存在，若不存在显示错误信息并返回；
        if(descriptor <0 || descriptor>15) {
            System.out.println("用户文件描述符不存在");
            return ;
        } else if(openFileTable.openFileTableItems[descriptor].state == 0){
            System.out.println("未打开文件");
            return;
        }



        // 根据文件描述符，从系统打开文件表中得到该文件的文件读写指针,判断该文件是否允许读操作，若不允许则显示错误信息并返回；
        OpenFileTableItem o = openFileTable.openFileTableItems[descriptor];



        if(!o.fcb.permission.equals( FCB.Permission.R) && !o.fcb.permission.equals(FCB.Permission.RW)) {
            System.out.println("不允许读操作");
            return;
        }

        if(size < 0 || size + o.readPointer > o.fcb.fileLeng) {
            System.out.println("读取大小不合法");
            return ;
        }

        //按照本次读字符数从当前文件指针开始读数据到缓冲区中
        cache = new byte[size];
        for(int i = 0; i < size; ++i){
            cache[i]=o.file[o.readPointer++];
        }

        System.out.println("读文件结果:"+new String(cache));
    }

    // 写文件

    /**
     * 写文件
     * @param descriptor 文件描述符
     * @param cache 缓存
     * @param size 大小
     */
    void write(int descriptor, byte[] cache, int size) {
        //判断用户文件描述符是否存在，若不存在显示错误信息并返回
        if(descriptor <0 || descriptor>15) {
            System.out.println("用户文件描述符不存在");
            return ;
        } else if(openFileTable.openFileTableItems[descriptor].state == 0){
            System.out.println("用户文件描述符不存在");
            return;
        }

        //根据文件描述符找到相应的打开文件表项，判断该文件是否允许写操作，若不允许则显示错误信息并返回；
        OpenFileTableItem o = openFileTable.openFileTableItems[descriptor];

        if(!o.fcb.permission.equals( FCB.Permission.W) && !o.fcb.permission.equals(FCB.Permission.RW)) {
            System.out.println("不允许写操作");
            return;
        }

        if(size < 0 ) {
            System.out.println("写入大小不合法");
        }



        int initialLeng = o.fcb.fileLeng;
        byte[] oldFile = o.file;
        //按照本次写字符数从文件尾开始将缓冲区中的数据写到文件中，并修改目录中的文件大小
        // write back
        o.fcb.fileLeng +=size;
        System.out.println("写操作完成前，内存映射文件为："+new String(o.file));

        o.file = new byte[o.fcb.fileLeng];
        for(int i = 0; i < initialLeng; ++i) {
            o.file[i] = oldFile[i];
        }

        for(int i = 0; i < size; ++i){
            o.file[o.writePointer++]=cache[i];
        }
        System.out.println("写操作完成后,内存映射文件为："+new String(o.file));
        o.dirty = 1;
    }

    public void exit() throws IOException{
        for(int i = 0; i < 16; ++i) {
            if(openFileTable.openFileTableItems[i].state == 1) {
                close(i);
            }
        }


    }


}
