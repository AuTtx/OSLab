#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <wait.h>
#include <sys/types.h>
#include <sys/msg.h>
#include <sys/ipc.h>
#include <string.h>

#define MSGKEY 75              // 消息队列的 key 为 75

struct msgform {               // 消息结构
   long mtype;                // 消息类型
   char mtext[1024];          // 消息的文本长度为 1k
} msg;

int msgqid;
int sent_client = 3;
int received = 3;


void CLIENT() {
   msgqid = msgget(MSGKEY, 0777|IPC_CREAT);           // 打开消息队列
   if(msgqid==-1) {
      exit(EXIT_FAILURE);
   }
   
   do {
      msg.mtype = sent_client;                       // 消息的类型从 10 到 1
      // 对消息内容进行赋值
      printf("CLIENT sent message NO.%d:",sent_client);
      scanf("%s",&msg.mtext);
 

      msgsnd(msgqid, &msg, 1024, 0);      // 发送消息 msg 到 msgid 消息队列
      sent_client--;
        sleep(1);
     
   } while (strcmp(msg.mtext, "q") && sent_client>0);
   printf("子进程结束！");
   
   
}

void SERVER() {
   
   msgqid = msgget(MSGKEY, 0777 |
                           IPC_CREAT);      // 创建一个所有用户都可以读、写、执行的队列
   do {
      
      if(msgrcv(msgqid, &msg, 1024, 0, 0)==-1){    // 从队列 msgid 接受消息 msg
         printf("server fail to receive");
         exit(EXIT_FAILURE);
      }
      printf("SERVER received from CLIENT, message: %s\n",msg.mtext); // 打印消息内容
      
      received--;
      //sleep(1);                            // 暂停 1 s，等待 CLIENT 接收
      
   } while ( received> 0 && strcmp(msg.mtext, "q")) ;                // 消息为 q 时，释放队列

   printf("父进程结束！");
  
   msgctl(msgqid, IPC_RMID, 0);             // 消除消息队列的标识符
}

int main() {
   pid_t pid1;
   int status;
   while ((pid1 = fork()) == -1);
   if (pid1 == 0) {                         // 子进程 1
      CLIENT();
      
   }
   else {
      SERVER();
 
   }
 
}
