package pio.daw.ra8.mercado.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad JPA que representa una ejecución completa del experimento
 * de mercado libre (distribución de Boltzmann/Pareto).
 *
 * Relaciones:
 *   Simulacion → Individuo:   @OneToMany (una simulación tiene muchos individuos)
 *   Simulacion → Intercambio: @OneToMany (una simulación tiene muchos intercambios)
 */
@Entity
public class Simulacion {

    @Id
    @GeneratedValue
    private long id;

    @Column(nullable = false)
    private String nombre;

    private int numRondas;
    private int numIndividuos;
    private double saldoInicial;

    /**
     * Lista de individuos de esta simulación.
     * cascade=ALL: al persistir/borrar la Simulacion se hace lo mismo con sus individuos.
     * fetch=LAZY: no carga los objetos en memoria hasta que se acceda a la lista.
     * mappedBy="simulacion": el lado propietario está en Individuo.simulacion.
     */
    @OneToMany(mappedBy = "simulacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Individuo> individuos = new ArrayList<>();

    /** Lista de intercambios de esta simulación. Mismo razonamiento que individuos. */
    @OneToMany(mappedBy = "simulacion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Intercambio> intercambios = new ArrayList<>();

    /** Constructor vacío requerido por JPA. */
    public Simulacion() {}

    public Simulacion(String nombre, int numRondas, int numIndividuos, double saldoInicial) {
        this.nombre        = nombre;
        this.numRondas     = numRondas;
        this.numIndividuos = numIndividuos;
        this.saldoInicial  = saldoInicial;
    }

    public long   getId()             { return id; }
    public String getNombre()         { return nombre; }
    public void   setNombre(String n) { nombre = n; }
    public int    getNumRondas()      { return numRondas; }
    public void   setNumRondas(int n) { numRondas = n; }
    public int    getNumIndividuos()  { return numIndividuos; }
    public void   setNumIndividuos(int n) { numIndividuos = n; }
    public double getSaldoInicial()   { return saldoInicial; }
    public void   setSaldoInicial(double s) { saldoInicial = s; }
    public List<Individuo>   getIndividuos()   { return individuos; }
    public List<Intercambio> getIntercambios() { return intercambios; }

    @Override
    public String toString() {
        return "Simulacion{id=" + id + ", nombre='" + nombre + "', rondas=" + numRondas
             + ", individuos=" + numIndividuos + ", saldoInicial=" + saldoInicial + '}';
    }
} 