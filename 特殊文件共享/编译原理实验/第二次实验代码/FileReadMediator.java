import java.io.BufferedReader;

public class FileReadMediator implements IFileReadMediator {
    private BufferedReader br;

    public FileReadMediator(String source) throws Exception {
        br = new BufferedReader(new java.io.FileReader(source));
    }

    @Override
    public String readFile() throws Exception {
        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
            result += '\n';
        }

        result += '\0';

        return result;
    }

    @Override
    public void close() throws Exception {
        br.close();
    }

    
}
