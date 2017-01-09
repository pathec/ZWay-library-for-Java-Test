/**
 * Copyright (C) 2016 by Software-Systementwicklung Zwickau Research Group
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.fh_zwickau.informatik.sensor;

import java.util.Date;
import java.util.List;
import java.util.Map;

import de.fh_zwickau.informatik.sensor.model.devicehistory.DeviceHistory;
import de.fh_zwickau.informatik.sensor.model.devicehistory.DeviceHistoryList;
import de.fh_zwickau.informatik.sensor.model.devices.Device;
import de.fh_zwickau.informatik.sensor.model.devices.DeviceList;
import de.fh_zwickau.informatik.sensor.model.instances.Instance;
import de.fh_zwickau.informatik.sensor.model.instances.InstanceList;
import de.fh_zwickau.informatik.sensor.model.locations.Location;
import de.fh_zwickau.informatik.sensor.model.locations.LocationList;
import de.fh_zwickau.informatik.sensor.model.modules.ModuleList;
import de.fh_zwickau.informatik.sensor.model.namespaces.NamespaceList;
import de.fh_zwickau.informatik.sensor.model.notifications.Notification;
import de.fh_zwickau.informatik.sensor.model.notifications.NotificationList;
import de.fh_zwickau.informatik.sensor.model.profiles.Profile;
import de.fh_zwickau.informatik.sensor.model.profiles.ProfileList;
import de.fh_zwickau.informatik.sensor.model.zwaveapi.controller.ZWaveController;
import de.fh_zwickau.informatik.sensor.model.zwaveapi.devices.ZWaveDevice;

public class ZWayApiClient implements IZWayApiCallbacks {

    public void runTest(String ipAddress, Integer port, String protocol, String username, String password,
            Integer remoteId, Boolean useRemoteService) {
        IZWayApi mZWayApi = new ZWayApiHttp(ipAddress, port, protocol, username, password, remoteId, useRemoteService,
                this);

        // Login Test
        String sid = mZWayApi.getLogin();
        System.out.println("*** Get login ***");
        if (sid != null) {
            System.out.println(">>> " + sid + "\n");
        } else {
            return;
        }

        // GetDevices Test
        System.out.println("*** Get (virtual) devices ***");

        DeviceList deviceList = mZWayApi.getDevices();
        if (deviceList != null) {
            for (Device device : deviceList.getDevices()) {
                System.out.println(">>> " + device.getDeviceId());
            }

            System.out.println();

            System.out.println("*** Get devices group by node id ***");

            for (Map.Entry<Integer, List<Device>> realDevice : deviceList.getDevicesGroupByNodeId().entrySet()) {
                System.out.println(">>> Node ID: " + realDevice.getKey());
                System.out.println(">>> Devices: " + realDevice.getValue().size() + " associated virtual devices");

                ZWaveDevice zwaveDevice = mZWayApi.getZWaveDevice(realDevice.getKey());
                if (zwaveDevice != null) {
                    System.out.println(
                            ">>> ZWave device name: " + zwaveDevice.getData().getGivenName().getValue() + "\n");
                }
            }
        }

        // GetInstances Test
        System.out.println("*** Get instances ***");

        InstanceList instanceList = mZWayApi.getInstances();
        if (instanceList != null) {
            for (Instance instance : instanceList.getInstances()) {
                System.out.println(">>> " + instance.getModuleId());
            }
        }

        // GetNotifications Test
        System.out.println();
        System.out.println("*** Get notifications ***");

        NotificationList notificationList = mZWayApi.getNotifications((int) ((new Date().getTime() / 1000) - (3600))); // Max.
                                                                                                                       // 1
                                                                                                                       // Hour
        if (notificationList != null) {
            for (Notification notification : notificationList.getNotifications()) {
                System.out.println(">>> " + notification);
            }
        }

        // GetController Test
        System.out.println("*** Get controller ***");

        ZWaveController zwaveController = mZWayApi.getZWaveController();
        if (zwaveController != null) {
            System.out.println(zwaveController);
        }

        // GetDevices Test (Asynchron)
        // System.out.println("*** Get (virtual) devices asynchron ***");
        // mZWayApi.getDevices(new IZWayCallback<DeviceList>() {
        //
        // @Override
        // public void onSuccess(DeviceList deviceList) {
        // for (Device device : deviceList.getDevices()) {
        // System.out.println(">>> " + device.getDeviceId());
        // }
        // }
        // });

        // System.out.println("*** Get device ***");
        // mZWayApi.getZWaveDevice(4, new IZWayCallback<ZWaveDevice>() {
        //
        // @Override
        // public void onSuccess(ZWaveDevice device) {
        // System.out.println(device.getInstances().get0());
        // }
        // });

        System.out.println();
        System.out.println("*** WebSocket ***");

        String protocolWebSocket = protocol.equals("http") ? "ws" : "wss";
        IZWayApi mZWayApiWebsocket = new ZWayApiWebSocket(ipAddress, port, protocolWebSocket, username, password,
                remoteId, useRemoteService, this, new IZWayApiWebSocketCallbacks() {

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("WebSocket error: " + throwable.getMessage());

                    }

                    @Override
                    public void onConnect() {
                        System.out.println("WebSocket connect");
                    }

                    @Override
                    public void onClose() {
                        System.out.println("WebSocket close.");
                    }
                });
        ((ZWayApiWebSocket) mZWayApiWebsocket).connect();

        System.out.println();
        System.out.println("*** Finish ***");

        try {
            System.in.read();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void apiError(String arg0, boolean arg1) {
        System.out.println("Z-Way API error: " + arg0 + "\n");
    }

    @Override
    public void authenticationError() {
        System.out.println("Z-Way API authentication error\n");
    }

    @Override
    public void responseFormatError(String arg0, boolean arg1) {
        System.out.println("Z-Way API response format error: " + arg0 + "\n");
    }

    @Override
    public void deleteInstanceResponse(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteLocationResponse(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void deleteProfileResponse(boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getDeviceCommandResponse(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getDeviceHistoriesResponse(DeviceHistoryList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getDeviceHistoryResponse(DeviceHistory arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getDeviceResponse(Device arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getDevicesResponse(DeviceList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getInstanceResponse(Instance arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getInstancesResponse(InstanceList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getLocationResponse(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getLocationsResponse(LocationList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getLoginResponse(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getModulesResponse(ModuleList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getNamespacesResponse(NamespaceList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getNotificationResponse(Notification arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getNotificationsResponse(NotificationList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getProfileResponse(Profile arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getProfilesResponse(ProfileList arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getRestartResponse(Boolean arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getStatusResponse(String arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getZWaveDeviceResponse(ZWaveDevice arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void httpStatusError(int arg0, String arg1, boolean arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postDeviceResponse(Device arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postInstanceResponse(Instance arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postLocationResponse(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void postProfileResponse(Profile arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putInstanceResponse(Instance arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putLocationResponse(Location arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putNotificationResponse(Notification arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void putProfileResponse(Profile arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void getZWaveControllerResponse(ZWaveController arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void message(int arg0, String arg1) {
        System.out.println("Z-Way API message: " + arg1 + "\n");

    }
}
