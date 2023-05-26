/**
 * @author hxq
 * @date 2022/5/3 下午8:19
 */
public class PCB {
    public String name = ""; // 名字
    public int time; // 运行时间
    public int priority; // 优先级

    public enum State {
        NEW, // 创建
        READY, // 就绪
        WAITING, // 阻塞
        RUNNING, // 运行
        END; // 终止
    }

    public State state; // 状态

    @Override
    public String toString() {
        return "PCB{" +
                "name='" + name + '\'' +
                ", time=" + time +
                ", priority=" + priority +
                ", state=" + state +
                '}';
    }
}
