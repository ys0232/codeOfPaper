package getInputResource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.*;
import java.text.DecimalFormat;
import java.text.Format;
import java.util.*;

/**
 * Created by lenovo on 2017/7/21.
 */
public class getDag {
    public void getdag(String filename) throws IOException {
        File file = new File(filename + ".xml");
        HashMap<Integer, Integer> fileValue = new HashMap<>();//(taskid,ouputDataSize)
        HashMap<Integer, String> parentAndChild = new HashMap<>();
        HashMap<Integer, String> runtime = new HashMap<>();//(taskid,runtime) record execu time of task
        SAXReader saxReader = new SAXReader();
        try {
            Document doc = saxReader.read(file);
            Element root = doc.getRootElement();
            Iterator iter = root.elementIterator();
            while (iter.hasNext()) {
                Element element = (Element) iter.next();
                String name = element.getName();
                if (name.equals("job")) {
                    Iterator jobIterator = element.elementIterator();
                    String ID = element.attributeValue("id");
                    int id = getStanderFormat(ID);
                    String time = element.attributeValue("runtime");
                    runtime.put(id, time);
                    int output = 0;
                   // int input = 0;
                    while (jobIterator.hasNext()) {
                        // get the size of job
                        Element jobElement = (Element) jobIterator.next();
                        String size = jobElement.attributeValue("size");
                        if (jobElement.attributeValue("link").equals("output")) {
                            output = output + Integer.valueOf(size);
                        }
                        fileValue.put(id, output);
                    }
                } else {
                    // get child taskid
                    String chileRef = element.attributeValue("ref");
                    int childID = getStanderFormat(chileRef);
                    // get parents taskid of child taskid
                    Iterator childIterator = element.elementIterator();
                    while (childIterator.hasNext()) {
                        Element eChild = (Element) childIterator.next();
                        String childName = eChild.getName();
                        if (childName.equals("parent")) {
                            String parentid = eChild.attributeValue("ref");
                            int parent = getStanderFormat(parentid);
                            if (parentAndChild.containsKey(childID))
                                parentAndChild.put(childID, parent + "," + parentAndChild.get(childID));
                            else
                                parentAndChild.put(childID, String.valueOf(parent));
                        }
                    }

                }
            }
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
        }

        HashMap<String, String> result = new HashMap<>();//(parentId_childId,transferDataSize)
        Iterator iterator = parentAndChild.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (int) iterator.next();//child taskid
            String[] strings = parentAndChild.get(id).split(",");//parents of child task
            for (String str : strings) {
                int temp = Integer.valueOf(str);
                result.put(str + "_" + id,String.valueOf( fileValue.get(temp)));
            }
        }
        PrintWriter pw = new PrintWriter(filename + ".txt");//store in DAGmodel_tasknum.txt
        Iterator iterator1 = result.keySet().iterator();
        while (iterator1.hasNext()) {
            String strings = (String) iterator1.next();
            String[] strings1 = strings.split("_");
            double time = Double.valueOf(result.get(strings)) / Math.pow(2, 27);//get transfer time
            DecimalFormat df = new DecimalFormat("0.00");

            int str1 = getStanderFormat(strings1[0]);
            int str2 = getStanderFormat(strings1[1]);

            System.out.println(str1 + "\t" + str2 + "\t" + result.get(strings) + "\t" + df.format(time));
            pw.write(str1 + "\t" + str2 + "\t" + df.format(time) + "\n");
            pw.flush();

        }
        PrintWriter pw1 = new PrintWriter(filename + "_.txt");//every task runtime store in DAGmodel_tasknum_.txt
        iterator1 = runtime.keySet().iterator();
        while (iterator1.hasNext()) {
            int strs = (int) iterator1.next();
            // int string = getStanderFormat(strs);
            System.out.println(strs + "\t" + runtime.get(strs));
            pw1.write(strs + "\t" + runtime.get(strs) + "\n");
            pw1.flush();
        }
    }

    public int getStanderFormat(String string) {
        string = string.replace("ID", "");
        int t = Integer.valueOf(string);
        return t;
    }

    public void makeDagStander(String transfile, String excutimefile, int taskNums, String DAGmodel) throws IOException {
        //加一个入节点和一个出节点
        File trans = new File(transfile + ".txt");
        File excufile = new File(excutimefile + ".txt");
        BufferedReader br = new BufferedReader(new InputStreamReader
                (new FileInputStream(trans), "utf-8"));
        String str = br.readLine();
        PrintWriter pw = new PrintWriter(transfile + "_" + (taskNums + 2) + ".txt");
        HashMap<String, String> child_parent = new HashMap<>();
        HashMap<String, String> parent_child = new HashMap<>();
        HashMap<String, String> result = new HashMap<>();//(parentsId_chlidId,transferTime)
        while (str != null) {
            String[] strs = str.split("\t");
            if (!strs[0].equals(strs[1])){
            child_parent.put(strs[1], strs[0]);
            parent_child.put(strs[0], strs[1]);
            int parent = Integer.valueOf(strs[0]);
            int child = Integer.valueOf(strs[1]);
            result.put((parent + 1) + "\t" + (child + 1), strs[2]);
            //in order to make enter taskid equal 0,every taskid increase to taskid+1
                 }
            str = br.readLine();
        }
        for (int i = 1; i < taskNums + 1; i++) {
            if (!child_parent.containsKey(i)) {
                //if task i has any parent,then leave task 0 is its parent,and transfer time is 0
                result.put(String.valueOf(0 + "\t" + (i)), String.valueOf(0));
            }
            if (!parent_child.containsKey(i) ) {
                //if task i don't have any child task,then leave only exit node as its child, and transfer time is 0
                result.put(String.valueOf((i) + "\t" + (taskNums + 1)), String.valueOf(0));
            }

        }
        //record the result into file named DAGmodel_tasknum_(tasknum+2).txt
        Iterator iterator = result.keySet().iterator();
        while (iterator.hasNext()) {
            String strs = (String) iterator.next();
            System.out.println(strs + "\t" + result.get(strs));
            pw.write(strs + "\t" + result.get(strs) + "\n");
            pw.flush();
        }
        br.close();
        pw.close();
        HashMap<String, String> read = new HashMap<>();

        br = new BufferedReader(new InputStreamReader(
                new FileInputStream(excufile), "utf-8"));
        str = br.readLine();
        while (str != null) {
            String[] strs = str.split("\t");
            int id=Integer.valueOf(strs[0]);
            read.put(String.valueOf(id+1), strs[1]);
            str = br.readLine();
        }
        //System.out.println(strings);
        pw = new PrintWriter(excufile);
        for (int i = 0; i <taskNums + 2; i++) {//make new virtual task's runtime is 0
            String t=String.valueOf(i);
            if (!read.containsKey(t)) {
                pw.write(i+ "\t" + 0 + "\n");
                pw.flush();
            } else {
                pw.write(i + "\t" + read.get(t) + "\n");
                pw.flush();
            }
        }
    }

    public static void main(String[] args) throws IOException {

        getDag dag1 = new getDag();
        String inputdir = "./input/";
        String DAGmodel = "Inspiral";
        int taskNums = 30;
        String dagfile=inputdir+DAGmodel + "/" + DAGmodel+"_"+taskNums;
        dag1.getdag(dagfile);//first get transfer time and runtime

       // String tranfile = inputdir + DAGmodel + "/" + DAGmodel + "_transfer_"+taskNums;
        String excufile = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums+"_" ;

        dag1.makeDagStander(dagfile, excufile, taskNums, DAGmodel);//and then add a output node and a input node

    }
}
