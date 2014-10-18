package com.localhost.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Bean自动创建简单maintain*.xml文件
 */
public class BeanToMaintain {

	/**页面名称*/
	private static final String PAGE_NAME = "maintainTmsDailyOperationPage";
	/**hql别名*/
	private static final String ALIAS = "daily";
	/**实体类*/
	private static final Class<?> clazz = null;//TmsDailyOperation.class;
	/**输出路径*/
	private static final String path = "D:/";
	
	private static String propertiesPath ;
	
	private static Properties props = new Properties();
    static {   
        try {  
        	propertiesPath = path+PAGE_NAME+".properties";
        	if(!new File(propertiesPath).exists()){
        		new File(propertiesPath).createNewFile();
        	}
            props.load(new FileInputStream(new File(propertiesPath)));   
        } catch (FileNotFoundException e) {   
            e.printStackTrace();   
            System.exit(-1);   
        } catch (IOException e) {          
            System.exit(-1);   
        }   
    }   
	
	public static void main(String[] args){
		try {
			new BeanToMaintain().writeDoc(new File(new File(path),PAGE_NAME+".xml"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void writeDoc(File file) throws FileNotFoundException, IOException{
		Element pages = new Element("pages");
		Document doc = new Document(pages);
		
		Element maintainPage = new Element("maintainPage");
		maintainPage.setAttribute("id", PAGE_NAME);
		maintainPage.setAttribute("title", PAGE_NAME);
		maintainPage.setAttribute("autoQuery", "true");
		maintainPage.setAttribute("entityClass", clazz.getName());
		maintainPage.setAttribute("onClose", "refreshParent");
		pages.addContent(maintainPage);
		
		Element workflow = new Element("workflow");
		Element datasource = new Element("datasource");
		Element columns = new Element("columns");
		maintainPage.addContent(workflow);
		maintainPage.addContent(datasource);
		maintainPage.addContent(columns);
		Field[] fields = getFileds(clazz);
		
		StringBuffer hql = new StringBuffer("SELECT ");
		Field field ;
		for (int i=0;i<fields.length;i++) {
			field = fields[i];
			Element column = new Element("column");
			if("serialVersionUID".equals(field.getName())){
				column.setAttribute("id",ALIAS+".id");
				column.setAttribute("visible","false");
			} else {
				column.setAttribute("id",getId(field));
				column.setAttribute("visible","true");
			}
			column.setAttribute("title",getId(field));
			column.setAttribute("sortable","true");
			column.setAttribute("horizonAlign","center");
			column.setAttribute("verticalAlign","middle");
			column.setAttribute("dataType",field.getType().getSimpleName().toLowerCase());
			columns.addContent(column);
			hql.append(getId(field)).append(",").append("\n");
			//writeProperties(column.getAttributeValue("id"), field.getAnnotations());
		}
		hql.append(" FROM ");
		hql.append(clazz.getSimpleName());
		hql.append(" "+ALIAS+" ");
		hql.append(" WHERE 1=1 ");
		CDATA data = new CDATA(hql.toString()); 
		datasource.addContent(data);
		prase(doc, file);
		
	}
	
	/**
	 * 写入XML文件
	 * @param doc
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void prase(Document doc,File file) throws FileNotFoundException, IOException{
		Format format=Format.getPrettyFormat();
		format.setEncoding("UTF-8");
		format.setIndent("      ");
		format.setLineSeparator("\r\n");   
		//不同Jdom版本格式化方式不同
//		XMLOutputter XMLOut = new XMLOutputter("      ",true,"UTF-8");//缩进，换行，编码
		XMLOutputter XMLOut = new XMLOutputter(format);//缩进，换行，编码
		XMLOut.output(doc, new FileOutputStream(file));
	}

	/**
	 * 写入.properties
	 * @param keyname
	 * @param keyvalue
	 */
    public static void writeProperties(String keyname,String keyvalue) {          
        try {   
            // 调用 Hashtable 的方法 put，使用 getProperty 方法提供并行性。   
            // 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。   
            OutputStream fos = new FileOutputStream(propertiesPath);   
            props.setProperty(keyname, keyvalue);   
            // 以适合使用 load 方法加载到 Properties 表中的格式，   
            // 将此 Properties 表中的属性列表（键和元素对）写入输出流   
            props.store(fos, "Update '" + keyname + "' value");   
        } catch (IOException e) {   
            System.err.println("属性文件更新错误");   
        }   
    }   
	
	private String getId(Field field){
		return ALIAS+"."+field.getName();
	}
	
	private Field[] getFileds(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		return fields;
	}
	
}
