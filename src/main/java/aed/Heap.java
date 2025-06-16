package aed;

import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
  private ArrayList<T> elementos;
  private ArrayList<Handle<T>> handles;

  public Heap() {
    elementos = new ArrayList<>();
    handles = new ArrayList<>();
  }

  public boolean esVacio() {
    return elementos.isEmpty();
  }

  public Handle<T> getH(int indice) {
    return handles.get(indice);
  }

  public int hijoDerecho(int i) {
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

  public int hijoIzquierdo(int i) {
    return (2 * i) + 1;
  }

  public int padre(int i) {
    return (i - 1) / 2;
  }

  public int cardinal() {
    return elementos.size();
  }

  public T verMax() {
    if (elementos == null) {
      return null;
    } else {
      return elementos.get(0);
    }

  }

  public Handle<T> insertar(T valor) {
    elementos.add(valor);
    Handle<T> h = new Handle<>(valor, elementos.size() - 1);
    handles.add(h);
    siftUp(h.posicion());
    return h;
  }

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

  public void actualizar(Handle<T> handle, T nuevoValor) {
    int i = handle.posicion();
    T viejoValor = elementos.get(i);
    handles.get(i).actualizarValor(nuevoValor);
    elementos.set(i, nuevoValor);

    if (nuevoValor.compareTo(viejoValor) > 0) {
      siftUp(i);
    } else {
      siftDown(i);
    }
  }

  private void siftUp(int i) {
    while (i > 0) {
      int padre = padre(i);
      if (elementos.get(i).compareTo(elementos.get(padre)) > 0) {
        swap(i, padre);
        i = padre;
      } else {
        break;
      }
    }
  }

  private void siftDown(int i) {
    int n = elementos.size();
    while (true) {
      int max = i;
      int izq = hijoIzquierdo(i);
      int der = hijoDerecho(i);

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
