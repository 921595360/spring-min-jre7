package com.silence.web.spring_min.util;

import java.lang.annotation.Annotation;

public class FiledUtil {

	/*public static <T> Annotation getDeclaredAnnotation(Object filed,T target){
		
		System.out.println(filed.getClass());
		
		Annotation[] declaredAnnotations;
		try {
			declaredAnnotations = Class.forName(filed.toString().substring(6)).getDeclaredAnnotations();
			for (int i = 0; i < declaredAnnotations.length; i++) {
				if(declaredAnnotations[i].getClass()==target){
					return declaredAnnotations[i];
				}
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}*/
}
