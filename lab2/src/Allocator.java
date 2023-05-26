import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

/**
 * @author hxq
 * @date 2022/5/4 下午11:03
 */
public class Allocator {
    public Memory memory = new Memory(); // 内存


    // 分配和回收服务
    public void service() throws IOException {
        int request = -1; // 请求类型，1为申请内存，2为释放内存，3为退出
        int leng = -1; // 申请的内存块的长度
        int index = -1; // 释放的内存块编号
        while(true) {
            // input:
            System.out.println("请输入请求编号（1.申请内存，2.释放内存，3.退出）");
            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            request = Integer.parseInt(scanner.readLine());
            if(request != 1 && request != 2 && request!= 3) {
                System.out.println("输入请求编号错误");
                continue;
            }

            // 退出
            if(request == 3) {
                System.out.println("退出！");
                break;
            }

            // 申请内存
            if(request == 1) {

                System.out.println("请输入申请内存的长度");
                leng = Integer.parseInt(scanner.readLine());
                allocate(leng);
                continue;
            } else { // 释放内存
                if(memory.allocatedBlocks.size() == 0) {
                    System.out.println("无可以释放的内存块");
                    continue;
                }
                System.out.println("已经分配的内存块编号为：0~"+(memory.allocatedBlocks.size()-1));
                System.out.println("请输入释放的内存块编号:");
                index = Integer.parseInt(scanner.readLine());
                if(index < 0 && index >= memory.allocatedBlocks.size()) {
                    System.out.println("释放的内存块编号不合法！");
                }
                recycle(memory.allocatedBlocks.get(index));
                continue;
            }

        }

    }

    /**
     * 分配服务，若成功分配，将分配的内存块加入allocatedBlocks
     * @param leng 请求的内存块长度
     * @return 成功则分配内存块，失败返回null!
     */
    public Block allocate(int leng) {

        Iterator<FreeBlock> it = memory.FBs.iterator();
        FreeBlock fb;
        boolean success = false; // 分配是否成功
        boolean flag = false; // 是否需要改变空闲内存号
        Block block = null;
        // 查找是否有符合条件的空闲内存块
        while(it.hasNext()) {
            fb = it.next();
            // 如果存在空闲内存块的长度大于等于请求的长度，分配成功
            // 首次适应算法
            if(fb.length >= leng) {
                int allocatedInitAddr = fb.initAddr+fb.length - leng;
                block = new Block(allocatedInitAddr,leng);
                System.out.println("分配成功，在"+fb+"分配空间，分配的内存块为："+block);
                memory.allocatedBlocks.add(block);
                success = true;

                // 剩余长度
                int leftLeng = fb.length - leng;
                // 剩余长度大于0，只要修改空闲内存块的长度
                if(leftLeng >0) {
                    fb.length = leftLeng;
                } else{// 剩余长度等于0， 要修改空闲内存号
                    it.remove();
                    flag = true;
                }
                break;
            }

        }
        // 分配失败，直接退出
        if(!success ){
            System.out.println("分配失败");

            return block;
        }

        // 修改空闲内存号
        if(flag ) {
            changeFBNum();
        }

        printFBs();

        return block;



    }

    /**
     * 回收内存块
     * @param block 释放的内存块
     */
    public void recycle(Block block) {
        if(!memory.allocatedBlocks.contains(block)) {
            System.out.println("回收请求不合法");
            return;
        }

        System.out.println("回收成功！");
        // 回收成功，从已分配的分区列表中去掉
        memory.allocatedBlocks.remove(block);

        StringBuilder res = new StringBuilder();

        // 释放的内存块的起始地址
        int upper = block.initAddr ;
        //释放的内存块的终止地址
        int downer = block.initAddr + block.length-1 ;


        boolean up = false; // 是否有上邻居
        FreeBlock upNeighbor = null; // 上邻居
        boolean down = false; // 是否有下邻居
        FreeBlock downNeighbor = null; // 下邻居

        // 循环查找是否有上邻居和下邻居
        for(FreeBlock fb : memory.FBs) {
            int fbUpper = fb.initAddr;
            int fbDowner = fb.initAddr+fb.length-1;

            if(upper == fbDowner+1) {// 有上邻居
                up = true;
                upNeighbor = fb;
            }

            if(downer == fbUpper-1){// 有下邻居
                down = true;
                downNeighbor = fb;
            }
        }

        boolean flag = false; // 是否要修改空闲块号
        // 只有上邻居
        // 修改上邻居的地址长度
        if(up && !down) {
            res.append("只有上邻居");
            upNeighbor.length +=block.length;
        }
        // 只有下邻居
        // 修改下邻居的地址长度和起始地址
        if(!up && down) {
            res.append("只有下邻居");
            downNeighbor.length += block.length;
            downNeighbor.initAddr = block.initAddr;
        }
        //有上邻居和下邻居
        // 修改上邻居的地址长度
        // 删除下邻居
        // 空闲分区链的空闲块号要修改
        if(up && down) {
            res.append("有上邻居和下邻居");
            upNeighbor.length +=block.length + downNeighbor.length;
            memory.FBs.remove(downNeighbor);
            flag = true;
        }
        // 没有上下邻居
        // 分配空闲块号，增加空闲块号
        // 修改空闲块号
        if(!up && !down) {

            res.append("没有上下邻居");
            flag = true;
            int index = -1;
            FreeBlock lastBlock = memory.FBs.getLast();
            if (block.initAddr > lastBlock.initAddr + lastBlock.length - 1) {
                // 插入最后
                FreeBlock newFB = new FreeBlock(memory.FBs.size(), block.initAddr, block.length);

                memory.FBs.add(newFB);
            }

            Iterator<FreeBlock> it = memory.FBs.iterator();
            while (it.hasNext()) {
                FreeBlock fb1 = it.next();
                if (fb1.initAddr > block.initAddr) {

                    index = fb1.num;

                    break;
                }
            }

            // 添加到index位置上
            memory.FBs.add(index, new FreeBlock(index, block.initAddr, block.length));

        }

        if(flag) {
            changeFBNum();
        }
        System.out.println(res.toString());

        printFBs();
    }



    /**
     * 修改空闲分区链的空闲块号
     */
    public void changeFBNum() {
        int size = memory.FBs.size();
        for(int i = 0; i < size; ++i) {
            memory.FBs.get(i).num = i;
        }
    }

    /**
     * 打印空闲块链表
     */
    public void printFBs(){
        System.out.println("此时的空闲块链表为：");
        for(FreeBlock fb : memory.FBs){
            System.out.println(fb);
        }
        System.out.println(" ");
    }

    public void init() {
        int size = 160;

        FreeBlock fb1 = new FreeBlock(0,0,128);
        FreeBlock fb2 = new FreeBlock(1,130,2);
        FreeBlock fb3 = new FreeBlock(2,140,2);
        FreeBlock fb4 = new FreeBlock(3,150,5);

        memory.FBs.add(fb1);
        memory.FBs.add(fb2);
        memory.FBs.add(fb3);
        memory.FBs.add(fb4);

        System.out.println("初始化内存......");
        printFBs();

    }

    public void serviceDemo1() {
        allocate(10);
        System.out.println("");
        allocate(20);
        System.out.println("");
        allocate(5);
        System.out.println("");
        allocate(1);
        System.out.println("");
        allocate(1);
        System.out.println("");
        allocate(50);
        System.out.println("");

        recycle(memory.allocatedBlocks.get(0));
        System.out.println("");
        recycle(memory.allocatedBlocks.get(2));
        System.out.println("");
        recycle(memory.allocatedBlocks.get(0));
        System.out.println("");
        recycle(memory.allocatedBlocks.get(0));
        System.out.println("");
        recycle(memory.allocatedBlocks.get(0));
    }



    public static void main(String[] args) {
        Allocator allocator = new Allocator();
        allocator.init();
        //allocator.serviceDemo1();
        try{
            allocator.service();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
