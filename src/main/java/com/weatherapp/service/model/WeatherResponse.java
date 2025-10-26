package com.weatherapp.service.model;

import java.util.List;

public class WeatherResponse {
    public Coord coord;
    public List<Weather> weather;
    public String base;
    public Main main;
    public long dt;
    public Sys sys;
    public String name;

    public static class Coord { public double lon; public double lat; }
    public static class Weather { public int id; public String main; public String description; public String icon; }
    public static class Main { public double temp; public double feels_like; public double temp_min; public double temp_max; public int pressure; public int humidity; }
    public static class Sys { public int type; public int id; public String country; public long sunrise; public long sunset; }
}
