package aed;

import java.util.ArrayList;

public class Berretacoin<T extends Comparable<T>> {
  private Heap<Transaccion> ultimo_bloque;
  private Heap<Usuario> usuarios;

  private int monto_bloque;
  private int cant_bloques;

  private ListaEnlazada<Transaccion> transacciones_copia;
  private IHandle<Usuario>[] lista_handles_usuarios;
  private ArrayList<IHandle<ListaEnlazada<Transaccion>>> lista_handles_transacciones; // lista de handles para poder
                                                                                      // modificar
  // la listaEnlazada en O(1)
  // puede ser un array simple porque no
  // vamos a tener mas elementos que
  // transacciones

  // P: cantidad total de usuarios
  // nb: cantidad de transacciones en el bloque

  public Berretacoin(int n_usuarios) {
    this.monto_bloque = 0; // O(1)
    this.cant_bloques = 0; // O(1)
    this.lista_handles_usuarios = new IHandle[n_usuarios + 1]; // O(1)

    ArrayList<Usuario> lista_usuarios = new ArrayList<>(); // O(1)

    for (int i = 1; i <= n_usuarios; i++) { // O(P)
      Usuario usuario = new Usuario(i, 0); // O(1) porque usuarios no pueden tener id negativo.
      lista_usuarios.add(usuario);
      lista_handles_usuarios[i] = usuario.handle();
    }

    this.usuarios = new Heap<>(); // O(1)
    usuarios.heapify(lista_usuarios); // O(P)
  }

  private void nuevoBloque(int cantidadTransacciones) { // es una funcion auxiliar que reinicia los atributos
                                                        // O(nb)
    this.ultimo_bloque = new Heap<>(); // O(1)
    this.monto_bloque = 0; // O(1)
    this.transacciones_copia = new ListaEnlazada<>(); // O(1)
    this.lista_handles_transacciones = new ArrayList<>(cantidadTransacciones); // O(nb)
    // la complejidad es O(nb)
  }

  private void actualizarSaldoUsuario(IHandle<Usuario> handleUsuario, int cambioMonto) { // es una funcion auxiliar que
    int saldo_actual = handleUsuario.valor().saldo();

    int nuevo_saldo = saldo_actual + cambioMonto; // O(1)
    handleUsuario.valor().setSaldo(nuevo_saldo); // O(1)

    usuarios.actualizar(handleUsuario, handleUsuario.valor()); // O(log P) porque la
                                                               // altura es log P
  }

  private void procesarTransaccion(Transaccion transaccion, int indice) { // es una funcion auxiliar
    IHandle handle = transacciones_copia.agregarAtras(transaccion); // accedemos a los nodos
    // de listaEnlazada
    // PROBLEMA!! O(1)
    // no usamos indice
    lista_handles_transacciones.add(handle); // N agregamos el handle del nodo al arra de lista_handles_transacciones en

    int comprador = transaccion.id_comprador(); // O(1)
    int vendedor = transaccion.id_vendedor(); // O(1)
    int monto = transaccion.monto(); // O(1)

    IHandle<Usuario> handleComprador = lista_handles_usuarios[comprador];
    IHandle<Usuario> handleVendedor = lista_handles_usuarios[vendedor];

    if (comprador != 0) {
      actualizarSaldoUsuario(handleComprador, -monto); // O(P)
    }

    actualizarSaldoUsuario(handleVendedor, monto); // O(P)
    // la complejidad es O(P)
  }

  public void agregarBloque(Transaccion[] transacciones) {
    nuevoBloque(transacciones.length); // O(nb)
    ArrayList<Transaccion> lista_transacciones = new ArrayList<>(); // O(1)

    for (int i = 0; i < transacciones.length; i++) { // O(nb)
      Transaccion transaccion = transacciones[i];
      procesarTransaccion(transaccion, i); // O(P)
      lista_transacciones.add(transaccion); // O(1)

      if (cant_bloques >= 3000 && transaccion.id_comprador() != 0) { // O(1)
        monto_bloque += transacciones[i].monto(); // O(1)
      }
    }

    if (cant_bloques < 3000) { // O(1)
      for (int p = 1; p < transacciones.length; p++) { // O(nb)
        monto_bloque += transacciones[p].monto(); // O(1)
      }
    }

    this.ultimo_bloque.heapify(lista_transacciones); // O(nb)
    this.cant_bloques++; // O(1)
    // la complejidad es O(nb*P)
  }

  public Transaccion txMayorValorUltimoBloque() {
    return this.ultimo_bloque.verMax(); // O(1)
    // la complejidad es O(1)
  }

  public Transaccion[] txUltimoBloque() {

    Transaccion[] res = new Transaccion[transacciones_copia.longitud()]; // O(nb)
    ListaEnlazada<Transaccion>.Nodo actual = transacciones_copia.primeroNodo(); // O(1)
    int i = 0; // O(1)
    while (actual != null) { // O(nb)
      res[i] = actual.valor; // O(1)
      actual = actual.next; // O(1)
      i++; // O(1)
    }

    return res;
    // la complejidad es O(nb)
  }

  public int maximoTenedor() {
    Usuario maximo_usuario = this.usuarios.verMax(); // O(1)
    return maximo_usuario.usuario();
    // la complejidad es O(1)
  }

  public int montoMedioUltimoBloque() {
    int cardinal = ultimo_bloque.cardinal(); // O(1)
    if (cardinal == 0) { // O(1)
      return 0;
    }

    int divisor = (cant_bloques < 3000 && cardinal > 1) ? cardinal - 1 : cardinal; // O(1)
    return this.monto_bloque / divisor; // O(1)
    // la complejidad es O(1)
  }

  private void revertirTransaccion(Transaccion transaccion) { // es una funcion auxiliar
    int comprador = transaccion.id_comprador(); // O(1)
    int vendedor = transaccion.id_vendedor(); // O(1)
    int monto = transaccion.monto(); // O(1)

    IHandle<Usuario> handleComprador = lista_handles_usuarios[comprador];
    IHandle<Usuario> handleVendedor = lista_handles_usuarios[vendedor];

    if (comprador != 0) { // O(1)
      actualizarSaldoUsuario(handleComprador, monto); // O(P)
    }

    actualizarSaldoUsuario(handleVendedor, -monto); // O(P)
    // la complejidad es O(P)
  }

  public void hackearTx() {
    Transaccion transaccion_maxima = ultimo_bloque.verMax(); // O(1)
    ultimo_bloque.eliminarMax(); // O(nb)

    revertirTransaccion(transaccion_maxima);// O(P)

    monto_bloque -= transaccion_maxima.monto(); // O(1)

    // IHandle<ListaEnlazada<Transaccion>> handleDelNodo =
    // lista_handles_transacciones.get(transaccion_maxima.id()); // O(1)
    // ListaEnlazada<Transaccion>.nodoAeliminar = handleDelNodo.valor(); // O(1)
    // transacciones_copia.eliminarPorNodo(nodoAeliminar);// O(1)
    // la complejidad es O(nb + P)
  }
}
