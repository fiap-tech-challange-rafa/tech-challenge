package br.com.fiap.techchallange.infrastructure.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health indicator customizado para verificar saúde da aplicação
 */
@Component("customHealth")
public class CustomHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        try {
            // Verificar se banco está responsivo
            if (isApplicationHealthy()) {
                return Health.up()
                        .withDetail("application", "Tech Challenge")
                        .withDetail("version", "3.0")
                        .withDetail("status", "Aplicação rodando com sucesso")
                        .build();
            } else {
                return Health.down()
                        .withDetail("application", "Tech Challenge")
                        .withDetail("issue", "Componente crítico indisponível")
                        .build();
            }
        } catch (Exception e) {
            return Health.outOfService()
                    .withException(e)
                    .build();
        }
    }

    private boolean isApplicationHealthy() {
        // Aqui você pode adicionar verificações específicas da sua aplicação
        return true;
    }
}
