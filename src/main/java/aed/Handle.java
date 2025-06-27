package aed;

interface IHandle<T> {
  public T valor();

  public void actualizarValor(T nuevoValor);

  public int posicion();

  public void actualizarPosicion(int indice);
}
