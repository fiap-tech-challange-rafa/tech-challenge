package br.com.fiap.techchallange.infrastructure.ordemservico;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOrdemServicoRepository extends JpaRepository<OrdemServicoEntity, Long> {
}
