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
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null || getClass() != obj.getClass())
      return false;
    Usuario usuario = (Usuario) obj;
    return id == usuario.id;
  }
}
