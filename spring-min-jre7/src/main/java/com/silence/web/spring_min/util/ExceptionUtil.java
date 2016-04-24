package com.silence.web.spring_min.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

	/**
	 * 
	 * getErrorInfoFromException(通过异常获取异常信息)  
	 * (这里描述这个方法适用条件 – 可选)  
	 * @param e
	 * @return   
	 *String  
	 * @exception   
	 * @since  1.0.0
	 */
	public static String getErrorInfoFromException(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "\r\n" + sw.toString() + "\r\n";
        } catch (Exception e2) {
            return "bad getErrorInfoFromException";
        }
    }
}
