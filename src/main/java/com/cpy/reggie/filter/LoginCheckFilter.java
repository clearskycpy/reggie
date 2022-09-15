package com.cpy.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.cpy.reggie.common.BaseContext;
import com.cpy.reggie.common.R;
import com.cpy.reggie.entity.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//登录全局拦截
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    // 用于比较路径的工具类路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest  request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 获取本次请求的uri
        String requestURI = request.getRequestURI();
//        log.info("拦截的到的请求 {}",requestURI);
//        直接放行资源
        String[] urls = {"/employee/login",
                "/employee/logout",
                "/backend/**","/front/**",
                "/user/sendMsg","/common/**",
                "/user/login"};

//        判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        if (check){
            filterChain.doFilter(request,response);
            return;
        }
//        放行
        if (request.getSession().getAttribute("employee") != null) {
            Long empId = (Long) request.getSession().getAttribute("employee");
//            将id存进当前线程 设置值
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("user") != null) {
            Long userId = (Long) request.getSession().getAttribute("user");
//            将id存进当前线程 设置值
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request,response);
            return;
        }

//        说明此时是没有登录的，需要以输出流的形式响应页面
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
//        未登录转跳登录

    }

    public boolean check(String[] uris, String requestURI){
        for (String s :
                uris) {
            boolean match = PATH_MATCHER.match(s,requestURI);
            if (match){
                return true;
            }
        }
            return false;
    }
}
