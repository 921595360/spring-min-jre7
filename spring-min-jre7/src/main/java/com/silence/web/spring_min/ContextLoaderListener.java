package com.silence.web.spring_min;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;
import com.silence.web.spring_min.anotation.Autowired;
import com.silence.web.spring_min.anotation.RequestMapping;
import com.silence.web.spring_min.bean.factory.BeanFactory;
import com.silence.web.spring_min.util.ClassUtil;
import com.silence.web.spring_min.util.FiledUtil;
import com.silence.web.spring_min.util.WebLogUtil;

/**
 * 容器加载监听类
 *   
 * ContextLoaderListener  
 *   
 * silence  
 * silence  
 * 2016年3月19日 上午9:15:36  
 *   
 * @version 1.0.0  
 *
 */
public class ContextLoaderListener implements ServletContextListener  {
	
	private static Logger logger = Logger.getLogger(ContextLoaderListener.class);
	
	private static ApplicationContext applicationContext;
	
	
	public void contextDestroyed(ServletContextEvent arg0) {
		logger.info("contextDestroyed");
	}

	public void contextInitialized(ServletContextEvent arg0) {
		logger.info("contextInitialized");
		applicationContext=new ApplicationContext();
		String basePackage=arg0.getServletContext().getInitParameter("basePackage");
		applicationContext.setBasePackage(basePackage);
		
		initBean();
		inject();
	}
	
	public void initBean(){
		
		String [] excludePackages={"com.silence.web.spring_min.util",
								   "com.silence.web.spring_min.anotation"	
								  };
		String [] excludeClasses={"com.silence.web.spring_min.ContextLoaderListener",
								  "com.silence.web.spring_min.DispatcherServlet"
								 };
		
		List<Class<?>> classes = ClassUtil.getClasses(ContextLoaderListener.class.getPackage().getName(),excludePackages,excludeClasses);
		
		classes = ClassUtil.getClasses(applicationContext.getBasePackage(),excludePackages,excludeClasses);
		
		for (int i = 0; i < classes.size(); i++) {
			Annotation[] declaredAnnotations = classes.get(i).getDeclaredAnnotations();
			
			for (int j = 0; j < declaredAnnotations.length; j++) {
				
				switch (declaredAnnotations[j].annotationType().getName()) {
				
				case "com.silence.web.spring_min.anotation.Controller":
					initController(classes.get(i));
					break;
				
				case "com.silence.web.spring_min.anotation.Service":
					try {
						BeanFactory.getInstance().setBean(classes.get(i).newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					break;	
				case "com.silence.web.spring_min.anotation.Component":
					try {
						BeanFactory.getInstance().setBean(classes.get(i).newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					break;	
				
				case "com.silence.web.spring_min.anotation.Repository":
					try {
						BeanFactory.getInstance().setBean(classes.get(i).newInstance());
					} catch (InstantiationException | IllegalAccessException e) {
						e.printStackTrace();
					}
					break;

				default:
					break;
				}
			}
		}
		WebLogUtil.addMsg(ContextLoaderListener.class.toString()+":==>initBean:"+BeanFactory.getInstance().getBeans());
		
	}
	
	/**
	 * 
	 * inject(依赖注入)  
	 *void  
	 * @since  1.0.0
	 */
	public void inject(){
		List<Object> beans = BeanFactory.getInstance().getBeans();
		for(Object bean:beans){
			Field[] declaredFields = bean.getClass().getDeclaredFields();
			
			Annotation resource=null;
			Annotation autowired=null;
			for (Field filed:declaredFields) {
				
				Annotation[] declaredAnnotations = filed.getDeclaredAnnotations();
				
				for (int i = 0; i < declaredAnnotations.length; i++) {
					
					String annotationStr=declaredAnnotations[i].toString();
					
					if(annotationStr.substring(1,annotationStr.indexOf("(")).equals("javax.annotation.Resource")){
						resource=declaredAnnotations[i];
						break;
					}else if(annotationStr.substring(1,annotationStr.indexOf("(")).equals("com.silence.web.spring_min.anotation.Autowired")){
						autowired=declaredAnnotations[i];
						break;
					}
				}
				
				if((null==resource)
					&& 	(null==autowired)
						){
					continue;
				} 
				
				try {
					WebLogUtil.addMsg(ContextLoaderListener.class.toString()+":==>inject:"+filed.getName()+":value:"+applicationContext.getBean(filed.getType()));
					filed.setAccessible(true);
					filed.set(bean, applicationContext.getBean(filed.getType()));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public void initController(Class<?> controller){
		try {
			
			BeanFactory.getInstance().setBean(controller.newInstance());
			
			Annotation[] declaredAnnotations;
			declaredAnnotations = getClass().forName(controller.toString().substring(6)).getDeclaredAnnotations();
			
			RequestMapping requestMapping=null;
			
			for (int i = 0; i < declaredAnnotations.length; i++) {
				String annotationStr=declaredAnnotations[i].toString();
				if(annotationStr.substring(1,annotationStr.indexOf("(")).equals("com.silence.web.spring_min.anotation.RequestMapping")){
					requestMapping=(RequestMapping) declaredAnnotations[i];
					break;
				}
			}
			
			String pathPrefix=null!=requestMapping?requestMapping.value():"";
			
			Method[] declaredMethods = controller.getDeclaredMethods();
			
			RequestMapping methodRequestMapping=null;
			
			for (int i = 0; i < declaredMethods.length; i++) {
				
			
				declaredAnnotations=declaredMethods[i].getDeclaredAnnotations();
				
				for (int j = 0; j < declaredAnnotations.length; j++) {
					
					String annotationStr=declaredAnnotations[j].toString();
					
					if(annotationStr.substring(1,annotationStr.indexOf("(")).equals("com.silence.web.spring_min.anotation.RequestMapping")){
						methodRequestMapping=(RequestMapping) declaredAnnotations[j];
						break;
					}
				}
				
				if(null==methodRequestMapping) continue;
				
				String path=pathPrefix+"/"+methodRequestMapping.value();
				
				applicationContext.addRequestMappings(path,declaredMethods[i]);
				
				WebLogUtil.addMsg(ContextLoaderListener.class.toString()+":==>initController:"+controller+":==>requestMapping:"+path);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ApplicationContext getApplicationContext(){
		return applicationContext;
	}
	

	
}
