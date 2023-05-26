import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * @author hxq
 * @date 2022/5/17 上午8:51
 */
public class Test {
    FileSystem fileSystem = new FileSystem();
    List<User> users = new ArrayList<>();
    List<String> usernames = new ArrayList<String>();

    public void initDiskFiles() {

        JSONObject dataJson = ReadJson("/Users/autt/Desktop/OSLab-master/lab3/diskfiles");//new JSONObject(data);
        JSONArray usersJson = dataJson.getJSONArray("users");
        for (int i = 0; i < usersJson.length(); i++) {
            JSONObject userJson = usersJson.getJSONObject(i);
            JSONArray fcbsJson = userJson.getJSONArray("fcbs");
            int fcblengJson = fcbsJson.length();
            List<FCB> fcbs = new ArrayList<>();
            for (int j = 0; j < fcblengJson; ++j) {
                JSONObject fcbJson = fcbsJson.getJSONObject(j);

                FCB.Permission permission;
                switch (fcbJson.getString("permission")) {
                    case "R":
                        permission = FCB.Permission.R;
                        break;
                    case "W":
                        permission = FCB.Permission.W;
                        break;
                    case "X":
                        permission = FCB.Permission.X;
                        break;
                    case "RW":
                        permission = FCB.Permission.RW;
                        break;
                    default:
                        permission = FCB.Permission.R;
                        break;
                }
                FCB fcb = new FCB(fcbJson.getString("filename"), permission, fcbJson.getInt("fileLeng"), new File(fcbJson.getString("location")));
                fcbs.add(fcb);
            }

            User user = new User(userJson.getString("username"));
            user.userFD.FCBs = fcbs;
            users.add(user);

        }
        System.out.println("dataJson:"+dataJson.toString());
        System.out.println("users:"+users);
    }

    public void init() {
        initDiskFiles();
        for(User user : users) {
            usernames.add(user.userName);
            fileSystem.masterFD.masterFDItems.put(user, user.userFD);
        }


        System.out.println("usernames:"+usernames);
        System.out.println("master file directory:"+fileSystem.masterFD.masterFDItems);
    }

    public void saveDiskFiles() {
        JSONObject datajson = new JSONObject();
        JSONArray usersjson = new JSONArray();
        for(User user : users) {
            JSONObject userJson = new JSONObject();
            userJson.put("username",user.userName);
            JSONArray fcbJsons = new JSONArray();
            for(FCB fcb : user.userFD.FCBs) {
                JSONObject fcbJson = new JSONObject();
                fcbJson.put("filename",fcb.fileName);
                fcbJson.put("permission",fcb.permission.toString());
                fcbJson.put("fileLeng",fcb.fileLeng);
                fcbJson.put("location",fcb.file.getAbsolutePath());
                fcbJsons.put(fcbJson);
            }
            userJson.put("fcbs",fcbJsons);
            usersjson.put(userJson);

        }
        datajson.put("users",usersjson);

        System.out.println("datajson:"+datajson);

        WriteJson("/Users/autt/Desktop/OSLab-master/lab3/diskfiles",datajson);
    }

    public static JSONObject ReadJson(String Path) {
        JSONObject dataJson = null;
        //读取json文件
        try {
            BufferedReader br = new BufferedReader(new FileReader(Path));
            String str = null;
            StringBuilder sb = new StringBuilder();
            while ((str = br.readLine()) != null) {
                sb.append(str).append("\n");
            }
            dataJson = new JSONObject(sb.toString());
            br.close();
        }
        catch (IOException | JSONException e) {

            e.printStackTrace();
        }
        return dataJson;
    }

    public static void WriteJson(String Path, JSONObject dataJson) {
        //写入json文件
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(Path));
            String ws = dataJson.toString();
            System.out.println(ws);
            bw.write(ws);
            // bw.newLine();
            bw.flush();
            bw.close();

        } // 读取原始json文件
        catch (IOException | JSONException e) {

            e.printStackTrace();
        }
    }




    public void demo() {
        try{
            System.out.println("列出u0的文件");
            fileSystem.dir("u0");
            System.out.println("");

            System.out.println("打开u0用户的f1文件");
            int i1 = fileSystem.open("u0","f1"); // i1=0
            System.out.println(" ");


            System.out.println("创建u2用户的f3文件");
            int i3 = fileSystem.create("u2","f3", FCB.Permission.W); //i3=1
            System.out.println(" ");

            System.out.println("创建u0用户的f4文件（已经存在）");
            int i4 = fileSystem.create("u0","f4",FCB.Permission.R);//i2=2
            System.out.println("");

            System.out.println("删除u0用户的f2文件");
            fileSystem.delete("u0","f2");
            System.out.println(" ");

            // read continuously, file pointer will move
            System.out.println("读f1（可读）的3个字符");
            byte[] cache1 = new byte[10];
            fileSystem.read(i1,cache1,3);
            System.out.println(" ");

            System.out.println("读f1（可读）的2个字符");
            cache1 = new byte[10];
            fileSystem.read(i1,cache1,2);
            System.out.println(" ");
//
//
            // read illegally
            System.out.println("读f3（可写）的2个字符");
            fileSystem.read(i3,cache1,2);
            System.out.println(" ");

            System.out.println("向f3末尾追加缓冲区（hxq）中的前两个字符");
            byte[] cache3 = "hxq".getBytes();
            fileSystem.write(i3,cache3,2);
            System.out.println("");

            System.out.println("向f3末尾追加缓冲区（hxq）中的前两个字符");
            fileSystem.write(i3,cache3,2);
            System.out.println("");
//
//
            System.out.println("关闭f1");
            fileSystem.close(i1);
            System.out.println(" ");

            System.out.println("关闭f3");
            fileSystem.close(i3);
            System.out.println(" ");


            System.out.println("关闭f4");
            fileSystem.close(i4);
            System.out.println(" ");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void service() throws IOException{
        int request = -1; // 请求类型，1 dir，2 open 打开文件 3 close 关闭文件 4 create 创建文件 5 delete 删除文件 6 read 读文件 7 write 写文件 8 退出
        boolean flag = true;
        while (flag) {
            // input:
            System.out.println("请输入请求编号（1 dir 列文件目录，2 open 打开文件 3 close 关闭文件 4 create 创建文件 5 delete 删除文件 6 read 读文件 7 write 写文件 8 退出）");
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            request = Integer.parseInt(scanner.readLine());
            if (request != 1 && request != 2 && request != 3 && request != 4 && request != 5 && request != 6 && request != 7 && request != 8) {
                System.out.println("输入请求编号错误");
                continue;
            }

            String username = "";
            String filename = "";
            int descriptor  = -1;
            FCB.Permission permission = FCB.Permission.R;
            String cacheString = "";
            int size = -1;
            User user = new User();
            switch (request){
                case 1:
//
                    username = getUsername();
                    fileSystem.dir(username);
                    break;
                case 2:
//
                    username = getUsername();
                     user = fileSystem.masterFD.getUserByName(username);
                    filename = getFilename(user);
                    fileSystem.open(username,filename);
                    break;
                case 3:
                    descriptor = getDescriptor();
                    fileSystem.close(descriptor);
                    break;
                case 4:
                    username = getUsername();
                     user = fileSystem.masterFD.getUserByName(username);
                    System.out.println("请输入文件名");


                    BufferedReader scanner1 = new BufferedReader(new InputStreamReader(System.in));
                    filename = (scanner1.readLine());

                    permission = getPermission();
                    fileSystem.create(username,filename,permission);
                    break;
                case 5:
                    username = getUsername();
                    user = fileSystem.masterFD.getUserByName(username);
                    filename = getFilename(user);
                    fileSystem.delete(username,filename);
                    break;
                case 6:
                    descriptor = getDescriptor();

                    size = getSize();
                    int fileLeng = fileSystem.openFileTable.openFileTableItems[descriptor].fcb.fileLeng;
                    if(size > fileLeng || size < 0 ) {
                        System.out.println("输入大小错误");
                    } else {
                        fileSystem.read(descriptor,cacheString.getBytes(),size);
                    }

                    break;
                case 7:
                    descriptor = getDescriptor();
                    size = getSize();
                    cacheString = getCache();
                    if(size > cacheString.length() || size < 0) {
                        System.out.println("输入大小错误");
                    } else {
                        fileSystem.write(descriptor,cacheString.getBytes(),size);
                    }
                    break;
                case 8:
                    fileSystem.exit();
                    saveDiskFiles();
                    flag = false;
                    break;
                default:
                    break;
            }
        }
    }

    public String getUsername() throws IOException{
        String username = "";

        while(true) {
            System.out.println("请输入用户名:u0~u7");
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            username = (scanner.readLine());
            if(!usernames.contains(username)) {
                System.out.println("用户名错误");
                continue;
            } else {
                break;
            }
        }
        return username;

    }

    public String getFilename(User user) throws IOException {
        String filename = "";
        while(true) {
            System.out.println("请输入文件名");
            StringBuilder sb  = new StringBuilder("该用户文件有：");
            List<String> filenames = new ArrayList<>();
            for(FCB fcb : fileSystem.masterFD.masterFDItems.get(user).FCBs) {
                filenames.add(fcb.fileName);
                sb.append(fcb.fileName+" ");
            }
            System.out.println(sb.toString());
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            filename = (scanner.readLine());
            if(!filenames.contains(filename)) {
                System.out.println("文件名错误");
               continue;
            } else {
                break;
            }
        }
        return filename;


    }


    public int getDescriptor( ) throws  IOException{
        int descriptor =-1;
        while(true) {
            System.out.println("请输入文件描述符:0~15");
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            descriptor = Integer.parseInt(scanner.readLine());
            if(descriptor < 0 || descriptor > 15) {
                System.out.println("文件描述符错误");
                continue;
            }
            else {
                break;
            }
        }
        return descriptor;

    }

    public int getSize() throws IOException{
        int size =-1;

        System.out.println("请输入大小");
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        size = Integer.parseInt(scanner.readLine());
//
        return size;
    }

    public FCB.Permission getPermission() throws IOException {
        int permission = -1;
        while(true) {
            System.out.println("请输入保护字段:1 可读，2 可写，3 可执行，4 可读写");
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            permission = Integer.parseInt(scanner.readLine());
            if (permission < 0 || permission > 4) {
                System.out.println("文件描述符错误");
                continue;
            } else {
                switch (permission) {
                    case 1:
                        return FCB.Permission.R;
                    case 2:
                        return FCB.Permission.W;
                    case 3:
                        return FCB.Permission.X;
                    case 4:
                        return FCB.Permission.RW;
                    default:
                        return FCB.Permission.R;
                }
            }

        }
    }

    public String getCache() throws IOException {
        String cache = "";
        System.out.println("请输入缓存区内容");

        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        cache = (scanner.readLine());
        return cache;
    }

    public static void main(String[] args) throws IOException {
        Test test = new Test();
        test.init();

        test.service();
    }

}
