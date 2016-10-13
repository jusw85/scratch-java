#!/bin/bash
java -cp "./lib/*" -Dcamel.configurationFile="./config/camel-spring2.xml" -Dlog4j.configurationFile="./config/log4j-debug.xml" com.sis.klaver.gateway.OrbcommTest
