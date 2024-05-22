from pyecharts import options as opts
from pyecharts.charts import Tree
import json
import os

path = os.path.join(os.path.dirname(__file__), 'result.json')
data = json.load(open(path))

c = (
    Tree(init_opts=opts.InitOpts(width="2560px", height="1440px"))
    .add(""
         ,data
         ,orient="TB"
         ,label_opts = opts.LabelOpts(font_size=15,position='top',font_weight='bold')
         ,pos_right ='5%'
         ,collapse_interval = 0
         ,initial_tree_depth = -1
         )
    .set_global_opts(title_opts=opts.TitleOpts(title="Tree-基本示例"))
    .render("tree_base.html")
)
