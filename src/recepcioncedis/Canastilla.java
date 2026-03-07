package recepcioncedis;

import java.time.LocalDateTime;
import java.util.Objects;

public class Canastilla {

    private String codigo;
    private String tipo; 
    private LocalDateTime fechaEscaneo;

    public Canastilla(String codigo, String tipo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Código inválido");
        }
        this.codigo = codigo.trim();
        this.tipo = tipo;
        this.fechaEscaneo = LocalDateTime.now();
    }

    public String getCodigo() { return codigo; }
    public String getTipo() { return tipo; }

    @Override
    public String toString() {
        return "Canastilla " + tipo + " | ID: " + codigo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Canastilla that = (Canastilla) o;
        return codigo.equals(that.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigo);
    }
}