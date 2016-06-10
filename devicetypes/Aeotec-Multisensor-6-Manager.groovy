/**

*  Copyright 2015 SmartThings

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

*/

 

//https://community.smartthings.com/t/set-configuration-value-by-user-input-in-preferences-help-please/25883

 

//jjohnstonson84

//jj565656

 

//file:///C:/Users/jandersson/Downloads/Engineering%20Spec%20-%20Aeon%20Labs%20MultiSensor%206.pdf

 

//Based on: https://github.com/robertvandervoort/SmartThings/tree/master/Aeon%20Multisensor%206

//https://community.smartthings.com/t/aeotech-multisensor-6-gen-5-zwave-plus-model-zw100-a/17732

 

//Text to speech

//https://raw.githubusercontent.com/SmartThingsUle/DLNA-PLAYER/master/MediaRenderer_Player.groovy

 

metadata {

                definition (name: "Aeon Multisensor 6 [USER]", namespace: "smartthings", author: "SmartThings") {

                                capability "Sensor"

                                capability "Configuration"

                                capability "Battery"

 

                                //Capabilities with state

                                capability "Motion Sensor"

        capability "Acceleration Sensor"

                                capability "Temperature Measurement"

                                capability "Relative Humidity Measurement"

                                capability "Illuminance Measurement"

                                capability "Ultraviolet Index"

                                capability "Acceleration Sensor"

 

                                //User states

                                attribute "powerStatus", "string"

                                attribute "powerSupply", "enum", ["USB", "Battery"]

 

        fingerprint deviceId: "0x2101", inClusters: "0x5E,0x86,0x72,0x59,0x85,0x73,0x71,0x84,0x80,0x30,0x31,0x70,0x7A,0xEF", outClusters: "0x5A"  //0x98 missing?

                }

 

                simulator {

                                status "no motion" : "command: 9881, payload: 00300300"

                                status "motion"    : "command: 9881, payload: 003003FF"

 

                                for (int i = 0; i <= 100; i += 20) {

                                                status "temperature ${i}F": new physicalgraph.zwave.Zwave().securityV1.securityMessageEncapsulation().encapsulate(

                                                                new physicalgraph.zwave.Zwave().sensorMultilevelV2.sensorMultilevelReport(

                                                                                scaledSensorValue: i, precision: 1, sensorType: 1, scale: 1)

                                                                ).incomingMessage()

                                }

 

                                for (int i = 0; i <= 100; i += 20) {

                                                status "humidity ${i}%":  new physicalgraph.zwave.Zwave().securityV1.securityMessageEncapsulation().encapsulate(

                                                                new physicalgraph.zwave.Zwave().sensorMultilevelV2.sensorMultilevelReport(scaledSensorValue: i, sensorType: 5)

                                                ).incomingMessage()

                                }

 

                                for (int i in [0, 20, 89, 100, 200, 500, 1000]) {

                                                status "illuminance ${i} lux":  new physicalgraph.zwave.Zwave().securityV1.securityMessageEncapsulation().encapsulate(

                                                                new physicalgraph.zwave.Zwave().sensorMultilevelV2.sensorMultilevelReport(scaledSensorValue: i, sensorType: 3)

                                                ).incomingMessage()

                                }

 

                                for (int i in [0, 5, 10, 15, 50, 99, 100]) {

                                                status "battery ${i}%":  new physicalgraph.zwave.Zwave().securityV1.securityMessageEncapsulation().encapsulate(

                                                                new physicalgraph.zwave.Zwave().batteryV1.batteryReport(batteryLevel: i)

                                                ).incomingMessage()

                                }

                                status "low battery alert":  new physicalgraph.zwave.Zwave().securityV1.securityMessageEncapsulation().encapsulate(

                                                new physicalgraph.zwave.Zwave().batteryV1.batteryReport(batteryLevel: 255)

                                ).incomingMessage()

 

                                status "wake up" : "command: 8407, payload: "

                }

 

                preferences {

 

                //BUG: Will the wakeup advice work during [secure] inclusion?  TEST!

                                input description: "Due to an ST bug, configuration must be done after initial setup. Edit device preferences from the things panel ONLY, NOT from SmartSetup or Marketplace.",

                                                                title: "Setup", displayDuringSetup: true, type: "paragraph", element: "paragraph"

 

                                input description: "To activate configuration changes on a battery powered sensor only, momentarilty tap the Action button on the sensor after pressing DONE on this screen.\nDuring successful configuration the LED on the sensor should illuminate solidly for ~20 seconds (green for non-secure mode, blue for secure). Double tap the Action button before connecting a new MultiSensor to initiate secure inclusion.\nPlease consult Aeon Labs MultiSensor 6 Engineering Specifications for additional information on these settings.",

                                                                title: "Configuration", displayDuringSetup: false, type: "paragraph", element: "paragraph"

 

                                input "MotionSensitivity",

                        "number",

                    title: "Motion Sensitivity (0 = Off, 1 Low to 5 High)",

                                                description: "A number from 0 - 5, from disabled to high sensitivity",

                                                range: "0..5",

                                                defaultValue: 5,

                                                required: true,

                                                displayDuringSetup: false

                                input "MotionReset",

                        "number",

                    title: "Motion Reset Time (10 to 3600)Sec",

                                                description: "Number of seconds to wait after the last motion event to report motion cleared",

                                                range: "10..3600",

                                                defaultValue: 240,

                                                required: true,

                                                displayDuringSetup: false          

 

                                input "WakeupInterval",

               "number",

            title: "Battery Power Wakeup Interval (240 to ?)Sec",

            description: "A value in seconds to wait between battery-powered sensor wakeups",

                                                range: "240..*",

            defaultValue: 3600,

            required: true,

            displayDuringSetup: false

                                input "DataReportInterval",

               "number",

            title: "Sensor Data Report Interval (5 to 2678400)Sec",

            description: "A value in seconds to wait between sensor data reports",

                                                range: "5..2678400",

            defaultValue: 3600,

            required: true,

            displayDuringSetup: false

                                input "BatteryReportInterval",

               "number",

            title: "Battery Level Report Interval (5 to 2678400)Sec",

            description: "A value in seconds to wait between battery level reports",

                                                range: "5..2678400",

            defaultValue: 43200,

            required: true,

            displayDuringSetup: false

               

                                input "ThresholdReporting",

               "number",

            title: "Report Thresholding (0 = Off, 1 = On)",

            description: "Value that determines if threshold reporting is used",

                                                range: "0..1",

            defaultValue: 1,

            required: true,

            displayDuringSetup: false

                               

                                input "TempOffset",

                                                "decimal",

                                                title: "Temperature Offset (-10 to 10)Deg",

            description: "Value to adjust temperature by",

            range: "-10..10",

                                                defaultValue: 0,

            required: false,

            displayDuringSetup: false

                                input "HumidityOffset",

               "number",

            title: "Humidity Offset (-50 to 50)%",

            description: "Value to adjust humidity by",

                                                range: "-50..50",

                                                defaultValue: 0,

                                                required: false,

            displayDuringSetup: false

                                input "LuminanceOffset",

               "number",

            title: "Luminance Offset (-1000 to 1000)lux",

            description: "Value to adjust luminance by",

            range: "-1000..1000",

                                                defaultValue: 0,

            required: false,

                        displayDuringSetup: false

                                input "UltravioletOffset",

               "number",

            title: "Ultraviolet Index Offset (-10 to 10)",

            description: "Value to adjust ultraviolet index by",

            range: "-10..10",

                                                defaultValue: 0,

                        required: false,

                                                displayDuringSetup: false

                }

 

                tiles(scale: 2) {

                                multiAttributeTile(name:"motion", type: "generic", width: 6, height: 4){

                                                tileAttribute ("device.motion", key: "PRIMARY_CONTROL") {

                                                                attributeState "active", label:'motion', icon:"st.motion.motion.active", backgroundColor:"#53a7c0"

                                                                attributeState "inactive", label:'no motion', icon:"st.motion.motion.inactive", backgroundColor:"#ffffff", defaultState: true

                                                }

                                }

       

                                valueTile("temperature", "device.temperature", inactiveLabel: false, width: 2, height: 2) {

                                                state "temperature", label:'${currentValue}°',

                                                backgroundColors:[

                                                                [value: 32, color: "#153591"],

                                                                [value: 44, color: "#1e9cbb"],

                                                                [value: 59, color: "#90d2a7"],

                                                                [value: 74, color: "#44b621"],

                                                                [value: 84, color: "#f1d801"],

                                                                [value: 92, color: "#d04e00"],

                                                                [value: 98, color: "#bc2323"]

                                                ]

                                }

       

                                valueTile("humidity", "device.humidity", inactiveLabel: false, width: 2, height: 2) {

                                                state "humidity", label:'${currentValue}% humidity', unit:"", backgroundColors:[

                                                                [value:   0, color: "#D6D6FF"],

                [value: 100, color: "#2E2EFF"]

                                                ]

                                }

 

                                valueTile("illuminance", "device.illuminance", inactiveLabel: false, width: 2, height: 2) {

            state "luminosity",label:'${currentValue} lux', unit:"", backgroundColors:[

                [value:    0, color: "#000000"],

                [value:    1, color: "#060053"],

                [value:    3, color: "#3E3900"],

                [value:   12, color: "#8E8400"],

                [value:   24, color: "#C5C08B"],

                [value:   36, color: "#DAD7B6"],

                [value:  128, color: "#F3F2E9"],

                [value: 1000, color: "#FFFFFF"]

            ]

        }

       

                                valueTile("ultravioletIndex", "device.ultravioletIndex", inactiveLabel: false, width: 2, height: 2) {

                                                state "ultravioletIndex", label:'${currentValue} UV index', unit:"", backgroundColors:[

 

                                                                //WHO UV Index Symbol Colors

                [value:  0, color: "#289500"],

                [value:  1, color: "#289500"],

                [value:  2, color: "#289500"],

                [value:  3, color: "#f7e400"],

                [value:  4, color: "#f7e400"],

                [value:  5, color: "#f7e400"],

                [value:  6, color: "#f85900"],

                [value:  7, color: "#f85900"],

                [value:  8, color: "#d8001d"],

                [value:  9, color: "#d8001d"],

                [value: 10, color: "#d8001d"],

               [value: 11, color: "#6b49c8"],

                [value: 99, color: "#6b49c8"]

 

/*

                                                                //WHO UV Index Map Colors

                                                                [value:  0, color: "#4eb400"],

                [value:  1, color: "#4eb400"],

                [value:  2, color: "#a0ce00"],

                [value:  3, color: "#f7e400"],

                [value:  4, color: "#f8b600"],

                [value:  5, color: "#f88700"],

                [value:  6, color: "#f85900"],

                [value:  7, color: "#e82c0e"],

                [value:  8, color: "#d8001d"],

                [value:  9, color: "#ff0099"],

                [value: 10, color: "#b54cff"],

                [value: 11, color: "#998cff"],

                [value: 99, color: "#998cff"]

*/

                                                ]

                                }

 

                                standardTile("acceleration", "device.acceleration", width: 2, height: 2) {

                                                state("active", label:'tampered', icon:"st.motion.acceleration.active", backgroundColor:"#ff0000")

                                                state("inactive", label:'clear', icon:"st.motion.acceleration.inactive", backgroundColor:"#00ff00", defaultState: true)

                                }

 

                                valueTile("powerStatus", "device.powerStatus", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {

                                                state "powerStatus", label:'${currentValue}', unit:""

                                }

 

                                main(["motion", "temperature", "humidity", "illuminance", "ultravioletIndex", "acceleration"])

                                details(["motion", "temperature", "humidity", "illuminance", "ultravioletIndex", "acceleration", "powerStatus"])

                }

}

 

 

//Hack to fix ST settings bug, supplies defaults from preferences if settings are missing.

def get_setting(String prefname) {

 

                //BUG: This painful hack is required because ST does not properly supply a settings property during initial device setup!!!

    //BUG: During calls to update() while in initial device setup, the settings property is not defined!

 

                if(this.hasProperty("settings"))

    {

        if(settings.containsKey(prefname))

        {

            log.info "Settings property exists: ${prefname} = ${settings[prefname]}"

 

            return settings[prefname]

                                }

 

                                log.error "Settings property exists, but ${prefname} is not present!"

    } else {

        log.error "Settings property doesn't exist!"

    }

 

                if(this.hasProperty("preferences"))

    {

        if(preferences["sections"]["input"][0].findAll{it.get("name")==prefname}.size() == 0)

        {

            log.error "FATAL: Preferences property exists, but ${prefname} is not present!"

 

            return null

        }

 

        log.error "Preferences default value: ${prefname} = ${preferences["sections"]["input"][0].findAll{it.get("name")==prefname}[0]['defaultValue']}"

 

        //BUG: This code is likely fragile.  If ST changes the structure of the preferences property, it will likely break!

        return preferences["sections"]["input"][0].findAll{it.get("name")==prefname}[0]['defaultValue']

    } else {

        log.error "FATAL: Preferences property doesn't exist!"

 

        return null

    }

}

 

 

//Called when settings are updated

def updated() {

 

/*

                log.error "Requesting parameter 9---------->"

                delayBetween([

                zwave.configurationV2.configurationGet(parameterNumber: 9)

    ])

*/

 

                if(!this.hasProperty("settings"))

    {

                                log.error "ST BUG: Missing settings map!!!"

                } else if(settings.size() == 0)

    {

                                log.error "ST BUG: Empty settings map!!!"

                }

 

                log.debug "Updated with settings: ${settings}"

   

                log.debug "${device.displayName} is now ${device.latestValue("powerSupply")}"

 

//Is this more code correct?

//            def result = []

 

                if (device.latestValue("powerSupply") == "USB")                                                                //case1: USB powered

    {

                //Device is USB powered and should always be awake, configure now.

                                response(configure())

//cc?

//                            result << response(configure())

                }

    else if (device.latestValue("powerSupply") == "Battery")                            //case2: battery powered

    { 

                                // setConfigured("false") is used by WakeUpNotification

                                setConfigured("false") //wait until the next time device wakeup to send configure command after user change preference

                }

    else                                                                                                                                                                                                                                   //case3: power source is not identified, ask user to properly pair the sensor again

    {

                                log.warn "Power source is not identified, check if sensor is powered by USB, if so > configure()"

                                def request = []

               //BUG Hack to force reconfigure if power supply source is unknown

                                request << zwave.configurationV1.configurationGet(parameterNumber: 103)

                                response(delay_commands(request))

//cc?

//                            result << response(delay_commands(request))

                }

//cc?

//    result

}

 

 

//General message handler

def parse(String description) {

                log.debug "parse() >> description: $description"

                def result = null

                if (description.startsWith("Err 106")) {

                                log.debug "parse() >> Err 106"

                                result = createEvent( name: "secureInclusion", value: "failed", isStateChange: true,

                                                                descriptionText: "This sensor failed to complete the network security key exchange. If you are unable to control it via SmartThings, you must remove it from your network and add it again.")

                } else if (description != "updated") {

                                log.debug "parse() >> zwave.parse(description)"

                                def cmd = zwave.parse(description, [0x31: 5, 0x30: 2, 0x84: 1])

                                if (cmd) {

                                                result = zwaveEvent(cmd)

                                }

                }

                log.debug "After zwaveEvent(cmd) >> Parsed '${description}' to ${result.inspect()}"

                return result

}

 

 

//Wakeup notification (battery power only)

def zwaveEvent(physicalgraph.zwave.commands.wakeupv1.WakeUpNotification cmd) {

 

                def result = [createEvent(descriptionText: "${device.displayName} woke up", isStateChange: false)]

 

                def cmds = []

                if (!isConfigured()) {

                                log.debug("late configure")

                                result << response(configure())

                } else {

                                log.debug("Device has been configured sending >> wakeUpNoMoreInformation()")

                                cmds << zwave.wakeUpV1.wakeUpNoMoreInformation().format()

                                result << response(cmds)

                }

                result

}

 

 

//Security Commands / Reports

def zwaveEvent(physicalgraph.zwave.commands.securityv1.NetworkKeyVerify cmd) {

 

                log.info "Executing zwaveEvent 98 (SecurityV1): 07 (NetworkKeyVerify) with cmd: $cmd (node is securely included)"

   

                state.sec = 1

   

                def result = [createEvent(name:"secureInclusion", value:"success", descriptionText:"Secure inclusion was successful", isStateChange: true)]

   

                result

}

 

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityCommandsSupportedReport cmd) {

 

                log.info "Executing zwaveEvent 98 (SecurityV1): 03 (SecurityCommandsSupportedReport) with cmd: $cmd"

   

                state.sec = 1

}

 

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd) {

 

//            log.info "SecurityMessageEncapsulation with cmd: $cmd"

 

                def encapsulatedCommand = cmd.encapsulatedCommand([0x31: 5, 0x30: 2, 0x84: 1])

                state.sec = 1 //secure mode detected

                log.debug "encapsulated: ${encapsulatedCommand}"

                if (encapsulatedCommand) {

                                zwaveEvent(encapsulatedCommand)

                } else {

                                log.warn "Unable to extract encapsulated cmd from $cmd"

                                createEvent(descriptionText: cmd.toString())

                }

}

 

 

//Motion Events (both modes)

def motionEvent(value) {

                def map = [name: "motion"]

                if (value) {

                                map.value = "active"

                                map.descriptionText = "$device.displayName detected motion"

                } else {

                                map.value = "inactive"

                                map.descriptionText = "$device.displayName motion has stopped"

                }

                createEvent(map)

}

 

def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv2.SensorBinaryReport cmd) {

                motionEvent(cmd.sensorValue)

}

 

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {

                motionEvent(cmd.value)

}

 

 

//Acceleration (tamper) Events

def zwaveEvent(physicalgraph.zwave.commands.notificationv3.NotificationReport cmd) {

 

                def result = []

 

                if (cmd.notificationType == 7) {

                                switch (cmd.event) {

                                                case 0:

                                                                result << motionEvent(0)

                                                                result << createEvent(name: "acceleration", value: "inactive", displayed: false)

                                                                break

                                                case 3:

                                                                result << createEvent(name: "acceleration", value: "active", descriptionText: "$device.displayName was tampered with")

                                                                break

                                                case 7:

                                                                result << motionEvent(1)

                                                                break

                                }

                } else {

                                log.warn "Need to handle this cmd.notificationType: ${cmd.notificationType}"

                                result << createEvent(descriptionText: cmd.toString(), isStateChange: false)

                }

   

                result

}

 

 

//Sensor data reports

def zwaveEvent(physicalgraph.zwave.commands.sensormultilevelv5.SensorMultilevelReport cmd){

                def map = [:]

                switch (cmd.sensorType) {

                                case 1:

                                                map.name = "temperature"

                                                def cmdScale = cmd.scale == 1 ? "F" : "C"

                                                map.value = convertTemperatureIfNeeded(cmd.scaledSensorValue, cmdScale, cmd.precision)

                                                map.unit = getTemperatureScale()

                                                break

                                case 3:

                                                map.name = "illuminance"

                                                map.value = cmd.scaledSensorValue.toInteger()

                                                map.unit = "lux"

                                                break

                                case 5:

                                                map.name = "humidity"

                                                map.value = cmd.scaledSensorValue.toInteger()

                                                map.unit = "%"

                                                break

                                case 0x1B:

                                                map.name = "ultravioletIndex"

                                                map.value = cmd.scaledSensorValue.toInteger()

                                                break

                                default:

                                                map.descriptionText = cmd.toString()

                }

                createEvent(map)

}

 

 

//Configuration data reports

def zwaveEvent(physicalgraph.zwave.commands.configurationv2.ConfigurationReport cmd) {

 

                log.debug "ConfigurationReport: $cmd"

                def result = []

                def value

                if (cmd.parameterNumber == 9 && cmd.configurationValue[0] == 0)

    {

//log.error cmd //ConfigurationReport(configurationValue: [0, 0], parameterNumber: 9, reserved11: 0, size: 2)

//log.error "USB POWER -------------------------------------!"

                                value = "USB"

                                if (!isConfigured())

        {

                                                log.debug("ConfigurationReport: configuring device")

                                                result << response(configure())

                                }

                                result << createEvent(name: "powerStatus", value: value, displayed: false)

                                result << createEvent(name: "powerSupply", value: value, displayed: false)

                } else if (cmd.parameterNumber == 9 && cmd.configurationValue[0] == 1) {

                                value = "Battery"

                                result << createEvent(name: "powerSupply", value: value, displayed: false)

                } else if (cmd.parameterNumber == 103) {

                //BUG Hack to force reconfigure if power supply source is unknown

                                result << response(configure())

                }

                result

}

 

 

//Battery report

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd) {

 

                def result = []

 

    //Create and add standard battery event to results

    def map = [ name: "battery", unit: "%" ]

                if (cmd.batteryLevel == 0xFF)

    {

                                map.value = 1

                                map.descriptionText = "${device.displayName} battery is low"

                                map.isStateChange = true

                } else {

                                map.value = cmd.batteryLevel

                }

                state.lastbatt = now()

                result << createEvent(map)

               

    //If on battery power, create and add (update) custom power status event for display in tile (x% battery)

    //If on USB power, leave state alone ("USB")

    if (device.latestValue("powerSupply") != "USB")

    {

                                result << createEvent(name: "powerStatus", value: "${map.value}% battery", displayed: false)

                }

 

                result

}

 

 

//Manufacturer info report

def zwaveEvent(physicalgraph.zwave.commands.manufacturerspecificv2.ManufacturerSpecificReport cmd) {

 

                log.info "Executing zwaveEvent 72 (ManufacturerSpecificV2) : 05 (ManufacturerSpecificReport) with cmd: $cmd"

                log.debug "manufacturerId:   ${cmd.manufacturerId}"

                log.debug "manufacturerName: ${cmd.manufacturerName}"

                log.debug "productId:        ${cmd.productId}"

                log.debug "productTypeId:    ${cmd.productTypeId}"

   

                def msr = String.format("%04X-%04X-%04X", cmd.manufacturerId, cmd.productTypeId, cmd.productId)

   

                updateDataValue("MSR", msr)

}

 

 

//Generic (unhandled?) events

def zwaveEvent(physicalgraph.zwave.Command cmd) {

                log.debug "General zwaveEvent cmd: ${cmd}"

                createEvent(descriptionText: cmd.toString(), isStateChange: false)

}

 

 

//Configuration

private setConfigured(configure) {

 

                updateDataValue("configured", configure)

 

}

 

private isConfigured() {

 

                getDataValue("configured") == "true"

 

}

 

def configure() {

 

                // This sensor joins as a secure device if you double-click the button to include it

                log.info "${device.displayName}: configure()"

 

                //Reset tamper (if user disables motion detector, will never be reset otherwise)

                sendEvent(name: "acceleration", value: "inactive", displayed: false)

 

                def request = []

 

    //Disable Configuration Lock

    request << zwave.configurationV1.configurationSet(parameterNumber: 252, size: 1, scaledConfigurationValue: 0)

//    request << zwave.configurationV1.configurationGet(parameterNumber: 252)

 

                //Set device wakeup interval

    request << zwave.wakeUpV2.wakeUpIntervalSet(seconds: get_setting('WakeupInterval'), nodeid: zwaveHubNodeId)

//    request << zwave.wakeUpV2.wakeUpIntervalGet()

 

    //"Configure low battery value"

    request << zwave.configurationV1.configurationSet(parameterNumber: 39, size: 1, scaledConfigurationValue: 10) //Set to 10%, default is 20%.  Device behavior not described in docs.

//    request << zwave.configurationV1.configurationGet(parameterNumber: 39)

 

                //Enable low temperature (<= -15C) alarm. Battery operation will likely fail at these temps.

                request << zwave.configurationV1.configurationSet(parameterNumber: 46, size: 1, scaledConfigurationValue: 1)

//    request << zwave.configurationV1.configurationGet(parameterNumber: 46)

 

                //"[sic] Enable waking up for 10 minutes when re-power on (battery mode) the MultiSensor."

    request << zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: 1)

//    request << zwave.configurationV1.configurationGet(parameterNumber: 2)

 

                //Code from old sensor gen, applicable?

                //Disable notification-style motion events

                //request << zwave.notificationV3.notificationSet(notificationType: 7, notificationStatus: 0) //NOTIFICATION_TYPE_BURGLAR

 

    //Motion sends binary sensor report

    request << zwave.configurationV1.configurationSet(parameterNumber: 5, size: 1, scaledConfigurationValue: 2)

//    request << zwave.configurationV1.configurationGet(parameterNumber: 5)

 

    //Send no-motion report x seconds after motion stops (default 240 secs)

    request << zwave.configurationV1.configurationSet(parameterNumber: 3, size: 2, scaledConfigurationValue: get_setting('MotionReset'))

//    request << zwave.configurationV1.configurationGet(parameterNumber: 3)

 

    //Enable/disable motion sensor and set sensitivity (0 = Off, 1 = Min to 5 = Max)

    request << zwave.configurationV1.configurationSet(parameterNumber: 4, size: 1, scaledConfigurationValue: get_setting('MotionSensitivity'))

//    request << zwave.configurationV1.configurationGet(parameterNumber: 4)

 

                //Disable threshold reporting

                request << zwave.configurationV1.configurationSet(parameterNumber: 40, size: 1, scaledConfigurationValue: get_setting('ThresholdReporting')) //Even when disabled, duplicate consecutive values will not be reported.

//    request << zwave.configurationV1.configurationGet(parameterNumber: 40)

    //TODO: Configure parameters 41 - 45 (thresholds) if using threshold reporting...

 

    //Configure temp offset

    request << zwave.configurationV1.configurationSet(parameterNumber: 201, size: 1, scaledConfigurationValue: get_setting('TempOffset') * 10)

//            request << zwave.configurationV1.configurationGet(parameterNumber: 201)

                               

    //Configure humidity offset

    request << zwave.configurationV1.configurationSet(parameterNumber: 202, size: 1, scaledConfigurationValue: get_setting('HumidityOffset'))

//            request << zwave.configurationV1.configurationGet(parameterNumber: 202)

                               

    //Configure luminance offset

    request << zwave.configurationV1.configurationSet(parameterNumber: 203, size: 2, scaledConfigurationValue: get_setting('LuminanceOffset'))

//            request << zwave.configurationV1.configurationGet(parameterNumber: 203)

                               

    //Configure ultraviolet offset

    request << zwave.configurationV1.configurationSet(parameterNumber: 204, size: 1, scaledConfigurationValue: get_setting('UltravioletOffset'))

//            request << zwave.configurationV1.configurationGet(parameterNumber: 204)

 

                //Set association groups

                request << zwave.associationV1.associationSet(groupingIdentifier: 1, nodeId: zwaveHubNodeId) //association group 1 (everything except battery)

                request << zwave.associationV1.associationSet(groupingIdentifier: 2, nodeId: zwaveHubNodeId) //association group 2 (battery)

 

                //Set association report flags

                // param 101 to 103 [4 bytes each]: 128 light sensor, 64 humidity, 32 temperature sensor, 16 ultraviolet sensor, 1 battery sensor

    // 227 = all reports

    request << zwave.configurationV1.configurationSet(parameterNumber: 101, size: 4, scaledConfigurationValue: 128|64|32|16) //association group 1 (everything except battery)

//            request << zwave.configurationV1.configurationGet(parameterNumber: 101)

                request << zwave.configurationV1.configurationSet(parameterNumber: 102, size: 4, scaledConfigurationValue: 1) //association group 2 (battery)

//            request << zwave.configurationV1.configurationGet(parameterNumber: 102)

                request << zwave.configurationV1.configurationSet(parameterNumber: 103, size: 4, scaledConfigurationValue: 0) //association group 3 (no report)

//            request << zwave.configurationV1.configurationGet(parameterNumber: 103) //Conflicts with delayed config hack

 

                //Configure association report intervals

                request << zwave.configurationV1.configurationSet(parameterNumber: 111, size: 4, scaledConfigurationValue: get_setting('DataReportInterval')) //association group 1 (everything except battery)

//            request << zwave.configurationV1.configurationGet(parameterNumber: 111)

                request << zwave.configurationV1.configurationSet(parameterNumber: 112, size: 4, scaledConfigurationValue: get_setting('BatteryReportInterval'))  //association group 2 (battery)

//            request << zwave.configurationV1.configurationGet(parameterNumber: 112)

                request << zwave.configurationV1.configurationSet(parameterNumber: 113, size: 4, scaledConfigurationValue: 0x28DE80)  //association group 3 (no report)

//            request << zwave.configurationV1.configurationGet(parameterNumber: 113)

 

                //Query sensor data

                request << zwave.batteryV1.batteryGet()

/*

                request << zwave.sensorBinaryV2.sensorBinaryGet(sensorType: 0x0C) //motion SENSOR_TYPE_MOTION

                request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x01) //temperature SENSOR_TYPE_TEMPERATURE_VERSION_1

                request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x05) //humidity SENSOR_TYPE_RELATIVE_HUMIDITY_VERSION_2

                request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x03) //illuminance SENSOR_TYPE_LUMINANCE_VERSION_1

                request << zwave.sensorMultilevelV5.sensorMultilevelGet(sensorType: 0x1B) //ultravioletIndex SENSOR_TYPE_ULTRAVIOLET_V5

*/

 

//            setConfigured("true")

 

                if(this.hasProperty("settings"))

    {

                    if(settings.size() != 0)

        {

                                                setConfigured("true")

//           state.configured = true

            sendEvent(name: "configured", value: true, descriptionText: "Configuration successful.", displayed: true)

        } else {

                                                setConfigured("false")

//           state.configured = false

                        log.error "ST BUG: Empty settings map!!!"

            sendEvent(name: "configured", value: false, descriptionText: "Configuration failed due to ST bug (empty settings map), defaults used. Edit device preferences and reconfigure!", displayed: true)

        }

                } else {

                                setConfigured("false")

//        state.configured = false

        log.error "ST BUG: Missing settings map!!!"

        sendEvent(name: "configured", value: false, descriptionText: "Configuration failed due to ST bug (missing settings map), defaults used. Edit device preferences and reconfigure!", displayed: true)

                }

 

                delay_commands(request) + ["delay 20000", zwave.wakeUpV1.wakeUpNoMoreInformation().format()]

}

 

 

//Command delay insertion / secure encapsulation

private delay_commands(commands, delay=1500 /*200*/) {

 

                log.info "sending commands: ${commands}"

                delayBetween(commands.collect{ encapsulate_command(it) }, delay)

 

}

 

private encapsulate_command(physicalgraph.zwave.Command cmd) {

 

                //If secure mode, encapsulate command

    if (state.sec) {

                                zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()

                } else {

                                cmd.format()

                }

   

}

 

 

/*

//            log.info "udv -------------------------------->"

//            updateDataValue("asdf", "value")

//            log.info "gdv --------------------------------> ${getDataValue("asdf")}"

 

//            log.info "state <--------------------------------"

//            state.asdf = "value"

//            log.info "state --------------------------------> ${state.asdf}"

 

def getDataByName(String name) {

        state[name] ?: device.getDataValue(name)

}

*/

 

/*

https://graph.api.smartthings.com/ide/doc/zwave-utils.html

http://wiki.micasaverde.com/index.php/ZWave_Command_Classes

 

https://github.com/robertvandervoort/SmartThings/blob/master/Aeon%20Multisensor%206/Engineering%20Spec%20-%20Aeon%20Labs%20MultiSensor%206.pdf

http://www.smarthus.info/support/manuals/zw_sikkerhet/aeotec_multisensor_tech.pdf (previous gen)

 

Command Class 0xef V1 supported.

Command Class 0x98 V1 supported.

Command Class 0x86 V2 supported.

Command Class 0x85 V2 supported.

Command Class 0x84 V2 supported.

Command Class 0x80 V1 supported.

Command Class 0x7a V2 supported.

Command Class 0x73 V1 supported.

Command Class 0x72 V2 supported.

Command Class 0x71 V3 supported.

Command Class 0x70 V1 supported.

Command Class 0x5e V2 supported.

Command Class 0x5a V1 supported.

Command Class 0x59 V1 supported.

Command Class 0x31 V5 supported.

Command Class 0x30 V1 supported.

Command Class 0x20 V1 supported.

 

ff

Parameter 0xfd is [255, 170, 14, 121].     //?

Parameter 0xfd is [255, 85, 13, 81].          //?

Parameter 0xfc is [0].

Parameter 0xfb is [0, 16].                                                              //?

Parameter 0xcc is [0].

Parameter 0xcb is [0, 0].

Parameter 0xca is [0].

Parameter 0xc9 is [0].

Parameter 0x88 is [12, 218].                                        //?

Parameter 0x80 is [1, 0, 0, 0].                                      //?

Parameter 0x71 is [0, 0, 14, 16].

Parameter 0x70 is [0, 0, 84, 96].

Parameter 0x6f is [0, 0, 1, 224].

6e

Parameter 0x67 is [0, 0, 0, 0].

Parameter 0x66 is [0, 0, 0, 1].

Parameter 0x65 is [0, 0, 0, 224].

64

Parameter 0x58 is [60].

Parameter 0x2e is [0].

Parameter 0x2d is [2].

Parameter 0x2c is [10].

Parameter 0x2b is [0, 100].

Parameter 0x2a is [10].

Parameter 0x29 is [0, 20].

Parameter 0x28 is [1].

Parameter 0x27 is [20].

Parameter 0x26 is [100, 100].                                      //?

Parameter 0x06 is [127].                                                               //?

Parameter 0x05 is [1].

Parameter 0x04 is [5].

Parameter 0x03 is [0, 240].

Parameter 0x02 is [0].

*/