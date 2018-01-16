import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class getDag {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {

		// s设置文件的路径
		String sourceFile = "./new/Montage_25.xml";
		// 设置读取的reader
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(sourceFile);
		} catch (DocumentException e) {

			e.printStackTrace();
		}

		// 规整存储输出文件名以及size大小
		Map<String, Integer> outputMap = new HashMap<String, Integer>();

		// 存储父子关系
		Map<String, List<String>> parentMap = new LinkedHashMap<>();

		Element root = document.getRootElement();
		Iterator it = root.elementIterator();

		while (it.hasNext()) {
			Element e = (Element) it.next();
			String elementName = e.getName();

			if (elementName.equals("job")) {

				Iterator jobIterator = e.elementIterator();
				String ID = e.attributeValue("id");
				int totalOutput = 0;

				while (jobIterator.hasNext()) {

					// 获得当前job下的每一行
					Element jobElement = (Element) jobIterator.next();

					String size = jobElement.attributeValue("size");

					if (jobElement.attributeValue("link").equals("output")) {
						totalOutput = totalOutput + Integer.valueOf(size);
						outputMap.put(ID, totalOutput);
					}
				}

			} else {
				// 获取child自己的ID的属性
				String chileRef = e.attributeValue("ref");

				// 迭代获取父节点
				Iterator childIterator = e.elementIterator();
				List<String> chileParentList = new ArrayList<String>();
				while (childIterator.hasNext()) {
					Element eChild = (Element) childIterator.next();
					String childName = eChild.getName();
					if (childName.equals("parent")) {
						chileParentList.add(eChild.attributeValue("ref"));
					}
				}
				parentMap.put(chileRef, chileParentList);

			}
		}

		// ===========================转换父子关系，整合为(父，子)的样式===================================

		// 输出父子关系

		Set<String> ids = parentMap.keySet();

		List<String> parentChild = new ArrayList<String>();
		for (String id : ids) {
			// 获取某个节点父节点
			List<String> parentList = parentMap.get(id);
			// 转换内容
			for (String parent : parentList) {
				// 文本输出时使用的换行“\r\n”，这是windows平台下的内容
				// 前为父，后为子
				String temp = parent + "," + id;
				parentChild.add(temp);

			}

		}

		// ====================================输出结果（父，子，传输数据）==================================
		FileWriter fw=new FileWriter("./new/Montage_25.txt");
		
		int length = parentChild.size();
		for (int i = 0; i < length; i++) {
			String[] str = parentChild.get(i).split(",");
			String parent = str[0];
			int input = outputMap.get(parent);
			String temp=str[0].replaceAll("ID", "") +"\t"+str[1].replaceAll("ID", "")+"\t"+input+"\r\n";
			
			fw.write(temp);
			System.out.println(temp);

		}
		fw.close();

		// ============================结尾=============================

	}

}
