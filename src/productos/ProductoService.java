
package productos;

import java.util.ArrayList;
import java.util.List;

public class ProductoService {

    private final List<Producto> productos = new ArrayList<>();

    public void agregarProducto(Producto producto) {
        productos.add(producto);
    }

    public List<Producto> listarProductos() {
        return new ArrayList<>(productos);
    }

    public Producto buscarPorId(int id) {
        for (Producto producto : productos) {
            if (producto.getId() == id) {
                return producto;
            }
        }

        return null;
    }

    public Producto buscarPorNombre(String nombre) {
        for (Producto producto : productos) {
            if (producto.getNombre().equalsIgnoreCase(nombre)) {
                return producto;
            }
        }

        return null;
    }

    public boolean eliminarProducto(int id) {
        Producto producto = buscarPorId(id);

        if (producto == null) {
            return false;
        }

        productos.remove(producto);
        return true;
    }
}
