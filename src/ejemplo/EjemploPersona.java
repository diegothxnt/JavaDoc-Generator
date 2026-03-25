package ejemplo;

import anotaciones.*;

@DocumentacionClase(
    nombre = "Persona",
    autor = "Juan Pérez",
    descripcion = "Clase que representa a una persona con sus datos básicos",
    version = "1.0"
)
public class EjemploPersona {
    
    @DocumentacionAtributo(descripcion = "Nombre completo de la persona")
    private String nombre;
    
    @DocumentacionAtributo(descripcion = "Edad de la persona en años")
    private int edad;
    
    @DocumentacionAtributo(descripcion = "Correo electrónico de contacto")
    private String email;
    
    @DocumentacionMetodo(descripcion = "Constructor que inicializa todos los atributos")
    public EjemploPersona(String nombre, int edad, String email) {
        this.nombre = nombre;
        this.edad = edad;
        this.email = email;
    }
    
    @DocumentacionMetodo(
        descripcion = "Obtiene el nombre de la persona",
        retorno = "String",
        parametros = {}
    )
    public String getNombre() {
        return nombre;
    }
    
    @DocumentacionMetodo(
        descripcion = "Establece el nombre de la persona",
        parametros = {"nombre - Nuevo nombre de la persona"}
    )
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    @DocumentacionMetodo(
        descripcion = "Obtiene la edad de la persona",
        retorno = "int"
    )
    public int getEdad() {
        return edad;
    }
    
    @DocumentacionMetodo(
        descripcion = "Establece la edad de la persona",
        parametros = {"edad - Nueva edad de la persona"}
    )
    public void setEdad(int edad) {
        if (edad >= 0 && edad <= 150) {
            this.edad = edad;
        }
    }
    
    @DocumentacionMetodo(
        descripcion = "Obtiene el correo electrónico",
        retorno = "String"
    )
    public String getEmail() {
        return email;
    }
    
    @DocumentacionMetodo(
        descripcion = "Establece el correo electrónico",
        parametros = {"email - Nuevo correo electrónico"}
    )
    public void setEmail(String email) {
        if (email != null && email.contains("@")) {
            this.email = email;
        }
    }
    
    @DocumentacionMetodo(
        descripcion = "Muestra la información completa de la persona",
        retorno = "void"
    )
    public void mostrarInformacion() {
        System.out.println("Nombre: " + nombre);
        System.out.println("Edad: " + edad);
        System.out.println("Email: " + email);
    }
    
    @Override
    public String toString() {
        return "Persona{nombre='" + nombre + "', edad=" + edad + ", email='" + email + "'}";
    }
}