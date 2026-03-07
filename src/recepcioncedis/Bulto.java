package recepcioncedis;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Bulto {

    private String codigo;
    private String nombre;
    private LocalDateTime fechaEscaneo;

    public Bulto(String codigo, String nombre) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("El código no puede estar vacío.");
        }
        this.codigo = codigo.trim();
        this.nombre = nombre;
        this.fechaEscaneo = LocalDateTime.now(); 
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }

    public String getFechaFormateada() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return fechaEscaneo.format(formatter);
    }

    @Override
    public String toString() {
        return "Producto: " + nombre + " | Código: " + codigo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bulto bulto = (Bulto) o;
        return codigo.equals(bulto.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}