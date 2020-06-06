package com.liukx.web.mvc;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Tlsy1
 * @since 2019-04-17 16:44
 **/
@Controller
public class ErrorPageController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request){
        //获取statusCode:401,404,500
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
//        if(statusCode == 401){
//            return "static/401";
//        }else if(statusCode == 404){
//            return "static/404";
//        }else if(statusCode == 403){
//            return "static/403";
//        }else{
//            return "static/500";
//        }

        if(statusCode == 404){
            return "static/404";
        }
        return "error";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
