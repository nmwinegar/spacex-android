package com.nickwinegar.spacexdemo.api;

import com.nickwinegar.spacexdemo.model.Launch;
import com.nickwinegar.spacexdemo.model.Launchpad;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SpaceXService {
    @Headers("Cache-Control: max-stale=3600")
    @GET("launches")
    Observable<List<Launch>> getLaunches();

    @Headers("Cache-Control: max-stale=3600")
    @GET("launches/upcoming")
    Observable<List<Launch>> getUpcomingLaunches();

    @Headers("Cache-Control: max-stale=3600")
    @GET("launches")
    Observable<List<Launch>> getLaunch(@Query("flight_number") int flightNumber);

    @Headers("Cache-Control: max-stale=3600")
    @GET("launchpads/{id}")
    Observable<Launchpad> getLaunchpad(@Path("id") String launchpadId);
}
