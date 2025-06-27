package aed;

public class ListaEnlazada<T> implements Secuencia<T> {
  private Nodo primero;
  private Nodo ultimo;
  private int largo;

  public class Handle implements IHandle<T> {
    private Nodo nodo;
    private int posicion;

    public Handle(Nodo nodo, int posicion) {
      this.nodo = nodo;
      this.posicion = posicion;
    }

    public T valor() {
      return nodo.valor;
    }

    public int posicion() {
      return posicion;
    }

    public void actualizarPosicion(int nuevaPos) {
      this.posicion = nuevaPos;
    }

    public void actualizarValor(T nuevoValor) {
      this.nodo.valor = nuevoValor;
    }
  }

  public class Nodo {
    T valor;
    Nodo next;
    Nodo prev;

    public Nodo(T v) {
      valor = v;
      next = null;
      prev = null;
    }
  }

  public ListaEnlazada(ListaEnlazada<T> lista) {
    Nodo actual = lista.primero;
    while (actual != null) {
      agregarAtras(actual.valor);
      actual = actual.next;
    }
  }

  public ListaEnlazada() {
    primero = null;
    ultimo = null;
    largo = 0;
  }

  public int longitud() {
    return largo;
  }

  public Nodo primeroNodo() {
    return primero;
  }

  public Handle agregarAtras(T elem) {
    Nodo nuevo = new Nodo(elem);
    if (primero == null) {
      primero = nuevo;
      ultimo = nuevo;
    } else {
      ultimo.next = nuevo;
      nuevo.prev = ultimo;
      ultimo = nuevo;
    }

    largo++;

    return new Handle(nuevo, largo - 1);
  }

  public T obtener(int i) { // O(n)
    Nodo actual = primero;
    for (int j = 0; j < i; j++) {
      actual = actual.next;
    }

    return actual.valor;
  }

  public void eliminarPorHandle(IHandle<Nodo> handle) {
    Nodo nodo = handle.valor();
    eliminarPorNodo(nodo);
  }

  public void eliminarPorNodo(Nodo nodo) {
    if (largo == 0 || nodo == null) {
      throw new UnsupportedOperationException("No existen nodos en la lista");
    }

    Nodo anterior = nodo.prev;
    Nodo proximo = nodo.next;

    if (nodo == primero && nodo == ultimo) {
      primero = null;
      ultimo = null;
    } else if (nodo == primero) {
      primero = nodo.next;
      if (primero != null)
        primero.prev = null;
    } else if (nodo == ultimo) {
      ultimo = nodo.prev;
      if (ultimo != null)
        ultimo.next = null;
    } else {
      anterior.next = proximo;
      if (proximo != null)
        proximo.prev = anterior;
    }

    largo--;
  }

  public void eliminar(int i) { // O(n)
    Nodo actual = primero;
    Nodo prev = primero;
    for (int j = 0; j < i; j++) {
      prev = actual;
      actual = actual.next;
    }
    if (i == 0) {
      primero = actual.next;
    } else {
      prev.next = actual.next;
    }
    largo = largo - 1;

  }

  public void modificarPosicion(int indice, T elem) {
    Nodo actual = primero;
    for (int j = 0; j < indice; j++) {
      actual = actual.next;
    }
    actual.valor = elem;

  }

  private class ListaIterador implements Iterador<T> {
    private int puntero;

    public ListaIterador() {
      puntero = 0;
    }

    public boolean haySiguiente() {
      return puntero < largo;

    }

    public boolean hayAnterior() {
      if (puntero == 0) {
        return false;
      } else {
        return true;
      }
    }

    public T siguiente() {
      Nodo actual = primero;
      for (int i = 0; i < puntero; i++) {
        actual = actual.next;
      }
      puntero += 1;
      return actual.valor;

    }

    public T anterior() {
      Nodo actual = ultimo;
      for (int i = largo; i > puntero; i--) {
        actual = actual.prev;
      }
      puntero -= 1;
      return actual.valor;
    }
  }

  public Iterador<T> iterador() {
    return new ListaIterador();
  }

}
