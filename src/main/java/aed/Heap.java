package aed;

import java.util.ArrayList;

public class Heap<T extends Comparable<T>> {
  private ArrayList<T> elementos;
  private ArrayList<Handle<T>> handles;

  // n es la longitud de elementos
  public Heap() {
    elementos = new ArrayList<>(); // O(1)
    handles = new ArrayList<>(); // O(1)
  }

  public boolean esVacio() {
    return elementos.isEmpty(); // O(1)
  }

  public Handle<T> getH(int indice) {
    return handles.get(indice); // O(1)
  }

  public int hijoDerecho(int i) {
    return (2 * i) + 2; // O(1)
  }

  public int hijoIzquierdo(int i) {
    return (2 * i) + 1; // O(1)
  }

  public int padre(int i) {
    return (i - 1) / 2; // O(1)
  }

  public int cardinal() {
    return elementos.size(); // O(1)
  }

  public T verMax() {
    if (elementos == null) { // O(1)
      return null;
    } else {
      return elementos.get(0);
    }

  }

  public Handle<T> insertar(T valor) {
    elementos.add(valor); // O(1)
    Handle<T> h = new Handle<>(valor, elementos.size() - 1); // O(1)
    handles.add(h); // O(1)
    siftUp(h.posicion()); // O(log n)
    return h;
  }

  public T eliminarMax() {
    if (esVacio()) // O(1)
      return null;

    T max = elementos.get(0); // O(1)
    int last = elementos.size() - 1; // O(1)
    swap(0, last); // O(1)
    elementos.remove(last); // O(n)
    handles.remove(last); // O(n)

    if (!esVacio()) {
      siftDown(0); // O(log n)
    }
    return max;
    // O(n) es n por .remove. preguntar!!!!!!!
  }

  public void heapify(ArrayList<T> lista) {
    elementos = new ArrayList<>(lista); // O(n)
    handles = new ArrayList<>(); // O(1)

    for (int i = 0; i < lista.size(); i++) { // O(n)
      handles.add(new Handle<>(lista.get(i), i)); // O(1)
    }

    for (int i = (elementos.size() / 2) - 1; i >= 0; i--) { // O(n/2 - 1) = O(n)
      siftDown(i); // O(log n)
    }
    // O(n log n)
  }

  public void actualizar(IHandle<T> handle, T nuevoValor) {
    int i = handle.posicion(); // O(1)
    T viejoValor = elementos.get(i); // O(1)
    handles.get(i).actualizarValor(nuevoValor); // handles es una array de handle, obtengo el elemento con posicion i
                                                // y utilizo el metodo actualizarValor de mi public Class Handle para
                                                // actualizar con mi nuevoValor
                                                // O(1)
    elementos.set(i, nuevoValor); // O(1)

    if (nuevoValor.compareTo(viejoValor) > 0) { // O(1)
      siftUp(i); // O(log n) porque la altura es log n en un heap porque esta balanceado
    } else {
      siftDown(i); // O(log n) porque la altura es log n en un heap porque esta balanceado
    }
  }

  private void siftUp(int i) {
    while (i > 0) { // O(log n)
      int padre = padre(i);
      if (elementos.get(i).compareTo(elementos.get(padre)) > 0) { // O(1)
        swap(i, padre); // intercambia de lugar el padre con el hijo O(1)
        i = padre; // O(1)
      } else {
        break;
      }
    }
  }

  private void siftDown(int i) {
    int n = elementos.size(); // O(1)
    while (true) { // O(log n)
      int max = i; // O(1)
      int izq = hijoIzquierdo(i); // O(1)
      int der = hijoDerecho(i); // O(1)

      if (izq < n && elementos.get(izq).compareTo(elementos.get(max)) > 0) { // O(1)
        max = izq; // O(1)
      }
      if (der < n && elementos.get(der).compareTo(elementos.get(max)) > 0) { // O(1)
        max = der; // O(1)
      }

      if (max == i) // O(1)
        break;

      swap(i, max); // O(1)
      i = max; // O(1)
    }
  }

  private void swap(int i, int j) { // cambia los valores de i y j en en elementos y en el handle
    T tempElemento = elementos.get(i); // O(1)
    elementos.set(i, elementos.get(j)); // O(1)
    elementos.set(j, tempElemento); // O(1)

    Handle<T> hi = handles.get(i); // O(1)
    Handle<T> hj = handles.get(j); // O(1)
    handles.set(i, hj); // O(1)
    handles.set(j, hi); // O(1)
    hi.actualizarPosicion(j); // O(1)
    hj.actualizarPosicion(i); // O(1)
  }

  public static class Handle<T> implements IHandle<T> {
    private T valor;
    private int posicion;

    public Handle(T valor, int posicion) {
      this.valor = valor;
      this.posicion = posicion;
    }

    public T valor() {
      return valor; // O(1)
    }

    public int posicion() {
      return posicion; // O(1)
    }

    public void actualizarPosicion(int nuevaPos) {
      this.posicion = nuevaPos; // O(1)
    }

    public void actualizarValor(T nuevoValor) {
      this.valor = nuevoValor; // O(1)
    }
  }
}
