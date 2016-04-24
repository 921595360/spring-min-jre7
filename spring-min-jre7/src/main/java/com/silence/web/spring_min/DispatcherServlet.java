package com.silence.web.spring_min;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.silence.web.spring_min.util.*;
import org.apache.log4j.Logger;

import com.silence.web.spring_min.bean.factory.BeanFactory;


/**
 * 主要用于控制流程
 * DispatcherServlet
 * <p/>
 * silence
 * silence
 * 2016年3月19日 上午8:59:32
 *
 * @version 1.0.0
 */
public class DispatcherServlet extends HttpServlet {
    private static Logger logger = Logger.getLogger(DispatcherServlet.class);

    private static final long serialVersionUID = 1L;

    public DispatcherServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        mapping(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        mapping(request, response);
    }

    /**
     * mapping(映射请求)
     *
     * @param request  request对象
     * @param response response对象
     * @since 1.0.0
     */
    @SuppressWarnings("null")
    public void mapping(HttpServletRequest request, HttpServletResponse response) {

        String contextPath = request.getServletContext().getContextPath();
        String path = request.getRequestURL().substring(request.getRequestURL().indexOf(contextPath) + contextPath.length());
        if (contextPath.length() == 0) {
            path = request.getRequestURL().substring("http://".length());
            path = path.substring(path.indexOf("/"));
        }

        //WebLogUtil.addMsg(DispatcherServlet.class.toString() + "mapping:正在进行请求映射path:" + path);
        Map<String, Method> requestMappings = ContextLoaderListener.getApplicationContext().getRequestMappings();
        //WebLogUtil.addMsg(DispatcherServlet.class.toString() + "mapping:requestMappings:" + requestMappings);

        Method method = requestMappings.get(path);

        //WebLogUtil.addMsg(DispatcherServlet.class.toString() + ":mapping==>method:" + method);

        setEncoding(request, response);
        try {
            if (method != null) {
                Class<?>[] parameterTypes = method.getParameterTypes();


                Map<String, Object> paramsMap = new HashMap<String, Object>();
                paramsMap.put("method", method);

                String paramString = "method.invoke(targetObj";
                Object targetObj = BeanFactory.getInstance().getBean(method.getDeclaringClass());
                paramsMap.put("targetObj", targetObj);
                //获取参数名称
                String[] parameterNames = ParameterNameUtils.getMethodParameterNames(method);


                //处理mopoyun中奇怪的多出的一个this参数，采用直接覆盖的方式
                int unUseIndex=-1;
                for(int i=0;i<parameterNames.length;i++){
                    if(parameterNames[i].equals("this")){
                        unUseIndex=i;
                        break;
                    }
                }
               if(unUseIndex>-1){
                   for(int i=unUseIndex+1;i<parameterNames.length;i++){
                       parameterNames[i-1]=parameterNames[i];
                   }
               }


                for (int i = 0; i < parameterTypes.length; i++) {

                    Object value = null;

                    switch (parameterTypes[i].getName()) {

                        case "javax.servlet.http.HttpServletRequest":
                            value = request;
                            break;

                        case "javax.servlet.http.HttpServletResponse":
                            value = response;
                            break;

                        case "java.lang.String":
                            value = request.getParameter(parameterNames[i]);
                            break;

                        case "int":
                            value = Integer.valueOf(request.getParameter(parameterNames[i]));
                            break;

                        case "java.lang.Integer":
                            value = Integer.valueOf(request.getParameter(parameterNames[i]));
                            break;

                        case "float":
                            value = Float.valueOf(request.getParameter(parameterNames[i]));
                            break;

                        case "java.lang.Float":
                            value = Float.valueOf(request.getParameter(parameterNames[i]));
                            break;

                        case "double":
                            value = Double.valueOf(request.getParameter(parameterNames[i]));
                            break;

                        case "java.lang.Double":
                            value = Double.valueOf(request.getParameter(parameterNames[i]));
                            break;

                        default:
                            String valueString = request.getParameter(parameterNames[i]);
                            value = parameterTypes[i].newInstance();
                            value = JSONUtil.toJavaBean(value, JSONUtil.toMap(valueString));
                            break;
                    }

                    paramString += "," + parameterNames[i];

                    paramsMap.put(parameterNames[i], value);

                }
                paramString += ")";

                //WebLogUtil.addMsg(DispatcherServlet.class.toString() + "mapping:准备执行：" + paramString);

                Object result = MeThodUtil.invokeMethod(paramString, paramsMap);

                afterInvoke(result, request, response);
            } else {
                //如果无法映射到方法，则直接输出文件
                mappingFile(path, response);

            }
        } catch (Exception e) {
            WebLogUtil.addMsg(DispatcherServlet.class.toString() + "mapping:映射失败：" + ExceptionUtil.getErrorInfoFromException(e));
            e.printStackTrace();
        }
    }

    /**
     * afterInvoke(控制器方法执行完毕后处理响应)
     *
     * @param result
     * @param request
     * @param response
     * @throws IOException
     * @since 1.0.0
     */
    public void afterInvoke(Object result, HttpServletRequest request, HttpServletResponse response) throws IOException {
        boolean isJson;

        switch (result.getClass().getName()) {

            case "java.util.ArrayList":

                //设置返回Arrayjson数据

                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(JSONUtil.toArrayJSON(((ArrayList)result)).toString());
                //WebLogUtil.addMsg(DispatcherServlet.class.toString() + "mapping:执行结束，处理结果：" + result);
                return;
            /*跳转页面*/
            case "com.silence.web.spring_min.ModelAndView":
                ModelAndView mv = (ModelAndView) result;
                try {
                    request.getRequestDispatcher(mv.getViewName()).forward(request, response);
                } catch (ServletException e) {
                    e.printStackTrace();
                }
                return;

            default:
                isJson = true;
                break;
        }
        if (isJson) {
            //设置返回json数据
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(JSONUtil.toJSON(result).toString());
        } else {
            response.getWriter().write(result.toString());
        }
        //WebLogUtil.addMsg(DispatcherServlet.class.toString() + "mapping:执行结束，处理结果：" + result);
    }

    /**
     * setEncoding (设置编码格式,目前未处理url中文乱码问题)
     *
     * @param request  request对象
     * @param response response对象
     */
    public void setEncoding(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.setCharacterEncoding("utf-8");
            response.setContentType("text/html;charset=utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 映射文件
     *
     * @param path
     * @param response
     */
    public void mappingFile(String path, HttpServletResponse response) {
        String filePath = this.getServletContext().getRealPath(path);
        InputStream is = null;
        OutputStream os;
        response.setHeader("Content-Type", "");


        try {
            os = response.getOutputStream();
            is = new FileInputStream(filePath);
            int len;
            byte[] b = new byte[1024];
            while ((len = is.read(b)) > 0) {
                os.write(b, 0, len);
            }
        } catch (Exception e) {

        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
