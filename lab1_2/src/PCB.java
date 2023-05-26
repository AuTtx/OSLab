/**
 * @author hxq
 * @date 2022/5/4 上午10:37
 */
public class PCB {
    public String name = ""; // 名字

    public enum State {
        NEW, // 创建
        READY, // 就绪
        BLOCKING, // 阻塞
        RUNNING, // 运行
        END; // 结束
    }

    public State state; // 状态

    @Override
    public String toString() {
        return "PCB{" +
                "name='" + name + '\'' +
                ", state=" + state +
                '}';
    }

    public PCB(String name) {
        this.name = name;
        this.state = State.READY;
    }


}
