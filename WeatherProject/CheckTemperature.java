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
public class CheckTemperature implements MenuInterface{
    private static HashMap<String, String> param = new HashMap<>();
    
    @Override
    public void execute() {
        System.out.println("Checking temperature...");
        param = new HashMap();
        param.put("hourly", "temperature_2m");
        WeatherUtils weather = new WeatherUtils("temperature", param);
        weather.fetchWeatherData();
    }
    
}
