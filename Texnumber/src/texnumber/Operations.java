/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package texnumber;
import java.util.*;
import java.io.*;
import java.text.SimpleDateFormat;
/**
 *
 * @author vlad gheorghiu
 */
public class Operations {

    String m_inputFile, m_outputFile;
    String m_inputPattern, m_outputPattern;
    List<String> m_labelList = new ArrayList<String>();

    int m_lines;

    Operations(String inputFile, String outputFile,
            String inputPattern, String outputPattern) throws IOException {
        m_inputFile = inputFile;
        m_outputFile = outputFile;

        m_inputPattern = inputPattern;
        m_outputPattern = outputPattern;

        m_lines=0;
        m_labelList = BuildLabelList(m_inputFile, m_inputPattern);
    }

    private List<String> BuildLabelList(String inputFile, String inputPattern)
            throws IOException {
        FileReader fin = new FileReader(inputFile);
        Scanner src = new Scanner(fin);
        List<String> labelList = new ArrayList<String>();

        while (src.hasNext()) // Read the file
        {
            String line = src.nextLine();
            // Build the token table
            String searchingPattern = new String("\\label{" + inputPattern);
            if (line.contains(searchingPattern)) // We found a \label{
            {
                // Find the relevant substrings \label{...} inside the token
                int indexa = 0;
                int indexb = 0;
                while (indexa != -1) {
                    indexa = line.indexOf(searchingPattern, indexb);
                    indexb = line.indexOf("}", indexa);
                    if (indexa != -1) {
                        labelList.add(line.substring(indexa + 7, indexb));
                    }
                }
            }
        }
        fin.close();
        return labelList;
    }

    private String ModifyLine(String line) {
        // reference = \eqref{
        // input_pattern = eqn
        // output_pattern = Aeqn

        String searchingPattern = new String("{" + m_inputPattern); // This is what we replace, i.e. \eqref{eqn
        String outputLine = new String();

        // Find the relevant substrings \label{...} inside the token
        int indexstart = 0;
        int indexa = 0;
        int indexb = 0;

        while (indexa != -1) {
            indexa = line.indexOf(searchingPattern, indexb);
            indexb = line.indexOf("}", indexa);
            String found = line.substring(indexa + 1, indexb); // i.e. eqn34

            outputLine = outputLine + line.substring(indexstart, indexa);
            // Now find the position of pattern in the labelList
            Iterator<String> itr = m_labelList.iterator();
            int pos = 0;
            while (itr.hasNext()) {
                pos++;
                String tmp = itr.next();
                if (tmp.equals(found)) {
                    found = m_outputPattern + pos;
                    break;
                }
            }
            indexstart = indexb + 1;
            outputLine = outputLine + "{" + found + "}";
            if (line.indexOf(searchingPattern, indexb) == -1) // Didn't find any other \eqref...
            {
                outputLine = outputLine + line.substring(indexstart, line.length());
                break;
            }
        }
        if(outputLine.compareTo(line)!=0)
            m_lines++;
        return outputLine;
    }

    public void ReplaceReferences() throws IOException {
        // patern = eqn

        FileReader fin = new FileReader(m_inputFile);
        Scanner src = new Scanner(fin);

        FileWriter fout = new FileWriter(m_outputFile);

        while (src.hasNext()) // Read the file
        {
            String inputLine = src.nextLine(); // We read a line
            String outputLine = new String(inputLine);

            // Modify, for \eqref, \ref and \label
            if (inputLine.contains("{" + m_inputPattern)) {
                outputLine = ModifyLine(inputLine);
            }

            // Write the results to the output file
            fout.write(outputLine + System.getProperty("line.separator"));
        }

        fin.close();
        fout.close();
}

    public void OutputLog() throws IOException {
        List<String> labelListNew = BuildLabelList(m_outputFile, m_outputPattern);
        // Build the new labels
        Iterator<String> itr = m_labelList.iterator();
        Iterator<String> itrNew = labelListNew.iterator();

        while (itr.hasNext()) {
                System.out.print(itr.next() + " -> " + itrNew.next() +
                        System.getProperty("line.separator"));
        }

        System.out.println();
        System.out.println("Input pattern: \\label{" + m_inputPattern);
        System.out.println("Output pattern: " + m_outputPattern);
        System.out.println("Total of " + m_labelList.size() + " labels." );
        System.out.print("Altered " + (int)(m_lines) + " lines.");

        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yy/MM/dd ");
        Date date = new java.util.Date();

        System.out.println(System.getProperty("line.separator")+
             "Current Time and Date: " + dateFormat.format(date));
    }
}
