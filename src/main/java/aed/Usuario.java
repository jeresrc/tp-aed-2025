package aed;

public class Usuario implements Comparable<Usuario> {
  private int id;
  private int saldo;
  private IHandle<Usuario> handle;

  public Usuario(int id, int saldo) {

    this.id = id;
    this.saldo = saldo;
    this.handle = new Handle<>(this, this.id);
  }

  public IHandle<Usuario> handle() {
    return handle;
  }

  public int usuario() {
    return id;
  }

  public int saldo() {
    return saldo;
  }

  public void setSaldo(int nuevoSaldo) {
    this.saldo = nuevoSaldo;
  }

  @Override
  public int compareTo(Usuario otro) {
    int comparacionSaldo = Integer.compare(this.saldo, otro.saldo);
    return comparacionSaldo != 0 ? comparacionSaldo : Integer.compare(otro.id, this.id);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Usuario usuario = (Usuario) obj;
    return id == usuario.id;
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
