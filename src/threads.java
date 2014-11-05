//	http://www.chuidiang.com/java/hilos/hilos_java.php
import java.io.*;
import java.util.*;

class Multihilo extends Thread {
	private Thread hilo;
	private String nombreHilo;
	private boolean continuar = true;
	
	Multihilo(String nombre){
		nombreHilo = nombre;
		System.out.println("Creando " +  nombreHilo);
	}
	
	public void detenHilo(){
		continuar=false;
	}
	
	public void run() {
		//if(continuar==true){
			System.out.println("Ejecutando " +  nombreHilo );
			try {
				for(int i = 4; i > 0 && continuar; i--) {
					//System.out.println("Hilo: " + nombreHilo + ", " + i);
					// vamos a dormir el hilo unos 50s
					Thread.sleep(3000);	//En linux son milisegundos
				}
			} catch (InterruptedException e) {
				System.out.println("Hilo " +  nombreHilo + " interrumpido.");
			}
		//}
		System.out.println("Hilo " +  nombreHilo + " termino.");
	}
	
	public void start () {
		System.out.println("Iniciando " +  nombreHilo );
		if (hilo == null){
			hilo = new Thread (this, nombreHilo);
			hilo.start ();
		}
	}
}

public class threads {
	public static void main(String args[]) {
		Scanner Leer=new Scanner(System.in);
		Multihilo hilo1 = new Multihilo( "Hilo-1");
		hilo1.start();
		System.out.println("Que hago aqui");
		String mensaje = Leer.nextLine();
		System.out.println(mensaje);
		hilo1.detenHilo();
		//Multihilo hilo2 = new Multihilo( "Hilo-2");
		//hilo2.start();
	}
}
