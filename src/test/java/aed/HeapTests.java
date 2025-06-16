package aed;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class HeapTests {
  private Heap<Integer> heap;

  @BeforeEach
  void setUp() {
    heap = new Heap<Integer>();
  }

  @Test
  public void heapVacio() {
    assertTrue(heap.esVacio());
    assertEquals(heap.cardinal(), 0);
  }

  @Test
  public void insertarUnElemento() {
    Heap.Handle<Integer> h = heap.insertar(10);

    assertFalse(heap.esVacio());
    assertEquals(heap.cardinal(), 1);
    assertEquals(heap.verMax(), 10);
    assertEquals(h.valor(), 10);
  }

  @Test
  public void mantenerElMaximo() {
    heap.insertar(10);
    Heap.Handle<Integer> h1 = heap.insertar(5);
    Heap.Handle<Integer> h2 = heap.insertar(7);

    Integer hijoIzq = heap.getH(heap.hijoIzquierdo(0)).valor();
    Integer hijoDer = heap.getH(heap.hijoDerecho(0)).valor();

    assertEquals(heap.verMax(), 10);
    assertEquals(hijoIzq, 5);
    assertEquals(hijoDer, 7);
    assertEquals(h1.valor(), 5);
    assertEquals(h2.valor(), 7);

  }

  @Test
  public void agregarNuevoMaximo() {
    heap.insertar(10);
    heap.insertar(5);
    heap.insertar(7);
    heap.insertar(11);

    Integer hijoIzq = heap.getH(heap.hijoIzquierdo(0)).valor();
    Integer hijoDer = heap.getH(heap.hijoDerecho(0)).valor();
    Integer hijoIzq2 = heap.getH(heap.hijoIzquierdo(1)).valor();

    assertEquals(heap.verMax(), 11);
    assertEquals(hijoIzq, 10);
    assertEquals(hijoDer, 7);
    assertEquals(hijoIzq2, 5);
  }

  @Test
  public void insertarRepetido() {
    heap.insertar(10);
    heap.insertar(5);
    heap.insertar(10);

    Integer hijoIzq = heap.getH(heap.hijoIzquierdo(0)).valor();
    Integer hijoDer = heap.getH(heap.hijoDerecho(0)).valor();

    assertEquals(heap.cardinal(), 3);
    assertEquals(heap.verMax(), 10);
    assertEquals(hijoIzq, 5);
    assertEquals(hijoDer, 10);
  }

  @Test
  public void sacarMaximo() {
    heap.insertar(10);
    heap.insertar(5);
    heap.insertar(7);
    heap.insertar(11);

    assertEquals(heap.eliminarMax(), 11);

    Integer hijoIzq = heap.getH(heap.hijoIzquierdo(0)).valor();
    Integer hijoDer = heap.getH(heap.hijoDerecho(0)).valor();

    assertEquals(heap.cardinal(), 3);
    assertEquals(heap.verMax(), 10);
    assertEquals(hijoIzq, 5);
    assertEquals(hijoDer, 7);
  }

  @Test
  public void actualizar() {
    heap.insertar(10);
    heap.insertar(5);
    heap.insertar(7);
    heap.insertar(11);

    heap.actualizar(heap.getH(0), 1);

    Integer hijoIzq = heap.getH(heap.hijoIzquierdo(0)).valor();
    Integer hijoDer = heap.getH(heap.hijoDerecho(0)).valor();
    Integer hijoIzq2 = heap.getH(heap.hijoIzquierdo(1)).valor();

    assertEquals(heap.cardinal(), 4);
    assertEquals(heap.verMax(), 10);
    assertEquals(hijoIzq, 5);
    assertEquals(hijoDer, 7);
    assertEquals(hijoIzq2, 1);

    heap.actualizar(heap.getH(1), 50);

    hijoIzq = heap.getH(heap.hijoIzquierdo(0)).valor();
    hijoDer = heap.getH(heap.hijoDerecho(0)).valor();
    hijoIzq2 = heap.getH(heap.hijoIzquierdo(1)).valor();

    assertEquals(heap.verMax(), 50);
    assertEquals(hijoIzq, 10);
    assertEquals(hijoDer, 7);
    assertEquals(hijoIzq2, 1);

    Heap.Handle<Integer> h = heap.insertar(-1);
    Integer padre = heap.getH(heap.padre(h.posicion())).valor();

    assertEquals(padre, 10);

    heap.actualizar(h, 100);

    hijoIzq = heap.getH(heap.hijoIzquierdo(0)).valor();
    hijoDer = heap.getH(heap.hijoDerecho(0)).valor();
    hijoIzq2 = heap.getH(heap.hijoIzquierdo(1)).valor();
    Integer hijoDer2 = heap.getH(heap.hijoDerecho(1)).valor();

    assertEquals(heap.verMax(), 100);
    assertTrue(
        heap.verMax() > hijoIzq && heap.verMax() > hijoDer && heap.verMax() > hijoIzq2 && heap.verMax() > hijoDer2);
    assertTrue(hijoIzq > hijoIzq2 && hijoIzq > hijoDer2);
  }

  @Test
  public void heapify() {
    ArrayList<Integer> lista = new ArrayList<>();
    Integer cap = 100;
    for (Integer i = 0; i < cap; i++) {
      lista.add(i);
    }

    heap.heapify(lista);

    assertEquals(heap.cardinal(), cap);

    for (Integer i = cap - 1; i > -1; i--) {
      assertEquals(heap.eliminarMax(), i);
      assertEquals(heap.cardinal(), i);
    }

    assertTrue(heap.esVacio());

  }

  @Test
  public void stress() {
    Integer cap = 1000;
    Random rand = new Random();
    ArrayList<Integer> insertados = new ArrayList<>();

    for (Integer i = 0; i < cap; i++) {
      Integer k = rand.nextInt();
      assertEquals(heap.cardinal(), i);
      heap.insertar(k);
      insertados.add(k);
    }

    insertados.sort(Comparator.reverseOrder());

    assertEquals(heap.cardinal(), cap);

    for (Integer i = 0; i < cap; i++) {
      assertEquals(insertados.get(i), heap.eliminarMax());
    }

    assertTrue(heap.esVacio());
  }
}
