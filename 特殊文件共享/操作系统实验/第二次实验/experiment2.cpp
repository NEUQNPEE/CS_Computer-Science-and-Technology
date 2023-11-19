#include <iostream>
#include <vector>
// #define maxSize 640
using namespace std;


int mem[641][1] = {0};

void printM(){
    int left = 0;
    //int right = -1;
    cout<<"------------空闲分区链-----------"<<endl;
    for(int i = 0; i <= 640; i++){
        
        if(mem[i][0] != mem[left][0]){
            cout<<"分区始址："<<left<<endl;
            cout<<"分区大小："<<i - left<<endl;
            if(mem[left][0] == 0)cout<<"分区状态：空闲"<<endl<<endl;
            else cout<<"执行进程"<<mem[left][0]<<endl<<endl;
            left = i;
        }
    }
    cout<<"------------空闲分区链-----------"<<endl<<endl;
}

void insFF(int num, int insSize){
    int left = -1;
    for(int i = 0; i < 640; i++){
        if(mem[i][0] == 0 && left == -1){
            left = i;
            continue;
        }
        if(left < 0)continue;

        int size;
        size = i - left;
        if(mem[i][0] == 0){
            if(insSize <= size + 1){
                for(int j = left; j <= i; j++)mem[j][0] = num;
                break;
            }
        }
        if(mem[i][0] != 0){
            if(insSize <= size){
                for(int j = left; j < i; j++)mem[j][0] = num;
            }
            else{
                left = -1;
                continue;
            }
        }
    }
    cout<<"插入进程"<<num<<"后，空闲分区链如下："<<endl;
    printM();
}

void insBF(int num, int insSize){
    int left = -1;
    int min_left = -1;
    int min_size = 1000;
    for(int i = 0; i <= 640; i++){
        if(mem[i][0] == 0 && left == -1){
            left = i;
            continue;
        }
        if(left < 0)continue;
        int size;
        size = i - left;
        if(mem[i][0] != 0){
            if(insSize < size && size < min_size){
                min_size = size;
                min_left = left;
            }
            left = -1;
        }
    }
    for(int i = min_left; i - min_left + 1 <= insSize; i++)mem[i][0] = num;
    cout<<"插入进程"<<num<<"后，空闲分区链如下："<<endl;
    printM();
}

void insWF(int num, int insSize){
    int left = -1;
    int max_left = -1;
    int max_size = -1;
    for(int i = 0; i <= 640; i++){
        if(mem[i][0] == 0 && left == -1){
            left = i;
            continue;
        }
        if(left < 0)continue;
        int size;
        size = i - left;
        if(mem[i][0] != 0){
            if(insSize < size && size > max_size){
                max_size = size;
                max_left = left;
            }
            left = -1;
        }
    }
    for(int i = max_left; i - max_left + 1 <= insSize; i++)mem[i][0] = num;
    cout<<"插入进程"<<num<<"后，空闲分区链如下："<<endl;
    printM();
}

void del(int num){
    for(int i = 0; i < 640; i++)
        if(mem[i][0] == num)mem[i][0] = 0;
    cout<<"删除进程"<<num<<"后，空闲分区链如下："<<endl;
    printM();
}

void FirstFit(){
    cout<<"--------------------首次适应算法--------------------"<<endl;
    insFF(1, 130);
    insFF(2, 60);
    insFF(3, 100);
    del(2);
    insFF(4, 200);
    del(3);
    del(1);
    insFF(5, 140);
    insFF(6, 60);
    insFF(7, 50);
    del(6);
    cout<<"--------------------首次适应算法--------------------"<<endl<<endl;
}

void BestFit(){
    cout<<"--------------------最佳适应算法--------------------"<<endl;
    insBF(1, 130);
    insBF(2, 60);
    insBF(3, 100);
    del(2);
    insBF(4, 200);
    del(3);
    del(1);
    insBF(5, 140);
    insBF(6, 60);
    insBF(7, 50);
    del(6);
    cout<<"--------------------最佳适应算法--------------------"<<endl<<endl;
}

void WorstFit(){
    cout<<"--------------------最差适应算法--------------------"<<endl;
    insWF(1, 130);
    insWF(2, 60);
    insWF(3, 100);
    del(2);
    insWF(4, 200);
    del(3);
    del(1);
    insWF(5, 140);
    insWF(6, 60);
    insWF(7, 50);
    del(6);
    cout<<"--------------------最差适应算法--------------------"<<endl<<endl;
}


int main(){
    mem[640][0] = -1000;    //临界值处理
    //FirstFit();
    BestFit();
    //WorstFit();
    /*
    for(int i = 0; i <= 640; i++)
        if(mem[i][0] != 0)cout<<i<<endl;
    */
    return 0;
}