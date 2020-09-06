package com.jason.myapp.utils;//package com.meiguan.ipsplayer.utils;
//
//import ch.qos.logback.classic.Logger;
//import ch.qos.logback.classic.LoggerContext;
//import ch.qos.logback.classic.android.LogcatAppender;
//import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
//import org.slf4j.LoggerFactory;
//
///**
// * Created by qiuzi on 15/11/4.
// */
//public class LogbackUtil {
//
//    public static void init() {
//        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//        lc.reset();
//
//        PatternLayoutEncoder logcatEncoder = new PatternLayoutEncoder();
//        logcatEncoder.setContext(lc);
//        logcatEncoder.setPattern("[%thread] %msg%n");
//        logcatEncoder.start();
//
//        LogcatAppender logcatAppender = new LogcatAppender();
//        logcatAppender.setContext(lc);
//        logcatAppender.setEncoder(logcatEncoder);
//        logcatAppender.start();
//
//        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
//        root.addAppender(logcatAppender);
//    }
//}
