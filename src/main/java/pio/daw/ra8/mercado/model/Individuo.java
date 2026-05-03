package pio.daw.ra8.mercado.model;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa un agente económico de la simulación.
 *
 * Relación:
 *   Individuo → Simulacion: @ManyToOne — es el lado PROPIETARIO (guarda la referencia).
 */
@Entity
public class Individuo {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String nombre;

    /** Saldo actual: cambia en cada intercambio. */
    private double saldoActual;

    /** Saldo inicial: referencia histórica, no cambia. */
    private double saldoInicial;

    /**
     * Simulación a la que pertenece.
     * fetch=LAZY: no carga toda la Simulacion (con sus miles de intercambios)
     * cada vez que se recupera un Individuo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Simulacion simulacion;

    /** Constructor vacío requerido por JPA. */
    public Individuo() {}

    public Individuo(String nombre, double saldoInicial, Simulacion simulacion) {
        this.nombre       = nombre;
        this.saldoInicial = saldoInicial;
        this.saldoActual  = saldoInicial; // arranca con el saldo completo
        this.simulacion   = simulacion;
    }

    public long       getId()           { return id; }
    public String     getNombre()       { return nombre; }
    public void       setNombre(String n) { nombre = n; }
    public double     getSaldoActual()  { return saldoActual; }
    public void       setSaldoActual(double s) { saldoActual = s; }
    public double     getSaldoInicial() { return saldoInicial; }
    public void       setSaldoInicial(double s) { saldoInicial = s; }
    public Simulacion getSimulacion()   { return simulacion; }
    public void       setSimulacion(Simulacion s) { simulacion = s; }

    @Override
    public String toString() {
        return "Individuo{id=" + id + ", nombre='" + nombre
             + "', saldoActual=" + String.format("%.2f", saldoActual) + '}';
    }
}