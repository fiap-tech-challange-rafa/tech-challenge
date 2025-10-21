package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CadastrarVeiculoServiceTest {

    private VeiculoRepository repository;
    private CadastrarVeiculoService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(VeiculoRepository.class);
        service = new CadastrarVeiculoService(repository);
    }

    @Test
    void deveCadastrarVeiculoComSucesso() {
        when(repository.buscarPorPlaca("ABC1234")).thenReturn(Optional.empty());
        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1234"), "Toyota", "Corolla", 2020);
        when(repository.salvar(any())).thenReturn(veiculo);

        Veiculo salvo = service.executar(1L, "ABC1234", "Toyota", "Corolla", 2020);

        assertEquals("ABC1234", salvo.getPlaca().valor());
        verify(repository).salvar(any());
    }

    @Test
    void deveLancarExcecaoSePlacaJaExistir() {
        Veiculo existente = new Veiculo(1L, new Placa("ABC1234"), "Fiat", "Uno", 2018);
        when(repository.buscarPorPlaca("ABC1234")).thenReturn(Optional.of(existente));

        assertThrows(IllegalStateException.class, () ->
                service.executar(1L, "ABC1234", "Toyota", "Corolla", 2020));
    }
}
