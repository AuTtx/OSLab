/**
 * @author hxq
 * @date 2022/5/4 下午11:02
 */
public class Block {
    public int initAddr; // 起始地址
    public int length; // 长度

    public Block(int initAddr, int length) {
        this.initAddr = initAddr;
        this.length = length;
    }

    @Override
    public String toString() {
        return "内存块{" +

                "长度=" + length +
                ", 地址范围："+ initAddr+"~"+(initAddr+length-1)+
                '}';
    }
}
