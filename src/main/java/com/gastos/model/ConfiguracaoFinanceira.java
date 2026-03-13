package com.gastos.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

/**
 * Entidade de configuração global do sistema - em geral existe UMA só linha no banco.
 * Guarda salário, e-mail e telefone para alertas, e se os alertas por e-mail estão ativos.
 */
@Entity
@Table(name = "configuracao_financeira")
public class ConfiguracaoFinanceira {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(precision = 12, scale = 2)
    private BigDecimal salario = BigDecimal.ZERO;  // Salário mensal usado na projeção e no dashboard

    @Column(name = "email_alertas")
    private String emailAlertas;  // Para onde enviar os alertas de contas a vencer

    @Column(name = "telefone_alertas")
    private String telefoneAlertas;  // Reservado para futuro (ex: WhatsApp)

    @Column(name = "dias_antes_alerta")
    private int diasAntesAlerta = 3;  // Enviar alerta X dias antes do vencimento

    @Column(name = "alertas_email_ativos")
    private boolean alertasEmailAtivos = true;  // Se false, o scheduler não envia e-mail

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
