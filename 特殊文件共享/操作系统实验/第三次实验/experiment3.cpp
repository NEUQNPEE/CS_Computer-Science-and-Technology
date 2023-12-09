#include <iostream>
#include <algorithm>
#include <cstdlib> 
#include <ctime> 
using namespace std;

int inst[320];  //指令序列
int mem[4];     //物理块
int waitTime[4];    //物理块中的指令多久时间后被使用。在Optimal算法使用
int enterTime[4];   //指令进入物理块多长时间。在FIFO算法使用
int restTime[4];    //物理块中的指令多长时间未被使用。在LRU算法使用
double T = 0;  //命中次数
double F = 0;  //缺页次数


void buildInst(){   //初始化指令序列
    int m = -1;
    srand(time(0));
    m = rand() % 320 + 0;
    inst[0] = m;
    inst[1] = m + 1;

    for(int i = 2; i < 320;){
        //前地址部分
        //srand(time(0));    
        m = rand() % (m - 1) + 0;
        inst[i] = m;
        i++;
        if(i >= 320)break;

        m = m + 1;
        inst[i] = m;
        i++;
        if(i >= 320)break;

        //后地址部分
        //srand(time(0));    
        m = rand() % (320 - m - 1) + (m + 1);
        inst[i] = m;
        i++;
        if(i >= 320)break;

        m = m + 1;
        inst[i] = m;
        i++;

    }

    cout<<"生成指令序列为:"<<endl;
    for(int i = 0; i < 320; i++)cout<<inst[i]<<' ';
    cout<<endl<<endl;
    
}

void select_op(int i){
    int page = inst[i] / 10;
    
    //检查是否命中
    for(int j = 0; j < 4; j++){
        if(mem[j] == page){
            T++;
            return;
        }
    }
    F++;

    //检查空闲物理块
    for(int j = 0; j < 4; j++){
        if(mem[j] == -1){
            mem[j] = page;
            return;
        }
    }

    //Optimal调度
    for(int j = 0; j < 4; j++){
        bool meet = false;  //记录物理块中的指令是否会在后续被执行
        for(int l = i; l < 320; l++){
            if((inst[l] / 10) == mem[j]){
                waitTime[j] = l - i;
                meet = true;
                break;
            }
        }
        if(!meet)waitTime[j] = 3200;
    }
    int max_j = 0;
    for(int j = 0; j < 4; j++){
        if(waitTime[j] > waitTime[max_j])max_j = j;
    }
    mem[max_j] = page;
}

void Optimal(){
    T = 0;
    F = 0;
    fill(waitTime + 0, waitTime + 4, 3200);
    for(int i = 0; i < 320; i++)select_op(i);
    double ans = F / (T + F);
    cout<<"Optimal算法缺页率为:";
    cout<<ans<<endl<<endl;
}

void select_FIFO(int i){
    int page = inst[i] / 10;
    
    //检查是否命中
    for(int j = 0; j < 4; j++){
        if(mem[j] == page){
            T++;
            return;
        }
    }
    F++;

    //检查空闲物理块
    for(int j = 0; j < 4; j++){
        if(mem[j] == -1){
            mem[j] = page;
            enterTime[j] = 1;
            return;
        }
    }

    //FIFO调度
    int max_j = 0;
    for(int j = 0; j < 4; j++){
        if(enterTime[j] > enterTime[max_j])max_j = j;
    }
    mem[max_j] = page;
    enterTime[max_j] = 0;
    for(int j = 0; j < 4; j++){
        enterTime[j]++;
    }
}

void FIFO(){
    T = 0;
    F = 0;
    fill(enterTime + 0, enterTime + 4, -1);
    for(int i = 0; i < 320; i++)select_FIFO(i);
    double ans = F / (T + F);
    cout<<"FIFO算法缺页率为:";
    cout<<ans<<endl<<endl;
}

void select_LRU(int i){
    int page = inst[i] / 10;
    
    //检查是否命中
    for(int j = 0; j < 4; j++){
        if(mem[j] == page){
            T++;
            restTime[j] = 1;
            return;
        }
    }
    F++;

    //检查空闲物理块
    for(int j = 0; j < 4; j++){
        if(mem[j] == -1){
            mem[j] = page;
            restTime[j] = 1;
            return;
        }
    }

    //LRU调度
    int max_j = 0;
    for(int j = 0; j < 4; j++){
        if(restTime[j] > restTime[max_j])max_j = j;
    }
    mem[max_j] = page;
    restTime[max_j] = 0;
    for(int j = 0; j < 4; j++){
        restTime[j]++;
    }
}

void LRU(){
    T = 0;
    F = 0;
    fill(restTime + 0, restTime + 4, -1);
    for(int i = 0; i < 320; i++)select_LRU(i);
    double ans = F / (T + F);
    cout<<"LRU算法缺页率为:";
    cout<<ans<<endl<<endl;
}


int main(){
    fill(inst + 0, inst + 320, -1);
    fill(mem + 0, mem + 4, -1);
    buildInst();

    Optimal();
    FIFO();
    LRU();

    return 0;
}