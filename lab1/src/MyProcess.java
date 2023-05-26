import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author hxq
 * @date 2022/5/3 下午7:57
 */
public class MyProcess {
    public PCB pcb = new PCB();
    public MyTime myTime = new MyTime();

    /**
     * 运行该进程
     * @param t 当前时间
     */
    public void execute(int t){
        pcb.time--; // 时间-1
        pcb.priority --; // 优先级-1
        if(myTime.start == -1) {
            myTime.start = t;
        }
        if(pcb.time == 0 ) {
            myTime.end = t+1;
        }
    }

    @Override
    public String toString() {
        return "MyProcess{" +
                "pcb=" + pcb +
                ", myTime=" + myTime +
                '}';
    }

    public MyProcess(String name) throws IOException {
        // 初始化，通过键盘输入优先级和时间


        this.pcb.name = name;
        this.pcb.state = PCB.State.READY;
        System.out.println("process "+name);
        BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("arrive time:");
        this.myTime.arrive = Integer.parseInt(scanner.readLine());
        System.out.println("execution time:");

        this.pcb.time = Integer.parseInt(scanner.readLine());
        this.myTime.execute = this.pcb.time;
        System.out.println("priority:");
        this.pcb.priority = Integer.parseInt(scanner.readLine());


        this.myTime.enterReady = Integer.MAX_VALUE;
    }

    public MyProcess(String name, int arrive, int execute) {
        this.pcb.name = name;
        this.pcb.time = execute;
        this.myTime.arrive = arrive;
        this.myTime.execute = execute;
        this.pcb.priority = 1;
    }


}
