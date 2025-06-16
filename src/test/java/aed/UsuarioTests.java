
package aed;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

public class UsuarioTests {

  @Test
  public void crearUsuario() {
    Usuario usuario = new Usuario(1, 100);

    assertEquals(usuario.saldo(), 100);
    assertEquals(usuario.usuario(), 1);
  }

  @Test
  public void compararUsuarioes() {
    Usuario usuario0 = new Usuario(10, 100);
    Usuario usuario1 = new Usuario(5, 10);
    Usuario usuario2 = new Usuario(6, 200);
    Usuario usuario3 = new Usuario(7, 200);
    Usuario usuario4 = new Usuario(6, 200);

    assertTrue(usuario0.compareTo(usuario1) == 1);
    assertTrue(usuario1.compareTo(usuario0) == -1);
    assertTrue(usuario0.compareTo(usuario3) == -1);
    assertTrue(usuario2.compareTo(usuario3) == 1);
    assertTrue(usuario2.compareTo(usuario4) == 0);
    assertTrue(usuario2.equals(usuario4));
    assertFalse(usuario3.equals(usuario4));
    assertTrue(usuario3.compareTo(usuario4) == -1);
  }
}
