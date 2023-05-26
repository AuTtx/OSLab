import java.util.*;

/**
 * @author hxq
 * @date 2022/5/4 下午11:03
 */
public class Memory {
    public int size; // 内存大小


    public LinkedList<FreeBlock> FBs = new LinkedList<>(); // 空闲分区链表
    public List<Block> allocatedBlocks = new ArrayList<>();// 已分配的分区列表



    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("内存{" +
                "长度=" + size +"空闲分区表:"+"\n");
        for(FreeBlock fb : FBs) {
            sb.append(fb+"\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
