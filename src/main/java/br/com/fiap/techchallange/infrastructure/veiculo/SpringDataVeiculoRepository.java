package br.com.fiap.techchallange.infrastructure.veiculo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SpringDataVeiculoRepository extends JpaRepository<VeiculoEntity, Long> {
    Optional<VeiculoEntity> findByPlaca(String placa);
    List<VeiculoEntity> findByClienteId(Long clienteId);
}
