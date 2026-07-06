package com.utn.scheduler;

import com.utn.model.entities.ClimaRecord;
import com.utn.repository.ClimaRepository;
import com.utn.service.EmailNotificationService;
import com.utn.service.WeatherApiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClimalertSchedulerTest {

  @Mock
  private WeatherApiService weatherApiService;

  @Mock
  private ClimaRepository climaRepository;

  @Mock
  private EmailNotificationService emailService;

  @InjectMocks
  private ClimalertScheduler climalertScheduler;

  private ClimaRecord registroCritico;
  private ClimaRecord registroNormal;

  @BeforeEach
  void setUp() {
    // Preparamos datos de prueba
    registroCritico = new ClimaRecord();
    registroCritico.setTemperatura(36.5);
    registroCritico.setHumedad(65);
    registroCritico.setFechaRegistro(LocalDateTime.now());
    registroCritico.setDetalleCompleto("{\"current\": {\"temp_c\": 36.5, \"humidity\": 65}}");

    registroNormal = new ClimaRecord();
    registroNormal.setTemperatura(30.0);
    registroNormal.setHumedad(50);
    registroNormal.setFechaRegistro(LocalDateTime.now());
    registroNormal.setDetalleCompleto("{\"current\": {\"temp_c\": 30.0, \"humidity\": 50}}");
  }

  @Test
  void testObtenerYGuardarClima_GuardaDatosCorrectamente() {
    // 1. Preparamos el mock de la API para devolver un JSON simulado
    String jsonSimulado = "{\"current\": {\"temp_c\": 36.5, \"humidity\": 65}}";
    when(weatherApiService.obtenerClimaActual()).thenReturn(jsonSimulado);

    // 2. Ejecutamos el method a probar
    climalertScheduler.obtenerYGuardarClima();

    // 3. Verificamos que el repositorio haya guardado el registro
    // Usamos ArgumentCaptor para capturar el objeto exacto que se intentó guardar
    ArgumentCaptor<ClimaRecord> recordCaptor = ArgumentCaptor.forClass(ClimaRecord.class);
    verify(climaRepository, times(1)).save(recordCaptor.capture());

    ClimaRecord registroGuardado = recordCaptor.getValue();

    // 4. Comprobamos que el JSON se parseó bien y se guardaron los datos correctos
    assertEquals(36.5, registroGuardado.getTemperatura());
    assertEquals(65, registroGuardado.getHumedad());
    assertEquals(jsonSimulado, registroGuardado.getDetalleCompleto());
  }

  @Test
  void testProcesarAlertas_ConCondicionesCriticas_EnviaCorreo() {
    // 1. Simulamos que la base de datos devuelve el registro crítico (>35° y >60% humedad)
    when(climaRepository.findTopByOrderByFechaRegistroDesc()).thenReturn(registroCritico);

    // 2. Ejecutamos el method
    climalertScheduler.procesarAlertas();

    // 3. Verificamos que se haya llamado al servicio de email exactamente 1 vez
    verify(emailService, times(1)).enviarAlerta(registroCritico.getDetalleCompleto());
  }

  @Test
  void testProcesarAlertas_SinCondicionesCriticas_NoEnviaCorreo() {
    // 1. Simulamos que la base de datos devuelve un clima normal
    when(climaRepository.findTopByOrderByFechaRegistroDesc()).thenReturn(registroNormal);

    // 2. Ejecutamos el method
    climalertScheduler.procesarAlertas();

    // 3. Verificamos que el servicio de email NUNCA se haya llamado
    verify(emailService, never()).enviarAlerta(anyString());
  }
}