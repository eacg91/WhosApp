#!usr/bin/env
import base64, argparse, sys, os, csv
from Examples import WhatsappCmdClient, WhatsappListenerClient	#Importar clase WhatsappListenerClient, para leer mensajes recibidos.
#................Clave de Acceso a WhatsApp............................
password = "1s81OT+2lOB3fspbvq+19qfNewM="			#Password dada al registrar el numero.
password = base64.b64decode(bytes(password.encode('utf-8')))	#Codificacion de Password para envio a los servidores de whatsApp.
username = '5213312286701'					#Numero de telefono para el inicio de secion.
keepAlive= True 						#Conexion persistente con el servidor.
autoack = False							#marcar como leidos
#......................................................................
#whats = WhatsappListenerClient(args['keepalive'], args['autoack'])
whats = WhatsappListenerClient(keepAlive, autoack)
whats.login(username, password)
