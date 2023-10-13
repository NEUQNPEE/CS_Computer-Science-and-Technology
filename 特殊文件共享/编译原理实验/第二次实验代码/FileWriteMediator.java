package corepackage.compiler;

import java.io.BufferedWriter;

public class FileWriteMediator implements IFileWriteMediator {
    private BufferedWriter bw;
    private String str;

    public FileWriteMediator(String target) throws Exception {
        bw = new BufferedWriter(new java.io.FileWriter(target));
        str = "";
    }
    
    @Override
    public void writeFile() throws Exception {
        bw.write(str);
        str = "";
    }

    @Override
    public void write(String str) {
        this.str += str;
    }

    @Override
    public void clear() {
        str = "";
    }

    @Override
    public void close() throws Exception {
        bw.close();
    }

}
