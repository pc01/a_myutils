package com.localhost.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Date;

import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * Bean自动转hbm.xml文件
 * 目前只适用于property和many-to-one，不支持component
 */
public class BeanToHbm {
	
	private static String STRING_LENGTH_1 = "50";
	
	private Class<?> class1;
	private String simpleName;
	private String fullName;
	private String hbmFileName;
	private File hbmFile;
	
	public BeanToHbm(Class<?> clazz,String path){
		this.class1 = clazz;
		this.simpleName = clazz.getSimpleName();
		this.fullName = clazz.getName();
		this.hbmFileName = simpleName.substring(0, 1).toLowerCase()+simpleName.replaceFirst("\\w","")+".hbm.xml";
		this.hbmFile = new File(new File(path),hbmFileName); 
	}
	
	public static void main(String[] args){
		Class<?> clazz = null;//TmsLeg.class;
		String path = "D:/";
//		path = Thread.currentThread().getContextClassLoader().getResource("").getPath();
//		path = clazz.getResource("/").getPath();
		new BeanToHbm(clazz,path).handle();
	}
	
	private void handle(){
		try {
			this.writeDoc(hbmFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 通过JDOM写XML文档
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void writeDoc(File file) throws FileNotFoundException, IOException{
		DocType docType = new DocType("hibernate-mapping",
				"-//Hibernate/Hibernate Mapping DTD 3.0//EN",
				"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
		Element hibernateMapping = new Element("hibernate-mapping");
		Document doc = new Document(hibernateMapping); 
		doc.setDocType(docType);
		Element clazz = new Element("class");
		Element id = new Element("id");
		Element generator = new Element("generator");
		Element param1 = new Element("param");
		Element param2 = new Element("param");
		clazz.setAttribute("name", fullName);
		clazz.setAttribute("table",underscoreName(simpleName));
		id.setAttribute("name","id");
		id.setAttribute("column","ID");
		id.setAttribute("type","long");
		generator.setAttribute("class","native");
		param1.setAttribute("name","sequence");
		param2.setAttribute("name","parameters");
		param1.setText("SEQ_"+underscoreName(simpleName));
		param2.setText("START WITH 1000");
		hibernateMapping.addContent(clazz);
		clazz.addContent(id);
		id.addContent(generator);
		generator.addContent(param1);
		generator.addContent(param2);
		
		Field[] fields = this.getFileds(class1);
		for (Field field : fields) {
			if(field.getModifiers()!=2){
				continue;
			}
			if(isJavaClass(field.getType())){
				Element property = new Element("property");
				property.setAttribute("name", field.getName());
				property.setAttribute("column", underscoreName(field.getName()));
				if(Integer.class.equals(field.getType())||int.class.equals(field.getType())){
					property.setAttribute("type", "integer");
				}
				else if(Double.class.equals(field.getType())||double.class.equals(field.getType())){
					property.setAttribute("type", "double");
				}
				else if(Long.class.equals(field.getType())||long.class.equals(field.getType())){
					property.setAttribute("type", "long");
				}
				else if(Float.class.equals(field.getType())||float.class.equals(field.getType())){
					property.setAttribute("type", "float");
				}
				else if(Boolean.class.equals(field.getType())||boolean.class.equals(field.getType())){
					property.setAttribute("type", "boolean");
				}
				else if(Date.class.equals(field.getType())){
					property.setAttribute("type", "timestamp");
				}
				else if(String.class.equals(field.getType())){
					property.setAttribute("type", "string");
					property.setAttribute("length",STRING_LENGTH_1);
				}
				clazz.addContent(property);
			}
			else{
				Element property = new Element("many-to-one");
				String className = field.getDeclaringClass().getName();
				property.setAttribute("name",field.getName());
				property.setAttribute("class",className);
				Element column = new Element("column");
				column.setAttribute("name",underscoreName(field.getName())+"_ID");
				property.addContent(column);
				clazz.addContent(property);
			}
		}
		
		prase(doc,file);
	}
	
	/**
	 * 驼峰命名格式转_命名格式
	 * @param name
	 * @return
	 */
	public static String underscoreName(String name) {
	    StringBuilder result = new StringBuilder();
	    if (name != null && name.length() > 0) {
	        // 将第一个字符处理成大写
	        result.append(name.substring(0, 1).toUpperCase());
	        // 循环处理其余字符
	        for (int i = 1; i < name.length(); i++) {
	            String s = name.substring(i, i + 1);
	            // 在大写字母前添加下划线
	            if (s.equals(s.toUpperCase()) && !Character.isDigit(s.charAt(0))) {
	                result.append("_");
	            }
	            // 其他字符直接转成大写
	            result.append(s.toUpperCase());
	        }
	    }
	    return result.toString();
	}
	
	/**
	 * 写入文档
	 * @param doc
	 * @param file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void prase(Document doc,File file) throws FileNotFoundException, IOException{
		//不同Jdom版本格式化方式不同
		Format format=Format.getPrettyFormat();
		format.setEncoding("gb2312");
		format.setIndent("      ");
		format.setLineSeparator("\r\n");   
		//XMLOutputter XMLOut = new XMLOutputter("      ",true,"UTF-8");//缩进，换行，编码
		XMLOutputter XMLOut = new XMLOutputter(format);//缩进，换行，编码
		XMLOut.output(doc, new FileOutputStream(file));
	}
	
	/**
	 * 获取类的字段
	 * @param clazz
	 * @return
	 */
	private Field[] getFileds(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();
		return fields;
	}
	
	/**
	 * 判断是否为Java定义类
	 * @param clz
	 * @return
	 */
	public static boolean isJavaClass(Class<?> clz) {
		return clz != null && clz.getClassLoader() == null;
	}

	/**
    	JAVA 反射机制中，Field的getModifiers()方法返回int类型值表示该字段的修饰符。
		其中，该修饰符是java.lang.reflect.Modifier的静态属性。
		对应表如下：
	 	PUBLIC: 1
		PRIVATE: 2
		PROTECTED: 4
		STATIC: 8
		FINAL: 16
		SYNCHRONIZED: 32
		VOLATILE: 64
		TRANSIENT: 128
		NATIVE: 256
		INTERFACE: 512
		ABSTRACT: 1024
		STRICT: 2048
	 */
}
