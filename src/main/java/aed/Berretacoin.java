package aed;

import java.util.ArrayList;

public class Berretacoin<T extends Comparable<T>> {
  private Heap<Transaccion> ultimo_bloque;
  private Heap<Usuario> Usuario;

  private int monto_bloque;
  private int cant_bloques;

  private ListaEnlazada<Transaccion> transacciones_copia;
  private int[] saldo; //
  private ArrayList<Handle<ListaEnlazada<Transaccion>.Nodo>> lista_de_handles;

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

    this.saldo = new int[n_usuarios];
    for (int i = 0; i < n_usuarios; i++) { // O(p)
      Usuario user = new Usuario(i + 1, 0);
      lista_usuarios.add(user);
    }

    this.Usuario = new Heap<>();
    Usuario.heapify(lista_usuarios); // O(n)
  }

  public void agregarBloque(Transaccion[] transacciones) {
    this.ultimo_bloque = new Heap<>();
    this.monto_bloque = 0;
    ArrayList<Transaccion> trans = new ArrayList<>();
    this.transacciones_copia = new ListaEnlazada<>();
    this.lista_de_handles = new ArrayList<Handle<ListaEnlazada<Transaccion>.Nodo>>(transacciones.length);

    for (int i = 0; i < transacciones.length; i++) {
      Transaccion tx = transacciones[i];
      int comprador = tx.id_comprador();
      int vendedor = tx.id_vendedor();
      int monto = tx.monto();

      ListaEnlazada<Transaccion>.Nodo nodo = transacciones_copia.agregarAtrasAux(tx);
      lista_de_handles.add(new Handle<ListaEnlazada<Transaccion>.Nodo>(nodo));

      if (comprador != 0) {
        Usuario handle_c = new Usuario(comprador, saldo[comprador - 1]);
        Heap.Handle<Usuario> handleComprador = Usuario.getHandle(handle_c);
        Usuario usuarioComprador = handleComprador.valor();
        Usuario actualizado_c = new Usuario(usuarioComprador.usuario(), usuarioComprador.saldo() - monto);

        Usuario.actualizar(handleComprador, actualizado_c);
        saldo[comprador - 1] -= monto;
      }

      Usuario handle_v = new Usuario(vendedor, saldo[vendedor - 1]);
      Heap.Handle<Usuario> handleVendedor = Usuario.getHandle(handle_v);
      Usuario usuarioVendedor = handleVendedor.valor();
      Usuario actualizado_v = new Usuario(usuarioVendedor.usuario(), usuarioVendedor.saldo() + monto);
      Usuario.actualizar(handleVendedor, actualizado_v);

      saldo[vendedor - 1] += monto;

      trans.add(tx);
      if (cant_bloques >= 3000 && comprador != 0) {
        monto_bloque += transacciones[i].monto();
      }
    }

    if (cant_bloques < 3000) {
      for (int p = 1; p < transacciones.length; p++) {
        monto_bloque += transacciones[p].monto();
      }
    }

    this.ultimo_bloque.heapify(trans); // O(n)
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
    Usuario maximo_usuario = this.Usuario.verMax();
    return maximo_usuario.usuario();
  }

  public int montoMedioUltimoBloque() {
    int cardinal = ultimo_bloque.cardinal();
    if (cardinal == 0) {
      return 0;
    } else if (cant_bloques < 3000 && cardinal > 1) {
      return this.monto_bloque / (cardinal - 1);
    } else {
      return this.monto_bloque / cardinal;
    }
  }

  public void hackearTx() {
    Transaccion maximo = ultimo_bloque.verMax();
    ultimo_bloque.eliminarMax(); // O(log nb)

    if (maximo.id_comprador() != 0) {

      Usuario hande_c = new Usuario(maximo.id_comprador(), saldo[(maximo.id_comprador()) - 1]);
      Heap.Handle<Usuario> handleComprador = Usuario.getHandle(hande_c);
      Usuario usuario_comprador = handleComprador.valor();
      Usuario actualizado_c = new Usuario(usuario_comprador.usuario(), usuario_comprador.saldo() + maximo.monto());

      Usuario.actualizar(handleComprador, actualizado_c); // O(log p)

      saldo[(maximo.id_comprador()) - 1] += maximo.monto();
    }

    Usuario hande_v = new Usuario(maximo.id_vendedor(), saldo[(maximo.id_vendedor()) - 1]);
    Heap.Handle<Usuario> handleVendedor = Usuario.getHandle(hande_v);
    Usuario usuario_vendedor = handleVendedor.valor();
    Usuario actualizado_v = new Usuario(usuario_vendedor.usuario(), usuario_vendedor.saldo() - maximo.monto());
    Usuario.actualizar(handleVendedor, actualizado_v); // O(log p)
    saldo[(maximo.id_vendedor()) - 1] -= maximo.monto();

    monto_bloque -= maximo.monto();

    Handle<ListaEnlazada<Transaccion>.Nodo> handleDelNodo = lista_de_handles.get(maximo.id());
    ListaEnlazada<Transaccion>.Nodo nodoAeliminar = handleDelNodo.valor();
    transacciones_copia.eliminarPorNodo(nodoAeliminar);
  }
}
