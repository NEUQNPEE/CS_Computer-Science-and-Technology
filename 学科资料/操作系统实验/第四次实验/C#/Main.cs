namespace vscodeCX
{
    public class Interface
    {
        // 主函数
        public static void Main(String[] args) {
            FileSystemManager manager = new();
            Run(manager);
        }

        private static void Run(FileSystemManager manager)
        {
            manager.ShowPath();
            String str;
            while((str = Console.ReadLine()) != null)
            {
                string[] command = str.Split(' ');
                switch(command[0])
                {
                    case "format":
                        manager.Format();
                        break;
                    case "mkdir":
                        if(command.Length == 1)
                        {
                            Console.WriteLine("请输入文件夹名");
                            break;
                        }
                        manager.MkDir(command[1]);
                        break;
                    case "rmdir":
                    case "rm":
                        manager.RM(command[1]);
                        break;
                    case "ls":
                        manager.LS();
                        break;
                    case "cd":
                        manager.CD(command[1]);
                        break; 
                    case "create":
                        if(command.Length == 1)
                        {
                            Console.WriteLine("请输入文件名");
                            break;
                        }

                        if(command.Length == 2)
                        {
                            Console.WriteLine("请输入文件大小");
                            break;
                        }
                        manager.Create(command[1], int.Parse(command[2]));
                        break;
                    case "open":
                        manager.Open(command[1]);
                        break;
                    case "close":
                        manager.Close();
                        break;
                    case "read":
                        manager.Read();
                        break;
                    case "write":
                        manager.Write(command[1],int.Parse(command[2]));
                        break;
                    case "exit":
                        manager.Exit();
                        return;
                    default:
                        Console.WriteLine("未知命令");
                        break;
                }
                manager.ShowPath();
            }
            
        }
        
    }
}