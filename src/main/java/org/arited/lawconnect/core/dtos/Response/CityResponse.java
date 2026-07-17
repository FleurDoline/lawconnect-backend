package org.arited.lawconnect.core.dtos.Response;

public class CityResponse {
    private Long id;
    private String cityName;
    private String cityRegion;

    public CityResponse() {}

    public CityResponse(Long id, String cityName, String cityRegion) {
        this.id = id;
        this.cityName = cityName;
        this.cityRegion = cityRegion;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCityRegion() { return cityRegion; }
    public void setCityRegion(String cityRegion) { this.cityRegion = cityRegion; }
}
