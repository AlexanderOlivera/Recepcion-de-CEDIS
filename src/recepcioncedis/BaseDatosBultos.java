package recepcioncedis;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class BaseDatosBultos {

    // Mapa Principal: ID Transferencia -> Mapa de Bultos
    private Map<String, Map<String, String>> inventarioBultos;
    // Mapa Principal: ID Transferencia -> Mapa de Canastillas (Código -> Tipo)
    private Map<String, Map<String, String>> inventarioCanastillas;
    
    // Datos de la sesión actual
    private Map<String, String> bultosActuales;
    private Map<String, String> canastillasActuales;

    public BaseDatosBultos() {
        inventarioBultos = new TreeMap<>();
        inventarioCanastillas = new TreeMap<>();
        
        crearDatosDePrueba();
        
        bultosActuales = new HashMap<>(); 
        canastillasActuales = new HashMap<>();
    }

    private void crearDatosDePrueba() {
        // --- TRANSFERENCIA 1 (Mix) ---
        Map<String, String> b1 = new HashMap<>();
        b1.put("111", "Coca-Cola 600ml Paquete");
        b1.put("222", "Sabritas Fuego Paquete");
        inventarioBultos.put("TRF-2025-001", b1);
        
        Map<String, String> c1 = new HashMap<>();
        c1.put("100100", "Gris - Abarrotes");
        c1.put("100101", "Gris - Abarrotes");
        c1.put("200200", "Azul - Farmacia");
        inventarioCanastillas.put("TRF-2025-001", c1);

        // --- TRANSFERENCIA 2 (Frío) ---
        Map<String, String> b2 = new HashMap<>();
        b2.put("444", "Agua Ciel 1L Paquete");
        inventarioBultos.put("TRF-2025-002", b2);
        
        Map<String, String> c2 = new HashMap<>();
        c2.put("300300", "Verde - Refrigerado");
        c2.put("300301", "Amarilla - Congelado");
        inventarioCanastillas.put("TRF-2025-002", c2);

        // --- TRANSFERENCIA 3 ---
        Map<String, String> b3 = new HashMap<>();
        b3.put("333", "Gansito Paquete 12pzas");
        inventarioBultos.put("TRF-2025-003", b3);
        
        Map<String, String> c3 = new HashMap<>();
        c3.put("400400", "Vino - Valor Alto"); // Botellas
        c3.put("100500", "Gris - Cigarros");
        inventarioCanastillas.put("TRF-2025-003", c3);

        // --- TRANSFERENCIA 4 ---
        Map<String, String> b4 = new HashMap<>();
        b4.put("222", "Sabritas Fuego Paquete");
        inventarioBultos.put("TRF-2025-004", b4);
        inventarioCanastillas.put("TRF-2025-004", new HashMap<>()); // Sin canastillas

        // --- TRANSFERENCIA 5 ---
        Map<String, String> b5 = new HashMap<>();
        b5.put("555", "Atún Dolores Paquete");
        inventarioBultos.put("TRF-2025-005", b5);
        
        Map<String, String> c5 = new HashMap<>();
        c5.put("100999", "Gris - Abarrotes");
        inventarioCanastillas.put("TRF-2025-005", c5);
    }

    public String[] getListaIDs() {
        return inventarioBultos.keySet().toArray(new String[0]);
    }

    public void setTransferenciaActual(String id) {
        bultosActuales = inventarioBultos.getOrDefault(id, new HashMap<>());
        canastillasActuales = inventarioCanastillas.getOrDefault(id, new HashMap<>());
    }

    // --- MÉTODOS BULTOS ---
    public boolean esBultoEsperado(String codigo) { return bultosActuales.containsKey(codigo); }
    public String getNombreBulto(String codigo) { return bultosActuales.getOrDefault(codigo, "Desconocido"); }
    public Map<String, String> getBultosEsperados() { return bultosActuales; }

    // --- MÉTODOS CANASTILLAS ---
    public boolean esCanastillaEsperada(String codigo) { return canastillasActuales.containsKey(codigo); }
    public String getTipoCanastilla(String codigo) { return canastillasActuales.getOrDefault(codigo, "Canastilla Extra"); }
    public Map<String, String> getCanastillasEsperadas() { return canastillasActuales; }
}