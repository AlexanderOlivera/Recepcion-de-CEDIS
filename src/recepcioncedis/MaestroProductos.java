package recepcioncedis;

import java.util.HashMap;
import java.util.Map;

/**
 * Catálogo global de productos para validar sobrantes.
 */
public class MaestroProductos {

    private Map<String, String> maestroProductos;

    public MaestroProductos() {
        maestroProductos = new HashMap<>();
        maestroProductos.put("111", "Coca-Cola 600ml Paquete");
        maestroProductos.put("222", "Sabritas Fuego Paquete");
        maestroProductos.put("333", "Gansito Paquete 12pzas");
        maestroProductos.put("444", "Agua Ciel 1L Paquete");
        maestroProductos.put("555", "Atún Dolores Paquete");
        maestroProductos.put("666", "Nescafé Dolca 100g");
        maestroProductos.put("777", "Fritos Limón y Sal 50g");
        maestroProductos.put("888", "Leche Lala 1L Entera");
        maestroProductos.put("999", "Cigarros Marlboro Rojo");
    }

    public boolean esProductoValido(String codigo) {
        return maestroProductos.containsKey(codigo);
    }

    public String getNombreProducto(String codigo) {
        return maestroProductos.getOrDefault(codigo, "Producto Inválido");
    }
}