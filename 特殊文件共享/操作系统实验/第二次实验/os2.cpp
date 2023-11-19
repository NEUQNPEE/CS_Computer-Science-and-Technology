// 用 C++ 语言来实现动态分区分配方式的模拟
#include <iostream>
#include <fstream>
#include <vector>
#include <sstream>
using namespace std;

// 函数声明
void ini();
void alloc(int taskId, int size);
void allocFirstFit(int taskId, int size);
void allocNextFit(int taskId, int size);
void allocBestFit(int taskId, int size);
void allocWorstFit(int taskId, int size);
void display();
void displayFreeBlockList();
void simulate();

// 内存块结构体，包括一个taskId值：任务号，0代表未占用，一个size：大小
struct MemoryBlock
{
    int taskId;
    int size;

    MemoryBlock(int taskId, int size)
    {
        this->taskId = taskId;
        this->size = size;
    }
};

// 链表结构体，包括一个内存块和前后指针
struct MemoryBlockList
{
    MemoryBlock *memoryBlock;
    MemoryBlockList *prev;
    MemoryBlockList *next;

    MemoryBlockList(MemoryBlock *memoryBlock)
    {
        this->memoryBlock = memoryBlock;
        this->prev = NULL;
        this->next = NULL;
    }

    MemoryBlockList(MemoryBlock *memoryBlock, MemoryBlockList *prev, MemoryBlockList *next)
    {
        this->memoryBlock = memoryBlock;
        this->prev = prev;
        this->next = next;
    }
};

MemoryBlockList *head;

// 空闲分区链，用于最佳适应算法和最坏适应算法，包括一个index：索引，一个指针，指向下一个结点
struct FreeBlockList
{
    int index;
    int size;
    FreeBlockList *next;

    FreeBlockList(int index)
    {
        this->index = index;
        this->next = NULL;
    }
};

FreeBlockList *freeBlockListHead;

// 算法枚举，分首次适应、循环首次适应、最佳适应、最坏适应
enum Algorithm
{
    firstFit,
    nextFit,
    bestFit,
    worstFit
};

// 用于控制算法的全局变量
Algorithm algorithm;

// 初始化函数
void ini()
{
    // 建立头结点
    head = new MemoryBlockList(new MemoryBlock(0, 0));

    // 建立首元结点，可用空间为 640K
    MemoryBlockList *first = new MemoryBlockList(new MemoryBlock(0, 640), head, NULL);

    // 将首元结点插入到头结点之后
    head->next = first;

    freeBlockListHead = new FreeBlockList(0);
}

// 空闲分区链更新函数
void freeBlockListupdate()
{
    // 从头结点开始遍历
    MemoryBlockList *mp = head->next;
    FreeBlockList *fp = freeBlockListHead;
    int index = 0;
    while (mp != NULL)
    {
        if (mp->memoryBlock->taskId == 0)
        {
            FreeBlockList *p = new FreeBlockList(index);
            p->size = mp->memoryBlock->size;
            fp->next = p;
            fp = fp->next;
        }
        mp = mp->next;
        index++;
    }

    if (algorithm == bestFit)
    {
        // 按size从小到大排序
        FreeBlockList *p = freeBlockListHead->next;
        while (p != NULL)
        {
            FreeBlockList *q = p->next;
            while (q != NULL)
            {
                if (p->size > q->size)
                {
                    int temp = p->size;
                    p->size = q->size;
                    q->size = temp;
                    temp = p->index;
                    p->index = q->index;
                    q->index = temp;
                }
                q = q->next;
            }
            p = p->next;
        }
    }

    if (algorithm == worstFit)
    {
        // 按size从大到小排序
        FreeBlockList *p = freeBlockListHead->next;
        while (p != NULL)
        {
            FreeBlockList *q = p->next;
            while (q != NULL)
            {
                if (p->size < q->size)
                {
                    int temp = p->size;
                    p->size = q->size;
                    q->size = temp;
                    temp = p->index;
                    p->index = q->index;
                    q->index = temp;
                }
                q = q->next;
            }
            p = p->next;
        }
    }
}

// 空间申请函数
void alloc(int taskId, int size)
{
    switch (algorithm)
    {
    case firstFit:
        allocFirstFit(taskId, size);
        break;
    case nextFit:
        // allocNextFit(taskId, size);
        break;
    case bestFit:
        allocBestFit(taskId, size);
        freeBlockListupdate();
        break;
    case worstFit:
        allocWorstFit(taskId, size);
        freeBlockListupdate();
        break;
    }
}

// 首次适应算法，要求以地址递增的顺序查找第一个能满足要求的空闲分区
void allocFirstFit(int taskId, int size)
{
    // 从头结点开始遍历
    MemoryBlockList *p = head->next;
    while (p != NULL)
    {
        if (p->memoryBlock->taskId)
        {
            p = p->next;
            continue;
        }

        // 如果当前结点的内存块未被占用且大小大于等于要申请的大小
        if (p->memoryBlock->size >= size)
        {
            // 如果当前结点的内存块大小大于要申请的大小
            if (p->memoryBlock->size > size)
            {
                // 新建内存块，大小为申请的大小
                MemoryBlock *newMemoryBlock = new MemoryBlock(taskId, size);

                // 将新建的内存块插入到当前结点之前
                MemoryBlockList *newMemoryBlockList = new MemoryBlockList(newMemoryBlock, p->prev, p);
                p->prev->next = newMemoryBlockList;
                p->prev = newMemoryBlockList;

                // 将当前结点的内存块大小减去申请的大小
                p->memoryBlock->size -= size;
            }
            else
            {
                // 将当前结点的内存块指针指向新建的内存块
                p->memoryBlock->taskId = taskId;
            }

            break;
        }
        else
        {
            p = p->next;
        }
    }
}

// 最佳适应算法，要求以地址递增的顺序查找最小的能满足要求的空闲分区
void allocBestFit(int taskId, int size)
{
    // 从头结点开始遍历
    MemoryBlockList *mp = head->next;
    FreeBlockList *fp = freeBlockListHead->next;
    int index = 0;
    while (fp != NULL)
    {
        if (fp->size < size)
        {
            fp = fp->next;
            continue;
        }

        index = fp->index;
        break;
    }

    for (int i = 0; i < index; i++)
    {
        mp = mp->next;
    }

    if (mp->memoryBlock->size > size)
    {
        // 新建内存块，大小为申请的大小
        MemoryBlock *newMemoryBlock = new MemoryBlock(taskId, size);

        // 将新建的内存块插入到当前结点之前
        MemoryBlockList *newMemoryBlockList = new MemoryBlockList(newMemoryBlock, mp->prev, mp);
        mp->prev->next = newMemoryBlockList;
        mp->prev = newMemoryBlockList;

        // 将当前结点的内存块大小减去申请的大小
        mp->memoryBlock->size -= size;
    }
    else
    {
        // 将当前结点的内存块指针指向新建的内存块
        mp->memoryBlock->taskId = taskId;
    }
}

// 最坏适应算法，要求以地址递增的顺序查找最大的能满足要求的空闲分区
void allocWorstFit(int taskId, int size)
{
    // 从头结点开始遍历
    MemoryBlockList *mp = head->next;
    FreeBlockList *fp = freeBlockListHead->next;
    int index = 0;
    while (fp != NULL)
    {
        if (fp->size < size)
        {
            fp = fp->next;
            continue;
        }

        index = fp->index;
        break;
    }

    for (int i = 0; i < index; i++)
    {
        mp = mp->next;
    }

    if (mp->memoryBlock->size > size)
    {
        // 新建内存块，大小为申请的大小
        MemoryBlock *newMemoryBlock = new MemoryBlock(taskId, size);

        // 将新建的内存块插入到当前结点之前
        MemoryBlockList *newMemoryBlockList = new MemoryBlockList(newMemoryBlock, mp->prev, mp);
        mp->prev->next = newMemoryBlockList;
        mp->prev = newMemoryBlockList;

        // 将当前结点的内存块大小减去申请的大小
        mp->memoryBlock->size -= size;
    }
    else
    {
        // 将当前结点的内存块指针指向新建的内存块
        mp->memoryBlock->taskId = taskId;
    }
}

// 空间释放函数
void free(int taskId)
{
    // 从头结点开始遍历
    MemoryBlockList *p = head->next;
    while (p != NULL)
    {

        if (p->memoryBlock->taskId != taskId)
        {
            p = p->next;
            continue;
        }

        // 1. 前一个结点的内存块未被占用
        if (p->prev->memoryBlock != NULL && p->prev->memoryBlock->taskId == 0)
        {
            // 将当前结点的内存块大小加上前一个结点的内存块大小
            p->memoryBlock->size += p->prev->memoryBlock->size;

            // 将前一个结点从链表中删除
            if (p->prev->prev != NULL)
            {
                if (p->prev->prev->memoryBlock != NULL)
                {
                    p->prev->prev->next = p;
                    p->prev = p->prev->prev;
                }
            }
        }

        // 2. 后一个结点的内存块未被占用
        if (p->next != NULL && p->next->memoryBlock->taskId == 0)
        {
            // 将当前结点的内存块大小加上后一个结点的内存块大小
            p->memoryBlock->size += p->next->memoryBlock->size;

            // 将后一个结点从链表中删除
            if (p->next->next != NULL)
            {
                p->next->next->prev = p;
            }
            p->next = p->next->next;
        }

        p->memoryBlock->taskId = 0;
        break;
    }

    freeBlockListupdate();
}

// 输出当前内存分配情况
void display()
{
    // 从头结点开始遍历
    MemoryBlockList *p = head->next;
    while (p != NULL)
    {
        cout << "id:" << p->memoryBlock->taskId << " size:" << p->memoryBlock->size;
        p = p->next;
        if (p != NULL)
        {
            cout << "   ->   ";
        }
    }

    cout << "\n\n";

    displayFreeBlockList();
}

// 输出当前空闲分区链
void displayFreeBlockList()
{
    // 从头结点开始遍历
    FreeBlockList *p = freeBlockListHead->next;
    cout << "空闲分区链：";
    while (p != NULL)
    {
        cout << "index:" << p->index;
        cout << " size:" << p->size;
        p = p->next;
        if (p != NULL)
        {
            cout << "   ->   ";
        }
    }

    cout << "\n\n";
}

// 模拟分配
void simulate()
{
    // 作业1申请 130K
    cout << "作业1申请 130K\n";
    alloc(1, 130);
    display();
    // 作业2申请 60K
    cout << "作业2申请 60K\n";
    alloc(2, 60);
    display();
    // 作业3申请 100K
    cout << "作业3申请 100K\n";
    alloc(3, 100);
    display();
    // 作业2释放 60K
    cout << "作业2释放 60K\n";
    free(2);
    display();
    // 作业4申请 200K
    cout << "作业4申请 200K\n";
    alloc(4, 200);
    display();
    // 作业3释放 100K
    cout << "作业3释放 100K\n";
    free(3);
    display();
    // 作业1释放 130K
    cout << "作业1释放 130K\n";
    free(1);
    display();
    // 作业5申请 140K
    cout << "作业5申请 140K\n";
    alloc(5, 140);
    display();
    // 作业6申请 60K
    cout << "作业6申请 60K\n";
    alloc(6, 60);
    display();
    // 作业7申请 50K
    cout << "作业7申请 50K\n";
    alloc(7, 50);
    display();
    // 作业6释放 60K
    cout << "作业6释放 60K\n";
    free(6);
    display();
}

int main()
{
    ini();

    // algorithm = firstFit;

    // simulate();

    // display();

    // algorithm = bestFit;

    // simulate();

    // display();

    // algorithm = worstFit;

    // simulate();

    // display();
}
