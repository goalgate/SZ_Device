package com.sz_device.Tools;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by zbsz on 2017/9/21.
 */
@Entity
public class UnUploadPackage {
    @Id(autoincrement = true)
    private Long id;

    private int method;

    private String jsonData;

    private Boolean upload;

    @Generated(hash = 603211533)
    public UnUploadPackage(Long id, int method, String jsonData, Boolean upload) {
        this.id = id;
        this.method = method;
        this.jsonData = jsonData;
        this.upload = upload;
    }

    @Generated(hash = 1137104450)
    public UnUploadPackage() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getMethod() {
        return this.method;
    }

    public void setMethod(int method) {
        this.method = method;
    }

    public String getJsonData() {
        return this.jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public Boolean getUpload() {
        return this.upload;
    }

    public void setUpload(Boolean upload) {
        this.upload = upload;
    }


}
