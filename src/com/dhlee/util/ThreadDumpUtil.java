package com.dhlee.util;


import java.util.Arrays;
import java.util.Hashtable;


public class ThreadDumpUtil {
    public String threadDump() throws Exception {
//        //name = "ExecuteThread: '"+name+"' for queue: 'weblogic.kernel.Default'";
//        ThreadGroup it = Thread.currentThread().getThreadGroup();
//
//        out.println("[group] " + it.getName() + "<p><p><p>");
//
//        Thread[] threads = new Thread[it.activeCount()] ;
//        out.println("active count : "+it.activeCount() + "<p>");
//        it.enumerate(threads);
//        out.println("<table border='0' align='center'>");
//        for(int i=0;i<threads.length;i++) {
//            if(threads[i]!=null) {
//                out.println("<tr>");
//                out.println(getStackTrace(threads[i]));
//                out.println("</tr>");
//            }
//        }
//        out.println("</table>");
    	return null;
    }
    
    static private String getStackTrace(Thread thread) {
        StringBuffer sb = new StringBuffer();
        sb.append("бс  " + thread.getName() + " ");
        sb.append("prio=" + thread.getPriority() + " ");
        sb.append(thread.isDaemon() ? "daemon " : " ");
        
        StackTraceElement stack[] = thread.getStackTrace();

        if (stack==null || stack.length<1)
	        return sb.toString();

        if (stack[0].getMethodName().equals("wait"))
            sb.append("wait \n");
        else if (stack[0].getMethodName().equals("sleep"))
            sb.append("sleep \n");
        else
            sb.append(stack[0].getMethodName() + " \n");

        for (int i=0; i<stack.length; i++) {
            String filename = stack[i].getFileName();
            if (filename == null) {
                // The source filename is not available
            }
            String className = stack[i].getClassName();
            String methodName = stack[i].getMethodName();
            boolean isNativeMethod = stack[i].isNativeMethod();
            int line = stack[i].getLineNumber();

            if (!className.startsWith("java") 
            	&& !className.startsWith("org.") 
            	&& !className.startsWith("edu.") 
            	&& !className.startsWith("sun.") 
            	&& !className.startsWith("com.sun."))
                sb.append("     > at ");
            else
                sb.append("       at ");
            sb.append(className + "." + methodName);
            sb.append("(" + filename + ":" + (isNativeMethod ? "native" : String.valueOf(line)) + ")");
            if (!className.startsWith("java") && !className.startsWith("org.") && !className.startsWith("sun.") && !className.startsWith("com.sun."))
                sb.append("-");
            sb.append("\n");
        }
        
//        if (stack[0].getMethodName().equals("wait"))
//            return "<w>" + sb.toString() + "</w>";
//        else if (stack[0].getMethodName().equals("sleep"))
//            return "<s>" + sb.toString() + "</s>";
//        else
            return sb.toString();
    }
    
    static public String dump() {
    	Hashtable hash = new Hashtable();
    	
    	StringBuffer sb = new StringBuffer();
    	ThreadGroup it = Thread.currentThread().getThreadGroup();
    	//sb.append("[group] " + it.getName() + "\n");
    	Thread[] threads = new Thread[it.activeCount()] ;
    	sb.append("active thread count : " + it.activeCount() + "\n");
    	it.enumerate(threads);
    	
    	//----------------------------------------------------------------------
    	///*
    	String[] threadNames = new String[threads.length];
    	for (int i=0; i<threadNames.length; i++) {
    		threadNames[i] = threads[i].getName() + " - " + threads[i].getState();
    		hash.put(threadNames[i], threads[i]);
    	}
    	Arrays.sort(threadNames);
    	//for (int i=0; i<threadNames.length; i++) {
    	//	sb.append("[" + i + "] " + threadNames[i] + "\n");
    	//}
    	//*/
    	
    	//----------------------------------------------------------------------
        for(int i=0;i<threadNames.length;i++) {
        	//if(threads[i] != null) {
        		sb.append(getStackTrace((Thread) hash.get(threadNames[i])));
        		//sb.append(threads[i].getName() + "\n");
        	//}
        }

        return sb.toString();
    }
}

