package aed;

public class Usuario implements Comparable<Usuario> {
  private int id;
  private int saldo;

  public Usuario(int id, int saldo) {
    this.id = id;
    this.saldo = saldo;
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
    if (this.saldo > otro.saldo) {
      return 1;
    }

    else if (this.saldo == otro.saldo) {
      if (this.id < otro.id) {
        return 1;
      } else if (this.id > otro.id) {
        return -1;
      } else {
        return 0;
      }
    } else {
      return -1;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;

    if (o == null || getClass() != o.getClass())
      return false;

    Usuario otro = (Usuario) o;

    return this.usuario() == otro.usuario(); // comparás solo por ID
  }
}
