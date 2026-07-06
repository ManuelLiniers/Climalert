package com.utn.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

@Service
public class WeatherApiService {

  @Value("${weatherapi.key}")
  private String apiKey;

  private final RestTemplate restTemplate;

  public WeatherApiService(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  public String obtenerClimaActual() {
    // Integración vía REST con WeatherAPI usando una ubicación fija (CABA).
    String url = "https://api.weatherapi.com/v1/current.json?key=" + apiKey + "&q=CABA";
    return restTemplate.getForObject(url, String.class);
  }
}