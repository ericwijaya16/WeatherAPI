/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.testingprojectnb15.WeatherProject;

/**
 *
 * @author ericw
 */
    public class ResultModel {
        
        private String address;
        private String time;
        private double temp;
        private String weather;
        
        public ResultModel(String address, String time, double temp, String weather) {
            this.address = address;
            this.time = time;
            this.temp = temp;
            this.weather = weather;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }

        public double getTemp() {
            return temp;
        }

        public void setTemp(double temp) {
            this.temp = temp;
        }

        public String getWeather() {
            return weather;
        }

        public void setWeather(String weather) {
            this.weather = weather;
        }
    
    }
