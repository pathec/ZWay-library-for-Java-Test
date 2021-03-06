/**
 * Copyright (C) 2016 by Software-Systementwicklung Zwickau Research Group
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fh_zwickau.informatik.sensor;

public class App {

    public static void main(String[] args) {
        ZWayApiClient client = new ZWayApiClient();

        try {
            Integer port = Integer.parseInt(args[1]);
            Integer remoteId = Integer.parseInt(args[5]);
            Boolean useRemoteService = false;
            if (args[6].equals("true")) {
                useRemoteService = true;
            }

            client.runTest(args[0], port, args[2], args[3], args[4], remoteId, useRemoteService);
        } catch (ArrayIndexOutOfBoundsException e0) {
            System.out.println("Error occured: wrong arguments");
            System.out.println("Possible arguments: ipAddress port protocol username password remoteId userRemote");
            System.out.println("Example: 192.168.178.26 8083 http admin admin 0 false");
            System.exit(-1);
        } catch (NumberFormatException e1) {
            System.out.println("The port must be a number");
            System.exit(-1);
        }
    }
}
