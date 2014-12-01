package com.insready.drupalcloud;

/**
 * Created by skyred on 12/1/14.
 */


import java.util.Map;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.QueryMap;


public interface RESTfulWebServices {
    @GET("/node/{nid}")
    Response nodeGet(@Path("nid") int nid);

    @GET("/node/{nid}")
    void nodeGet(@Path("nid") int nid, Callback<Response> cb);

    @GET("/comment/{cid}")
    Response commentGet(@Path("cid") int cid);

    @GET("/comment/{cid}")
    void commentGet(@Path("cid") int cid, Callback<Response> cb);

    @GET("/{entity_name}/{eid}")
    Response entityGet(@Path("eid") int eid);

    @GET("/{entity_name}/{eid}")
    void entityGet(@Path("eid") int eid, Callback<Response> cb);

    @GET("/user/{uid}")
    Response userGet(@Path("uid") int uid);

    @GET("/user/{uid}")
    void userGet(@Path("uid") int uid, Callback<Response> cb);

    @GET("/views/{view_name}/{display_name}")
    Response userGet(@Path("view_name") String view_name, @Path("display_name") String display_name, @QueryMap Map<String, String> options);

    @GET("/views/{view_name}/{display_name}")
    void userGet(@Path("view_name") String view_name, @Path("display_name") String display_name, @QueryMap Map<String, String> options, Callback<Response> cb);

}
