import framework.AbstractGrader;
import framework.project5.Grader;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        {
            InputStream input = new FileInputStream("/home/cs323/Desktop/cs323/CS323-Compilers-2025F-Projects/project5_testcases/test02/splc.c");
//            OutputStream output = new FileOutputStream("testcases/project3/err_05.txt");
            OutputStream output = System.out;
            AbstractGrader grader = new Grader(input, output, System.out);
            grader.run();
        }
    }
}