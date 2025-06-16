package aed;

public class Transaccion implements Comparable<Transaccion> {
  private int id;
  private int id_comprador;
  private int id_vendedor;
  private int monto;

  public Transaccion(int id, int id_comprador, int id_vendedor, int monto) {
    this.id = id;
    this.id_comprador = id_comprador;
    this.id_vendedor = id_vendedor;
    this.monto = monto;
  }

  @Override
  public int compareTo(Transaccion otro) {
    if (this.monto > otro.monto) {
      return 1;
    } else if (this.monto == otro.monto) {
      if (this.id > otro.id) {
        return 1;
      } else if (this.id < otro.id) {
        return -1;
      } else {
        return 0;
      }
    } else {
      return -1;
    }
  }

  @Override
  public boolean equals(Object otro) {
    if (this == otro)
      return true;
    if (otro == null || getClass() != otro.getClass())
      return false;
    Transaccion that = (Transaccion) otro;
    return id == that.id &&
        id_comprador == that.id_comprador &&
        id_vendedor == that.id_vendedor &&
        monto == that.monto;
  }

  public int monto() {
    return monto;
  }

  public int id() {
    return id;
  }

  public int id_comprador() {
    return id_comprador;
  }

  public int id_vendedor() {
    return id_vendedor;
  }
}
