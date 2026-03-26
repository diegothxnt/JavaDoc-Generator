import procesador.GeneradorDocumentacion;
import ejemplo.EjemploPersona;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== GENERADOR DE DOCUMENTACIÓN ===");
        System.out.println("1. Documentar clase de ejemplo (Persona)");
        System.out.println("2. Documentar clase específica");
        System.out.println("3. Salir");
        System.out.print("Seleccione una opción: ");
        
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Limpiar buffer
        
        try {
            switch (opcion) {
                case 1:
                    documentarClase(EjemploPersona.class);
                    break;
                case 2:
                    System.out.print("Ingrese el nombre completo de la clase (ej: java.util.ArrayList): ");
                    String nombreClase = scanner.nextLine();
                    try {
                        Class<?> clase = Class.forName(nombreClase);
                        documentarClase(clase);
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error: Clase no encontrada - " + nombreClase);
                    }
                    break;
                case 3:
                    System.out.println("¡Hasta luego!");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        } catch (Exception e) {
            System.err.println("Error al generar documentación: " + e.getMessage());
            e.printStackTrace();
        }
        
        scanner.close();
    }
    
    private static void documentarClase(Class<?> clase) {
        try {
            System.out.println("\nGenerando documentación para: " + clase.getSimpleName());
            GeneradorDocumentacion generador = new GeneradorDocumentacion(clase);
            generador.generarDocumentacion();
            System.out.println("¡Documentación generada exitosamente!\n");
        } catch (IOException e) {
            System.err.println("Error al escribir archivo: " + e.getMessage());
        }
    }
}