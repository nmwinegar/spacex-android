package com.nickwinegar.spacexdemo;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nickwinegar.spacexdemo.util.ConnectionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionServiceTest {
    @Mock
    private SpaceXDemoApp mockApplication;
    @Mock
    private ConnectivityManager mockConnectivityManager;
    @Mock
    private NetworkInfo mockNetworkInfo;

    private ConnectionService connectionService;

    @Before
    public void setup() {
        when(mockApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockConnectivityManager);
        connectionService = new ConnectionService(mockApplication);
    }

    @Test
    public void connectionService_WhenConnectivityManagerIsNullReturnFalse() {
        when(mockApplication.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(null);

        boolean result = connectionService.isConnected();
        Assert.assertFalse(result);
    }

    @Test
    public void connectionService_WhenActiveNetworkIsNullReturnFalse() {
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(null);

        boolean result = connectionService.isConnected();
        Assert.assertFalse(result);
    }

    @Test
    public void connectionService_WhenActiveNetworkIsNotConnectedReturnFalse() {
        when(mockNetworkInfo.isConnectedOrConnecting()).thenReturn(false);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);

        boolean result = connectionService.isConnected();
        Assert.assertFalse(result);
    }

    @Test
    public void connectionService_WhenActiveNetworkIsConnectedReturnTrue() {
        when(mockNetworkInfo.isConnectedOrConnecting()).thenReturn(true);
        when(mockConnectivityManager.getActiveNetworkInfo()).thenReturn(mockNetworkInfo);

        boolean result = connectionService.isConnected();
        Assert.assertTrue(result);
    }
}
