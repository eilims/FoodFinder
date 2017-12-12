package com.eilims.danielb.foodfinder;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by DanielB on 10/2/2016.
 */

public class BusinessSelector {

    YelpAPIFactory apiFactory;
    YelpAPI yelpAPI;
    String client_id = "xVPgWOQ7NT9tlNM0zvI1Rw";
    String client_secret = "xxx";
    String token = "iXibWHqxTbfJ0juqXnXAd866aOFrrKVl";
    String token_secret = "xxx";
    String catagories = "restaurants"; //TODO will be set by appropriate method later
    Location mCurrentLocation;
    Response<SearchResponse> response = null;

    public BusinessSelector(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
        apiFactory = new YelpAPIFactory(
                client_id,
                client_secret,
                token,
                token_secret
        );
        yelpAPI = apiFactory.createAPI();
    }

    private SearchResponse getRestaurantList(){
        SearchResponse search = null;
        HashMap<String, String> parameters = new HashMap<>();
        parameters.put("category_filter", catagories);
        parameters.put("limit", "10");
        parameters.put("radius_filter", "3200");
        parameters.put("sort_by", "distance");
        parameters.put("term", "food");
//        CoordinateOptions coordinate = CoordinateOptions.builder()
//                .latitude(mCurrentLocation.getLatitude())
//                .longitude(mCurrentLocation.getLongitude()).build();
//        Call<SearchResponse> call = yelpAPI.search(coordinate,parameters);
//        response = call.execute();
        MyTask myTask = new MyTask();
        myTask.execute(parameters);
        while(response == null){

        }
        search = response.body();
        return search;
    }

    public Business returnBusiness(){ //TODO add parameter inputs from user
        Business selectedBusiness = null;
        SearchResponse response = getRestaurantList();
        Random numGen = new Random();
        int number = numGen.nextInt(11);
        selectedBusiness = response.businesses().get(number);
        return selectedBusiness;
    }

    private class MyTask extends AsyncTask<HashMap,Integer, Integer>{


        @Override
        protected Integer doInBackground(HashMap... params) {
            CoordinateOptions coordinate = CoordinateOptions.builder()
                .latitude(mCurrentLocation.getLatitude())
                .longitude(mCurrentLocation.getLongitude()).build();
            Call<SearchResponse> call = yelpAPI.search(coordinate,params[0]);
            try {
                response = call.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }


}
