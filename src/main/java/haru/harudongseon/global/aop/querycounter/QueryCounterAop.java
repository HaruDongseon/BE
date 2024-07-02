package haru.harudongseon.global.aop.querycounter;

import java.lang.reflect.Proxy;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Profile({"test", "local"})
public class QueryCounterAop {

    private final QueryCounter queryCounter;

    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Object getConnection(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object connection = proceedingJoinPoint.proceed();
        return Proxy.newProxyInstance(
                connection.getClass().getClassLoader(),
                connection.getClass().getInterfaces(),
                new ConnectionProxyHandler(connection, queryCounter)
        );
    }
}
