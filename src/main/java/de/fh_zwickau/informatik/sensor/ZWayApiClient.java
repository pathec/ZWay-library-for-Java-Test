/**
 * Copyright (C) 2016 by Software-Systementwicklung Zwickau Research Group
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fh_zwickau.informatik.sensor;

import static de.fh_zwickau.informatik.sensor.ZWayConstants.*;

import java.io.UnsupportedEncodingException;
import java.net.HttpCookie;
import java.net.URLEncoder;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.fh_zwickau.informatik.sensor.model.devices.DeviceCommand;
import de.fh_zwickau.informatik.sensor.model.devices.types.SwitchBinary;
import de.fh_zwickau.informatik.sensor.model.login.LoginForm;

public class ZWayApiClient {

    public void runTest(String ipAddress, Integer port, String protocol, String username, String password)
            throws InterruptedException, TimeoutException, ExecutionException {

        HttpClient httpClient = new HttpClient();

        /**
         * Start HTTP client
         */
        try {
            httpClient.start();
        } catch (Exception e) {
            System.out.println("Can not start HttpClient: " + e.getMessage());
            System.exit(-1);
        }

        /**
         * Login
         */
        LoginForm loginForm = new LoginForm(true, username, password, false, 1);

        Request loginRequest = httpClient
                .newRequest(protocol + "://" + ipAddress + ":" + port + "/ZAutomation/api/v1/" + PATH_LOGIN)
                .method(HttpMethod.POST).header(HttpHeader.ACCEPT, "application/json")
                .header(HttpHeader.CONTENT_TYPE, "application/json")
                .content(new StringContentProvider(new Gson().toJson(loginForm)), "application/json");

        ContentResponse loginResponse = loginRequest.send();

        if (loginResponse.getStatus() != HttpStatus.OK_200) {
            System.out.println("Login request failed: " + loginResponse.getStatus() + " " + loginResponse.getReason());
            System.exit(-1);
        }

        String zwaySessionId = null;
        try {
            JsonObject responseDataAsJson = new Gson().fromJson(loginResponse.getContentAsString(), JsonObject.class)
                    .get("data").getAsJsonObject();

            zwaySessionId = responseDataAsJson.get("sid").getAsString(); // extract SID field
        } catch (JsonParseException e) {
            System.out.println("Login request failed: " + e.getMessage());
            System.exit(-1);
        }

        if (zwaySessionId == null) {
            System.out.println("Authentication failed!");
            System.exit(-1);
        }

        /**
         * Device
         */

        String deviceId = ""; // TODO

        Request deviceRequest = httpClient
                .newRequest(
                        protocol + "://" + ipAddress + ":" + port + "/ZAutomation/api/v1/" + PATH_DEVICES + deviceId)
                .method(HttpMethod.GET).header(HttpHeader.ACCEPT, "application/json")
                .header(HttpHeader.CONTENT_TYPE, "application/json")
                .cookie(new HttpCookie("ZWAYSession", zwaySessionId));

        ContentResponse deviceResponse = deviceRequest.send();

        if (deviceResponse.getStatus() != HttpStatus.OK_200) {
            System.out
                    .println("Device request failed: " + deviceResponse.getStatus() + " " + deviceResponse.getReason());
            System.exit(-1);
        }

        SwitchBinary device = null;
        try {
            // Response -> String -> Json -> extract data field
            JsonObject deviceAsJson = new Gson().fromJson(deviceResponse.getContentAsString(), JsonObject.class)
                    .get("data").getAsJsonObject();

            device = new Gson().fromJson(deviceAsJson, SwitchBinary.class);
            device.setCommandHandler(null); // !!! Don't call device command's, for example: device.on()

        } catch (JsonParseException e) {
            System.out.println("Device request failed: " + e.getMessage());
            System.exit(-1);
        }

        if (device == null) {
            System.out.println("Device request failed!");
            System.exit(-1);
        }

        /**
         * Device Command (on/off for binary switch)
         */
        if (device != null) {
            DeviceCommand command = new DeviceCommand(device.getDeviceId(), "off", null);

            System.out.println(protocol + "://" + ipAddress + ":" + port + "/ZAutomation/api/v1/"
                    + buildGetDeviceCommandPath(command));

            // TODO HTTP request
        }

        /**
         * Stop HTTP client
         */
        try {
            httpClient.stop();
        } catch (Exception e) {
            System.out.println("Unable to stop HttpClient: " + e.getMessage());
        }

        try {
            System.in.read();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String buildGetDeviceCommandPath(DeviceCommand command) {
        String path = StringUtils.replace(PATH_DEVICES_COMMAND, "{vDevName}", command.getDeviceId());
        path = StringUtils.replace(path, "{command}", command.getCommand());

        if (command.getParams() != null) {
            path += "?";

            Integer index = 0;
            for (Entry<String, String> entry : command.getParams().entrySet()) {
                if (index > 0) {
                    path += "&";
                }
                try {
                    path += URLEncoder.encode(entry.getKey(), "UTF-8") + "="
                            + URLEncoder.encode(entry.getValue(), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    System.out.println("Device command parameter invalid: " + e.getMessage());
                    return null;
                }
                index++;
            }
        }

        return path;
    }
}
