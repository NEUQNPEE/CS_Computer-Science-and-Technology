// 用 C++ 语言来实现对 N 个进程采用动态优先权优先算法的进程调度。
#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
using namespace std;

// 状态枚举
enum State
{
    ready, // 就绪态
    run,   // 运行态
    block  // 阻塞态
};

// PCB结构体
struct Process
{
    int id;         // 进程标识符
    int priority;   // 优先级
    int cputime;    // CPU时间
    int alltime;    // 所需时间
    int startblock; // 运行多少时间后阻塞
    int blocktime;  // 阻塞多少时间后就绪
    State state;    // 进程状态

    Process(int id, int priority, int cputime, int alltime, int startblock, int blocktime)
    {
        this->id = id;
        this->priority = priority;
        this->cputime = cputime;
        this->alltime = alltime;
        this->startblock = startblock;
        this->blocktime = blocktime;
        this->state = ready;
    }

    Process()
    {
        this->id = 0;
        this->priority = 0;
        this->cputime = 0;
        this->alltime = 0;
        this->startblock = 0;
        this->blocktime = 0;
        this->state = ready;
    }
};

// 链表结构体，包括一个PCB和一个指针
struct PCBList
{
    Process *process;
    PCBList *next;

    PCBList(Process *process)
    {
        this->process = process;
        this->next = NULL;
    }

    PCBList()
    {
        this->process = NULL;
        this->next = NULL;
    }
};

// 就绪队列
PCBList *readyQueueHead = new PCBList();
PCBList *readyQueueP = readyQueueHead->next;

// 阻塞队列
PCBList *blockQueueHead = new PCBList();
PCBList *blockQueueP = blockQueueHead->next;

// 进程完成的顺序
vector<int> processOrder = vector<int>();

// 交换两个进程的PCB
void Swap(Process *&a, Process *&b)
{
    Process *temp = a;
    a = b;
    b = temp;
}

// 根据优先级排序
void Sort(PCBList *&head)
{
    // 链表为空||没有元素||只有一个元素
    if (head == NULL || head->next == NULL)
    {
        return;
    }

    PCBList *pre = head;
    PCBList *cur = pre->next;
    PCBList *next = NULL;

    // 开始冒泡排序
    while (cur != NULL)
    {
        next = cur->next;
        pre = head;
        while (pre->next != cur)
        {
            pre = pre->next;
        }
        while (next != NULL)
        {
            // 优先级高的在前面
            if (cur->process->priority < next->process->priority)
            {
                Swap(cur->process, next->process);
            }
            // 优先级相同，所需时间短的在前面
            else if (cur->process->priority == next->process->priority)
            {
                if (cur->process->alltime > next->process->alltime)
                {
                    Swap(cur->process, next->process);
                }
                // 优先级相同，所需时间相同，id小的在前面
                else if (cur->process->alltime == next->process->alltime)
                {
                    if (cur->process->id > next->process->id)
                    {
                        Swap(cur->process, next->process);
                    }
                    else
                    {
                        pre = pre->next;
                        next = next->next;
                    }
                }
                else
                {
                    pre = pre->next;
                    next = next->next;
                }
            }
            else
            {
                pre = pre->next;
                next = next->next;
            }
        }
        cur = cur->next;
    }
}

// 输出一个链表
void Print(PCBList *&head)
{
    PCBList *p = head->next;
    while (p != NULL)
    {
        cout << p->process->id << " ";
        p = p->next;
    }
    cout << endl;
}

// 输出详细信息
void PrintDetail(PCBList *&head)
{
    PCBList *p = head->next;
    while (p != NULL)
    {
        cout << "id:" << p->process->id << " ";
        cout << "priority:" << p->process->priority << " ";
        cout << "cputime:" << p->process->cputime << " ";
        cout << "alltime:" << p->process->alltime << " ";
        cout << "startblock:" << p->process->startblock << " ";
        cout << "blocktime:" << p->process->blocktime << " ";
        cout << "state:" << p->process->state << " ";
        cout << endl;
        p = p->next;
    }
    cout << endl;
}

// 处理就绪队列
void processReadyQueue()
{
    // 首先检查就绪队列有没有进程
    if (readyQueueHead->next == NULL)
    {
        return;
    }

    // 运行优先级最高的进程
    PCBList *p = readyQueueHead->next;
    p->process->state = run;
    p->process->priority -= 3;
    p->process->cputime++;
    p->process->alltime--;

    // 如果alltime为0，说明进程运行完成，将其从就绪队列中删除,并将其加入processOrder
    if (p->process->alltime == 0)
    {
        processOrder.push_back(p->process->id);
        readyQueueHead->next = p->next;
        p = readyQueueHead->next;
    }

    // 运行完成，处理其他进程
    // 首先，其他就绪态进程的优先级加1,运行态进程变为就绪态
    p = readyQueueHead->next;
    while (p != NULL)
    {
        if (p->process->state == ready)
        {
            p->process->priority++;
        }

        if (p->process->state == run)
        {
            p->process->state = ready;
        }

        p = p->next;
    }
}

void schedulingProcess()
{

    // 对就绪队列进行排序，让优先级最高的进程运行
    Sort(readyQueueHead);

    // debug 输出
    // printf("这里是schedulingProcess函数的开始\n");
    // printf("就绪队列：\n");
    // PrintDetail(readyQueueHead);
    // printf("阻塞队列：\n");
    // PrintDetail(blockQueueHead);

    PCBList *p = readyQueueHead->next;

    // 首先，处理就绪队列
    processReadyQueue();

    // 其次，阻塞态进程的阻塞时间减1
    p = blockQueueHead->next;
    while (p != NULL)
    {
        if (p->process->state == block)
        {
            p->process->blocktime--;
        }
        p = p->next;
    }

    // 最后，阻塞时间为0的进程加入就绪队列
    p = blockQueueHead->next;
    PCBList *pre = blockQueueHead;
    while (p != NULL)
    {
        if (p->process->blocktime == 0)
        {
            p->process->state = ready;
            if (readyQueueHead->next == NULL)
            {
                readyQueueHead->next = new PCBList(p->process);
                readyQueueP = readyQueueHead->next;
            }
            else
            {
                while (readyQueueP->next != NULL)
                {
                    readyQueueP = readyQueueP->next;
                }
                readyQueueP->next = new PCBList(p->process);
            }

            // 将进程从阻塞队列中删除
            pre->next = p->next;
            p = pre->next;
        }
        else
        {
            p = p->next;
            pre = pre->next;
        }
    }

    // 输出两个队列
    // printf("这里是schedulingProcess函数的结束\n");
    // printf("就绪队列：\n");
    // PrintDetail(readyQueueHead);
    // printf("阻塞队列：\n");
    // PrintDetail(blockQueueHead);
}

int main()
{
    // 首先读入input.txt，格式为| ID | PRIORITY | CPUTIME | ALLTIME | STARTBLOCK | BLOCKTIME |，用空格分隔
    int processNum = 0;

    ifstream file("input.txt");

    if (!file.is_open())
    {
        cout << "文件打开失败" << endl;
        return 0;
    }

    string line;
    // 建立头结点
    PCBList *head = new PCBList();
    // 链表指针
    PCBList *p = head;

    int processIndex = 0;
    while (getline(file, line))
    {
        istringstream iss(line);
        vector<int> numbers;
        int num;
        while (iss >> num)
        {
            numbers.push_back(num);
        }

        Process *process = new Process(numbers[0], numbers[1], numbers[2], numbers[3], numbers[4], numbers[5]);
        // 将进程排成队列
        p->next = new PCBList(process);
        p = p->next;
    }

    p = head->next;

    // 将BLOCKTIME为0的进程加入就绪队列，否则加入阻塞队列
    while (p != NULL)
    {
        if (p->process->blocktime == 0)
        {
            p->process->state = ready;
            // 将进程排成队列
            if (readyQueueHead->next == NULL)
            {
                readyQueueHead->next = new PCBList(p->process);
                readyQueueP = readyQueueHead->next;
            }
            else
            {
                readyQueueP->next = new PCBList(p->process);
                readyQueueP = readyQueueP->next;
            }
        }
        else
        {
            p->process->state = block;
            // 将进程排成队列
            if (blockQueueHead->next == NULL)
            {
                blockQueueHead->next = new PCBList(p->process);
                blockQueueP = blockQueueHead->next;
            }
            else
            {
                blockQueueP->next = new PCBList(p->process);
                blockQueueP = blockQueueP->next;
            }
        }
        p = p->next;
    }

    // 输出两个队列
    // printf("就绪队列：");
    // Print(readyQueueHead);
    // printf("阻塞队列：");
    // Print(blockQueueHead);

    // 模拟函数
    while (readyQueueHead->next != NULL || blockQueueHead->next != NULL)
    {
        schedulingProcess();
    }

    // 输出结果
    // printf("进程完成的顺序：");
    for (int i = 0; i < processOrder.size(); i++)
    {
        cout << processOrder[i] << " ";
    }

    file.close();
}