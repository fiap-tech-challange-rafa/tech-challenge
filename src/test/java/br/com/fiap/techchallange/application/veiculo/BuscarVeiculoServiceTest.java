package br.com.fiap.techchallange.application.veiculo;

import br.com.fiap.techchallange.domain.veiculo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BuscarVeiculoServiceTest {

    private VeiculoRepository repository;
    private BuscarVeiculoService service;

    @BeforeEach
    void setUp() {
        repository = Mockito.mock(VeiculoRepository.class);
        service = new BuscarVeiculoService(repository);
    }

    @Test
    void deveRetornarVeiculoPorId() {
        Veiculo v = new Veiculo(1L, new Placa("ABC1234"), "Toyota", "Corolla", 2020);
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(v));

        Veiculo result = service.porId(1L);

        assertEquals("ABC1234", result.getPlaca().valor());
    }

    @Test
    void deveLancarExcecaoQuandoVeiculoNaoEncontrado() {
        when(repository.buscarPorId(1L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.porId(1L));
    }

    @Test
    void deveListarVeiculosPorCliente() {
        when(repository.listarPorClienteId(1L)).thenReturn(List.of(
                new Veiculo(1L, new Placa("AAA1111"), "Fiat", "Uno", 2018),
                new Veiculo(1L, new Placa("BBB2222"), "VW", "Gol", 2019)
        ));

        List<Veiculo> lista = service.porCliente(1L);
        assertEquals(2, lista.size());
    }

    @Test
    void deveListarTodosVeiculos() {
        when(repository.listarTodos()).thenReturn(List.of(
                new Veiculo(1L, new Placa("CCC3333"), "Chevrolet", "Onix", 2020)
        ));

        List<Veiculo> lista = service.listarTodos();
        assertEquals(1, lista.size());
        assertEquals("Onix", lista.get(0).getModelo());
    }
}
