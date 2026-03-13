package com.gastos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Classe principal da aplicação - ponto de entrada quando você executa o programa.
 * O método main() é o primeiro código que a JVM (máquina virtual Java) executa.
 */
@SpringBootApplication  // Diz ao Spring: "Esta é uma aplicação Spring Boot - escaneie e configure automaticamente componentes (controllers, services, repositories)"
@EnableScheduling       // Habilita tarefas agendadas - permite usar @Scheduled (ex: AlertaService que roda todo dia às 8h)
public class GastosApplication {

    /**
     * Método main - por aqui a aplicação começa.
     * - public: pode ser chamado de fora
     * - static: pertence à classe, não precisa criar objeto para chamar
     * - void: não retorna nada
     * - String[] args: argumentos da linha de comando (quase não usamos)
     */
    public static void main(String[] args) {
        // SpringApplication.run sobe o servidor (Tomcat na porta 8080), carrega todos os controllers, services, repositories e conecta ao banco H2
        SpringApplication.run(GastosApplication.class, args);
    }
}
