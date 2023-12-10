namespace vscodeCX
{
    [Serializable]
    public class FCB
    {
        // 目录与文件列表
        public Dictionary<string, FCB> filesAndDirsList = new Dictionary<string, FCB>();

        // 名称
        private string name;

        // 属性
        private int attribute;

        // 起始盘块号
        private int startNum;

        // 大小
        private int size;

        // 父目录
        private FCB father = null;

        // 文件内容
        private string content = "";

        public FCB(string name, int attribute, int startNum, int size)
        {
            this.name = name;
            this.attribute = attribute;
            this.startNum = startNum;
            this.size = size;
        }

        public string Name
        {
            get { return name; }
            set { name = value; }
        }

        public int Attribute
        {
            get { return attribute; }
            set { attribute = value; }
        }

        public int StartNum
        {
            get { return startNum; }
            set { startNum = value; }
        }

        public int Size
        {
            get { return size; }
            set { size = value; }
        }

        public FCB Father
        {
            get { return father; }
            set { father = value; }
        }

        public string Content
        {
            get { return content; }
            set { content = value; }
        }
    }
}
