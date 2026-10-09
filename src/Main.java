import excepciones.StockInsuficienteException;
import pedidos.LineaPedido;
import pedidos.Pedido;
import productos.Producto;
import productos.ProductoService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final ProductoService productoService = new ProductoService();
    private static final List<Pedido> pedidos = new ArrayList<>();

    public static void main(String[] args) {
        int opcion = 0;

        while (opcion != 7) {
            mostrarMenu();

            try {
                opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1 -> agregarProducto();
                    case 2 -> listarProductos();
                    case 3 -> buscarActualizarProducto();
                    case 4 -> eliminarProducto();
                    case 5 -> crearPedido();
                    case 6 -> listarPedidos();
                    case 7 -> System.out.println("Programa finalizado.");
                    default -> System.out.println("Opcion no valida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Debes ingresar un numero valido.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }

            System.out.println();
        }

        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("===== GESTION DE PRODUCTOS =====");
        System.out.println("1. Agregar producto");
        System.out.println("2. Listar productos");
        System.out.println("3. Buscar/Actualizar producto");
        System.out.println("4. Eliminar producto");
        System.out.println("5. Crear pedido");
        System.out.println("6. Listar pedidos");
        System.out.println("7. Salir");
        System.out.print("Selecciona una opcion: ");
    }

    private static void agregarProducto() {
        System.out.print("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.print("Precio: ");
        double precio = Double.parseDouble(scanner.nextLine());

        System.out.print("Stock: ");
        int stock = Integer.parseInt(scanner.nextLine());

        Producto producto = new Producto(nombre, precio, stock);
        productoService.agregarProducto(producto);

        System.out.println("Producto agregado correctamente.");
    }

    private static void listarProductos() {
        List<Producto> productos = productoService.listarProductos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }

        for (Producto producto : productos) {
            System.out.println(producto);
        }
    }

    private static void buscarActualizarProducto() {
        System.out.print("Ingresa el ID del producto: ");
        int id = Integer.parseInt(scanner.nextLine());

        Producto producto = productoService.buscarPorId(id);

        if (producto == null) {
            System.out.println("Producto no encontrado.");
            return;
        }

        System.out.println(producto);
        System.out.println("1. Actualizar precio");
        System.out.println("2. Actualizar stock");
        System.out.println("3. No modificar");
        System.out.print("Selecciona una opcion: ");

        int opcion = Integer.parseInt(scanner.nextLine());

        switch (opcion) {
            case 1 -> {
                System.out.print("Nuevo precio: ");
                double precio = Double.parseDouble(scanner.nextLine());
                producto.setPrecio(precio);
                System.out.println("Precio actualizado.");
            }
            case 2 -> {
                System.out.print("Nuevo stock: ");
                int stock = Integer.parseInt(scanner.nextLine());
                producto.setStock(stock);
                System.out.println("Stock actualizado.");
            }
            case 3 -> System.out.println("Sin modificaciones.");
            default -> System.out.println("Opcion no valida.");
        }
    }

    private static void eliminarProducto() {
        System.out.print("ID del producto a eliminar: ");
        int id = Integer.parseInt(scanner.nextLine());

        if (productoService.eliminarProducto(id)) {
            System.out.println("Producto eliminado.");
        } else {
            System.out.println("Producto no encontrado.");
        }
    }

    private static void crearPedido()
            throws NumberFormatException {

        List<Producto> productos = productoService.listarProductos();

        if (productos.isEmpty()) {
            System.out.println("Primero debes registrar productos.");
            return;
        }

        listarProductos();

        System.out.print("Cuantas lineas tendra el pedido: ");
        int cantidadLineas = Integer.parseInt(scanner.nextLine());

        if (cantidadLineas <= 0) {
            System.out.println("La cantidad debe ser mayor que cero.");
            return;
        }

        List<LineaPedido> lineasTemporales = new ArrayList<>();

        try {
            for (int i = 0; i < cantidadLineas; i++) {
                System.out.print("ID del producto: ");
                int id = Integer.parseInt(scanner.nextLine());

                Producto producto = productoService.buscarPorId(id);

                if (producto == null) {
                    throw new IllegalArgumentException(
                            "No existe el producto con ID " + id
                    );
                }

                System.out.print("Cantidad: ");
                int cantidad = Integer.parseInt(scanner.nextLine());

                if (cantidad <= 0) {
                    throw new IllegalArgumentException(
                            "La cantidad debe ser mayor que cero."
                    );
                }

                int cantidadYaSolicitada = 0;

                for (LineaPedido linea : lineasTemporales) {
                    if (linea.getProducto().getId() == id) {
                        cantidadYaSolicitada += linea.getCantidad();
                    }
                }

                if (cantidad + cantidadYaSolicitada > producto.getStock()) {
                    throw new StockInsuficienteException(
                            "Stock insuficiente para " + producto.getNombre()
                    );
                }

                lineasTemporales.add(new LineaPedido(producto, cantidad));
            }

            Pedido pedido = new Pedido();

            for (LineaPedido linea : lineasTemporales) {
                pedido.agregarLinea(linea);
            }

            System.out.println("Detalle del pedido:");

            for (LineaPedido linea : pedido.getLineas()) {
                System.out.println(
                        linea.getProducto().getNombre()
                                + " x " + linea.getCantidad()
                                + " = $" + String.format(
                                "%.2f", linea.calcularSubtotal()
                        )
                );
            }

            System.out.println(
                    "Total: $" + String.format("%.2f", pedido.calcularTotal())
            );

            System.out.print("Confirmar pedido? (s/n): ");
            String confirmacion = scanner.nextLine();

            if (confirmacion.equalsIgnoreCase("s")) {
                for (LineaPedido linea : lineasTemporales) {
                    linea.getProducto().reducirStock(linea.getCantidad());
                }

                pedidos.add(pedido);
                System.out.println("Pedido registrado correctamente.");
            } else {
                System.out.println("Pedido cancelado.");
            }

        } catch (StockInsuficienteException e) {
            System.out.println("No se pudo crear el pedido: " + e.getMessage());
        }
    }

    private static void listarPedidos() {
        if (pedidos.isEmpty()) {
            System.out.println("Todavia no hay pedidos registrados.");
            return;
        }

        for (Pedido pedido : pedidos) {
            System.out.println("Pedido ID: " + pedido.getId());

            for (LineaPedido linea : pedido.getLineas()) {
                System.out.println(
                        "- " + linea.getProducto().getNombre()
                                + " x " + linea.getCantidad()
                                + " = $" + String.format(
                                "%.2f", linea.calcularSubtotal()
                        )
                );
            }

            System.out.println(
                    "Total: $" + String.format("%.2f", pedido.calcularTotal())
            );
            System.out.println("----------------------------");
        }
    }
}
