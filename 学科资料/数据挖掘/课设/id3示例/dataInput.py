# 读取new_train.csv

import pandas as pd
import numpy as np
import os
import json

path = os.path.join(os.path.dirname(__file__), 'new_train.csv')
data = pd.read_csv(path)

engilshDataIndexs = ['campaign','pdays','duration','age','job', 'marital', 'education', 'default', 'housing', 'loan', 'contact', 'month', 'day_of_week', 'poutcome', 'y']

def handleEnglishData(index):
    mapping = {}
    
    # 根据index找到需要处理的列
    # column = data[index]
    # 新建一个列，将原列的数据复制过来
    column = data[index].copy()
    
    # 处理数据
    if index == 'age':
        for i in range(len(column)):
            column[i] = column[i] // 10
    elif index == 'duration':
        for i in range(len(column)):
            # 如果大于1200（足足20分钟）的话，就设为5
            if column[i] > 1200:
                column[i] = 5
            else:
                column[i] = column[i] // 240
    elif index == 'pdays':
        for i in range(len(column)):
            if column[i] == 999:
                column[i] = 0
            else:
                column[i] = column[i] // 7 + 1
    elif index == 'campaign':
        for i in range(len(column)):
            if column[i] > 5 and column[i] <= 10:
                column[i] = 6
            elif column[i] > 10 and column[i] <= 20:
                column[i] = 7
            elif column[i] > 20:
                column[i] = 8
    elif index == 'month':
        # 旺季
        for i in range(len(column)):
            if column[i] in ['mar', 'oct', 'sep', 'dec']:
                column[i] = 1
            else:
                column[i] = 0
    else:
        for row in column:
            if row not in mapping:
                mapping[row] = len(mapping)
        print(mapping)
        
        for i in range(len(column)):
            column[i] = mapping[column[i]]
            
        # 将映射关系存入map.json
            
        with open(os.path.join(os.path.dirname(__file__), 'map' + index + '.json'), 'w') as f:
            json.dump(mapping, f,ensure_ascii=False)
            
    # 将处理后的数据写回data
    data[index] = column
        

def main():
    # 输出一下每列的题头
    print(data.columns)
    
    for index in engilshDataIndexs:
        handleEnglishData(index)
        
    # 处理后的数据写为train_ready.csv
    data.to_csv(os.path.join(os.path.dirname(__file__), 'train_ready.csv'), index=False)
    
    
if __name__ == '__main__':
    main()