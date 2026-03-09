package com.gastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "configuracao_financeira")
public class ConfiguracaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 12, scale = 2)
    private BigDecimal salario = BigDecimal.ZERO;

    @Column(name = "email_alertas")
    private String emailAlertas;

    @Column(name = "telefone_alertas")
    private String telefoneAlertas;

    @Column(name = "dias_antes_alerta")
    private int diasAntesAlerta = 3;

    @Column(name = "alertas_email_ativos")
    private boolean alertasEmailAtivos = true;

    public ConfiguracaoFinanceira() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getSalario() { return salario; }
    public void setSalario(BigDecimal salario) { this.salario = salario; }
    public String getEmailAlertas() { return emailAlertas; }
    public void setEmailAlertas(String emailAlertas) { this.emailAlertas = emailAlertas; }
    public String getTelefoneAlertas() { return telefoneAlertas; }
    public void setTelefoneAlertas(String telefoneAlertas) { this.telefoneAlertas = telefoneAlertas; }
    public int getDiasAntesAlerta() { return diasAntesAlerta; }
    public void setDiasAntesAlerta(int diasAntesAlerta) { this.diasAntesAlerta = diasAntesAlerta; }
    public boolean isAlertasEmailAtivos() { return alertasEmailAtivos; }
    public void setAlertasEmailAtivos(boolean alertasEmailAtivos) { this.alertasEmailAtivos = alertasEmailAtivos; }
}
