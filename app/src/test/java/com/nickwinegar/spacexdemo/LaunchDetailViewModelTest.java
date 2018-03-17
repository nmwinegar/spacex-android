package com.nickwinegar.spacexdemo;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;

import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.di.AppComponent;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.ui.launch.launchDetail.LaunchDetailViewModel;
import com.nickwinegar.spacexdemo.util.ConnectionService;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.plugins.RxJavaPlugins;
import io.reactivex.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class LaunchDetailViewModelTest {
    @Rule public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();
    @Mock private SpaceXDemoApp mockApplication;
    @Mock private AppComponent mockAppComponent;
    @Mock private ConnectionService mockConnectionService;
    @Mock private SpaceXService mockSpaceXService;
    @Mock private Observer<String> errorObserver;
    @Mock private Observer<Launch> launchObserver;

    private LaunchDetailViewModel viewModel;
    private int testFlightNumber = 999;

    @Before
    public void setup() {
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        when(mockApplication.getAppComponent()).thenReturn(mockAppComponent);
        viewModel = new LaunchDetailViewModel(mockApplication);
        viewModel.connectionService = mockConnectionService;
        viewModel.spaceXService = mockSpaceXService;
    }

    @Test
    public void launchListViewModel_InitializesOnCreate() {
        Assert.assertNotNull("view model has been initialized", viewModel);
    }

    @Test
    public void launchListViewModel_LaunchReturnedByApiOnSuccess() {
        // Given the device is connected and Space X API call succeeds
        List<Launch> testLaunches = getTestLaunches();
        when(mockConnectionService.isConnected()).thenReturn(true);
        when(mockSpaceXService.getLaunch(testFlightNumber)).thenReturn(Observable.just(testLaunches));
        viewModel.getLaunch(testFlightNumber).observeForever(launchObserver);
        viewModel.getErrorMessage().observeForever(errorObserver);

        // when launches are requested from the VM
        viewModel.getLaunch(testFlightNumber);

        // launch observers should be notified on change, and no error should occur
        verify(errorObserver, never()).onChanged(any());
        verify(launchObserver, atLeastOnce()).onChanged(testLaunches.get(0));
    }

    @Test
    public void launchListViewModel_GetLaunchErrorMessageWhenNotConnected() {
        // Given the device does not have an available network connection
        when(mockConnectionService.isConnected()).thenReturn(false);
        viewModel.getErrorMessage().observeForever(errorObserver);

        // When launches are requested from the VM
        viewModel.getLaunch(testFlightNumber);

        // An error notification should be sent
        verify(errorObserver).onChanged("Unable to get launch, network is unavailable.");
    }

    @Test
    public void launchListViewModel_GetLaunchErrorMessageWhenApiCallFails() {
        // Given the device is connected but the Space X api call fails
        when(mockConnectionService.isConnected()).thenReturn(true);
        when(mockSpaceXService.getLaunch(testFlightNumber)).thenReturn(Observable.error(new Exception("Test Exception")));
        viewModel.getErrorMessage().observeForever(errorObserver);

        // When launches are requested from the VM
        viewModel.getLaunch(testFlightNumber);

        // An error notification should be sent
        verify(errorObserver).onChanged("Error retrieving launch information.");
    }

    public List<Launch> getTestLaunches() {
        Launch testLaunch = new Launch();
        testLaunch.flightNumber = 999;
        testLaunch.launchDateTimestamp = new Date().getTime();
        List<Launch> testLaunches = new ArrayList<>();
        testLaunches.add(testLaunch);
        return testLaunches;
    }
}
