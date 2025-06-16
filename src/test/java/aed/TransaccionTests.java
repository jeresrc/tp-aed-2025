package aed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class TransaccionTests {

  @Test
  public void crearTransaccion() {
    Transaccion tx = new Transaccion(0, 1, 5, 100);

    assertEquals(tx.id_comprador(), 1);
    assertEquals(tx.id(), 0);
    assertEquals(tx.id_vendedor(), 5);
    assertEquals(tx.monto(), 100);
  }

  @Test
  public void compararTransacciones() {
    Transaccion tx0 = new Transaccion(0, 1, 5, 100);
    Transaccion tx1 = new Transaccion(5, 0, 10, 10);
    Transaccion tx2 = new Transaccion(6, 5, 2, 200);
    Transaccion tx3 = new Transaccion(7, 5, 2, 200);
    Transaccion tx4 = new Transaccion(6, 8, 7, 200);

    assertTrue(tx0.compareTo(tx1) == 1);
    assertTrue(tx1.compareTo(tx0) == -1);
    assertTrue(tx0.compareTo(tx3) == -1);
    assertTrue(tx2.compareTo(tx3) == -1);
    assertTrue(tx2.compareTo(tx4) == 0);
    assertFalse(tx2.equals(tx4));
    assertFalse(tx3.equals(tx4));
    assertTrue(tx3.compareTo(tx4) == 1);

  }
}
