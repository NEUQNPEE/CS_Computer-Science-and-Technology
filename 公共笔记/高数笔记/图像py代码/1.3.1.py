import matplotlib.pyplot as plt
import numpy as np

# 生成x的取值范围
x = np.linspace(0, 2.5, 100)

# 计算y的取值
y1 = np.ones_like(x)
y2 = x
y3 = x**2/2

# 创建图形并绘制函数图像
plt.plot(x, y1, label='y=1')
plt.plot(x, y2, label='y=x')
plt.plot(x, y3, label='y=x^2')

# 添加图例和标题
plt.legend()
plt.title('Function Graphs')

# 显示图形
plt.show()