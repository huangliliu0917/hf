package com.hf.user.api;

import com.hf.base.dispatcher.DispatchResult;
import com.hf.base.dispatcher.DispatcherFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/common")
public class CommonApi {
    @Autowired
    private DispatcherFactory dispatcherFactory;

    @RequestMapping(value="/{page}")
    public ModelAndView dispatch(HttpServletRequest request, @PathVariable("page")String page) {
        ModelAndView modelAndView = new ModelAndView();
        DispatchResult dispatchResult = dispatcherFactory.getDispatcher(request,page).dispatch(request,page);
        modelAndView.setViewName(String.format("/%s",dispatchResult.getPage()));

        if(!org.apache.commons.collections.MapUtils.isEmpty(dispatchResult.getData())) {
            for(String key: dispatchResult.getData().keySet()) {
                modelAndView.addObject(key,dispatchResult.getData().get(key));
            }
        }

        return modelAndView;
    }
}
