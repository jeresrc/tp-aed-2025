package aed;

import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
  private ArrayList<T> elementos;
  private ArrayList<Handle<T>> handles;

  public Heap() {
    elementos = new ArrayList<>();
    handles = new ArrayList<>();
  }

  /**
   * Devuelve verdadero si el heap está vacío.
   */
  public boolean esVacio() {
    return elementos.isEmpty();
  }

  public Handle<T> getH(int indice) {
    return handles.get(indice);
  }

  public int hijoderecho(int i) {
    return (2 * i) + 2;
  }

  public Handle<T> getHandle(T usuario) {
    for (int i = 0; i < handles.size(); i++) {
      if (handles.get(i).valor().equals(usuario)) {
        return handles.get(i);
      }

    }
    return null;
  }

  public int hijoizquierdo(int i) {
    return (2 * i) + 1;
  }

  public int padre(int i) {
    return (i - 1) / 2;
  }

  /**
   * Devuelve la cantidad de elementos en el heap.
   */
  public int cardinal() {
    return elementos.size();
  }

  /**
   * Devuelve el elemento máximo (raíz del heap), sin eliminarlo.
   */
  public T verMax() {
    if (elementos == null) {
      return null;
    } else {
      return elementos.get(0);
    }

  }

  /**
   * Inserta un nuevo elemento en el heap y retorna su handle asociado.
   */
  public Handle<T> insertar(T valor) {
    elementos.add(valor);
    Handle<T> h = new Handle<>(valor, elementos.size() - 1);
    handles.add(h);
    siftUp(h.posicion());
    return h;
  }

  /**
   * Elimina y devuelve el elemento máximo del heap.
   */
  public T eliminarMax() {
    if (esVacio())
      return null;

    T max = elementos.get(0);
    int last = elementos.size() - 1;
    swap(0, last);
    elementos.remove(last);
    handles.remove(last);

    if (!esVacio()) {
      siftDown(0);
    }
    return max;
  }

  /**
   * Convierte una lista de elementos en un heap en O(n).
   */
  public void heapify(ArrayList<T> lista) {
    elementos = new ArrayList<>(lista);
    handles = new ArrayList<>();

    for (int i = 0; i < lista.size(); i++) {
      handles.add(new Handle<>(lista.get(i), i));
    }

    for (int i = (elementos.size() / 2) - 1; i >= 0; i--) {
      siftDown(i);
    }
  }

  /**
   * Actualiza el valor asociado a un handle y reordena el heap según sea
   * necesario.
   */
  public void actualizar(Handle<T> handle, T nuevoValor) {
    int i = handle.posicion();
    T viejoValor = elementos.get(i);
    handles.get(i).actualizarValor(nuevoValor);
    elementos.set(i, nuevoValor);
    // handle.actualizarValor(nuevoValor);

    if (nuevoValor.compareTo(viejoValor) > 0) {
      siftUp(i);
    } else {
      siftDown(i);
    }
  }

  /**
   * Reordena hacia arriba el elemento en la posición i.
   */
  private void siftUp(int i) {
    while (i > 0) {
      int padre = padre(i); // Remplazar por le nuevo public padre
      if (elementos.get(i).compareTo(elementos.get(padre)) > 0) {
        swap(i, padre);
        i = padre;
      } else {
        break;
      }
    }
  }

  /**
   * Reordena hacia abajo el elemento en la posición i.
   */
  private void siftDown(int i) {
    int n = elementos.size();
    while (true) {
      int max = i;
      int izq = hijoizquierdo(i); // Remplazar por el public hijoIzquierdo
      int der = hijoderecho(i); // Remplazar por el public hijoDerecho

      if (izq < n && elementos.get(izq).compareTo(elementos.get(max)) > 0) {
        max = izq;
      }
      if (der < n && elementos.get(der).compareTo(elementos.get(max)) > 0) {
        max = der;
      }

      if (max == i)
        break;

      swap(i, max);
      i = max;
    }
  }

  /**
   * Intercambia los elementos en las posiciones i y j, actualizando también los
   * handles.
   */
  private void swap(int i, int j) {
    T tempElemento = elementos.get(i);
    elementos.set(i, elementos.get(j));
    elementos.set(j, tempElemento);

    Handle<T> hi = handles.get(i);
    Handle<T> hj = handles.get(j);
    handles.set(i, hj);
    handles.set(j, hi);
    hi.actualizarPosicion(j);
    hj.actualizarPosicion(i);
  }

  /**
   * Clase interna que representa un puntero a un elemento dentro del heap.
   */
  public static class Handle<T> {
    private T valor;
    private int posicion;

    public Handle(T valor, int posicion) {
      this.valor = valor;
      this.posicion = posicion;
    }

    public T valor() {
      return valor;
    }

    public int posicion() {
      return posicion;
    }

    public void actualizarPosicion(int nuevaPos) {
      this.posicion = nuevaPos;
    }

    public void actualizarValor(T nuevoValor) {
      this.valor = nuevoValor;
    }
  }
}
