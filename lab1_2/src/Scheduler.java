import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author hxq
 * @date 2022/5/4 上午10:38
 */
public class Scheduler {
    public List<PCB> readyQueue = new ArrayList<>(); //就绪队列

    public List<PCB> blockQueue = new ArrayList<>();//阻塞队列

    public PCB current;// 当前运行的进程

    public List<PCB> ps = new ArrayList<>(); // 记录没有结束的进程

    /**
     * 初始化进程，加入就绪队列，记录在ps中
     * @param n 进程数
     */
    public void init(int n) {
        for(int i = 1; i <= n; i++) {
            PCB p = new PCB("p"+i);
            readyQueue.add(p);
            ps.add(p);

        }
    }

    public void schedule() throws Exception{
        while(!ps.isEmpty()) {
            // 让用户输入调度哪个进程，进程状态转化原因
            String pname = "";
            int reason = -1;
            PCB process = ps.get(0);
            StringBuilder sb = new StringBuilder();
            List<String> names = new ArrayList<>();
            for(PCB p : ps) {
                names.add(p.name);
                sb.append(p.name+" ");
            }
            System.out.println("进程名字有："+sb);
            System.out.println("请选择将要调度的进程名字");

            BufferedReader scanner = new BufferedReader(new InputStreamReader(System.in));
            pname = scanner.readLine();
            if(!names.contains(pname)) {
                System.out.println("进程名字不合法，请重新选择");
                continue;
            }

            for(PCB p : ps) {
                if(p.name .equals( pname)) {
                    process = p;
                    System.out.println("选中的进程的pcb:"+process);
                }
            }

            System.out.println(" ");
            System.out.println("调度原因有:");
            System.out.println("1 进程调度，就绪->运行");
            System.out.println("2 申请输入/输出，运行->阻塞");
            System.out.println("3 输入/输出完成，阻塞->就绪");
            System.out.println("4 中断，运行->就绪");
            System.out.println("5 退出，运行->终止");
            System.out.println("请输入原因编号（1、2、3、4、5），选择调度原因:");

            reason = Integer.parseInt(scanner.readLine());
            if( !( (reason == 1) || (reason == 2) || (reason == 3) || (reason == 4) || (reason == 5) ) ) {
                System.out.println("调度原因输入不合法");
                continue;
            }

            switchTo( process,  reason);
        }

        


    }

    /**
     * 状态转换
     * @param process 进程
     * @param reason 状态转换原因
     */
    public void switchTo(PCB process, int reason) {
        switch(reason) {
            case 1: //进程调度，就绪->运行
                dispatch(process);
                break;
            case 2: //2 申请输入/输出，运行->阻塞
                IOWait(process);
                break;
            case 3: //3 输入/输出完成，阻塞->就绪
                IOComplete(process);
                break;
            case 4: //4 中断，运行->就绪
                interrupt(process);
                break;
            case 5: //5 退出，运行->终止
                exit(process);
                break;
            default:
                break;

        }
    }

    /**
     * 调度选中
     * @param process 进程
     */
    public void dispatch(PCB process) {
        if(process.state != PCB.State.READY) {
            System.out.println("调度失败");
            return;
        }
        process.state = PCB.State.RUNNING;

        System.out.println("调度成功，由于进程调度, 进程"+process.name+"的状态转变：就绪->运行");
        System.out.println("该进程的pcb:"+process);
        readyQueue.remove(process);
        current = process;

        analysis();
    }

    /**
     * IO等待
     * @param process 进程
     */
    public void IOWait(PCB process) {
        if(process.state != PCB.State.RUNNING) {
            System.out.println("调度失败");
            return;
        }
        process.state = PCB.State.BLOCKING;

        System.out.println("调度成功，由于申请输入/输出, 进程"+process.name+"的状态转变：运行->阻塞");
        blockQueue.add(process);
        current = null;

        analysis();
    }

    /**
     * IO结束
     * @param process 进程
     */
    public void IOComplete(PCB process) {
        if(process.state != PCB.State.BLOCKING) {
            System.out.println("调度失败");
            return;
        }
        process.state = PCB.State.READY;

        System.out.println("调度成功，由于输入/输出结束, 进程"+process.name+"的状态转变：阻塞->就绪");
        readyQueue.add(process);
        blockQueue.remove(process);

        analysis();
    }

    /**
     * 退出
     * @param process 进程
     */
    public void exit(PCB process) {
        if(process.state != PCB.State.RUNNING) {
            System.out.println("调度失败");
            return;
        }
        process.state = PCB.State.END;

        System.out.println("调度成功，由于退出, 进程"+process.name+"的状态转变：运行->结束");
        ps.remove(process);
        current = null;

        analysis();
    }

    /**
     * 中断
     * @param process 进程
     */
    public void interrupt(PCB process) {
        if(process.state != PCB.State.RUNNING) {
            System.out.println("调度失败");
            return;
        }
        process.state = PCB.State.READY;

        System.out.println("调度成功，由于中断, 进程"+process.name+"的状态转变：运行->就绪");
        readyQueue.add(process);
        current = null;

        analysis();
    }

    public void analysis() {
        //System.out.println("正在运行的进程："+current.name);
        StringBuilder sb = new StringBuilder();
        sb.append("就绪队列：");
        for (PCB myProcess : readyQueue) {
            sb.append(myProcess.name + " ");
        }
        sb.append("\n");
        sb.append("阻塞队列:");
        for(PCB myProcess : blockQueue) {
            sb.append(myProcess.name+" ");
        }
        System.out.println(sb);
        System.out.println(" ");
    }

    public void scheduleDemo(int n) {
        try {
            init(n);
            schedule();
            analysis();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scheduler s = new Scheduler();
        s.scheduleDemo(3);
    }


}
