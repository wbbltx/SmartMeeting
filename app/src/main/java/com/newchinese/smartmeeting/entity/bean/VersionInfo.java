package com.newchinese.smartmeeting.entity.bean;

/**
 * Created by Administrator on 2017/11/1 0001.
 */

public class VersionInfo {

//              "version": "2.0",
//             "create_date": "2017-11-08 11:32:31",
//             "update_url": "111",
//             "description": "111",
//             "interiorAppUrl": "http://182.92.99.12:8080/images/M00/00/68/tlxjDFoCesyAPrbbAAACJUGgFsg045.txt"

    private String version;
    private String create_date;
    private String interiorAppUrl;
    private String description;

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getCreate_date() {
        return create_date;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public String getInteriorAppUrl() {
        return interiorAppUrl;
    }

    public void setInteriorAppUrl(String interiorAppUrl) {
        this.interiorAppUrl = interiorAppUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "version=" + version +
                ", create_date='" + create_date + '\'' +
                ", interiorAppUrl='" + interiorAppUrl + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
