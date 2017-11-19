package framework.core.annotations;

import framework.parsers.Scope;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by denysburlakov on 05.11.2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Repository {
    String name() default "";
    Scope scope() default Scope.SINGLETON;

}
