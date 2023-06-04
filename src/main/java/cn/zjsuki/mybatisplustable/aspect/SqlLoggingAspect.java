package cn.zjsuki.mybatisplustable.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * @program: mybatis-plus-table
 * @description: 打印sql语句
 * @author: LiYu
 * @create: 2023-06-04 13:25
 **/

@Component
@Aspect
public class SqlLoggingAspect {
    @Before("execution(* org.apache.ibatis.session.SqlSession.select*(String, ..)) || " +
            "execution(* org.apache.ibatis.session.SqlSession.insert*(String, ..)) || " +
            "execution(* org.apache.ibatis.session.SqlSession.update*(String, ..)) || " +
            "execution(* org.apache.ibatis.session.SqlSession.delete*(String, ..))")
    public void logSqlStatement(JoinPoint joinPoint) {
        String sqlId = joinPoint.getArgs()[0].toString();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Executing SQL statement for method " + methodName + ": " + sqlId);
    }

    @AfterReturning(pointcut = "execution(* org.apache.ibatis.session.defaults.DefaultSqlSession.select*(String, ..)) || " +
            "execution(* org.apache.ibatis.session.defaults.DefaultSqlSession.insert*(String, ..)) || " +
            "execution(* org.apache.ibatis.session.defaults.DefaultSqlSession.update*(String, ..)) || " +
            "execution(* org.apache.ibatis.session.defaults.DefaultSqlSession.delete*(String, ..))",
            returning = "result")
    public void logSqlStatementResult(JoinPoint joinPoint, Object result) {
        String sqlId = joinPoint.getArgs()[0].toString();
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Executed SQL statement for method " + methodName + ": " + sqlId);
    }

    @Before("execution(* com.baomidou.mybatisplus.extension.service.IService.select*(..)) || " +
            "execution(* com.baomidou.mybatisplus.extension.service.IService.insert*(..)) || " +
            "execution(* com.baomidou.mybatisplus.extension.service.IService.update*(..)) || " +
            "execution(* com.baomidou.mybatisplus.extension.service.IService.delete*(..))")
    public void logSqlStatementPlus(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Executing SQL statement for method " + methodName);
    }

    @AfterReturning(pointcut = "execution(* com.baomidou.mybatisplus.extension.service.IService.select*(..)) || " +
            "execution(* com.baomidou.mybatisplus.extension.service.IService.insert*(..)) || " +
            "execution(* com.baomidou.mybatisplus.extension.service.IService.update*(..)) || " +
            "execution(* com.baomidou.mybatisplus.extension.service.IService.delete*(..))",
            returning = "result")
    public void logSqlStatementResultPlus(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        System.out.println("Executed SQL statement for method " + methodName);
    }
}

