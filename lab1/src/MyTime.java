/**
 * @author hxq
 * @date 2022/5/3 下午8:20
 */
public class MyTime {

    public int arrive; // 到达时间
    public int execute; // 运行时间
    public int start = -1; // 开始时间
    public int end ; // 结束时间
    public int turnaround ; // 周转时间
    public double wTurnaround; // 带权周转时间

    public int enterReady;


    @Override
    public String toString() {
        return "MyTime{" +
                "arrive=" + arrive +
                ", execute=" + execute +
                ", start=" + start +
                ", end=" + end +
                ", turnaround=" + turnaround +
                ", wTurnaround=" + wTurnaround +
                ", enterReady=" + enterReady +
                '}';
    }
}
