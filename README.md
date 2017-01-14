# Z-Way library for Java - Test (Model only)

This project provides a test application for Z-Way library for Java. The application doesn't modify any Z-Way configuration or device setup. Only the following GET request will be performed:

- GET login
- GET device
- GET device command

## Usage

- Install Maven if not already installed
- Clone project and navigate with console to the root folder of the project
- Build JAR file: `mvn package`
- Run application: `java -jar ./target/zway-lib-test/app.jar ipAddress port protocol username password`
- Example arguments: `192.168.2.1 8083 http admin admin` 

## Dependencies

- Z-Way library for Java (contained as JAR file)

- Gson (com.google.code.gson:gson:2.4 - https://github.com/google/gson)
- Apache Commons Lang (org.apache.commons:commons-lang3:3.4 - http://commons.apache.org/proper/commons-lang/)
- Jetty :: Asynchronous HTTP Client (org.eclipse.jetty:jetty-client:9.3.11.v20160721 - http://www.eclipse.org/jetty)
- Jetty :: Http Utility (org.eclipse.jetty:jetty-http:9.3.11.v20160721 - http://www.eclipse.org/jetty)
- Jetty :: IO Utility (org.eclipse.jetty:jetty-io:9.3.11.v20160721 - http://www.eclipse.org/jetty)
- Jetty :: Utilities (org.eclipse.jetty:jetty-util:9.3.11.v20160721 - http://www.eclipse.org/jetty)
- Jetty :: WebSocket :: API (org.eclipse.jetty.websocket:websocket-api:9.3.12.v20160915 - http://www.eclipse.org/jetty)
- Jetty :: WebSocket :: Client (org.eclipse.jetty.websocket:websocket-client:9.3.12.v20160915 - http://www.eclipse.org/jetty)
- Jetty :: WebSocket :: Common (org.eclipse.jetty.websocket:websocket-common:9.3.12.v20160915 - http://www.eclipse.org/jetty)
- SLF4J API Module (org.slf4j:slf4j-api:1.7.21 - http://www.slf4j.org)
- SLF4J Simple Binding (org.slf4j:slf4j-simple:1.7.21 - http://www.slf4j.org)

## License

Copyright (C) 2016 by [Software-Systementwicklung Zwickau](http://www.software-systementwicklung.de/) Research Group

All rights reserved. This program and the accompanying materials
are made available under the terms of the Eclipse Public License v1.0
which accompanies this distribution, and is available at
http://www.eclipse.org/legal/epl-v10.html

This project uses 3rd party tools. You can find the list of 3rd party tools including their authors and licenses [here](LICENSE-3RD-PARTY.txt).

<br>
<img src="doc/BMWi_4C_Gef_en.jpg" width="200">