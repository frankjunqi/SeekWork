package com.xdz.seekwork.network.api;

import com.xdz.seekwork.network.entity.seekwork.MMachineInfo;

import retrofit2.Call;
import retrofit2.http.GET;
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
}
