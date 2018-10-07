package com.nickwinegar.spacexdemo;

import android.arch.core.executor.testing.InstantTaskExecutorRule;
import android.arch.lifecycle.Observer;
import android.net.Uri;
import android.net.UrlQuerySanitizer;

import com.nickwinegar.spacexdemo.api.SpaceXService;
import com.nickwinegar.spacexdemo.di.AppComponent;
import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.model.LaunchLinks;
import com.nickwinegar.spacexdemo.model.LaunchSite;
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
    @Mock private UrlQuerySanitizer mockUrlSanitizer;
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
        viewModel.urlSanitizer = mockUrlSanitizer;
    }

    @Test
    public void launchDetailViewModel_InitializesOnCreate() {
        Assert.assertNotNull("view model has been initialized", viewModel);
    }

    @Test
    public void launchDetailViewModel_LaunchReturnedByApiOnSuccess() {
        // Given the device is connected and Space X API call succeeds
        List<Launch> testLaunches = getTestLaunches();
        when(mockConnectionService.isConnected()).thenReturn(true);
        when(mockUrlSanitizer.getValue("v")).thenReturn("ABcdEFgH");
        when(mockSpaceXService.getLaunch(testFlightNumber)).thenReturn(Observable.just(testLaunches));
        viewModel.getLaunch().observeForever(launchObserver);
        viewModel.getErrorMessage().observeForever(errorObserver);

        // when launches are requested from the VM
        viewModel.loadPreviousLaunch(testFlightNumber);

        // launch observers should be notified on change, and no error should occur
        verify(errorObserver, never()).onChanged(any());
        verify(launchObserver, atLeastOnce()).onChanged(testLaunches.get(0));
    }

    @Test
    public void launchDetailViewModel_GetLaunchErrorMessageWhenNotConnected() {
        // Given the device does not have an available network connection
        when(mockConnectionService.isConnected()).thenReturn(false);
        when(mockApplication.getString(R.string.launch_network_unavailable_message)).thenReturn("Unable to get launch, network is unavailable.");
        viewModel.getErrorMessage().observeForever(errorObserver);

        // When launches are requested from the VM
        viewModel.getLaunch();
        viewModel.loadPreviousLaunch(testFlightNumber);

        // An error notification should be sent
        verify(errorObserver).onChanged("Unable to get launch, network is unavailable.");
    }

    @Test
    public void launchDetailViewModel_GetLaunchErrorMessageWhenApiCallFails() {
        // Given the device is connected but the Space X api call fails
        when(mockConnectionService.isConnected()).thenReturn(true);
        when(mockSpaceXService.getLaunch(testFlightNumber)).thenReturn(Observable.error(new Exception("Test Exception")));
        viewModel.getErrorMessage().observeForever(errorObserver);

        // When launches are requested from the VM
        viewModel.getLaunch();
        viewModel.loadPreviousLaunch(testFlightNumber);

        // An error notification should be sent
        verify(errorObserver).onChanged("Error retrieving launch information.");
    }

    @Test
    public void launchDetailViewModel_upcomingLaunchReturnedByApiOnSuccess() {
        // Given the device is connected and Space X API call succeeds
        List<Launch> testLaunches = getTestLaunches();
        when(mockConnectionService.isConnected()).thenReturn(true);
        when(mockSpaceXService.getUpcomingLaunches()).thenReturn(Observable.just(testLaunches));
        viewModel.getLaunch().observeForever(launchObserver);
        viewModel.getErrorMessage().observeForever(errorObserver);

        // when launches are requested from the VM
        viewModel.loadUpcomingLaunch(testFlightNumber);

        // launch observers should be notified on change, and no error should occur
        verify(errorObserver, never()).onChanged(any());
        verify(launchObserver, atLeastOnce()).onChanged(testLaunches.get(0));
    }

    @Test
    public void launchDetailViewModel_getVideoWebUriReturnsExpectedUri() {
        // Given the device is connected and Space X API call succeeds
        List<Launch> testLaunches = getTestLaunches();
        when(mockConnectionService.isConnected()).thenReturn(true);
        when(mockSpaceXService.getLaunch(testFlightNumber)).thenReturn(Observable.just(testLaunches));
        viewModel.getLaunch()
                .observeForever(launch -> {
                    // When web uri is requested from the VM
                    Uri uri = viewModel.getVideoWebUri();

                    // web uri is correct
                    Assert.assertEquals("http://www.youtube.com/watch?v=ABcdEFgH", uri.toString());
                });
        viewModel.loadPreviousLaunch(testFlightNumber);
    }

    @Test
    public void launchDetailViewModel_getVideoAppUriReturnsExpectedUri() {
        // Given the device is connected and Space X API call succeeds
        List<Launch> testLaunches = getTestLaunches();
        when(mockConnectionService.isConnected()).thenReturn(true);
        when(mockSpaceXService.getLaunch(testFlightNumber)).thenReturn(Observable.just(testLaunches));
        viewModel.getLaunch()
                .observeForever(launch -> {
                    // When web uri is requested from the VM
                    Uri uri = viewModel.getVideoAppUri();

                    // web uri is correct
                    Assert.assertEquals("vnd.youtube:ABcdEFgH", uri.toString());
                });
        viewModel.loadPreviousLaunch(testFlightNumber);
    }

    public List<Launch> getTestLaunches() {
        Launch testLaunch = new Launch(999, "", null, new LaunchLinks("", "https://www.youtube.com/watch?v=ABcdEFgH", ""), new Date().getTime(), true, new LaunchSite("tst", "Test launchsite"));
        List<Launch> testLaunches = new ArrayList<>();
        testLaunches.add(testLaunch);
        return testLaunches;
    }
}
