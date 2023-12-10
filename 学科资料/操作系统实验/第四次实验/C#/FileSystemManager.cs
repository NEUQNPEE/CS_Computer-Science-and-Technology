using System.Text.Json;
using System.Text;

namespace vscodeCX
{
    public class FileSystemManager
    {
        // 用于确定是目录还是文件
        private const int IS_DIR = 3;
        private const int IS_FILE = 2;

        // 当前目录
        private FCB currentDir;

        // MAP，用于存储所有的文件
        private Dictionary<string, FCB> totalFiles = new();

        static class FATManageMent
        {
            // FAT 终结符
            public const int FAT_END = 255;

            // FAT表的盘块个数
            public const int BLOCK_SIZE = 128;

            // 剩余盘块
            public static int available;

            public static int[] FAT = new int[BLOCK_SIZE];

            public static void initFAT()
            {
                // FAT表初始化，FAT[0]为根目录
                Array.Fill(FAT, 0);
                FAT[0] = FAT_END;
                // 剩余块数
                available = BLOCK_SIZE - 1;
            }

            public static int allocFAT(int size)
            {
                int[] startNum = new int[BLOCK_SIZE];
                int currentBlock = 1;
                int allocatedBlockCount = 0;

                while (allocatedBlockCount < size)
                {
                    if (FAT[currentBlock] == 0)
                    {
                        // 给文件分配空闲磁盘块
                        startNum[allocatedBlockCount] = currentBlock;
                        // 如果已经分配了磁盘块，就把上一个磁盘块的下一个磁盘块号指向当前磁盘块
                        if (allocatedBlockCount > 0)
                        {
                            FAT[startNum[allocatedBlockCount - 1]] = currentBlock;
                        }
                        allocatedBlockCount++;
                    }
                    currentBlock++;
                }

                // 注意此时currentBlock已经++，所以要减一
                FAT[currentBlock - 1] = FAT_END;
                return startNum[0];
            }

            // 释放文件对应的FAT表占用空间
            public static void delFAT(int startNum)
            {
                int nextBlock;
                int currentBlock = startNum;
                int freeSize = 0;
                while (FAT[currentBlock] != FAT_END)
                {
                    nextBlock = FAT[currentBlock];
                    FAT[currentBlock] = 0;
                    currentBlock = nextBlock;
                    freeSize++;
                }
                FAT[currentBlock] = 0;
                available += freeSize + 1;
            }

            public static void FATlist()
            {
                Console.WriteLine(string.Join(", ", FAT));
            }
        }

        public FileSystemManager()
        {
            FATManageMent.initFAT();
            // 根目录命名为root
            FCB root = new("root", 0, 0, 1);
            root.Father = root;
            currentDir = root;
        }

        // 1. 格式化
        public void Format()
        {
            string path = "./File.json";

            try
            {
                using (FileStream fs = new FileStream(path, FileMode.Create))
                {
                    foreach (FCB fCB in totalFiles.Values)
                    {
                        string json = JsonSerializer.Serialize(fCB);
                        byte[] bytes = Encoding.UTF8.GetBytes(json);
                        fs.Write(bytes, 0, bytes.Length);
                        fs.WriteByte((byte)'\n');
                    }
                }
            }
            catch (IOException e)
            {
                Console.WriteLine(e.Message);
            }

            Console.WriteLine("格式化成功");
        }

        // 2. 创建目录
        public void MkDir(string name)
        {
            // 如果当前目录下已经有同名目录，则报错
            if (currentDir.filesAndDirsList.ContainsKey(name))
            {
                Console.WriteLine("当前目录下已经有同名目录");
                return;
            }

            if (FATManageMent.available <= 0)
            {
                Console.WriteLine("磁盘空间不足");
                return;
            }

            int startNum = FATManageMent.allocFAT(1);
            FCB newDir = new(name, IS_DIR, startNum, 1);
            newDir.Father = currentDir;
            currentDir.filesAndDirsList.Add(name, newDir);
            FATManageMent.available--;

            Console.WriteLine("创建目录成功");
        }

        // 3. 删除目录/9. 删除文件
        public void RM(string name)
        {
            FCB target = currentDir.filesAndDirsList[name];
            // 如果当前目录下没有同名目标，则报错
            if (target == null)
            {
                Console.WriteLine("删除失败，目标不存在");
                return;
            }

            // 如果要删除目录，但是目录下有文件，则提示用户是否删除
            if (target.Attribute == IS_DIR && target.filesAndDirsList.Count > 0)
            {
                Console.WriteLine("目录下有文件，是否删除？(y/n)");
                string input = Console.ReadLine() ?? "";
                if (input == "y")
                {
                    // 删除目录下的所有文件
                    foreach (FCB fCB in target.filesAndDirsList.Values)
                    {
                        FATManageMent.delFAT(fCB.StartNum);
                        totalFiles.Remove(fCB.Name);
                    }
                    // 删除目录
                    currentDir.filesAndDirsList.Remove(name);
                    FATManageMent.delFAT(target.StartNum);
                    FATManageMent.available++;
                    Console.WriteLine("删除成功");
                }
                else
                {
                    Console.WriteLine("取消删除");
                }

                return;
            }

            // 如果要删除的是文件
            currentDir.filesAndDirsList.Remove(name);
            totalFiles.Remove(name);
            FATManageMent.delFAT(target.StartNum);
            Console.WriteLine((target.Attribute == IS_DIR ? "目录" : "文件") + target.Name + "删除成功");
        }

        // 4. 显示目录
        public void LS()
        {
            if (currentDir.filesAndDirsList.Count == 0)
            {
                Console.WriteLine("当前目录为空");
                return;
            }

            Console.WriteLine("文件名\t 文件类型\t 文件大小\t 起始盘块号");
            foreach (FCB fCB in currentDir.filesAndDirsList.Values)
            {
                Console.WriteLine(
                    fCB.Name
                        + "\t "
                        + (fCB.Attribute == IS_DIR ? "目录" : "文件")
                        + "\t "
                        + fCB.Size
                        + "\t "
                        + fCB.StartNum
                );
            }
        }

        // 显示目录
        public void ShowPath()
        {
            Console.Write(currentDir.Name + ">>");
        }

        // 5. 更改当前目录
        public void CD(string name)
        {
            if (name == "../")
            {
                CDPP();
                return;
            }

            FCB target = currentDir.filesAndDirsList[name];
            if (target == null)
            {
                Console.WriteLine("目录不存在");
                return;
            }

            if (target.Attribute == IS_FILE)
            {
                Console.WriteLine("目标是文件,打开文件请使用open命令");
                return;
            }

            currentDir = target;
        }

        // 返回上一级目录
        public void CDPP()
        {
            if (currentDir.Father == null)
            {
                Console.WriteLine("返回上一级目录失败");
                return;
            }

            currentDir = currentDir.Father;
        }

        // 6. 创建文件
        public void Create(string name, int size)
        {
            // 如果当前目录下已经有同名文件，则报错
            if (currentDir.filesAndDirsList.ContainsKey(name))
            {
                Console.WriteLine("当前目录下已经有同名文件");
                return;
            }

            if (FATManageMent.available < size)
            {
                Console.WriteLine("磁盘空间不足");
                return;
            }

            int startNum = FATManageMent.allocFAT(size);
            FCB newFile = new(name, IS_FILE, startNum, size) { Father = currentDir };
            currentDir.filesAndDirsList.Add(name, newFile);
            totalFiles.Add(name, newFile);
            FATManageMent.available -= size;

            Console.WriteLine("创建文件成功");
        }

        // 7. 打开文件
        public void Open(string name)
        {
            if (!currentDir.filesAndDirsList.TryGetValue(name, out FCB target))
            {
                Console.WriteLine("文件不存在");
                return;
            }

            if (target.Attribute == IS_DIR)
            {
                Console.WriteLine("目标是目录,打开目录请使用cd命令");
                return;
            }

            Console.WriteLine("打开文件成功");
            currentDir = target;
        }

        // 8. 关闭文件
        public void Close()
        {
            if (currentDir.Attribute == IS_FILE)
            {
                Console.WriteLine("文件已关闭");
                CDPP();
                return;
            }

            Console.WriteLine("没有打开的文件");
        }

        // 10. 读文件
        public void Read()
        {
            if (currentDir.Attribute != IS_FILE)
            {
                Console.WriteLine("当前未打开文件");
                return;
            }

            Console.WriteLine(currentDir.Content);
        }

        // 11. 写文件
        public void Write(string content, int offset)
        {
            if (currentDir.Attribute != IS_FILE)
            {
                Console.WriteLine("当前未打开文件");
                return;
            }

            if (offset > currentDir.Size)
            {
                Console.WriteLine("写入失败，偏移量超过文件大小");
                return;
            }

            if (offset < 0)
            {
                Console.WriteLine("写入失败，偏移量不能为负数");
                return;
            }

            // 如果本来文件内容就为空，那么直接写入
            if (currentDir.Content.Length == 0)
            {
                currentDir.Content = content;
                Console.WriteLine("写入成功");
                return;
            }
            currentDir.Content =
                currentDir.Content[..offset]
                + content
                + currentDir.Content[offset..];
            Console.WriteLine("写入成功");
        }

        // 12. 退出
        public void Exit()
        {
            Console.WriteLine("退出成功");
            Environment.Exit(0);
        }
    }
}
