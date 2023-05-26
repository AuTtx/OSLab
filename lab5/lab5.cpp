#include<iostream>
using namespace std;

#define PRODUCER "producer"
#define CONSUMER "consumer"
#define PRODUCTS 10

#define PRODUCE 0
#define P 1
#define PUT 2
#define V 3
#define GOTO 4
#define GET 5
#define CONSUME 6
#define NOP 7

struct PCB
{
	string name;	//进程名
	string state;		//状态  运行态、就绪态、等待态和完成态
	string reason;	//等待原因  等待信号量s1或s2
	int breakPoint;	//断点信息  一旦进程再度占有处理器则从断点位置继续运行；处于完成状态，进程执行结束
	struct PCB* next;
};

string states[4] = { "run", "ready", "block", "done" };
int in = 0, out = 0;		//生产者指针 消费者指针
int x = 1;          //取出生产/消费的字符
int products[PRODUCTS];	 //10件产品
int product = 0;
int s1, s2;		 //信号量
int PC = 0;		//指令计数器，假设模拟的指令长度为1，每执行一条模拟指令后，PC加1，提取出下一条指令地址。

int PA[5] = { PRODUCE, P, PUT, V, GOTO };	//PA[i] PA[PC] 存放 生产者 程序中的一条模拟指令执行的入口地址
int SA[5] = { P, GET, V, CONSUME, GOTO };	//SA[i] SA[PC] 存放 消费者 程序中的一条模拟指令执行的入口地址

PCB* producer, * consumer, * pcbNow; //生产者 消费者 现行进程

//初始化工作包括对信号量s1、s2赋初值，对生产者、消费者进程的PCB初始化。初始化后转向处理调度程序
void Init()
{
	cout << "init" << endl;

	s1 = 10, s2 = 0;

	producer = new PCB;
	producer->name = PRODUCER;
	producer->breakPoint = 0;
	producer->state = states[1];
	producer->next = NULL;

	consumer = new PCB;
	consumer->name = CONSUMER;
	consumer->breakPoint = 0;
	consumer->state = states[1];
	consumer->next = NULL;

	pcbNow = producer;
	PC = 0;
}

//将信号量s减去1，若结果小于0，则执行原语的进程被置成等待信号量s的状态。
void p()
{
	if (pcbNow == producer)
	{
		cout << "[生产者]执行P操作" << endl;
		if (--s1 < 0)
		{
			pcbNow->state = states[2];
			pcbNow->reason = "[生产者]阻塞：等待s1";
			cout << pcbNow->reason << endl;
			pcbNow->breakPoint = PC;
		}
		else
		{
			pcbNow->state = states[1];
			pcbNow->breakPoint = PC;
		}
	}
	else
	{
		cout << "[消费者]执行P操作" << endl;
		if (--s2 < 0)
		{
			pcbNow->state = states[2];
			pcbNow->reason = "[消费者]阻塞：等待s2";
			cout << pcbNow->reason << endl;
			pcbNow->breakPoint = PC;
		}
		else
		{
			pcbNow->state = states[1];
			pcbNow->breakPoint = PC;
		}
	}
}

//将信号量s加1，若结果不大于0，则释放一个等待信号量s的进程。
void v()
{
	if (pcbNow == producer)
	{
		cout << "[生产者]执行V操作" << endl;
		if (++s2 <= 0 && consumer->state == states[2]) 
		{
			consumer->state = states[1];
			cout<<"[消费者]被唤醒"<<endl;
		}
	}
	else
	{
		cout << "[消费者]执行V操作" << endl;
		if (++s1 <= 0 && producer->state == states[2])
		{
			producer->state = states[1];
			cout<<"[消费者]被唤醒"<<endl;
		}
	}
	pcbNow->state = states[1];
	pcbNow->breakPoint = PC;
}

// B[IN]: =product;  IN: = (IN+1) mod 10
void Put()
{
	if (product >= 10)
	{
		cout << "[生产者]无法放产品" << endl;
		pcbNow->state = states[3];//?
		
		return;
	}
	cout << "[生产者]放产品" << endl;
	product += 1;
	products[in] = x;
	in = (in + 1) % PRODUCTS;

	pcbNow->state = states[1];
	pcbNow->breakPoint = PC;
}

// x:=B[out];  out: =(out+1) mod 10	consumer->next = NULL;

void Get()
{
	if (product <= 0)
	{
		cout << "[消费者]无法拿产品" << endl;
	
		return;
	}
	else
	{
		cout << "[消费者]拿产品" << endl;
		product -= 1;
		x = products[out];
		out = (out + 1) % PRODUCTS;
	}

	pcbNow->state = states[1];
	pcbNow->breakPoint = PC;

}

//输入一个字符放入C中
void Produce()
{
	cout << "[生产者]生产一个产品" << x << endl;
	pcbNow->state = states[1];
	pcbNow->breakPoint = PC;
}

//打印或显示x中的字符
void Consume()
{
	cout << "[消费者]消费一个产品" << x << endl;
	pcbNow->state = states[1];
	pcbNow->breakPoint = PC;
}

//GOTO L	PC: =L
void Goto(int L)
{
	int complete = 0;
	if (pcbNow == producer)
	{
		cout << "[生产者]执行goto "<<L << endl;
		cout<<"[生产者]是否完成？（1.完成，0.未完成）"<<endl;
		cin>>complete;
	}
	else
	{
		cout << "[消费者]执行goto " <<L << endl;
		cout<<"[消费者]是否完成？（1.完成，0.未完成）"<<endl;
		cin>>complete;
	}

	if(!complete) {
		pcbNow->state = states[1];
		pcbNow->breakPoint = L;
	} else {
		pcbNow->state = states[3];
	}


	
}

//NOP	空操作
void Nop()
{
	;
	pcbNow->state = states[1];
	pcbNow->breakPoint = PC;
}

//模拟处理器指令执行程序：按“指令计数器”PC之值执行指定的指令，且PC加1指向下一条指令。
void Excute()
{
	
	int j = (pcbNow == producer) ? PA[PC] : SA[PC];
	PC = PC + 1;
	switch (j)
	{
	case 0:
		Produce(); 
		
		break;
	case 1:
		p();
		break;
	case 2:
		Put(); 
		break;
	case 3:
		v();
		break;
	case 4:
		Goto(0);
		break;
	case 5:
		Get(); 
		break;
	case 6:
		Consume();
		break;
	case 7:
		Nop(); 
		break;
	}
	

}

/*处理器调度程序：
在计算机系统中，进程并发执行时，任一进程占用处理器执行完一条指令后就有可能被打断而让出处理器由其它进程运行。
故在模拟系统中也类似处理，每当执行一条模拟的指令后，保护当前进程的现场，让它成为非运行态，
由处理器调度程序按随机数再选择一个就绪进程占用处理器运行。
*/
void Processor()
{
	while (1)
	{
		if(producer->state==states[3]||consumer->state==states[3]) {
			cout << "end"<<endl;
			exit(0);
		}

		if (producer->state == states[1] && consumer->state == states[1])
		{
			pcbNow = ((rand() % 2) == 0) ? producer : consumer;

		}
		else if(producer->state == states[1])
		{
			pcbNow = producer;
		}
		else if(consumer->state == states[1])
		{	
			pcbNow = consumer;
		}

		
		pcbNow->state = states[0];
		PC = pcbNow->breakPoint;
		Excute();

		string line = "";
		getline(cin,line);

	}
}

int main()
{

	Init();
	Processor();

	return 0;
}