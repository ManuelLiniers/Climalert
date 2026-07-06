package com.utn.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

  private final JavaMailSender mailSender;

  public EmailNotificationService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void enviarAlerta(String detalleClima) {
    SimpleMailMessage message = new SimpleMailMessage();
    // Destinatarios requeridos por el sistema.
    message.setTo("admin@clima.com", "emergencias@clima.com", "meteorologia@clima.com");
    message.setSubject("¡ALERTA METEOROLÓGICA CRÍTICA!");
    // El correo debe incluir el detalle completo del clima.
    message.setText("Se han detectado condiciones climáticas peligrosas. Detalle completo:\n" + detalleClima);

    mailSender.send(message);
  }
}