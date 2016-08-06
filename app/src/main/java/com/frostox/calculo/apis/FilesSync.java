package com.frostox.calculo.apis;

import com.frostox.calculo.entities.File;
import com.frostox.calculo.entities.wrappers.FileListWrapper;

import java.util.List;

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
