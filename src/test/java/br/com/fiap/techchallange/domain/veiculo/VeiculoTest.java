package br.com.fiap.techchallange.domain.veiculo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class VeiculoTest {

    @Test
    void deveCriarVeiculoValido() {
        Placa placa = new Placa("ABC1234");
        Veiculo veiculo = new Veiculo(1L, placa, "Toyota", "Corolla", 2022);

        assertEquals(1L, veiculo.getClienteId());
        assertEquals(placa, veiculo.getPlaca());
        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2022, veiculo.getAno());
    }

    @Test
    void deveLancarExcecaoQuandoClienteOuPlacaForemNulos() {
        assertThrows(IllegalArgumentException.class, () -> new Veiculo(null, new Placa("ABC1234"), "Marca", "Modelo", 2022));
        assertThrows(IllegalArgumentException.class, () -> new Veiculo(1L, null, "Marca", "Modelo", 2022));
    }

    @Test
    void deveAtualizarDadosCorretamente() {
        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1234"), "Toyota", "Corolla", 2022);
        veiculo.atualizarDados(new Placa("BRA2E19"), "Honda", "Civic", 2023);

        assertEquals("Honda", veiculo.getMarca());
        assertEquals("Civic", veiculo.getModelo());
        assertEquals(2023, veiculo.getAno());
        assertEquals(new Placa("BRA2E19"), veiculo.getPlaca());
    }

    @Test
    void deveIgnorarCamposNulosOuInvalidosNaAtualizacao() {
        Veiculo veiculo = new Veiculo(1L, new Placa("ABC1234"), "Toyota", "Corolla", 2022);
        veiculo.atualizarDados(null, "  ", "", 1800); // ano inválido

        assertEquals("Toyota", veiculo.getMarca());
        assertEquals("Corolla", veiculo.getModelo());
        assertEquals(2022, veiculo.getAno());
        assertEquals(new Placa("ABC1234"), veiculo.getPlaca());
    }
}
