package com.xdz.seekwork.network.api;

import com.xdz.seekwork.network.entity.seekwork.MMachineInfo;
import com.xdz.seekwork.network.entity.seekwork.MPickQueryByRFID;
import com.xdz.seekwork.network.entity.seekwork.MReplenish;
import com.xdz.seekwork.network.entity.seekwork.MRoad;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 */
public interface SeekWorkService {

    @GET("ManageOperate/GetMachineInfo")
    Call<SrvResult<MMachineInfo>> getMachineInfo(
            @Query("vendingMachineGuid") String vendingMachineGuid
    );


    //请求服务，验证是否是管理员卡
    @GET("ManageOperate/LoginValidate")
    Call<SrvResult<Boolean>> loginValidate(
            @Query("machineCode") String machineCode,
            @Query("cardNo") String cardNo
    );

    // 货道补货
    @GET("PickUp/QueryRoad")
    Call<SrvResult<List<MRoad>>> queryRoad(
            @Query("machineCode") String machineCode
    );

    // 提交货道补货
    @Headers({
            "Accept: application/json",
            "Content-type: application/json"
    })
    @POST("ManageOperate/Replenish")
    Call<SrvResult<Boolean>> replenish(@Body MReplenish mReplenish);


    // 刷卡取货
    @GET("PickUp/PickQueryByRFID")
    Call<SrvResult<MPickQueryByRFID>> pickQueryByRFID(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("pickNum") int pickNum
    );


    // 刷卡取货成功提交确认
    @GET("PickUp/PickSuccess")
    Call<SrvResult<Boolean>> pickSuccess(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("pickNum") int pickNum
    );

    // 此货道被谁借走
    @GET("BorrowAndBack/GetLastBorrowName")
    Call<SrvResult<String>> getLastBorrowName(
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode
    );


    // 借货 还货 请求
    @GET("BorrowAndBack/AuthBorrowAndBack")
    Call<SrvResult<Boolean>> authBorrowAndBack(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("BorrowBackType") int BorrowBackType
    );

    // 借货成功确认
    @GET("BorrowAndBack/BorrowComplete")
    Call<SrvResult<Boolean>> borrowComplete(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("successCount") int successCount
    );

    // 还货成功确认
    @GET("BorrowAndBack/BackComplete")
    Call<SrvResult<Boolean>> backComplete(
            @Query("cardNo") String cardNo,
            @Query("machineCode") String machineCode,
            @Query("cabNo") String cabNo,
            @Query("roadCode") String roadCode,
            @Query("successCount") int successCount
    );
}
