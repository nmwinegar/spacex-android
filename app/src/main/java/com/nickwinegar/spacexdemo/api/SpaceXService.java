package com.nickwinegar.spacexdemo.api;

import com.nickwinegar.spacexdemo.model.Launch;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface SpaceXService {
    @GET("launches")
    Observable<List<Launch>> getLaunches();
}
