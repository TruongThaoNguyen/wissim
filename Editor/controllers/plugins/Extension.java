package controllers.plugins;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 
 * @author khaclinh
 *
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface Extension {

	 int ordinal() default 0;
	 
}
