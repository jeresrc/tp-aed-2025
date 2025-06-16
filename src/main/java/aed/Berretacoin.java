package aed;

import java.util.ArrayList;

public class Berretacoin<T extends Comparable<T>> {
  private Heap<Transaccion> ultimo_bloque;
  private Heap<Usuario> usuarios;

  private int monto_bloque;
  private int cant_bloques;

  private ListaEnlazada<Transaccion> transacciones_copia;
  private int[] saldos;
  private ArrayList<Handle<ListaEnlazada<Transaccion>.Nodo>> lista_handles;

  public static class Handle<Nodo> {
    private Nodo valor;

    public Handle(Nodo valor) {
      this.valor = valor;
    }

    public Nodo valor() {
      return valor;
    }

    public void actualizarValor(Nodo valor) {
      this.valor = valor;
    }
  }

  public Berretacoin(int n_usuarios) {
    this.monto_bloque = 0;
    this.cant_bloques = 0;

    ArrayList<Usuario> lista_usuarios = new ArrayList<>();

    this.saldos = new int[n_usuarios];
    for (int i = 0; i < n_usuarios; i++) { // O(p)
      Usuario user = new Usuario(i + 1, 0);
      lista_usuarios.add(user);
    }

    this.usuarios = new Heap<>();
    usuarios.heapify(lista_usuarios); // O(n)
  }

  private void nuevoBloque(int cantidadTransacciones) {
    this.ultimo_bloque = new Heap<>();
    this.monto_bloque = 0;
    this.transacciones_copia = new ListaEnlazada<>();
    this.lista_handles = new ArrayList<>(cantidadTransacciones);
  }

  private void actualizarSaldoUsuario(int idUsuario, int cambioMonto) {
    int saldo_actual = saldos[idUsuario - 1];
    Usuario usuario_temp = new Usuario(idUsuario, saldo_actual);
    Heap.Handle<Usuario> handle = usuarios.getHandle(usuario_temp);
    Usuario usuario = handle.valor();

    int nuevo_salgo = usuario.saldo() + cambioMonto;
    Usuario usuario_actualizado = new Usuario(usuario.usuario(), nuevo_salgo);
    usuarios.actualizar(handle, usuario_actualizado);

    saldos[idUsuario - 1] = nuevo_salgo;
  }

  private void procesarTransaccion(Transaccion transaccion, int indice) {
    ListaEnlazada<Transaccion>.Nodo nodo = transacciones_copia.agregarAtrasAux(transaccion);
    lista_handles.add(new Handle<>(nodo));

    int comprador = transaccion.id_comprador();
    int vendedor = transaccion.id_vendedor();
    int monto = transaccion.monto();

    if (comprador != 0) {
      actualizarSaldoUsuario(comprador, -monto);
    }
    actualizarSaldoUsuario(vendedor, monto);
  }

  public void agregarBloque(Transaccion[] transacciones) {
    nuevoBloque(transacciones.length);
    ArrayList<Transaccion> lista_transacciones = new ArrayList<>();

    for (int i = 0; i < transacciones.length; i++) {
      Transaccion transaccion = transacciones[i];
      procesarTransaccion(transaccion, i);
      lista_transacciones.add(transaccion);

      if (cant_bloques >= 3000 && transaccion.id_comprador() != 0) {
        monto_bloque += transacciones[i].monto();
      }
    }

    if (cant_bloques < 3000) {
      for (int p = 1; p < transacciones.length; p++) {
        monto_bloque += transacciones[p].monto();
      }
    }

    this.ultimo_bloque.heapify(lista_transacciones); // O(n)
    this.cant_bloques++;
  }

  public Transaccion txMayorValorUltimoBloque() {
    return this.ultimo_bloque.verMax();
  }

  public Transaccion[] txUltimoBloque() {

    Transaccion[] res = new Transaccion[transacciones_copia.longitud()];
    ListaEnlazada<Transaccion>.Nodo actual = transacciones_copia.primeroNodo();
    int i = 0;
    while (actual != null) {
      res[i] = actual.valor;
      actual = actual.next;
      i++;
    }

    return res;
  }

  public int maximoTenedor() {
    Usuario maximo_usuario = this.usuarios.verMax();
    return maximo_usuario.usuario();
  }

  public int montoMedioUltimoBloque() {
    int cardinal = ultimo_bloque.cardinal();
    if (cardinal == 0) {
      return 0;
    }

    int divisor = (cant_bloques < 3000 && cardinal > 1) ? cardinal - 1 : cardinal;
    return this.monto_bloque / divisor;
  }

  private void revertirTransaccion(Transaccion transaccion) {
    int comprador = transaccion.id_comprador();
    int vendedor = transaccion.id_vendedor();
    int monto = transaccion.monto();

    if (comprador != 0) {
      actualizarSaldoUsuario(comprador, monto);
    }
    actualizarSaldoUsuario(vendedor, -monto);
  }

  public void hackearTx() {
    Transaccion transaccion_maxima = ultimo_bloque.verMax();
    ultimo_bloque.eliminarMax(); // O(log nb)

    revertirTransaccion(transaccion_maxima);

    monto_bloque -= transaccion_maxima.monto();

    Handle<ListaEnlazada<Transaccion>.Nodo> handleDelNodo = lista_handles.get(transaccion_maxima.id());
    ListaEnlazada<Transaccion>.Nodo nodoAeliminar = handleDelNodo.valor();
    transacciones_copia.eliminarPorNodo(nodoAeliminar);
  }
}
