import json
import os

path = os.path.join(os.path.dirname(__file__), 'result.json')
data = json.load(open(path))

# 凡检测到data中name为Indexs中的字符串的情况，就根据map(index).json中的映射关系，将其children的name从value替换为映射关系的key
Indexs = ['job', 'marital', 'education', 'default', 'housing', 'loan', 'contact', 'month', 'day_of_week', 'poutcome', 'y']

# 递归遍历data
def dfs(data):
    for item in data:
        name = item.get('name')
        if name in Indexs:
            # 读取map.json
            mapping = json.load(open(os.path.join(os.path.dirname(__file__), 'map' + name + '.json')))
            for child in item.get('children'):
                for key, value in mapping.items():
                    if child['name'] == value:
                        child['name'] = key
        
        if name == 'job':
            for child in item.get('children'):
                if child['name'] == 'admin.':
                    child['name'] = '行政'
                if child['name'] == 'blue-collar':
                    child['name'] = '蓝领'
                if child['name'] == 'entrepreneur':
                    child['name'] = '企业家'
                if child['name'] == 'housemaid':
                    child['name'] = '家政'
                if child['name'] == 'management':
                    child['name'] = '管理'
                if child['name'] == 'retired':
                    child['name'] = '退休'
                if child['name'] == 'self-employed':
                    child['name'] = '自由职业'
                if child['name'] == 'services':
                    child['name'] = '服务'
                if child['name'] == 'student':
                    child['name'] = '学生'
                if child['name'] == 'technician':
                    child['name'] = '技术'
                if child['name'] == 'unemployed':
                    child['name'] = '失业'
                if child['name'] == 'unknown':
                    child['name'] = '未知'
            item['name'] = '职业'
        if name == 'marital':
            for child in item.get('children'):
                if child['name'] == 'divorced':
                    child['name'] = '离婚'
                if child['name'] == 'married':
                    child['name'] = '已婚'
                if child['name'] == 'single':
                    child['name'] = '单身'
                if child['name'] == 'unknown':
                    child['name'] = '未知'
            item['name'] = '婚姻状况'
        if name == 'education':
            for child in item.get('children'):
                if child['name'] == 'basic.4y':
                    child['name'] = '4年制'
                if child['name'] == 'basic.6y':
                    child['name'] = '6年制'
                if child['name'] == 'basic.9y':
                    child['name'] = '9年制'
                if child['name'] == 'high.school':
                    child['name'] = '高中'
                if child['name'] == 'illiterate':
                    child['name'] = '文盲'
                if child['name'] == 'professional.course':
                    child['name'] = '职高'
                if child['name'] == 'university.degree':
                    child['name'] = '大学'
                if child['name'] == 'unknown':
                    child['name'] = '未知'
            item['name'] = '教育程度'
        if name == 'default':
            for child in item.get('children'):
                if child['name'] == 'no':
                    child['name'] = '无'
                if child['name'] == 'yes':
                    child['name'] = '有'
                if child['name'] == 'unknown':
                    child['name'] = '未知'
            item['name'] = '信用违约'
        if name == 'housing':
            for child in item.get('children'):
                if child['name'] == 'no':
                    child['name'] = '无'
                if child['name'] == 'yes':
                    child['name'] = '有'
                if child['name'] == 'unknown':
                    child['name'] = '未知'
            item['name'] = '住房贷款'
        if name == 'loan':
            for child in item.get('children'):
                if child['name'] == 'no':
                    child['name'] = '无'
                if child['name'] == 'yes':
                    child['name'] = '有'
                if child['name'] == 'unknown':
                    child['name'] = '未知'
            item['name'] = '个人贷款'
        if name == 'contact':
            for child in item.get('children'):
                if child['name'] == 'cellular':
                    child['name'] = '蜂窝'
                if child['name'] == 'telephone':
                    child['name'] = '电话'
            item['name'] = '联系方式'
        if name == 'day_of_week':
            for child in item.get('children'):
                if child['name'] == 'fri':
                    child['name'] = '周五'
                if child['name'] == 'mon':
                    child['name'] = '周一'
                if child['name'] == 'thu':
                    child['name'] = '周四'
                if child['name'] == 'tue':
                    child['name'] = '周二'
                if child['name'] == 'wed':
                    child['name'] = '周三'
            item['name'] = '周几'
        if name == 'poutcome':
            for child in item.get('children'):
                if child['name'] == 'failure':
                    child['name'] = '失败'
                if child['name'] == 'nonexistent':
                    child['name'] = '不存在'
                if child['name'] == 'success':
                    child['name'] = '成功'
            item['name'] = '之前营销'
        if name == 'age':
            for child in item.get('children'):
                child['name'] *= 10
            item['name'] = '年龄'
        if name == 'duration':
            for child in item.get('children'):
                if child['name'] == 5:
                    child['name'] = '20min+'
                else:
                    child['name'] = str(child['name']*4) + 'min+'
            item['name'] = '通话时长'
        if name == 'pdays':
            for child in item.get('children'):
                if child['name'] == 0:
                    child['name'] = '从未'
                else:
                    # child['name'] = (child['name'] - 1) * 7
                    child['name'] = str((child['name'] - 1)) + '周'
            item['name'] = '时隔'
        if name == 'campaign':
            for child in item.get('children'):
                if child['name'] == 6:
                    child['name'] = str(5) + '+次'
                elif child['name'] == 7:
                    child['name'] = str(10) + '+次'
                elif child['name'] == 8:
                    child['name'] = str(20) + '+次'
                else:
                    child['name'] = str(child['name']) + '次'
            item['name'] = '联系次数'
        if name == 'month':
            for child in item.get('children'):
                if child['name'] == 1:
                    child['name'] = '旺季'
                else:
                    child['name'] = '淡季'
            item['name'] = '月份'
        if 'children' in item:
            dfs(item.get('children'))
dfs(data)

# 存储
with open(os.path.join(os.path.dirname(__file__), 'result.json'), 'w') as f:
    json.dump(data, f)