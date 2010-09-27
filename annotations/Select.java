package annotations;
import java.lang.annotation.*;

//TODO: can we access the parameter name at compile-time to use the parameter 
// name as the default path 
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Select {
	public String value();
}
