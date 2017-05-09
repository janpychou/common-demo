package io.sugo.kafka.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by gary on 16-10-6.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IpInfo
{
    private String country;
    private String area;
    private String region;
    private String isp;
    private String city;

    public IpInfo() {}
    public IpInfo(String country) {
        this.country = country;
    }
    public IpInfo(String country, String region, String city, String isp) {
        this.country = country;
        this.region =region;
        this.city = city;
        this.isp = isp;
    }
    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getIsp() {
        return isp;
    }

    public void setIsp(String isp) {
        this.isp = isp;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
