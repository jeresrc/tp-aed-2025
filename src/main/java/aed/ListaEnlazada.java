package aed;

public class ListaEnlazada<T> implements Secuencia<T> {
  private Nodo primero;
  private Nodo ultimo;
  private int largo;

  public class Nodo {
    T valor;
    Nodo sig;
    Nodo ant;

    public Nodo(T v) {
      valor = v;
      sig = null;
      ant = null;
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

  public void agregarAdelante(T elem) {
    Nodo nuevo = new Nodo(elem);

    if (primero == null) {
      primero = nuevo;
      ultimo = nuevo;
      largo += 1;
    }

    else {
      nuevo.sig = primero;
      primero = nuevo;
      largo += 1;
    }

  }

  public void agregarAtras(T elem) {
    Nodo nuevo = new Nodo(elem);
    if (primero == null) {
      primero = nuevo;
      ultimo = nuevo;
    } else {
      ultimo.sig = nuevo;
      nuevo.ant = ultimo;
      ultimo = nuevo;
    }
    largo++;

    // Si el elemento es Transaccion, guardo la referencia al nodo

  }

  public Nodo primeroNodo() {
    return primero;
  }

  public Nodo agregarAtrasAux(T elem) {
    Nodo nuevo = new Nodo(elem);
    if (primero == null) {
      primero = nuevo;
      ultimo = nuevo;
    } else {
      ultimo.sig = nuevo;
      nuevo.ant = ultimo;
      ultimo = nuevo;
    }

    largo++;

    // Si el elemento es Transaccion, guardo la referencia al nodo

    return nuevo;
  }

  public T obtener(int i) { // O(n)
    Nodo actual = primero;
    for (int j = 0; j < i; j++) {
      actual = actual.sig;
    }

    return actual.valor;
  }

  public void eliminarPorNodo(Nodo nodo) {
    if (largo == 0 || nodo == null) {
      throw new UnsupportedOperationException("No se puede eliminar este nodo");
    }

    Nodo anterior = nodo.ant;
    Nodo proximo = nodo.sig;

    if (nodo == primero && nodo == ultimo) {
      primero = null;
      ultimo = null;
    } else if (nodo == primero) {
      primero = nodo.sig;
      if (primero != null)
        primero.ant = null;
    } else if (nodo == ultimo) {
      ultimo = nodo.ant;
      if (ultimo != null)
        ultimo.sig = null;
    } else {
      anterior.sig = proximo;
      if (proximo != null)
        proximo.ant = anterior;
    }

    largo--;
  }

  public void eliminar(int i) { // O(n)
    Nodo actual = primero;
    Nodo prev = primero;
    for (int j = 0; j < i; j++) {
      prev = actual;
      actual = actual.sig;
    }
    if (i == 0) {
      primero = actual.sig;
    } else {
      prev.sig = actual.sig;
    }
    largo = largo - 1;

  }

  public void modificarPosicion(int indice, T elem) {
    Nodo actual = primero;
    for (int j = 0; j < indice; j++) {
      actual = actual.sig;
    }
    actual.valor = elem;

  }

  public ListaEnlazada(ListaEnlazada<T> lista) {
    Nodo actual = lista.primero;
    while (actual != null) {
      agregarAtras(actual.valor);
      actual = actual.sig;
    }
  }

  @Override
  public String toString() {
    Nodo actual = primero;
    StringBuilder l = new StringBuilder();
    while (actual != null) {
      if (actual.sig == null) {
        l.append(actual.valor + "]");
      } else if (actual == primero) {
        l.append("[" + actual.valor + ", ");
      } else {
        l.append(actual.valor + ", ");
      }
      actual = actual.sig;
    }
    return l.toString();
  }

  private class ListaIterador implements Iterador<T> {
    private int dedito;

    public ListaIterador() {
      dedito = 0;
    }

    public boolean haySiguiente() {
      return dedito < largo;

    }

    public boolean hayAnterior() {
      if (dedito == 0) {
        return false;
      } else {
        return true;
      }
    }

    public T siguiente() {
      Nodo actual = primero;
      for (int i = 0; i < dedito; i++) {
        actual = actual.sig;
      }
      dedito += 1;
      return actual.valor;

    }

    public T anterior() {
      Nodo actual = ultimo;
      for (int i = largo; i > dedito; i--) {
        actual = actual.ant;
      }
      dedito -= 1;
      return actual.valor;
    }
  }

  public Iterador<T> iterador() {
    return new ListaIterador();
  }

}
