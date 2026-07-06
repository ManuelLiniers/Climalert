package com.utn.scheduler;

import com.utn.model.entities.ClimaRecord;
import com.utn.repository.ClimaRepository;
import com.utn.service.EmailNotificationService;
import com.utn.service.WeatherApiService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import org.json.JSONObject; // O usa Jackson (ObjectMapper) para parsear el JSON

@Component
public class ClimalertScheduler {

  private final WeatherApiService weatherApiService;
  private final ClimaRepository climaRepository;
  private final EmailNotificationService emailService;

  public ClimalertScheduler(WeatherApiService weatherApiService,
                            ClimaRepository climaRepository,
                            EmailNotificationService emailService) {
    this.weatherApiService = weatherApiService;
    this.climaRepository = climaRepository;
    this.emailService = emailService;
  }

  // Se ejecuta cada 5 minutos (300000 milisegundos)[cite: 1].
  @Scheduled(fixedRate = 300000)
  public void obtenerYGuardarClima() {
    String jsonRespuesta = weatherApiService.obtenerClimaActual();

    // Parseo básico del JSON (asumiendo org.json)
    JSONObject jsonObj = new JSONObject(jsonRespuesta);
    JSONObject current = jsonObj.getJSONObject("current");

    double temp = current.getDouble("temp_c");
    int humedad = current.getInt("humidity");

    // Almacenar localmente para registro histórico[cite: 1].
    ClimaRecord record = new ClimaRecord();
    record.setTemperatura(temp);
    record.setHumedad(humedad);
    record.setFechaRegistro(LocalDateTime.now());
    record.setDetalleCompleto(jsonRespuesta);

    climaRepository.save(record);
    System.out.println("Datos climáticos guardados con éxito.");
  }

  // Se ejecuta cada 1 minuto (60000 milisegundos)[cite: 1].
  @Scheduled(fixedRate = 60000)
  public void procesarAlertas() {
    // Analizar la última información disponible[cite: 1].
    ClimaRecord ultimoRegistro = climaRepository.findTopByOrderByFechaRegistroDesc();

    if (ultimoRegistro != null) {
      // Condición de alerta: temperatura mayor a 35° y humedad superior a 60%[cite: 1].
      if (ultimoRegistro.getTemperatura() > 35.0 && ultimoRegistro.getHumedad() > 60) {
        System.out.println("¡Alerta detectada! Enviando correos...");
        emailService.enviarAlerta(ultimoRegistro.getDetalleCompleto());
      }
    }
  }
}