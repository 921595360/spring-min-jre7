package com.silence.web.spring_min;

/**
 * Created by silence on 2016/4/8.
 */
public class ModelAndView {

    public ModelAndView(){}

    public ModelAndView(String viewName){
        this.viewName=viewName;
    }

    private String viewName;

    public String getViewName(){return this.viewName;}

    public void setViewName(String viewName){this.viewName=viewName;}
}
