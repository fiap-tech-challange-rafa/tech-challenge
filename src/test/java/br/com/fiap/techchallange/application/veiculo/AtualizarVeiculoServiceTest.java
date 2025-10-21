package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AtualizarVeiculoServiceTest {

    private VeiculoRepository repository;
    private AtualizarVeiculoService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(VeiculoRepository.class);
        service = new AtualizarVeiculoService(repository);
    }

    @Test
    void deveAtualizarVeiculoComSucesso() {
        Veiculo existente = new Veiculo(1L, new Placa("ABC1234"), "Toyota", "Corolla", 2020);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.salvar(any())).thenReturn(existente);

        Veiculo atualizado = service.executar(1L, "BRA2E19", "Honda", "Civic", 2022);

        assertEquals("BRA2E19", atualizado.getPlaca().valor());
        assertEquals("Honda", atualizado.getMarca());
        assertEquals("Civic", atualizado.getModelo());
        assertEquals(2022, atualizado.getAno());
    }

    @Test
    void deveLancarExcecaoSeVeiculoNaoEncontrado() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () ->
                service.executar(1L, "ABC1234", "Toyota", "Corolla", 2020));
    }

    @Test
    void deveLancarExcecaoSePlacaNovaJaExistir() {
        Veiculo existente = new Veiculo(1L, new Placa("ABC1234"), "Toyota", "Corolla", 2020);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(existente));
        when(repository.buscarPorPlaca("XYZ9876"))
                .thenReturn(Optional.of(new Veiculo(2L, new Placa("XYZ9876"), "Fiat", "Uno", 2019)));

        assertThrows(IllegalStateException.class, () ->
                service.executar(1L, "XYZ9876", "Toyota", "Corolla", 2021));
    }
}
