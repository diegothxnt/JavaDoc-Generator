package anotaciones;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DocumentacionClase {
    String nombre();
    String autor();
    String descripcion();
    String version();
}