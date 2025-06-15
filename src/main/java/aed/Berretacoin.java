package aed;

import java.util.ArrayList;

public class Berretacoin<T extends Comparable<T>> {
  private Heap<Transaccion> ultimo_bloque;
  private Heap<Usuario> Usuario;

  private int monto_bloque;
  private int cant_bloques;

  private ListaEnlazada<Transaccion> transacciones_copia;
  private int[] saldo; //
  private ArrayList<Handle<ListaEnlazada.Nodo>> lista_de_handles;

  public static class Handle<Nodo> {
    private Nodo valor;

    public Handle(Nodo valor) {
      this.valor = valor;
    }

    public Nodo valor() {
      return valor;
    }

    public void actualizarValor(Nodo nuevoValor) {
      this.valor = nuevoValor;
    }
  }

  public Berretacoin(int n_usuarios) {// Loop de O(p) + heapify O(p) = O(p)
    this.monto_bloque = 0; // Al no haber bloques todavia, no hay transaciones por lo que el monto es cero
    this.cant_bloques = 0; // Al arrancar mi Berretacoin mi cant bloques es cero

    ArrayList<Usuario> lista_usuarios = new ArrayList<>();

    this.saldo = new int[n_usuarios];
    for (int i = 0; i < n_usuarios; i++) { // O(p)
      Usuario user = new Usuario(i + 1, 0); // O(1)
      lista_usuarios.add(user);
    }

    this.Usuario = new Heap<>(); // Inicio mi heap Usarios, donde voy a guardar el saldo de cada usuario, O(1)
    Usuario.heapify(lista_usuarios); // Convierto mi lista_usuarios en un heap con complejidad Linneal Tq = O(n)
  }

  public void agregarBloque(Transaccion[] transacciones) {// Itera nb transacciones, y hace heap.actualizar O(log p) dos
                                                          // veces por transacción
    this.ultimo_bloque = new Heap<>(); // Nuevo bloque por lo que borro el anterior heap y inicio denuevo, O(1)
    this.monto_bloque = 0; // Arranco un nuevo bloque por lo que reinicio el monto bloque
    ArrayList<Transaccion> trans = new ArrayList<>(); // Creo un ArrayList para guardar todas las transaciones del
                                                      // bloque, O(1)
    this.transacciones_copia = new ListaEnlazada<>();
    this.lista_de_handles = new ArrayList<Handle<ListaEnlazada.Nodo>>(transacciones.length);

    for (int i = 0; i < transacciones.length; i++) { // O(nb)
      Transaccion tx = transacciones[i]; // O(1)
      int comprador = tx.id_comprador(); // O(1)
      int vendedor = tx.id_vendedor(); // O(1)
      int monto = tx.monto(); // O(1)

      ListaEnlazada.Nodo nodo = transacciones_copia.agregarAtrasAux(tx);
      lista_de_handles.add(new Handle<ListaEnlazada.Nodo>(nodo));

      if (comprador != 0) { // Si el comprador no es el sistema (id_comprador == 0) //O(1)
        Usuario handle_c = new Usuario(comprador, saldo[comprador - 1]); // O(1)
        Heap.Handle<Usuario> handleComprador = Usuario.getHandle(handle_c); // Obtener el handle del comprador //O(1)
        Usuario usuarioComprador = handleComprador.valor(); // Obtengo el monto del comprador en el handle //O(1)
        Usuario actualizado_c = new Usuario(usuarioComprador.usuario(), usuarioComprador.saldo() - monto); // O(1)
        Usuario.actualizar(handleComprador, actualizado_c); // Reordenar el heap con el nuevo monto del comprador
                                                            // //O(log p)
        saldo[comprador - 1] -= monto; // O(1)
      }

      // Actualizar el saldo del vendedor
      Usuario handle_v = new Usuario(vendedor, saldo[vendedor - 1]); // O(1)
      Heap.Handle<Usuario> handleVendedor = Usuario.getHandle(handle_v); // Obtener el handle del vendedor //O(1)
      Usuario usuarioVendedor = handleVendedor.valor(); // Obtengo el monto del vendedor en el handle //O(1)
      // usuarioVendedor.setSaldo(usuarioVendedor.saldo() + monto); // Actualizo el
      // monto vendedor
      Usuario actualizado_v = new Usuario(usuarioVendedor.usuario(), usuarioVendedor.saldo() + monto); // O(1)
      Usuario.actualizar(handleVendedor, actualizado_v); // Reordenar el heap con el nuevo monto del vendedor //O(log
                                                         // p)

      saldo[vendedor - 1] += monto; // O(1)

      trans.add(tx); // O(1)
      if (cant_bloques >= 3000 && comprador != 0) {
        monto_bloque += transacciones[i].monto(); // O(1)
      }
    }

    if (cant_bloques < 3000) {
      for (int p = 1; p < transacciones.length; p++) { // O(n-1)
        monto_bloque += transacciones[p].monto();
      }
    }

    this.ultimo_bloque.heapify(trans); // Convierto mi trans en un heap con complejidad Linneal tq : O(n)
    this.cant_bloques++; // Sumo 1 a la cantidad de bloques de mi Berretacoin //O(1)

  }

  public Transaccion txMayorValorUltimoBloque() { // Devuelve raíz del heap ultimo_bloque, O(1)
    return this.ultimo_bloque.verMax();
  }

  public Transaccion[] txUltimoBloque() {// Recorre la lista enlazada una sola vez, O(nb)

    Transaccion[] res = new Transaccion[transacciones_copia.longitud()];
    ListaEnlazada<Transaccion>.Nodo actual = transacciones_copia.primeroNodo();
    int i = 0;
    while (actual != null) {
      res[i] = actual.valor;
      actual = actual.sig;
      i++;
    }

    return res;
  }

  public int maximoTenedor() {// Devuelve el máximo del heap de Usuario, O(1)
    Usuario maximo_usuario = this.Usuario.verMax(); // Guardo el id_usuario y el saldo del usuario con mayor saldo
    return maximo_usuario.usuario(); // Devuelvo el id del usuario con mayor saldo
  }

  public int montoMedioUltimoBloque() {// Accede a una suma ya calculada y la divide, O(1)
    int cardinal = ultimo_bloque.cardinal();
    if (cardinal == 0) {
      return 0;
    } else if (cant_bloques < 3000 && cardinal > 1) {
      return this.monto_bloque / (cardinal - 1);
    } else {
      return this.monto_bloque / cardinal;
    }
  }

  public void hackearTx() { // Elimina max del heap (O(log nb)) y actualiza dos heaps (O(log p) c/u)

    Transaccion maximo = ultimo_bloque.verMax(); // O(1)
    ultimo_bloque.eliminarMax(); // O(log nb)

    if (maximo.id_comprador() > 0) {

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

    Handle<ListaEnlazada.Nodo> handleDelNodo = lista_de_handles.get(maximo.id());
    ListaEnlazada.Nodo nodoAeliminar = handleDelNodo.valor();
    transacciones_copia.eliminarPorNodo(nodoAeliminar);
  }
}
