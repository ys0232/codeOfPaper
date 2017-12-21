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
        HashMap<Integer, String> fileValue = new HashMap<>();//(序号i,input_output)
        HashMap<Integer, String> parentAndChild = new HashMap<>();
        HashMap<Integer, String> runtime = new HashMap<>();
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
                    int input = 0;
                    while (jobIterator.hasNext()) {

                        // 获得当前job下的每一行
                        Element jobElement = (Element) jobIterator.next();

                        String size = jobElement.attributeValue("size");

                        if (jobElement.attributeValue("link").equals("output")) {
                            output = output + Integer.valueOf(size);
                        }
                        if (jobElement.attributeValue("link").equals("input")) {
                            input = input + Integer.valueOf(size);
                        }
                        fileValue.put(id, input + "_" + output);
                    }
                } else {
                    // 获取child自己的ID的属性
                    String chileRef = element.attributeValue("ref");
                    int childID = getStanderFormat(chileRef);
                    // 迭代获取父节点
                    Iterator childIterator = element.elementIterator();
                    ///  String parent =null;
                    // List<String> chileParentList = new ArrayList<String>();
                    while (childIterator.hasNext()) {
                        Element eChild = (Element) childIterator.next();
                        String childName = eChild.getName();

                        if (childName.equals("parent")) {
                            String parentid = eChild.attributeValue("ref");
                            int parent = getStanderFormat(parentid);
                            if (parentAndChild.containsKey(chileRef))
                                parentAndChild.put(childID, parent + "," + parentAndChild.get(chileRef));
                            else
                                parentAndChild.put(childID, String.valueOf(parent));
                        }
                    }

                }
            }
        } catch (DocumentException e) {
            System.out.println(e.getMessage());
        }

        HashMap<String, String> result = new HashMap<>();//(父节点p_子节点c,传输文件大小w)
        Iterator iterator = parentAndChild.keySet().iterator();
        while (iterator.hasNext()) {
            int id = (int) iterator.next();//子节点
            String[] strings = parentAndChild.get(id).split(",");//子节点对应的父节点集合
            // System.out.println(id+"\t"+Arrays.toString(strings));
            for (String str : strings) {
                //System.out.println(fileValue);
                int temp = Integer.valueOf(str);
                String[] strings1 = fileValue.get(temp).split("_");//input_output
                result.put(str + "_" + id, strings1[1]);
            }
        }
        PrintWriter pw = new PrintWriter(filename + ".txt");
        Iterator iterator1 = result.keySet().iterator();
        while (iterator1.hasNext()) {
            String strings = (String) iterator1.next();
            String[] strings1 = strings.split("_");
            double time = Double.valueOf(result.get(strings)) / Math.pow(2, 27);
            DecimalFormat df = new DecimalFormat("0.00");

            int str1 = getStanderFormat(strings1[0]);
            int str2 = getStanderFormat(strings1[1]);

            System.out.println(str1 + "\t" + str2 + "\t" + result.get(strings) + "\t" + df.format(time));
            pw.write(str1 + "\t" + str2 + "\t" + df.format(time) + "\n");
            pw.flush();

        }
        PrintWriter pw1 = new PrintWriter(filename + "_.txt");
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
        HashMap<String, String> result = new HashMap<>();//父节点  子节点  传输时间
        while (str != null) {
            String[] strs = str.split("\t");
            if (!strs[0].equals(strs[1])){
            child_parent.put(strs[1], strs[0]);
            parent_child.put(strs[0], strs[1]);
            int parent = Integer.valueOf(strs[0]);
            int child = Integer.valueOf(strs[1]);
            result.put((parent + 1) + "\t" + (child + 1), strs[2]);//每个节点编号增加一个单位，使唯一入节点编号为0
                 }
            str = br.readLine();
        }
        for (int i = 1; i < taskNums + 1; i++) {
            if (!child_parent.containsKey(i)) {
                //i无父节点，令唯一入节点0为它的父节点，且传输时间为0
                result.put(String.valueOf(0 + "\t" + (i)), String.valueOf(0));
            }
            if (!parent_child.containsKey(i) ) {
                //i不作为父节点，令唯一出节点(taskNums)作为它的子节点，且传输时间为0
                result.put(String.valueOf((i) + "\t" + (taskNums + 1)), String.valueOf(0));
            }

        }
        //写入传输时间文件
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
       // String strings = "";
        while (str != null) {

            String[] strs = str.split("\t");
            int id=Integer.valueOf(strs[0]);
            read.put(String.valueOf(id+1), strs[1]);
            str = br.readLine();
        }
        //System.out.println(strings);
        pw = new PrintWriter(excufile);
        for (int i = 0; i <taskNums + 2; i++) {
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
     /*   String filename = "./inputDir/Inspiral/Inspiral_30";
        //File file=new File("./new/Montage.xml");
        getDag dag = new getDag();
        dag.getdag(filename);*/
        //  DecimalFormat df=new DecimalFormat("0.00");
        //System.out.println(df.format(3432423.43543543));
        String inputdir = "./inputDir/";
        String DAGmodel = "Sipht";
        int taskNums = 968;
        //int processorNums = 10;
        String tranfile = inputdir + DAGmodel + "/" + DAGmodel + "_"+taskNums;
        String excufile = inputdir + DAGmodel + "/" + DAGmodel + "_" + taskNums + "";
        getDag dag1 = new getDag();
         //dag1.getdag(tranfile);
        dag1.makeDagStander(tranfile, excufile, taskNums, DAGmodel);

    }
}
