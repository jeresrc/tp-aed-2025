package aed;

public class Handle<T extends Comparable<T>> {
  private T ref;
  private int indice;

  public Handle(T ref, int indice) {
    this.ref = ref;
    this.indice = indice;
  }

  public T getElemento() {
    return ref;
  }

  public boolean esVacia() {
    if (ref == null) {
      return true;
    }
    return false;
  }

  public void setElemento(T ref) {
    this.ref = ref;
  }

  public int getIndice() {
    return indice;
  }

  public void setIndice(int indice) {
    this.indice = indice;
  }

}
