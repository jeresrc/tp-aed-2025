package aed;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

public class BerretacoinTests {
  private Berretacoin berretacoin;
  private Transaccion[] transacciones;
  private Transaccion[] transacciones2;
  private Transaccion[] transacciones3;

  // Helper class para trackear saldos de usuarios
  private class SaldoTracker {
    private Map<Integer, Integer> saldos;

    public SaldoTracker(int usuarios) {
      saldos = new HashMap<>();
      for (int i = 1; i <= usuarios; i++) {
        saldos.put(i, 0);
      }
    }

    public void aplicarTransaccion(Transaccion tx) {
      if (tx.id_comprador() == 0) {
        saldos.put(tx.id_vendedor(), saldos.get(tx.id_vendedor()) + tx.monto());
      } else {
        saldos.put(tx.id_comprador(), saldos.get(tx.id_comprador()) - tx.monto());
        saldos.put(tx.id_vendedor(), saldos.get(tx.id_vendedor()) + tx.monto());
      }
    }

    public int getSaldo(int usuario) {
      return saldos.get(usuario);
    }

    public boolean puedeGastar(int usuario, int monto) {
      return usuario == 0 || getSaldo(usuario) >= monto;
    }

    public int getMaximoTenedor() {
      int maxSaldo = -1;
      int maxUsuario = Integer.MAX_VALUE;

      for (Map.Entry<Integer, Integer> entry : saldos.entrySet()) {
        int usuario = entry.getKey();
        int saldo = entry.getValue();

        if (saldo > maxSaldo || (saldo == maxSaldo && usuario < maxUsuario)) {
          maxSaldo = saldo;
          maxUsuario = usuario;
        }
      }
      return maxUsuario;
    }

    public void revertirTransaccion(Transaccion tx) {
      if (tx.id_comprador() == 0) {
        saldos.put(tx.id_vendedor(), saldos.get(tx.id_vendedor()) - tx.monto());
      } else {
        saldos.put(tx.id_comprador(), saldos.get(tx.id_comprador()) + tx.monto());
        saldos.put(tx.id_vendedor(), saldos.get(tx.id_vendedor()) - tx.monto());
      }
    }
  }

  @BeforeEach
  void setUp() {
    berretacoin = new Berretacoin(10);

    transacciones = new Transaccion[] {
        new Transaccion(0, 0, 2, 1), // 2 -> $1
        new Transaccion(1, 2, 3, 1), // 3 -> $1
        new Transaccion(2, 3, 4, 1) // 4 -> $1
    };

    transacciones2 = new Transaccion[] {
        new Transaccion(0, 0, 4, 1), // 4 -> $2
        new Transaccion(1, 4, 1, 2), // 1 -> $2
        new Transaccion(2, 1, 2, 1) // 1 -> $1 , 2 -> $1
    };

    transacciones3 = new Transaccion[] {
        new Transaccion(0, 0, 1, 1), // 1 -> $2, 2 -> $1
        new Transaccion(1, 1, 2, 2), // 2 -> $3
        new Transaccion(2, 2, 3, 3), // 3 -> $3
        new Transaccion(3, 3, 1, 2), // 1 -> $2, 3 -> $1
        new Transaccion(4, 1, 2, 1), // 1 -> $1, 2 -> $1, 3 -> $1
        new Transaccion(5, 2, 3, 1) // 1 -> $1, 3 -> $2
    };
  } // Montos Usuarios 1= 2$ , 2= 1$ , 3 = 2$ , 4 = 1$ Los demas 5 al 10 = 0 $ ---
    // Manu

  @Test
  public void maximoTenedorEsElMinimoCuandoNuevo() {
    assertEquals(berretacoin.maximoTenedor(), 1);
  }

  @Test
  public void unSoloUsuario() {
    Berretacoin sistema = new Berretacoin(1);
    assertEquals(1, sistema.maximoTenedor());

    Transaccion[] bloque = { new Transaccion(0, 0, 1, 1) };
    sistema.agregarBloque(bloque);

    assertEquals(1, sistema.maximoTenedor());
    assertEquals(new Transaccion(0, 0, 1, 1), sistema.txMayorValorUltimoBloque());
    assertEquals(0, sistema.montoMedioUltimoBloque());
  }

  @Test
  public void bloqueConSoloTransaccionDeCreacion() {
    Transaccion[] bloque = { new Transaccion(0, 0, 3, 1) };
    berretacoin.agregarBloque(bloque);

    assertEquals(3, berretacoin.maximoTenedor());
    assertEquals(new Transaccion(0, 0, 3, 1), berretacoin.txMayorValorUltimoBloque());
    assertEquals(0, berretacoin.montoMedioUltimoBloque());

    Transaccion[] resultado = berretacoin.txUltimoBloque();
    assertEquals(1, resultado.length);
    assertEquals(new Transaccion(0, 0, 3, 1), resultado[0]);
  }

  @Test
  public void agregarBloque() {
    Transaccion[] copia_transacciones = new Transaccion[] {
        new Transaccion(0, 0, 2, 1), // 2 -> $1
        new Transaccion(1, 2, 3, 1), // 3 -> $1
        new Transaccion(2, 3, 4, 1) // 4 -> $1
    };

    berretacoin.agregarBloque(transacciones);
    assertEquals(berretacoin.maximoTenedor(), 4);
    assertEquals(berretacoin.txMayorValorUltimoBloque(), new Transaccion(2, 3, 4, 1));
    assertEquals(berretacoin.montoMedioUltimoBloque(), 1);
    assertTrue(Arrays.equals(berretacoin.txUltimoBloque(), copia_transacciones));
  }

  @Test
  public void agregarVariosBloques() {
    Transaccion[] copia_transacciones2 = new Transaccion[] {
        new Transaccion(0, 0, 4, 1), // 4 -> $2
        new Transaccion(1, 4, 1, 2), // 1 -> $2
        new Transaccion(2, 1, 2, 1) // 1 -> $1 , 2 -> $1
    };

    Transaccion[] copia_transacciones3 = new Transaccion[] {
        new Transaccion(0, 0, 1, 1), // 1 -> $2, 2 -> $1
        new Transaccion(1, 1, 2, 2), // 2 -> $3
        new Transaccion(2, 2, 3, 3), // 3 -> $3
        new Transaccion(3, 3, 1, 2), // 1 -> $2, 3 -> $1
        new Transaccion(4, 1, 2, 1), // 1 -> $1, 2 -> $1, 3 -> $1
        new Transaccion(5, 2, 3, 1) // 1 -> $1, 3 -> $2
    };

    berretacoin.agregarBloque(transacciones);
    berretacoin.agregarBloque(transacciones2);

    assertEquals(berretacoin.maximoTenedor(), 1);
    assertEquals(berretacoin.txMayorValorUltimoBloque(), new Transaccion(1, 4, 1, 2));
    assertEquals(berretacoin.montoMedioUltimoBloque(), 4 / 3);
    assertTrue(Arrays.equals(berretacoin.txUltimoBloque(), copia_transacciones2));

    berretacoin.agregarBloque(transacciones3);

    assertEquals(berretacoin.maximoTenedor(), 3);
    assertEquals(berretacoin.txMayorValorUltimoBloque(), new Transaccion(2, 2, 3, 3));
    assertEquals(berretacoin.montoMedioUltimoBloque(), 10 / 6);
    assertTrue(Arrays.equals(berretacoin.txUltimoBloque(), copia_transacciones3));
  }

  @Test
  public void empatesEnMaximoTenedor() {
    Transaccion[] bloque1 = {
        new Transaccion(0, 0, 1, 1),
        new Transaccion(1, 1, 2, 1)
    };
    berretacoin.agregarBloque(bloque1);
    assertEquals(2, berretacoin.maximoTenedor()); // 1 tiene 0, 2 tiene 1

    Transaccion[] bloque2 = {
        new Transaccion(2, 0, 3, 1),
        new Transaccion(3, 2, 1, 1)
    };
    berretacoin.agregarBloque(bloque2);
    assertEquals(1, berretacoin.maximoTenedor()); // 1 y 3 tienen 1, se elige 1
  }

  @Test
  public void empatesEnTransaccionMayorValor() {
    Transaccion[] bloque = {
        new Transaccion(0, 0, 1, 1),
        new Transaccion(1, 1, 2, 1),
        new Transaccion(2, 2, 3, 1),
        new Transaccion(3, 3, 4, 1),
        new Transaccion(4, 4, 5, 1)
    };
    berretacoin.agregarBloque(bloque);

    Transaccion mayorValor = berretacoin.txMayorValorUltimoBloque();
    assertEquals(new Transaccion(4, 4, 5, 1), mayorValor);
  }

  @Test
  public void multiplesBloquesPequenos() {
    SaldoTracker tracker = new SaldoTracker(10);

    for (int i = 0; i < 10; i++) {
      ArrayList<Transaccion> transacciones = new ArrayList<>();

      int receptor = (i % 9) + 1;
      Transaccion creacion = new Transaccion(0, 0, receptor, 1);
      transacciones.add(creacion);
      tracker.aplicarTransaccion(creacion);

      int comprador = receptor;
      int vendedor = ((i + 1) % 9) + 1;
      if (comprador != vendedor && tracker.puedeGastar(comprador, 1)) {
        Transaccion normal = new Transaccion(1, comprador, vendedor, 1);
        transacciones.add(normal);
        tracker.aplicarTransaccion(normal);
      }

      berretacoin.agregarBloque(transacciones.toArray(new Transaccion[0]));
      assertEquals(tracker.getMaximoTenedor(), berretacoin.maximoTenedor());
    }
  }

  /////////////////////////////////////////////////////////////////////////////////////////////
  @Test
  public void hackeoUnicaTransaccion() {
    Transaccion[] bloque = { new Transaccion(0, 0, 1, 1) };
    berretacoin.agregarBloque(bloque);
    berretacoin.hackearTx();
    Transaccion[] resultado = berretacoin.txUltimoBloque();
    assertEquals(0, resultado.length);
  }

  @Test
  public void hackearConUnBloque() {
    Transaccion[] transacciones_hackeadas = new Transaccion[] {
        new Transaccion(0, 0, 2, 1), // 2 -> $1
        new Transaccion(1, 2, 3, 1), // 3 -> $1
    };

    berretacoin.agregarBloque(transacciones);

    berretacoin.hackearTx();

    assertEquals(berretacoin.maximoTenedor(), 3);
    assertEquals(berretacoin.txMayorValorUltimoBloque(), new Transaccion(1, 2, 3, 1));
    assertEquals(berretacoin.montoMedioUltimoBloque(), 1);
    assertTrue(Arrays.equals(berretacoin.txUltimoBloque(), transacciones_hackeadas));
  }

  @Test
  public void hackeoTotalDeBloque() {
    Transaccion[] bloque = {
        new Transaccion(0, 0, 1, 1),
        new Transaccion(1, 1, 2, 1),
        new Transaccion(2, 2, 3, 1)
    };
    berretacoin.agregarBloque(bloque);

    berretacoin.hackearTx();
    assertTrue(Arrays.equals((Arrays.copyOfRange(bloque, 0, bloque.length - 1)), berretacoin.txUltimoBloque()));

    berretacoin.hackearTx();
    assertTrue(Arrays.equals((Arrays.copyOfRange(bloque, 0, bloque.length - 2)), berretacoin.txUltimoBloque()));
    assertEquals(0, berretacoin.montoMedioUltimoBloque());

    berretacoin.hackearTx();
    assertTrue(Arrays.equals(Arrays.copyOfRange(bloque, 0, bloque.length - 3), berretacoin.txUltimoBloque()));
    assertEquals(0, berretacoin.montoMedioUltimoBloque());
  }

  @Test
  public void hackeoAfectaSaldos() {
    SaldoTracker tracker = new SaldoTracker(5);

    Transaccion[] bloque = {
        new Transaccion(0, 0, 1, 1),
        new Transaccion(1, 1, 2, 1)
    };

    for (Transaccion tx : bloque) {
      tracker.aplicarTransaccion(tx);
    }

    berretacoin.agregarBloque(bloque);
    assertEquals(tracker.getMaximoTenedor(), berretacoin.maximoTenedor());

    Transaccion hackeada = berretacoin.txMayorValorUltimoBloque();
    berretacoin.hackearTx();
    tracker.revertirTransaccion(hackeada);

    assertEquals(tracker.getMaximoTenedor(), berretacoin.maximoTenedor());
  }

  @Test
  public void hackearConVariosBloques() {
    Transaccion[] transacciones3_hackeadas = new Transaccion[] {
        new Transaccion(0, 0, 1, 1), // 1 -> $2, 2 -> $1
        new Transaccion(1, 1, 2, 2), // 2 -> $3
        new Transaccion(3, 3, 1, 2), // 1 -> $2, 2 -> $3, 3 -> -$2
        new Transaccion(4, 1, 2, 1), // 1 -> $1, 2 -> $4, 3 -> -$2
        new Transaccion(5, 2, 3, 1) // 1 -> $1, 2 -> $3, 3 -> -$1
    };

    berretacoin.agregarBloque(transacciones);
    berretacoin.agregarBloque(transacciones2);
    berretacoin.agregarBloque(transacciones3);

    berretacoin.hackearTx();

    assertEquals(berretacoin.maximoTenedor(), 2);
    assertEquals(berretacoin.txMayorValorUltimoBloque(), new Transaccion(3, 3, 1, 2));
    assertEquals(berretacoin.montoMedioUltimoBloque(), 7 / 5);
    assertTrue(Arrays.equals(berretacoin.txUltimoBloque(), transacciones3_hackeadas));
  }

  @Test
  public void hackearVariasVeces() {
    Transaccion[] transacciones3_hackeadas_2 = new Transaccion[] {
        new Transaccion(0, 0, 1, 1), // 1 -> $2, 2 -> $1
        new Transaccion(1, 1, 2, 2), // 2 -> $3
        new Transaccion(4, 1, 2, 1), // 1 -> -$1, 2 -> $4
        new Transaccion(5, 2, 3, 1) // 1 -> -$1, 2 -> $3, 3 -> $1
    };
    Transaccion[] transacciones3_hackeadas_3 = new Transaccion[] {
        new Transaccion(0, 0, 1, 1), // 1 -> $2, 2 -> $1
        new Transaccion(4, 1, 2, 1), // 1 -> $1, 2 -> $2
        new Transaccion(5, 2, 3, 1) // 1 -> $1, 2 -> $1, 3 -> $1
    };

    berretacoin.agregarBloque(transacciones);
    berretacoin.agregarBloque(transacciones2);
    berretacoin.agregarBloque(transacciones3);

    berretacoin.hackearTx();
    berretacoin.hackearTx();

    assertEquals(berretacoin.maximoTenedor(), 2);
    assertEquals(berretacoin.txMayorValorUltimoBloque(), new Transaccion(1, 1, 2, 2));
    assertEquals(berretacoin.montoMedioUltimoBloque(), 5 / 4);
    assertTrue(Arrays.equals(berretacoin.txUltimoBloque(), transacciones3_hackeadas_2));

    berretacoin.hackearTx();

    assertEquals(berretacoin.maximoTenedor(), 1);
    assertEquals(berretacoin.txMayorValorUltimoBloque(), new Transaccion(5, 2, 3, 1));
    assertEquals(berretacoin.montoMedioUltimoBloque(), 1);
    assertTrue(Arrays.equals(berretacoin.txUltimoBloque(), transacciones3_hackeadas_3));
  }

  @Test
  public void testStress() {
    int USUARIOS = 3000;
    int BLOQUES = 5000;
    int MAX_TX_POR_BLOQUE = 1000;
    int MAX_HACKEOS = 100;

    Berretacoin sistema = new Berretacoin(USUARIOS);
    SaldoTracker tracker = new SaldoTracker(USUARIOS);
    for (int bloque = 0; bloque < BLOQUES; bloque++) {
      ArrayList<Transaccion> transacciones = new ArrayList<>();

      if (bloque < 3000) {
        int receptor = USUARIOS - bloque;
        Transaccion creacion = new Transaccion(transacciones.size(), 0, receptor, 1);
        transacciones.add(creacion);
        tracker.aplicarTransaccion(creacion);
      }

      for (int i = 0; i < MAX_TX_POR_BLOQUE; i++) {
        int comprador = (i + bloque) % USUARIOS + 1;
        int vendedor = (i + bloque + 1) % USUARIOS + 1;
        if (comprador == vendedor)
          continue;

        int maxMonto = Math.max(0, tracker.getSaldo(comprador));
        if (maxMonto <= 0)
          continue;

        int monto = Math.min(maxMonto, (i % 10) + 1);

        if (tracker.puedeGastar(comprador, monto)) {
          Transaccion tx = new Transaccion(transacciones.size(), comprador, vendedor, monto);
          transacciones.add(tx);
          tracker.aplicarTransaccion(tx);
        }
      }

      if (!transacciones.isEmpty()) {
        int monto_total = 0;
        int tx_total = 0;
        for (Transaccion tx : transacciones) {
          if (tx.id_comprador() == 0)
            continue;
          monto_total += tx.monto();
          tx_total++;
        }
        sistema.agregarBloque(transacciones.toArray(new Transaccion[0]));
        assertEquals(tracker.getMaximoTenedor(), sistema.maximoTenedor());
        int monto_medio = tx_total == 0 ? 0 : monto_total / tx_total;
        assertEquals(monto_medio, sistema.montoMedioUltimoBloque());

        if (bloque % 3 == 0) {
          int num_tx = Math.min(MAX_HACKEOS, transacciones.size());
          for (int j = 0; j < num_tx; j++) {
            Transaccion hackeada = sistema.txMayorValorUltimoBloque();
            sistema.hackearTx();
            tracker.revertirTransaccion(hackeada);
            transacciones.removeIf(tx -> tx.compareTo(hackeada) == 0);
            if (hackeada.id_comprador() != 0) {
              monto_total -= hackeada.monto();
              tx_total--;
            }
            monto_medio = tx_total == 0 ? 0 : monto_total / tx_total;
            assertEquals(monto_medio, sistema.montoMedioUltimoBloque());
            assertTrue(Arrays.equals(transacciones.toArray(new Transaccion[0]), sistema.txUltimoBloque()));
            assertEquals(tracker.getMaximoTenedor(), sistema.maximoTenedor());
          }
        }
      }
    }
  }
}
