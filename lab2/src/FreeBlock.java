/**
 * @author hxq
 * @date 2022/5/4 下午11:02
 */
public class FreeBlock extends Block{
    public int num; // 分区号

    public FreeBlock(int num, int initAddr, int length) {
        super(initAddr,length);
        this.num = num;
    }

    @Override
    public String toString() {
        return "空闲分区块{" +
                "分区号=" + num +
                ",  长度=" + length +
        ", 地址范围："+ initAddr+"~"+(initAddr+length-1)+
                '}';
    }
}
