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
		String comando = "python yowsup-cli -c config.3312286701 -l";
		//while(continuar==true){
			
			try {
				String[] command = {"sh","-c",comando};
				Process process = Runtime.getRuntime().exec(command);
				
				InputStream is = process.getInputStream();
				byte[] buffer = new byte[1024];	
				//for(int count = 0; (count = is.read(buffer)) >= 0;) {
					System.out.write(buffer, 0, is.read(buffer)); 
				//}
			} catch (Exception e) {
				e.printStackTrace();
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

public class th2 {
	
	public static void bash(String comando) {
	// http://felinfo.blogspot.mx/2009/12/ejecutar-comandos-linux-y-ms-dos-desde.html
		try {
			String[] command = {"sh","-c",comando};
			Process process = Runtime.getRuntime().exec(command);
			
			InputStream is = process.getInputStream();
			byte[] buffer = new byte[1024];	
			for(int count = 0; (count = is.read(buffer)) >= 0;) {
				System.out.write(buffer, 0, count); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
