'''
Copyright (c) <2012> Tarek Galal <tare2.galal@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of this 
software and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, 
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject to the following 
conditions:

The above copyright notice and this permission notice shall be included in all 
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR 
A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
'''

import os
parentdir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
os.sys.path.insert(0,parentdir)
import datetime, sys
import json, time

if sys.version_info >= (3, 0):
	raw_input = input

from Yowsup.connectionmanager import YowsupConnectionManager

class WhatsappListenerClientJSON:
	
	def __init__(self, keepAlive = False, sendReceipts = False):
		self.sendReceipts = sendReceipts
		
		connectionManager = YowsupConnectionManager()
		connectionManager.setAutoPong(keepAlive)

		self.signalsInterface = connectionManager.getSignalsInterface()
		self.methodsInterface = connectionManager.getMethodsInterface()
		
		self.signalsInterface.registerListener("message_received", self.onMessageReceived)
		self.signalsInterface.registerListener("image_received", self.onImageReceived)
		self.signalsInterface.registerListener("auth_success", self.onAuthSuccess)
		self.signalsInterface.registerListener("auth_fail", self.onAuthFailed)
		self.signalsInterface.registerListener("disconnected", self.onDisconnected)
		
		self.cm = connectionManager
		self.connected = True
	
	def login(self, username, password):
		self.username = username
		self.methodsInterface.call("auth_login", (username, password))
		
		while True:
			time.sleep(3)
			if not self.connected:
				break
		
		if self.connected:
			exit(0)
		else:
			exit(1)

	def onAuthSuccess(self, username):
		print("Authed %s" % username)
		self.connected = True
		self.methodsInterface.call("ready")

	def onAuthFailed(self, username, err):
		print("Auth failed")
		self.connected = False

	def onDisconnected(self, reason):
		print("Disconnected because %s" %reason)
		self.connected = False

	def onImageReceived(self, messageId, jid, preview, url, size, wantsReceipt, isBroadcast):
		#print("Image received: Id:%s Jid:%s Url:%s size:%s" %(messageId, jid, url, size))
		
		paramdict = {}
		wtype = "IMAGE"
		messageContent = ""
		pushName = ""
		timestamp = ""
		for i in ('messageId', 'jid', 'pushName', 'timestamp', 'messageContent', 'url', 'size', 'wtype'):
			paramdict[i] = locals()[i]
		sys.stdout.write(json.dumps(paramdict))
		
		if wantsReceipt and self.sendReceipts:
			self.methodsInterface.call("message_ack", (jid, messageId))
		
	def onMessageReceived(self, messageId, jid, messageContent, timestamp, wantsReceipt, pushName, isBroadCast):
		#formattedDate = datetime.datetime.fromtimestamp(timestamp).strftime('%d-%m-%Y %H:%M:%S')
		#print("{} {} {} {} {}".format(messageId, jid, pushName, timestamp, formattedDate, messageContent))

		paramdict = {} 
		wtype = "MESSAGE"
		url = ""
		size = ""
		for i in ('messageId', 'jid', 'pushName', 'timestamp', 'messageContent', 'url', 'size', 'wtype'):
			paramdict[i] = locals()[i]
		sys.stdout.write(json.dumps(paramdict))
		
		if wantsReceipt and self.sendReceipts:
			self.methodsInterface.call("message_ack", (jid, messageId))
