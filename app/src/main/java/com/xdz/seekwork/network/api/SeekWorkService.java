package com.xdz.seekwork.network.api;

import com.xdz.seekwork.network.entity.machineinfo.MMachineInfo;

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

}
