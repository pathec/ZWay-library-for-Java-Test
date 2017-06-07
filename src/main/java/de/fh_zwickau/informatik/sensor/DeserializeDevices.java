/**
 * Copyright (C) 2016 by Software-Systementwicklung Zwickau Research Group
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fh_zwickau.informatik.sensor;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.fh_zwickau.informatik.sensor.model.devices.DeviceListDeserializer;

public class DeserializeDevices {

    public static void main(String[] args) {
        try {
            HttpClient mHttpClient = new HttpClient();
            mHttpClient.setConnectTimeout(5000);
            mHttpClient.start();

            Request request = mHttpClient.newRequest("http://localhost:8085/ZAutomation/api/v1/devices")
                    .method(HttpMethod.GET).header(HttpHeader.ACCEPT, "application/json")
                    .header(HttpHeader.CONTENT_TYPE, "application/json");

            ContentResponse response = request.send();

            try {
                JsonObject responseDataAsJson = new Gson().fromJson(response.getContentAsString(), JsonObject.class)
                        .get("data").getAsJsonObject();

                JsonArray devicesAsJson = responseDataAsJson.get("devices").getAsJsonArray();

                new DeviceListDeserializer().deserializeDeviceList(devicesAsJson, null);
            } catch (JsonParseException e) {
                System.out.println("Unexpected response format: " + e.getMessage());
            } finally {
                mHttpClient.stop();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
