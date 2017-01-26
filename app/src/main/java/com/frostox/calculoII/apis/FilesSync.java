package com.frostox.calculoII.apis;

import com.frostox.calculoII.entities.wrappers.FileListWrapper;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by roger on 26/4/16.
 */
public interface FilesSync  {
    @POST("sync.php")
    Call<FileListWrapper> getDownloadableFiles(@Body FileListWrapper wrapper);


}
