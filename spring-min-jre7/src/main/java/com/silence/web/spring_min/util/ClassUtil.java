package com.silence.web.spring_min.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 
 *   
 * ClassUtil  
 *   
 * silence  
 * silence  
 * 2016年3月19日 上午10:14:42  
 *   
 * @version 1.0.0  
 *
 */
public class ClassUtil {

	/**
	 * 获取指定对象的属性（包含父类的属性）
	 * @param bean
	 * @return
	 */
	public static Field [] getAllFields(Object bean){
		Field[] fields= bean.getClass().getDeclaredFields();
		Field [] superFileds= bean.getClass().getSuperclass().getDeclaredFields();
		Field [] tmp=new Field[fields.length+superFileds.length];
		System.arraycopy(fields,0,tmp,0,fields.length);
		System.arraycopy(superFileds,0,tmp,fields.length,superFileds.length);
		fields=tmp;
		return fields;
	}

	/**
	 * 获取指定对象的所有方法（包含父类方法）
	 * @param bean
	 * @return
	 */
	public static Method [] getAllMethods(Object bean){
		Method [] methods=bean.getClass().getDeclaredMethods();
		Method [] superMethods=bean.getClass().getSuperclass().getMethods();
		Method [] tmp1=new Method[methods.length+superMethods.length];
		System.arraycopy(methods,0,tmp1,0,methods.length);
		System.arraycopy(superMethods,0,tmp1,methods.length,superMethods.length);
		methods=tmp1;
		return methods;
	}
	

	public static List<Class<?>> getClasses(String packageName,String [] excludePackages,String [] excludeClasses){
		 //第一个class类的集合  
        List<Class<?>> classes = new ArrayList<Class<?>>();  
        //是否循环迭代  
        boolean recursive = true;  
        //获取包的名字 并进行替换  
        String packageDirName = packageName.replace('.', '/');  
        //定义一个枚举的集合 并进行循环来处理这个目录下的things  
        Enumeration<URL> dirs;  
        try {  
            dirs = Thread.currentThread().getContextClassLoader().getResources(packageDirName);  
            //循环迭代下去  
            while (dirs.hasMoreElements()){  
                //获取下一个元素  
                URL url = dirs.nextElement();  
                //得到协议的名称  
                String protocol = url.getProtocol();  
                //如果是以文件的形式保存在服务器上  
                if ("file".equals(protocol)) {  
                    //获取包的物理路径  
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");  
                    //以文件的方式扫描整个包下的文件 并添加到集合中  
                    findAndAddClassesInPackageByFile(packageName, filePath, recursive, classes,excludePackages,excludeClasses);  
                } else if ("jar".equals(protocol)){  
                    //如果是jar包文件   
                    //定义一个JarFile  
                    JarFile jar;  
                    try {  
                        //获取jar  
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();  
                        //从此jar包 得到一个枚举类  
                        Enumeration<JarEntry> entries = jar.entries();  
                        //同样的进行循环迭代  
                        while (entries.hasMoreElements()) {  
                            //获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件  
                            JarEntry entry = entries.nextElement();  
                            String name = entry.getName();  
                            //如果是以/开头的  
                            if (name.charAt(0) == '/') {  
                                //获取后面的字符串  
                                name = name.substring(1);  
                            }  
                            //如果前半部分和定义的包名相同  
                            if (name.startsWith(packageDirName)) {  
                                int idx = name.lastIndexOf('/');  
                                //如果以"/"结尾 是一个包  
                                if (idx != -1) {  
                                    //获取包名 把"/"替换成"."  
                                    packageName = name.substring(0, idx).replace('/', '.');  
                                }  
                                //如果可以迭代下去 并且是一个包  
                                if ((idx != -1) || recursive){  
                                    //如果是一个.class文件 而且不是目录  
                                    if (name.endsWith(".class") && !entry.isDirectory()) {  
                                        //去掉后面的".class" 获取真正的类名  
                                        String className = name.substring(packageName.length() + 1, name.length() - 6);  
                                        try {  
                                        	boolean isOk=true;
                                        	
                                        	if(null!=excludePackages){
                                        		for (int i = 0; i < excludePackages.length; i++) {
    												if(packageName.equals(excludePackages[i])){
    													isOk=false;
    													break;
    												}
    											}
                                        	}
                                        	
                                        	
                                        	if(!isOk) continue;
                                        	
                                        	if(null!=excludeClasses){
                                        		for (int i = 0; i < excludeClasses.length; i++) {
    												if(excludeClasses[i].equals(packageName + '.' + className)){
    													isOk=false;
    													break;
    												}
    											}
                                        	}
                                        	
                                        	
                                        	if(!isOk) continue;
                                            //添加到classes  
                                            classes.add(Class.forName(packageName + '.' + className));  
                                        } catch (ClassNotFoundException e) {  
                                            e.printStackTrace();  
                                        }  
                                      }  
                                }  
                            }  
                        }  
                    } catch (IOException e) {  
                        e.printStackTrace();  
                    }   
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
         
        return classes;  
	}
	
	
	 public static void findAndAddClassesInPackageByFile(String packageName, String packagePath, final boolean recursive, List<Class<?>> classes,String [] excludePackages,String [] excludeClasses){  
	        //获取此包的目录 建立一个File  
	        File dir = new File(packagePath);  
	        //如果不存在或者 也不是目录就直接返回  
	        if (!dir.exists() || !dir.isDirectory()) {  
	            return;  
	        }  
	        //如果存在 就获取包下的所有文件 包括目录  
	        File[] dirfiles = dir.listFiles(new FileFilter() {  
	        //自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)  
	              public boolean accept(File file) {  
	                return (recursive && file.isDirectory()) || (file.getName().endsWith(".class"));  
	              }  
	            });  
	        //循环所有文件  
	        for (File file : dirfiles) {  
	            //如果是目录 则继续扫描  
	            if (file.isDirectory()) {  
	                findAndAddClassesInPackageByFile(packageName + "." + file.getName(),  
	                                      file.getAbsolutePath(),  
	                                      recursive,  
	                                      classes,excludePackages,excludeClasses);  
	            }  
	            else {  
	                //如果是java类文件 去掉后面的.class 只留下类名  
	                String className = file.getName().substring(0, file.getName().length() - 6);  
	                try {
	                	boolean isOk=true;
	                	
	                	if(null!=excludePackages){
                    		for (int i = 0; i < excludePackages.length; i++) {
								if(packageName.equals(excludePackages[i])){
									isOk=false;
									break;
								}
							}
                    	}
                    	
                    	
                    	if(!isOk) continue;
                    	
                    	if(null!=excludeClasses){
                    		for (int i = 0; i < excludeClasses.length; i++) {
								if(excludeClasses[i].equals(packageName + '.' + className)){
									isOk=false;
									break;
								}
							}
                    	}
                    	
                    	if(!isOk) continue;
	                	
	                    //添加到集合中去  
	                    classes.add(Class.forName(packageName + '.' + className));  
	                } catch (ClassNotFoundException e) {  
	                    e.printStackTrace();  
	                }  
	            }  
	        }  
	    }  
}
