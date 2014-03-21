/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package texnumber;

/**
 *
 * @author vlad gheorghiu
 */
import java.io.*;

public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            File in = new File(args[0]);
            File out = new File(args[1]);

            if (in.getAbsolutePath().compareTo(out.getAbsolutePath()) == 0) {
                System.out.println("Cannot use the same output as the input!");
                return;
            }

            try {
                Operations op = new Operations(args[0], args[1],
                        args[2], args[3]);
                op.ReplaceReferences();
                op.OutputLog();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Usage: java -jar Texnumber.jar <in.tex> " +
                    "<out.tex> <pattern> <replacement>");
            System.out.println("(c) Vlad Gheorghiu 2012, vsoftco@gmail.com");
            return;
        }
    }
}

