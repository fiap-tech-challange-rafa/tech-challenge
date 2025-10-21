package br.com.fiap.techchallange.infrastructure.peca;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataPecaRepository extends JpaRepository<PecaEntity, Long> {
    Optional<PecaEntity> findBySku(String sku);
}
