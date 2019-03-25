/**
 *  Fully Kiosk Browser Controller.
 * 	Allows user to interface with Fully API allowing for TTS and other functions. 
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *	Mar 24, 2019 V2.00 Arn Burkhoff: Compatability with SmartThings and Hubitat in a single module instance
 *	Mar 24, 2019 V1.05 Arn Burkhoff: Update to 1.05 Hubitat version level. Add setScreenBrightness
 *	Mar 23, 2019 V1.04 Arn Burkhoff: Update to 1.04 Hubitat version level.
 *	Mar 22, 2019 V1.00 Arn Burkhoff: Add chime command giving partial Lannouncer compatability.
 *  Mar 22, 2019 v1.00 Arn Burkhoff: Port to Smarthings from Hubitat
 *	Mar 21, 2019 V1.00 Gavin Campbell: Released on Hubitat
 */

metadata {
    definition (name: "Fully Kiosk Browser Controller", namespace: "GvnCampbell", author: "Gavin Campbell", importUrl: "https://github.com/GvnCampbell/Hubitat/blob/master/Drivers/FullyKioskBrowserController.groovy") {
		capability "Switch Level"
		capability "Tone"
		if (isSmartThings())
			{
			capability "Speech Synthesis"
			attribute "volume", "Number"
			}
		else
			capability "SpeechSynthesis"
		capability "AudioVolume"
        capability "Refresh"
		capability "Actuator"
		command "launchAppPackage"
		command "bringFullyToFront"
		command "screenOn"
		command "screenOff"
		command "triggerMotion"
		command "startScreensaver"
		command "stopScreensaver"
		command "loadURL",["String"]
		command "loadStartURL"
		command "setScreenBrightness",["Number"]
        command "chime"
    }
	preferences {
		input(name:"serverIP",type:"string",title:"Server IP Address",defaultValue:"",required:true)
		input(name:"serverPort",type:"string",title:"Server Port",defaultValue:"2323",required:true)
		input(name:"serverPassword",type:"string",title:"Server Password",defaultValue:"",required:true)
		input(name:"toneFile",type:"string",title:"Tone Audio File URL",defaultValue:"",required:false)
		input(name:"appPackage",type:"string",title:"Application to Launch",defaultValue:"",required:false)
		input(name:"loggingLevel",type:"enum",title:"Logging Level",description:"Set the level of logging.",options:["none","debug","trace","info","warn","error"],defaultValue:"debug",required:true)
    }
	if (isSmartThings())
		{
		tiles
			{
			standardTile("screenOn", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Screen On', action:"screenOn", backgroundColor: "#00a0dc"
				}
			controlTile("levelSliderControl", "device.level", "slider", height: 1,
	             width: 1, inactiveLabel: false, range:"(0..255)")
	            {
			    state "default", action:"setScreenBrightness", label:"Screen Brightness"
				}
			standardTile("screenOff", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Screen Off', action:"screenOff", backgroundColor: "#ffffff"
				}
			standardTile("speak", "device.speech", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Speak', action:"Speech Synthesis.speak", icon:"st.Electronics.electronics13"
				}
			standardTile("beep", "device.tone", inactiveLabel: false, decoration: "flat")
				{
				state "default", label:'Play Beep', action:'beep', inactiveLabel:false, decoration: "flat"
				}
			standardTile("launchapp", "device.speech", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Launch App', action:"launchAppPackage"
				}
			standardTile("fullyfront", "device.speech", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Fully to Front', action:"bringFullyToFront"
				}
			standardTile("setmotion", "device.speech", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Trigger Motion', action:"triggerMotion"
				}
			standardTile("saverOn", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Screen Saver On', action:"startScreensaver", backgroundColor: "#00a0dc"
				}
			standardTile("saverOff", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Screen Saver Off', action:"stopScreensaver"
				}
			standardTile("loadUrl", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Load Url', action:"loadURL"
				}
			standardTile("loadStartUrl", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Load Start URL', action:"loadStartURL"
				}
			standardTile("Mute", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Mute', action:"mute"
				}
			standardTile("Unmute", "device.switch", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'UnMute', action:"unmute"
				}
			standardTile("volumeup", "device.speech", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Volume up 10%', action:"volumeUp"
				}
			controlTile("volumeSliderControl", "device.volume", "slider", height: 1,
	             width: 1, inactiveLabel: false, range:"(0..100)")
	            {
			    state "volume", action:"setVolume", label:"${currentValue}"
				}
			standardTile("volumedown", "device.speech", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Volume down 10%', action:"volumeDown"
				}
			controlTile("levelSliderControl", "device.level", "slider",  height: 4, width: 2, inactiveLabel: false, decoration: "flat") 
	        	{
			    state "level", label:"Screen Brightness", action:"switch level.setLevel"
				}
       
			standardTile("refresh", "device.speech", inactiveLabel: false, decoration: "flat") 
				{
				state "default", label:'Screen Refresh', action:"refresh"
				}
			}
		}	
	}   
}

// *** [ Initialization Methods ] *********************************************
def installed() {
	def logprefix = "[installed] "
    logger logprefix
    initialize()
}
def updated() {
	def logprefix = "[updated] "
    logger logprefix
	initialize()
}
def initialize() {
	def logprefix = "[initialize] "
    logger logprefix
    
    refresh()
}

// *** [ Device Methods ] *****************************************************
def beep() {
	def logprefix = "[beep] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=playSound&url=${toneFile}")
}
def chime() {beep()}
def launchAppPackage() {
	def logprefix = "[launchAppPackage] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=startApplication&package=${appPackage}")
}
def bringFullyToFront() {
	def logprefix = "[bringFullyToFront] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=toForeground")
}
def screenOn() {
	def logprefix = "[screenOn] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=screenOn")
}
def screenOff() {
	def logprefix = "[screenOff] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=screenOff")
}
def setScreenBrightness(value) {
	def logprefix = "[setScreenBrightness] "
	logger(logprefix+"value:${value}","trace")
	sendCommandPost("cmd=setStringSetting&key=screenBrightness&value=${value}")
}
def setLevel(value) {
	def logprefix = "[setLevel] "
	logger(logprefix+"value:${value}","trace")
    def newValue = Math.floor((255 / 100) * value).trunc()
	logger(logprefix+"value:${newValue}","trace")
    setScreenBrightness("${newValue}")
    sendEvent(name: "level", value: "${value}")
}
def triggerMotion() {
	def logprefix = "[triggerMotion] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=triggerMotion")
}
def startScreensaver() {
	def logprefix = "[startScreensaver] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=startScreensaver")
}
def stopScreensaver() {
	def logprefix = "[stopScreensaver] "
    logger(logprefix,"trace")
	sendCommandPost("cmd=stopScreensaver")
}
def loadURL(url='google.com') {
	def logprefix = "[loadURL] "
	logger(logprefix+"url:${url}","trace")
	sendCommandPost("cmd=loadURL&url=${url}")
}
def loadStartURL() {
	def logprefix = "[loadStartURL] "
	logger(logprefix,"trace")
	sendCommandPost("cmd=loadStartURL")
}
def speak(text="Fully Kiosk TTS Device Handler") {
	def logprefix = "[speak] "
	logger(logprefix+"text:${text}","trace")
	sendCommandPost("cmd=textToSpeech&text=${java.net.URLEncoder.encode(text, "UTF-8")}")
}
def setVolume(volumeLevel) {
	def logprefix = "[setVolume] "
	logger(logprefix+"volumeLevel:${volumeLevel}")
	for (i=1;i<=10;i++) {
		sendCommandPost("cmd=setAudioVolume&level=${volumeLevel}&stream=${i}")
	}
	sendEvent([name:"volume",value:volumeLevel])
}
def volumeUp() {
	def logprefix = "[volumeUp] "
	logger(logprefix)
	def newVolume = device.currentValue("volume")
	if (newVolume) {
		newVolume = newVolume.toInteger() + 10
		newVolume = Math.min(newVolume,100)
		setVolume(newVolume)
	}
}
def volumeDown() {
	def logprefix = "[volumeDown] "
	logger(logprefix)
	def newVolume = device.currentValue("volume")
	if (newVolume) {
		newVolume = newVolume.toInteger() - 10
		newVolume = Math.max(newVolume,0)
		setVolume(newVolume)
	}
}
def mute() {
	def logprefix = "[mute] "
	logger(logprefix)
}
def unmute() {
	def logprefix = "[unmute] "
	logger(logprefix)
}
def refresh() {
  	def logprefix = "[refresh] "
  	logger logprefix
	sendCommandPost("cmd=deviceInfo")
}

// *** [ Platform Determination Methods ] *************************************
private isSmartThings()
	{ 
	return (physicalgraph?.device?.HubAction)
	}
private isHubitat() 
	{ 
	return (hubitat?.device?.HubAction)
	}

// *** [ Communication Methods ] **********************************************
def sendCommandPost(cmdDetails="")
	{
  	def logprefix = "[sendCommandPost] "
  	logger logprefix
	if (isSmartThings())
		STsendCommandPost(cmdDetails)
	else
		HEsendCommandPost(cmdDetails)
	}

// [Hubitat Communications]****************************************************
def HEsendCommandPost(cmdDetails="") {
	def logprefix = "[HEsendCommandPost] "
	logger(logprefix+"cmdDetails:${cmdDetails}","trace")
	def postParams = [
		uri: "http://${serverIP}:${serverPort}/?type=json&password=${serverPassword}&${cmdDetails}",
		requestContentType: 'application/json',
		contentType: 'application/json'
	]
	logger(logprefix+postParams)
	asynchttpPost("sendCommandCallback", postParams, null)
}
def sendCommandCallback(response, data) {
	def logprefix = "[sendCommandCallback] "
    logger(logprefix+"response.status: ${response.status}","trace")
	if (response?.status == 200) {
		logger(logprefix+"response.data: ${response.data}","trace")
		def jsonData = parseJson(response.data)
		if (jsonData?.ip4 || jsonData?.status == "OK") {
			logger(logprefix+"Updating last activity.","trace")
			sendEvent([name:"refresh"])
		}
	}
}

//	[SmartThing Communications] *********************************************** 
def STsendCommandPost(cmdDetails="") 
	{
	def logprefix = "[STsendCommandPost] "
	logger(logprefix+"cmdDetails:${cmdDetails} to ${serverIP}:${serverPort}","trace")
    if (serverIP?.trim()) 
    	{
        def hosthex = convertIPtoHex(serverIP)
        def porthex = convertPortToHex(serverPort)
        device.deviceNetworkId = "$hosthex:$porthex"
        def headers = [:] 
        headers.put("HOST", "$serverIP:$serverPort")
        headers.put("Content-Type", "application/json")
        def method = "POST"
	    def hubAction = physicalgraph.device.HubAction.newInstance(
            method: method,
            path: "/?type=json&password=${serverPassword}&${cmdDetails}",
            headers: headers
            );
        logger(logprefix+"hubAction: ${hubAction}","trace")
        return hubAction
		}
	}

def parse(description) {
	def logprefix = "[parse] "
	logger(logprefix+"description: ${description}","trace")
    
    def msg = parseLanMessage(description)
    
    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    
    logger(logprefix+"headersAsString: ${headersAsString}","trace")
	
    def json = new groovy.json.JsonSlurper().parseText(body)  
	def brightness = json.screenBrightness
    
    logger(logprefix+"json: ${json}","trace")
    logger(logprefix+"brightness: ${json.screenBrightness}","trace")
    
    def brightnessPercentage = Math.floor((brightness.toInteger()/255) * 100).trunc()
    logger(logprefix+"brightnessPercentage: ${brightnessPercentage}","trace")
    def evt1 = createEvent(name: "level", value: "${brightnessPercentage}")
    return evt1
}

private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02X', it.toInteger() ) }.join()
//    log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
    return hex
}

private String convertPortToHex(port) {
    String hexport = port.toString().format( '%04X', port.toInteger() )
//    log.debug hexport
    return hexport
}	
// *** [ Logger ] *************************************************************
private logger(loggingText,loggingType="debug") {
	def internalLogging = false
	def internalLoggingSize = 500
	if (internalLogging) { if (!state.logger) {	state.logger = [] }	} else { state.logger = [] }

	loggingType = loggingType.toLowerCase()
	def forceLog = false
	if (loggingType.endsWith("!")) {
		forceLog = true
		loggingType = loggingType.substring(0, loggingType.length() - 1)
	}
	def loggingTypeList = ["trace","debug","warn","info","error"]
	if (!loggingTypeList.contains(loggingType)) { loggingType="debug" }
	if ((!loggingLevel||loggingLevel=="none") && loggingType == "error") {
	} else if (forceLog) {
	} else if (loggingLevel == "debug" || (loggingType == "error")) {
	} else if (loggingLevel == "trace" && (loggingType == "trace" || loggingType == "info")) {
	} else if (loggingLevel == "info"  && (loggingType == "info")) {
	} else if (loggingLevel == "warn"  && (loggingType == "warn")) {
	} else { loggingText = null }
	if (loggingText) {
		log."${loggingType}" loggingText
		if (internalLogging) {
			if (state.logger.size() >= internalLoggingSize) { state.logger.pop() }
			state.logger.push("<b>log.${loggingType}:</b>\t${loggingText}")
		}
	}
}