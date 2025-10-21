package br.com.fiap.techchallange.infrastructure.servico;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataServicoRepository extends JpaRepository<ServicoEntity, Long> {
    Optional<ServicoEntity> findByCodigo(String codigo);
}
