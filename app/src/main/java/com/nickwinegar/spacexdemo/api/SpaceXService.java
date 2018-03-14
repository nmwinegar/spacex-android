package com.nickwinegar.spacexdemo.api;

import com.nickwinegar.spacexdemo.model.Launch;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

public interface SpaceXService {
    @Headers("Cache-Control: max-stale=3600")
    @GET("launches")
    Observable<List<Launch>> getLaunches();

    @Headers("Cache-Control: max-stale=3600")
    @GET("launches")
    Observable<List<Launch>> getLaunch(@Query("flight_number") int flightNumber);

}
