package procesador;

import anotaciones.*;
import java.lang.reflect.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GeneradorDocumentacion {
    
    private StringBuilder markdown;
    private Class<?> clase;
    
    public GeneradorDocumentacion(Class<?> clase) {
        this.clase = clase;
        this.markdown = new StringBuilder();
    }
    
    public void generarDocumentacion() throws IOException {
        // Crear directorio de documentación si no existe
        File docDir = new File("documentacion");
        if (!docDir.exists()) {
            docDir.mkdir();
        }
        
        // Generar contenido
        generarEncabezado();
        generarInformacionClase();
        generarPropiedades();
        generarMetodos();
        
        // Guardar archivo
        String nombreArchivo = "documentacion/" + clase.getSimpleName() + ".md";
        try (FileWriter writer = new FileWriter(nombreArchivo)) {
            writer.write(markdown.toString());
        }
        
        System.out.println("Documentación generada: " + nombreArchivo);
    }
    
    private void generarEncabezado() {
        markdown.append("#  CLASE: ").append(clase.getSimpleName()).append("\n\n");
        markdown.append("### **File**\n\n");
        markdown.append("`").append(clase.getSimpleName()).append(".java`\n\n");
        
        // Paquete
        if (clase.getPackage() != null) {
            markdown.append("### **Package**\n\n");
            markdown.append("`").append(clase.getPackage().getName()).append("`\n\n");
        }
        
        markdown.append("---\n\n");
    }
    
    private void generarInformacionClase() {
        markdown.append("##  [@DocumentacionClase] - INFORMACIÓN DE LA CLASE\n\n");
        
        // Verificar anotación personalizada
        if (clase.isAnnotationPresent(DocumentacionClase.class)) {
            DocumentacionClase docClase = clase.getAnnotation(DocumentacionClase.class);
            markdown.append("|  Anotación |  Valor |\n");
            markdown.append("|--------------|----------|\n");
            markdown.append("| **@DocumentacionClase.nombre()** | ").append(docClase.nombre()).append(" |\n");
            markdown.append("| **@DocumentacionClase.autor()** | ").append(docClase.autor()).append(" |\n");
            markdown.append("| **@DocumentacionClase.descripcion()** | ").append(docClase.descripcion()).append(" |\n");
            markdown.append("| **@DocumentacionClase.version()** | ").append(docClase.version()).append(" |\n");
        } else {
            markdown.append("* Esta clase NO tiene la anotación @DocumentacionClase*\n\n");
            markdown.append("| Propiedad | Valor |\n");
            markdown.append("|-----------|-------|\n");
            markdown.append("| **Nombre** | ").append(clase.getSimpleName()).append(" |\n");
            markdown.append("| **Paquete** | ").append(clase.getPackageName()).append(" |\n");
        }
        
        // Verificar si es subclase (INFORMACIÓN AUTOMÁTICA - NO VIENE DE ANOTACIÓN)
        markdown.append("\n###  [INFORMACIÓN AUTOMÁTICA - Reflection]\n\n");
        markdown.append("| Propiedad | Valor |\n");
        markdown.append("|-----------|-------|\n");
        markdown.append("| **Nombre de clase** | `").append(clase.getSimpleName()).append("` |\n");
        markdown.append("| **Nombre completo** | `").append(clase.getName()).append("` |\n");
        
        if (clase.getSuperclass() != null && !clase.getSuperclass().equals(Object.class)) {
            markdown.append("| **Subclase de** | `").append(clase.getSuperclass().getSimpleName()).append("` |\n");
        } else {
            markdown.append("| **Subclase de** | No (clase base) |\n");
        }
        
        markdown.append("| **Modificadores** | `").append(Modifier.toString(clase.getModifiers())).append("` |\n");
        markdown.append("\n---\n\n");
    }
    
    private void generarPropiedades() {
        Field[] campos = clase.getDeclaredFields();
        
        if (campos.length == 0) {
            markdown.append("##  ATRIBUTOS\n\n");
            markdown.append("*No se encontraron atributos en esta clase.*\n\n");
            markdown.append("---\n\n");
            return;
        }
        
        markdown.append("##  ATRIBUTOS\n\n");
        
        for (Field campo : campos) {
            markdown.append("###  ATRIBUTO: `").append(campo.getName()).append("`\n\n");
            
            // Tabla con información
            markdown.append("|  Dato |  Valor / Fuente |\n");
            markdown.append("|---------|-------------------|\n");
            
            // Tipo (Reflection)
            markdown.append("| **Tipo** | `").append(campo.getType().getSimpleName()).append("` *(Reflection - getType())* |\n");
            
            // Modificadores (Reflection)
            markdown.append("| **Modificadores** | `").append(Modifier.toString(campo.getModifiers())).append("` *(Reflection - getModifiers())* |\n");
            
            // Descripción (desde anotación)
            if (campo.isAnnotationPresent(DocumentacionAtributo.class)) {
                DocumentacionAtributo doc = campo.getAnnotation(DocumentacionAtributo.class);
                markdown.append("| **Descripción** | ").append(doc.descripcion()).append(" *(@DocumentacionAtributo)* |\n");
            } else {
                markdown.append("| **Descripción** | *(Sin anotación @DocumentacionAtributo)* |\n");
            }
            
            // Métodos asociados (Reflection)
            markdown.append("| **Métodos asociados** | ");
            boolean tieneGetter = false;
            boolean tieneSetter = false;
            String nombreCampo = campo.getName();
            String capitalizado = nombreCampo.substring(0, 1).toUpperCase() + nombreCampo.substring(1);
            
            for (Method metodo : clase.getDeclaredMethods()) {
                if (metodo.getName().equals("get" + capitalizado) ||
                    (campo.getType().equals(boolean.class) && metodo.getName().equals("is" + capitalizado))) {
                    markdown.append("Getter: `").append(metodo.getName()).append("()` ");
                    tieneGetter = true;
                }
                if (metodo.getName().equals("set" + capitalizado)) {
                    markdown.append("Setter: `").append(metodo.getName()).append("()` ");
                    tieneSetter = true;
                }
            }
            
            if (!tieneGetter && !tieneSetter) {
                markdown.append("No se encontraron getters/setters");
            }
            markdown.append(" *(Reflection)* |\n");
            
            markdown.append("\n");
        }
        
        markdown.append("---\n\n");
    }
    
    private void generarMetodos() {
        Constructor<?>[] constructores = clase.getDeclaredConstructors();
        Method[] metodos = clase.getDeclaredMethods();
        
        if (constructores.length == 0 && metodos.length == 0) {
            markdown.append("##  MÉTODOS\n\n");
            markdown.append("*No se encontraron métodos en esta clase.*\n\n");
            markdown.append("---\n\n");
            return;
        }
        
        markdown.append("##  MÉTODOS\n\n");
        
        // ========== CONSTRUCTORES ==========
        if (constructores.length > 0) {
            markdown.append("###  CONSTRUCTORES\n\n");
            
            for (Constructor<?> constructor : constructores) {
                markdown.append("####  `").append(clase.getSimpleName()).append("(");
                Parameter[] params = constructor.getParameters();
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) markdown.append(", ");
                    markdown.append(params[i].getType().getSimpleName());
                }
                markdown.append(")`\n\n");
                
                markdown.append("|  Dato |  Valor / Fuente |\n");
                markdown.append("|---------|-------------------|\n");
                markdown.append("| **Tipo** | Constructor |\n");
                markdown.append("| **Modificadores** | `").append(Modifier.toString(constructor.getModifiers())).append("` *(Reflection - getModifiers())* |\n");
                
                // Parámetros (Reflection)
                if (params.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Parameter param : params) {
                        sb.append("`").append(param.getType().getSimpleName()).append("` ").append(param.getName()).append(", ");
                    }
                    String paramsStr = sb.toString();
                    paramsStr = paramsStr.substring(0, paramsStr.length() - 2);
                    markdown.append("| **Parámetros** | ").append(paramsStr).append(" *(Reflection - getParameters())* |\n");
                } else {
                    markdown.append("| **Parámetros** | Ninguno |\n");
                }
                
                // Descripción (desde anotación)
                if (constructor.isAnnotationPresent(DocumentacionMetodo.class)) {
                    DocumentacionMetodo doc = constructor.getAnnotation(DocumentacionMetodo.class);
                    markdown.append("| **Descripción** | ").append(doc.descripcion()).append(" *(@DocumentacionMetodo)* |\n");
                } else {
                    markdown.append("| **Descripción** | *(Sin anotación @DocumentacionMetodo)* |\n");
                }
                
                markdown.append("| **Retorno** | No aplica (constructor) |\n");
                markdown.append("\n");
            }
            markdown.append("---\n\n");
        }
        
        // ========== MÉTODOS ==========
        if (metodos.length > 0) {
            markdown.append("###  MÉTODOS\n\n");
            
            for (Method metodo : metodos) {
                markdown.append("####  `").append(metodo.getName()).append("(");
                Parameter[] params = metodo.getParameters();
                for (int i = 0; i < params.length; i++) {
                    if (i > 0) markdown.append(", ");
                    markdown.append(params[i].getType().getSimpleName());
                }
                markdown.append(")`\n\n");
                
                markdown.append("|  Dato |  Valor / Fuente |\n");
                markdown.append("|---------|-------------------|\n");
                
                // Retorno (Reflection)
                markdown.append("| **Retorno** | `").append(metodo.getReturnType().getSimpleName()).append("` *(Reflection - getReturnType())* |\n");
                
                // Modificadores (Reflection)
                markdown.append("| **Modificadores** | `").append(Modifier.toString(metodo.getModifiers())).append("` *(Reflection - getModifiers())* |\n");
                
                // Parámetros (Reflection)
                if (params.length > 0) {
                    StringBuilder sb = new StringBuilder();
                    for (Parameter param : params) {
                        sb.append("`").append(param.getType().getSimpleName()).append("` ").append(param.getName()).append(", ");
                    }
                    String paramsStr = sb.toString();
                    paramsStr = paramsStr.substring(0, paramsStr.length() - 2);
                    markdown.append("| **Parámetros** | ").append(paramsStr).append(" *(Reflection - getParameters())* |\n");
                } else {
                    markdown.append("| **Parámetros** | Ninguno |\n");
                }
                
                // Descripción (desde anotación)
                if (metodo.isAnnotationPresent(DocumentacionMetodo.class)) {
                    DocumentacionMetodo doc = metodo.getAnnotation(DocumentacionMetodo.class);
                    markdown.append("| **Descripción** | ").append(doc.descripcion()).append(" *(@DocumentacionMetodo)* |\n");
                } else {
                    markdown.append("| **Descripción** | *(Sin anotación @DocumentacionMetodo)* |\n");
                }
                
                // Verificar si es getter/setter
                String esGetterSetter = determinarGetterSetter(metodo);
                if (!esGetterSetter.equals("No")) {
                    markdown.append("| **Tipo especial** | ").append(esGetterSetter).append(" *(detectado por Reflection)* |\n");
                }
                
                // Verificar si es sobreescrito
                if (!metodo.getDeclaringClass().equals(clase)) {
                    markdown.append("| **Sobrescrito de** | `").append(metodo.getDeclaringClass().getSimpleName()).append("` *(Reflection - getDeclaringClass())* |\n");
                }
                
                markdown.append("\n");
            }
            markdown.append("---\n\n");
        }
    }
    
    private String determinarGetterSetter(Method metodo) {
        String nombre = metodo.getName();
        if (nombre.startsWith("get") && nombre.length() > 3 && metodo.getParameterCount() == 0) {
            return "Getter";
        } else if (nombre.startsWith("set") && nombre.length() > 3 && metodo.getParameterCount() == 1) {
            return "Setter";
        } else if (nombre.startsWith("is") && nombre.length() > 2 && metodo.getParameterCount() == 0) {
            return "Getter (boolean)";
        }
        return "No";
    }
}