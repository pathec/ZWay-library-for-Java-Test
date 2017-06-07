/**
 * Copyright (C) 2016 by Software-Systementwicklung Zwickau Research Group
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fh_zwickau.informatik.sensor;

import java.io.File;
import java.net.HttpCookie;
import java.nio.file.Paths;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.MultiPartContentProvider;
import org.eclipse.jetty.client.util.PathContentProvider;
import org.eclipse.jetty.client.util.StringContentProvider;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.http.HttpStatus;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import de.fh_zwickau.informatik.sensor.model.login.LoginForm;

public class IconUpload {

    public static void main(String[] args) {
        try {
            HttpClient mHttpClient = new HttpClient();

            /**
             * Start HTTP client
             */
            try {
                mHttpClient.start();
            } catch (Exception e) {
                System.out.println("Can not start HttpClient: " + e.getMessage());
                System.exit(-1);
            }

            /**
             * Login
             */
            LoginForm loginForm = new LoginForm(true, "admin", "8s10f90g", false, 1);

            Request loginRequest = mHttpClient.newRequest("http://192.168.178.26:8083/ZAutomation/api/v1/login")
                    .method(HttpMethod.POST).header(HttpHeader.ACCEPT, "application/json")
                    .header(HttpHeader.CONTENT_TYPE, "application/json")
                    .content(new StringContentProvider(new Gson().toJson(loginForm)), "application/json");

            ContentResponse loginResponse = loginRequest.send();

            if (loginResponse.getStatus() != HttpStatus.OK_200) {
                System.out.println(
                        "Login request failed: " + loginResponse.getStatus() + " " + loginResponse.getReason());
                System.exit(-1);
            }

            String zwaySessionId = null;
            try {
                JsonObject responseDataAsJson = new Gson()
                        .fromJson(loginResponse.getContentAsString(), JsonObject.class).get("data").getAsJsonObject();

                zwaySessionId = responseDataAsJson.get("sid").getAsString(); // extract SID field
            } catch (JsonParseException e) {
                System.out.println("Login request failed: " + e.getMessage());
                System.exit(-1);
            }

            /**
             * Icon upload
             */
            File image = new File("");

            MultiPartContentProvider multiPart = new MultiPartContentProvider();
            multiPart.addFilePart("file", image.getName(), new PathContentProvider(Paths.get(image.getPath())), null);
            multiPart.close();
            ContentResponse response = mHttpClient
                    .newRequest("http://192.168.178.26:8083/ZAutomation/api/v1/icons/upload").method(HttpMethod.POST)
                    .header(HttpHeader.ACCEPT, "application/json").cookie(new HttpCookie("ZWAYSession", zwaySessionId))
                    .content(multiPart).send();

            System.out.println("Icon upload request: " + response.getContentAsString());

            mHttpClient.stop();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }
}
