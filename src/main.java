//import org.python.util.PythonInterpreter;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class main {
	Scanner Leer=new Scanner(System.in);
	static boolean imgtmp;
	static String claveNacional = "521";
	static String ArchivoRemitentes="Senders.txt";
	static Stack<String> numerosRemitentes = new Stack<String>();
	
	public static String sisOp() {
		//Capacidad para identificar sistema operativo
		return System.getProperty("os.name");
	}
	
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
	
	public static String EligeArchivo(String tipo){
		JFileChooser ArchivoSeleccion = new JFileChooser();
		FileNameExtensionFilter filtro;
		if(tipo.equals("imagen")){
			filtro = new FileNameExtensionFilter(".jpg, .png, .gif","JPG","PNG","GIF","jpg","png","gif");
			ArchivoSeleccion.setFileFilter(filtro);
		}else if(tipo.equals("sonido")){
			filtro = new FileNameExtensionFilter(".mp3, .wav","MP3","WAV","mp3","wav");
			ArchivoSeleccion.setFileFilter(filtro);
		}else if(tipo.equals("video")){
			filtro = new FileNameExtensionFilter(".mp4","MP4","mp4");
			ArchivoSeleccion.setFileFilter(filtro);
        }
        ArchivoSeleccion.showOpenDialog(ArchivoSeleccion);
        String path= ArchivoSeleccion.getSelectedFile().getAbsolutePath();
        System.out.println("\n "+path); 
		return path;
	}
	
	public static String antiBug_Path(String path){
		String cmdpath = path;
		String temporales = "imgtmp";
		int p;
		while( path.contains(" ") ){
			p = path.lastIndexOf(" ");
			path = path.substring(0,p)+"_"+path.substring(p+1);
			cmdpath = cmdpath.substring(0,p)+"\\"+cmdpath.substring(p);
			imgtmp = true;
		}
		if(imgtmp){
			path = temporales+path.substring(path.lastIndexOf("/"));
			bash("mkdir "+temporales);
			bash("cp "+cmdpath+" "+path );
			System.out.println(path);
		}
		return path;
	}
	
	public static String LeerVerif(String numero,String buscar) {
		int p;
		try{
			FileInputStream fstream = new FileInputStream("verif."+numero);
            DataInputStream entrada = new DataInputStream(fstream);	// Objeto de entrada
            BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));	// Buffer de Lectura
            String linea;
            while ((linea = buffer.readLine())!= null){
				p = linea.indexOf(" ");
				if( linea.substring( 0,p-1 ).equals(buscar) )
					return linea.substring(p+1);
            }
            entrada.close();
        }catch (Exception e){
            System.err.println("Ocurrio un error: " + e.getMessage());
        }
        return "No Encontrado";
    }
	
	public static void LeerArchivoTXT(String Archivo) {
		int p;
		String linea,numero;
		try{
			FileInputStream fstream = new FileInputStream(Archivo);
			DataInputStream entrada = new DataInputStream(fstream);	// Objeto de entrada
			BufferedReader buffer = new BufferedReader(new InputStreamReader(entrada));	// Buffer de Lectura
			if(Archivo.equals(ArchivoRemitentes))
				numerosRemitentes.clear();
			while ((linea = buffer.readLine())!= null){
				p = Archivo.indexOf(".");
				if(Archivo.equals(ArchivoRemitentes) && !linea.equals("config.example") && !linea.contains("~")){
					p = linea.lastIndexOf(".");
					numero = linea.substring(p+1);
					if( LeerVerif(numero,"status").equals("ok") )
						numerosRemitentes.add( numero );
				}
			}
			entrada.close();
		}catch (Exception e){
			System.err.println("Ocurrio un error: " + e.getMessage());
		}
    }
	
	public static void contenidoConfig(String CC,String phone, String id, String pass){
		String contenido;
		contenido = "cc="+CC+"\n";
		contenido += "phone="+CC+phone+"\n";
		contenido += "id="+id+"\n";
		contenido += "password="+pass+"\n";
		try{
            FileWriter fw=new FileWriter("config."+phone);
            fw.write( contenido );
            fw.close();
        }catch(IOException e){
            System.out.println("Error E/S: "+e);
        }
	}
	
	public static void AltaWhatsApp(String numero){
		Scanner Leer=new Scanner(System.in);
		String codigo,confirmacion;
		claveNacional = "521";	//Preguntar país, para Mexico la clave nacional default es 521
		contenidoConfig(claveNacional,numero," "," ");
		bash("python yowsup-cli -c config."+numero+" --requestcode sms");	//activacion por SMS
		System.out.print(" En breve recibirá en su telefono celular un codigo de verificación (WhatsApp Code) de tres digitos + guion + tres digitos (000-000).");
		do{
			System.out.print("\n Por favor, revise en su telefono celular, escriba el codigo a continuación: ");
			codigo = Leer.next();
			//	¿ Será necesaria confirmacion del codigo por segunda ocasion ?
			/*if( codigo.length()==7 | codigo.lastIndexOf("-")==3 | codigo.contains("-") ){
				System.out.print("\n\t Por favor, confirma el codigo, escribiendolo por segunda vez: ");
				confirmacion = Leer.next();
			}*/
		}while( codigo.length()!=7 | codigo.lastIndexOf("-")!=3 | !codigo.contains("-") );
		bash("python yowsup-cli -c config."+numero+" --register "+codigo+" >verif."+numero);	//registro y obtención de password al servidor WhatsApp
		contenidoConfig(claveNacional,numero,LeerVerif(numero,"login"),LeerVerif(numero,"pw"));
	}
	
	public static String EstableceNumero(String aQuien) {
		Scanner Leer=new Scanner(System.in);
		int n;
		String telefono;
		boolean unico = false;
		aQuien = aQuien.toUpperCase();
		if(aQuien.equals("REMITENTE")){
			bash("ls |grep config. >"+ArchivoRemitentes);
			LeerArchivoTXT(ArchivoRemitentes);
			if(numerosRemitentes.isEmpty()){
				System.out.println("\t No hay Remitentes Registrados en este Sistema ! ");
				aQuien = "NUEVO";
			}else{
				System.out.println("\t Remitentes Registrados en este Sistema: ");
				for(n=0;n<numerosRemitentes.size();n++){
					if(n%4==0)
						System.out.println();
					else
						System.out.print("\t");
					System.out.print(numerosRemitentes.get(n).toString());
				}
			}
		}
		do{
			if(numerosRemitentes.size()==1 && aQuien.equals("REMITENTE")){
				telefono = numerosRemitentes.get(0).toString();
				System.out.println("\t\tUsando "+telefono);
			}else{
				System.out.print("\nEscribe Telefono "+aQuien);
				if(aQuien.equals("REMITENTE"))
					System.out.print(" (Si el numero no está Registrado, se procederá al Alta)");
				else if(aQuien.equals("NUEVO"))
					System.out.print(" (Se procederá al Alta y Registro)");
				System.out.print(": ");
				telefono = Leer.next();
			}
		}while( telefono.length()>10 | telefono.length()<10 );	//Repite pregunta si numero no coincide con cantidad de digitos que debería tener
		if( aQuien.equals("REMITENTE") ){
			if(numerosRemitentes.toString().contains(telefono)){
				//leer clave nacional del config.numero y establecer clave nacional
				return telefono;
			}else
				AltaWhatsApp(telefono);
		}else if( aQuien.equals("NUEVO") )
			AltaWhatsApp(telefono);
		return telefono;
	}
	
	public static void main (String args[]) {
		int opcion=0;
		String telefonoD="x",mensaje="x";
		Scanner Leer=new Scanner(System.in);
		main objeto=new main();
		imgtmp = false;
		System.out.println("\t\t WHOSAPP ");
		if (sisOp().indexOf("Linux") != -1) {
			String miNumero = EstableceNumero("REMITENTE");
			do{
				System.out.println("\n\t Escriba el numero de la opcion que usted elija ");
				System.out.println(" 0. Salir");
				System.out.println(" 1. Alta de Nuevo Remitente en WhatsApp");
				System.out.println(" 2. Enviar mensaje de texto");
				System.out.println(" 3. Leer mensajes de texto");
				System.out.println(" 4. Enviar una imagen");
				System.out.println(" 5. Enviar un video (.mp4)");
				System.out.println(" 6. Enviar un sonido (.mp3|.wav)");
				System.out.println(" 7. Configurar Remitente");
				//System.out.println(" *. Agenda local de Destinatarios");	//nuevo menu para Agregar o Borrar destinatarios elegibles
				System.out.println(" 8. Configurar Destinatarios");
				System.out.print("\t Tu respuesta es: ");
				opcion = Leer.nextInt();
				switch(opcion){
					case 0:
						System.out.println("\t GOOD BYE !");
						break;
					case 1:
						System.out.println("\t Alta de numero en WhatsApp/WhosApp");
						EstableceNumero("NUEVO");
						break;
					case 2:
						System.out.println("\t ENVIAR MENSAJE de TEXTO");
						if(telefonoD.equals("x"))
							telefonoD = EstableceNumero("destinatario");
						System.out.print(" Escribe MENSAJE: ");
						mensaje = Leer.nextLine();
						mensaje = Leer.nextLine();
						bash("python yowsup-cli -c config."+miNumero+" -w -s "+claveNacional+telefonoD+" \" "+mensaje+" \" ");
						break;
					case 3:
						System.out.println("\t LEER MENSAJES de TEXTO");
						bash("python yowsup-cli -c config."+miNumero+" -l");
						break;
					case 4:
						System.out.println("\t ENVIAR una IMAGEN");
						if(telefonoD.equals("x"))
							telefonoD = EstableceNumero("destinatario");
						System.out.print(" Se abrira una ventana para que seleccione la imagen que desea enviar. Por favor, espere. ");
						try{
							mensaje = EligeArchivo("imagen");
							bash("python yowsup-cli -c config."+miNumero+" -p "+claveNacional+telefonoD+" "+antiBug_Path(mensaje) );
						}catch(Exception e){
							System.out.println("\n Envio de Archivo Imagen fue Cancelado ");
						}
						break;
					case 5:
						System.out.println("\t ENVIAR un VIDEO");
						if(telefonoD.equals("x"))
							telefonoD = EstableceNumero("destinatario");
						System.out.print(" Se abrira una ventana para que seleccione el video que desea enviar. Por favor, espere. ");
						try{
							mensaje = EligeArchivo("video");
							antiBug_Path(mensaje);
							bash("python yowsup-cli -c config."+miNumero+" -t "+claveNacional+telefonoD+" "+antiBug_Path(mensaje) );
						}catch(Exception e){
							System.out.println("\n Envio de Archivo Video fue Cancelado ");
						}
						break;
					case 6:
						System.out.println("\t ENVIAR un Archivo de SONIDO");
						if(telefonoD.equals("x"))
							telefonoD = EstableceNumero("destinatario");
						System.out.print(" Se abrira una ventana para que seleccione el archivo mp3 o wav que desea enviar. Por favor, espere. ");
						try{
							mensaje = EligeArchivo("sonido");
							antiBug_Path(mensaje);
							bash("python yowsup-cli -c config."+miNumero+" -q "+claveNacional+telefonoD+" "+antiBug_Path(mensaje) );
						}catch(Exception e){
							System.out.println("\n Envio de Archivo Sonido fue Cancelado ");
						}
						break;
					case 7:
						System.out.println("\t CAMBIAR de REMITENTE");
						//nuevo menu para seleccionar remitente existente en config.numero u ofrecer alta de nuevo remitente
						//1. cambiar de numero remitente
						miNumero = EstableceNumero("REMITENTE");
						//x. Establecer diferente REMITENTE para cada envio
						//x. Mismo REMITENTE para todos los envios (hasta volver a configurar otro REMITENTE o cerrar el programa)
						//x. Asignar un Alias a numero REMITENTE
						break;
					case 8:
						System.out.println("\t CAMBIAR de DESTINATARIO");
							//nuevo menu para elegir de agenda o usar sin agenda
						telefonoD = EstableceNumero("DESTINATARIO");
						//x. Establecer diferente DESTINATARIO para cada envio
						//x. Mismo DESTINATARIO para todos los envios (hasta volver a configurar otro DESTINATARIO o cerrar el programa)
						//x. multiples DESTINATARIOs
						//x. Asignar un Alias a numero DESTINATARIO
						//x. VOLVER al menu principal
						break;
					default:
						System.out.println("\t NO elegiste nada");
				}
			}while(opcion!=0);
			if(imgtmp)
				bash("rm -r imgtmp/");
			bash("rm "+ArchivoRemitentes);
		}else{
			System.out.println("\t Hola "+sisOp()+" ! ");
			System.out.println(" Los comandos pueden no funcionar correctamente fuera de Linux...\n Espere a que se agreguen comandos para otros Sistemas Operativos. Gracias.");
		}
	}
}

