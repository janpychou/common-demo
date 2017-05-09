/*
 * Copyright (C) 2012 GZ-ISCAS Inc., All Rights Reserved.
 */
package io.sugo.user.group.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @Description: 日志的帮助类，目的是的将不同级别的LOG输出到不同的LOG文件中  
 * @Author janpychou@qq.com
 * @CreateDate:   [May 26, 2015 8:03:45 PM]   
 *
 */
public class LogUtil {

	private static final String DEBUG = "debug";
	private static final String INFO = "info";
	private static final String WARN = "warn";
	private static final String ERROR = "error";
	private static final String FATAL = "fatal";

	private static Log debugLog = LogFactory.getLog(DEBUG);
	private static Log infoLog = LogFactory.getLog(INFO);
	private static Log warnLog = LogFactory.getLog(WARN);
	private static Log errorLog = LogFactory.getLog(ERROR);
	private static Log fatalLog = LogFactory.getLog(FATAL);
	
	public static boolean isDebugEnabled(){
		return debugLog.isDebugEnabled();
	}
	
	public static boolean isInfoEnabled(){
		return infoLog.isInfoEnabled();
	}
	
	public static boolean isErrorEnabled(){
		return errorLog.isErrorEnabled();
	}
	
	public static boolean isWarnEnabled(){
		return warnLog.isWarnEnabled();
	}
	
	public static boolean isFatalEnabled(){
		return fatalLog.isFatalEnabled();
	}
	
	public static void debug(Object debug) {
		debug(debug, null);
	}
	
	public static void debug(Throwable t) {
		debug(t.getMessage(), t);
	}

	public static void debug(Object debug, Throwable t) {
		debugLog.debug(debug, t);
	}
	
	public static void formatInfo(String string, Object... replaceStrings){
	    info(StringUtil.formatByNumber(string, replaceStrings), null);
	}
	
	public static void formatWarn(String string, Object... replaceStrings){
        warn(StringUtil.formatByNumber(string, replaceStrings), null);
    }
	
	public static void formatError(String string, Object... replaceStrings){
        error(StringUtil.formatByNumber(string, replaceStrings), null);
    }

	public static void info(Object info) {
		info(info, null);
	}
	
	public static void info(Throwable t) {
		info(t.getMessage(), t);
	}

	public static void info(Object info, Throwable t) {
		infoLog.info(info, t);
	}

	public static void warn(Throwable t) {
		warn(t.getMessage(), t);
	}

	public static void warn(Object warn) {
		warn(warn, null);
	}

	public static void warn(Object warn, Throwable t) {
		warnLog.warn(warn, t);
	}

	public static void error(Throwable t) {
		error(t.getMessage(), t);
	}

	public static void error(Object error) {
		error(error, null);
	}

	public static void error(Object error, Throwable t) {
		errorLog.error(error, t);
	}

	public static void fatal(Throwable t) {
		fatal(t.getMessage(), t);
	}

	public static void fatal(Object fatal) {
		fatal(fatal, null);
	}

	public static void fatal(Object fatal, Throwable t) {
		fatalLog.fatal(fatal, t);
	}
	
}
