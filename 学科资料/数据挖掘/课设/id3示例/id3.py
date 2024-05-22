import pandas as pd
import numpy as np
import os
import matplotlib.pyplot as plt
import networkx as nx
import json

openDebugMessage = False

path = os.path.join(os.path.dirname(__file__), 'train_ready.csv')
data = pd.read_csv(path)

# 把job一列删掉
data = data.drop(['job'], axis=1)

# 获取数据的总数
total = len(data)
print("数据总数为：", total)
    
# 获取数据中的分类
targetIndex = data['y']

IndexValues = {}
IndexHs = {}

# 计算信息熵
def calcEntropy(targetIndex,total):
    print("当前数据量为：", total)
    # 计算每个分类的概率
    p = {}
    for c in targetIndex:
        if c not in p:
            p[c] = 1
        else:
            p[c] += 1
            
    print("目标属性分类情况：", p)
    
    # 计算信息熵
    entropy = 0
    for c in p:
        print("分类", c, "的概率为：", p[c] / total)
        entropy += -p[c] / total * np.log2(p[c] / total)
    
    print("信息熵为：", entropy)
    return entropy

# 计算条件熵
def calcConditionalEntropy(data,index):
    values = data[index]
    # 计算每个随机变量的概率
    thisIndexValue = {}
    for v in values:
        if v not in thisIndexValue:
            thisIndexValue[v] = 1
        else:
            thisIndexValue[v] += 1
    
    if openDebugMessage:
        print("随机变量", index, "的取值情况：", thisIndexValue)
        
    IndexValues[index] = thisIndexValue
    
    # 计算在每个随机变量的情况下，目标属性的概率
    thisIndexH = {}
    # 提取data的index列与'y'列
    tempData = data[[index, 'y']]
    for v in tempData.values:
        if v[1] == 1:
            if v[0] not in thisIndexH:
                thisIndexH[v[0]] = 1
            else:
                thisIndexH[v[0]] += 1
                
            for v in thisIndexValue:
                if v not in thisIndexH:
                    thisIndexH[v] = 0
    

    # for v1, v2 in zip(values, targetIndex):
    #     # 只统计targetIndex为1的情况
    #     if v2 == 1:
    #         if v1 not in thisIndexH:
    #             thisIndexH[v1] = 1
    #         else:
    #             thisIndexH[v1] += 1
                
    #     # 遍历完了还有v1没有出现的情况，给它赋值0
    #     for v in thisIndexValue:
    #         if v not in thisIndexH:
    #             thisIndexH[v] = 0
    
    if openDebugMessage:
        print("随机变量", index, "的取值情况下，目标属性为1的情况：", thisIndexH)
        outputData = pd.DataFrame([thisIndexValue, thisIndexH])
        # 第三行为概率
        outputData.loc[2] = outputData.loc[1] / outputData.loc[0]
        print(outputData)
            
        
    IndexHs[index] = thisIndexH
    
    # 计算每个取值下的信息熵
    everyValueEntropy = {}
    for v in thisIndexValue:
        p = thisIndexH[v] / thisIndexValue[v]
        if p == 0 or p == 1:
            everyValueEntropy[v] = 0
        else:
            everyValueEntropy[v] = -p * np.log2(p) - (1 - p) * np.log2(1 - p)
        
    # 计算条件熵
    conditionalEntropy = 0
    for v in everyValueEntropy:
        conditionalEntropy += thisIndexValue[v] / len(data) * everyValueEntropy[v]
    
    if openDebugMessage:
        print("随机变量", index, "的条件熵为：", conditionalEntropy)
    return conditionalEntropy

# 计算信息增益
def calcInformationGain(entropy, conditionalEntropy):
    if openDebugMessage:
        print("信息增益为：", entropy - conditionalEntropy)
    return entropy - conditionalEntropy

def getInformationGain(entropy,data):
    IGs = {}
    for index in data.columns[:-1]:
        conditionalEntropy = calcConditionalEntropy(data,index)
        informationGain = calcInformationGain(entropy, conditionalEntropy)
        IGs[index] = informationGain
        if openDebugMessage:
            print("随机变量", index, "的信息增益为：", informationGain)
            
    return IGs

# 每轮结束后信息输出
def outputMessageByRound(IG,IndexValue,IndexH):
    print("信息增益最大的随机变量", IG, "的取值情况为：", IndexValue)
    print("信息增益最大的随机变量", IG, "的取值情况下，目标属性为1的情况为：", IndexH)
    for item in zip(IndexValue, IndexH):
        print("随机变量", IG, "的取值为", item[0][0], "时，目标属性为1的概率为：", item[1][1] / item[0][1])
    
    
# 输出绘图数据
def outputGraphData(IGs,num):
    IGs = pd.DataFrame(IGs)
    IGs.to_csv(os.path.join(os.path.dirname(__file__), 'Gain' + str(num) + '.csv'), index=False)

# 存储绘图数据
def saveGraphData(IGs,num,IndexValue,IndexH):
    p = {}
    for item in zip(IndexValue, IndexH):
        p[item[0][0]] = "{:.1%}".format(item[1][1] / item[0][1])
    graphData = []
    temp = []
    for item in IndexValues[IGs[0][0]]:
        t = {
            "name": item[0],
            "children": [
                {
                    "name": p[item[0]],
                }
            ]
        }
        temp.append(t)
    graphData.append({"name": IGs[0][0], "children": temp})
    
    with open(os.path.join(os.path.dirname(__file__), 'result' + str(num) + '.json'), 'w') as f:
        json.dump(graphData, f)

# IndexValues与IndexHs的排序
def sortIndexValuesAndIndexHs(IGs):
    IndexValues[IGs[0][0]] = sorted(IndexValues[IGs[0][0]].items(), key=lambda x: x[0])
    IndexHs[IGs[0][0]] = sorted(IndexHs[IGs[0][0]].items(), key=lambda x: x[0])

def id3(targetColumn,data,num):
    entropy = calcEntropy(data['y'], len(data))
    IGs = getInformationGain(entropy, data)
    IGs = sorted(IGs.items(), key=lambda x: x[1], reverse=True)
    print("信息增益排序前三为：", IGs[:3])
    
    outputGraphData(IGs,num)
    
    sortIndexValuesAndIndexHs(IGs)
    
    saveGraphData(IGs,num,IndexValues[IGs[0][0]],IndexHs[IGs[0][0]])
    
    outputMessageByRound(IGs[0][0],IndexValues[IGs[0][0]],IndexHs[IGs[0][0]])
def main():
    # 随机变量
    Indexs = data.columns[:-1]
    # print("随机变量为：", Indexs)
    
    id3(targetIndex,data,0)

    # 第二轮
    # 提取duration为0的数据构成新的数据集
    newData1 = data[data['duration'] == 0]
    # 删去duration列
    newData1 = newData1.drop(['duration'], axis=1)
    
    id3(newData1['y'],newData1,1)
    

    # 第三轮
    # 提取pdays为0的数据构成新的数据集
    newData2 = newData1[data['pdays'] == 0]
    newData2 = newData2.drop(['pdays'], axis=1)
    
    id3(newData2['y'],newData2,2)

    # # 第四轮
    # # 提取month为0的数据构成新的数据集
    # newData3 = newData2[data['month'] == 0]
    # newData3 = newData3.drop(['month'], axis=1)
    
    # id3(newData3['y'],newData3,3)
    

    
    # # 第五轮
    # # 提取age = 1、2、3、4、5、6的数据，分别构成新的数据集
    # newData4_1 = newData3[newData3['age'] == 2]
    # newData4_1 = newData4_1.drop(['age'], axis=1)
    # newData4_2 = newData3[newData3['age'] == 3]
    # newData4_2 = newData4_2.drop(['age'], axis=1)
    # newData4_3 = newData3[newData3['age'] == 4]
    # newData4_3 = newData4_3.drop(['age'], axis=1)
    # newData4_4 = newData3[newData3['age'] == 5]
    # newData4_4 = newData4_4.drop(['age'], axis=1)
    # newData4_5 = newData3[newData3['age'] == 6]
    # newData4_5 = newData4_5.drop(['age'], axis=1)

    # id3(newData4_1['y'],newData4_1,4.1)
    # id3(newData4_2['y'],newData4_2,4.2)
    # id3(newData4_3['y'],newData4_3,4.3)
    # id3(newData4_4['y'],newData4_4,4.4)
    # id3(newData4_5['y'],newData4_5,4.5)
    
    #####################################
    #tip 第一次回溯到第三轮
    # # 第四轮
    # # 提取month为1的数据构成新的数据集
    # newData3 = newData2[data['month'] == 1]
    # newData3 = newData3.drop(['month'], axis=1)
    
    # id3(newData3['y'],newData3,3)
    
    ######################################
    #tip 第二次回溯到第一轮
    # # 第二轮
    # # 提取duration为1的数据构成新的数据集
    # newData1 = data[data['duration'] == 1]
    # newData1 = newData1.drop(['duration'], axis=1)
    
    # id3(newData1['y'],newData1,1)
    
    # # 第三轮
    # # 提取poutcome为0、1的数据构成新的数据集
    # newData2_1 = newData1[newData1['poutcome'] == 0]
    # newData2_1 = newData2_1.drop(['poutcome'], axis=1)
    # newData2_2 = newData1[newData1['poutcome'] == 1]
    # newData2_2 = newData2_2.drop(['poutcome'], axis=1)
    
    # id3(newData2_1['y'],newData2_1,2.1)
    # id3(newData2_2['y'],newData2_2,2.2)
    
    # # 第四轮
    # # 分别提取两个数据集中的mouth为0的数据构成新的数据集
    # newData3_1 = newData2_1[newData2_1['month'] == 0]
    # newData3_1 = newData3_1.drop(['month'], axis=1)
    # newData3_2 = newData2_2[newData2_2['month'] == 0]
    # newData3_2 = newData3_2.drop(['month'], axis=1)
    
    # id3(newData3_1['y'],newData3_1,3.1)
    # id3(newData3_2['y'],newData3_2,3.2)
    
    # # 第五轮
    # # 提取newData3_1中的contact为0、1的数据构成新的数据集
    # newData4_1 = newData3_1[newData3_1['contact'] == 0]
    # newData4_1 = newData4_1.drop(['contact'], axis=1)
    # newData4_2 = newData3_1[newData3_1['contact'] == 1]
    # newData4_2 = newData4_2.drop(['contact'], axis=1)
    
    # id3(newData4_1['y'],newData4_1,4.1)
    # id3(newData4_2['y'],newData4_2,4.2)

if __name__ == '__main__':
    main()