package com.dhlee.undertow.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dhlee.util.ThreadDumpUtil;

import io.undertow.servlet.handlers.DefaultServlet;

public class AdapterServlet extends DefaultServlet {
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ThreadGroup it = Thread.currentThread().getThreadGroup();
    	int threadCount = it.activeCount() ;
    	response.getWriter().print("undertow servlet response! - " + request.getRequestURI() + ", threadCount="+threadCount);	
    	response.getWriter().print(ThreadDumpUtil.dump());
	}

}
