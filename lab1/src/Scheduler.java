import java.io.IOException;
import java.util.*;

/**
 * @author hxq
 * @date 2022/5/3 下午8:20
 */
public class Scheduler {
    public int timer = 0;//时钟
    public MyProcess current; //当前运行的程序

    public double turnaround; //平均周转时间
    public double wTurnaround; // 平均带权周转时间

    Comparator<MyProcess> comparator = new Comparator<MyProcess>() {
        @Override
        public int compare(MyProcess p1, MyProcess p2) {

             if(p2.pcb.priority != p1.pcb.priority)  {
                 return p2.pcb.priority - p1.pcb.priority;
             } else {
                 return p1.myTime.enterReady - p2.myTime.enterReady;
             }


        }
    };

    public Queue<MyProcess> readyQueue = new PriorityQueue<>(8,comparator); //就绪队列

    public Queue<MyProcess> endQueue = new LinkedList<>();//终止队列

    public List<MyProcess> ps = new ArrayList<>();


    /**
     * 初始化进程，进程到达时间不相同，不能全部加入优先级队列
     * @param n 进程数
     * @throws IOException
     */
    public void init(int n) throws IOException {


        for (int i = 1; i <= n; i++) {
            ps.add(new MyProcess("p"+i));
        }
    }

    /**
     * 调度
     */
    public void schedule() {
        // 初始化列表不为空，说明有进程没有开始；就绪队列不为空，说明有进程没有结束
        while(!readyQueue.isEmpty() || !ps.isEmpty()) {

            // 进程的开始时间等于当前时间，加入优先级队列，从初始化列表中删除
            Iterator<MyProcess> it = ps.iterator();
            while(it.hasNext()) {
                MyProcess p = it.next();
                if(p.myTime.arrive == timer ){
                    readyQueue.add(p);
                    it.remove();

                }
            }

            // 如果就绪队列为空，说明该时间片没有进程需要开始，
            if(readyQueue.isEmpty()) {
                continue;
            }

            current = readyQueue.poll();

            System.out.println("时间:"+timer);
            System.out.println("当前运行的进程："+current.pcb.name);
            StringBuilder sb = new StringBuilder();
            sb.append("就绪队列：");
            for (MyProcess myProcess : readyQueue) {
                sb.append(myProcess.pcb.name + " ");
            }
            sb.append("\n");
            sb.append("结束队列:");
            for(MyProcess myProcess : endQueue) {
                sb.append(myProcess.pcb.name+" ");
            }
            System.out.println(sb);
            System.out.println(" ");

            current.execute(timer);

            // 程序没有运行结束，加入就绪队列
            if(current.pcb.time > 0) {
                current.myTime.enterReady = timer+1;
                readyQueue.add(current);
            }

            // 程序运行结束，加入结束队列
            if(current.pcb.time == 0) {
                endQueue.add(current);
            }

            timer ++;
        }
    }

    public void result() {
        for(MyProcess myProcess : endQueue) {
            // 计算周转时间和带权周转时间
            myProcess.myTime.turnaround = myProcess.myTime.end - myProcess.myTime.arrive;
            myProcess.myTime.wTurnaround = myProcess.myTime.turnaround * 1.0 / myProcess.myTime.execute;
            this.turnaround += myProcess.myTime.turnaround;
            this.wTurnaround += myProcess.myTime.wTurnaround;
        }

        // 计算平均周转时间和平均带权周转时间
        this.turnaround = this.turnaround *1.0/endQueue.size();
        this.wTurnaround = this.wTurnaround *1.0 / endQueue.size();

        // 可视化输出
        System.out.printf("%8s %8s %8s %8s %8s %8s %8s","作业","提交时间","运行时间","开始时间","完成时间","周转时间","带权周转时间");
        System.out.println(" ");
       // System.out.println("作业"+'\t'+"提交时间"+'\t'+"运行时间"+'\t'+"开始时间"+'\t'+"完成时间"+'\t'+"周转时间"+'\t'+"带权周转时间");
        for(MyProcess myProcess : endQueue) {
            //System.out.println(myProcess.pcb.name+'\t'+myProcess.myTime.arrive+'\t'+myProcess.myTime.execute+'\t'+myProcess.myTime.start+'\t'+myProcess.myTime.end+'\t'+myProcess.myTime.turnaround+'\t'+myProcess.myTime.wTurnaround);
            System.out.printf("%8s %8s   %8s    %8s    %8s    %8s   %8s",myProcess.pcb.name,myProcess.myTime.arrive,myProcess.myTime.execute,myProcess.myTime.start,myProcess.myTime.end,myProcess.myTime.turnaround,(double)Math.round(myProcess.myTime.wTurnaround*100)/100);
            System.out.println(" ");
        }
    }



    public void scheduleDemo() throws IOException{
        init(5);
        schedule();
        result();
    }

    public void initTest() {
        MyProcess p1 = new MyProcess("p1",0,3);
        MyProcess p2 = new MyProcess("p2",1,2);
        MyProcess p3 = new MyProcess("p3",2,1);
        MyProcess p4 = new MyProcess("p4",3,2);
        MyProcess p5 = new MyProcess("p5",4,2);
        ps.add(p1);
        ps.add(p2);
        ps.add(p3);
        ps.add(p4);
        ps.add(p5);
    }

    public static void main(String[] args) throws IOException {
        Scheduler s = new Scheduler();
//        s.initTest();
//        s.schedule();
//        s.result();
        s.scheduleDemo();
    }
}
