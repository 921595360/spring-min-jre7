package com.silence.web.spring_min.util;

import java.util.ArrayList;
import java.util.List;

public class WebLogUtil {
	public static List<String> msgs;
	
	static{
		msgs=new ArrayList<>();
	}
	
	public static void addMsg(String msg){
		msgs.add(msg);
	}
	
	public static List<String> getMsgs(){
		return msgs;
	}

}
