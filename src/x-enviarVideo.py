#!usr/bin/env

import base64
from Examples2 import WhatsappEchoClient             #Importa la Clase WhatsappEchoClient, dedicada a envio de mensajes.
#................Clave de Acceso a WhatsApp............................
password = "1s81OT+2lOB3fspbvq+19qfNewM="			#Password dada al registrar el numero.
password = base64.b64decode(bytes(password.encode('utf-8')))	#Codificacion de Password para envio a los servidores de whatsApp.
username = '5213312286701'					#Numero de telefono para el inicio de sesion.
wait = False 						#Conexion con el servidor no se cierra hasta haber enviado mensaje.
#......................................................................
whats = WhatsappEchoClient("5213312286701", "Aqui te va tu mensaje", wait)     #Inicia el cliente para el envio de mensajes por WhatsApp.
whats.login(username, password)                                                 #Autentifica el dispositivo con el cliente de WhatsApp.
