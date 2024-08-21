/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.testingprojectnb15.WeatherProject;

import com.mycompany.testingprojectnb15.WeatherProject.utils.WeatherUtils;
import java.util.HashMap;

/**
 *
 * @author ericw
 */
public class CheckForecastOfAnCity implements MenuInterface{
    private static HashMap<String, String> param = new HashMap<>();
    
    @Override
    public void execute() {
        System.out.println("Checking forecast of a specific city..."); 
        param.put("hourly", "temperature_2m");
        param.put("current", "weather_code"); 
        WeatherUtils weather = new WeatherUtils("city", param);
        weather.fetchWeatherData();
    }
    
}
