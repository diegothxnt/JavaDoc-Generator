package anotaciones;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface DocumentacionMetodo {
    String descripcion();
    String[] parametros() default {};
    String retorno() default "void";
}