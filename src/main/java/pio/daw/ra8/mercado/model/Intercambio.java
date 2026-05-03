package pio.daw.ra8.mercado.model;

import jakarta.persistence.*;

/**
 * Entidad JPA que representa un intercambio de dinero entre dos individuos.
 *
 * Relaciones:
 *   Intercambio → Simulacion:       @ManyToOne
 *   Intercambio → Individuo emisor:   @ManyToOne (el que pierde dinero)
 *   Intercambio → Individuo receptor: @ManyToOne (el que gana dinero)
 */
@Entity
public class Intercambio {

    @Id
    @GeneratedValue
    private long id;

    /** Número de ronda en la que ocurrió (1-basado). */
    private int numRonda;

    /** Cantidad transferida en u.m. Siempre positiva. */
    private double importe;

    @ManyToOne(fetch = FetchType.LAZY)
    private Simulacion simulacion;

    /** Individuo que transfiere dinero (pierde importe u.m.). */
    @ManyToOne(fetch = FetchType.LAZY)
    private Individuo emisor;

    /** Individuo que recibe dinero (gana importe u.m.). */
    @ManyToOne(fetch = FetchType.LAZY)
    private Individuo receptor;

    /** Constructor vacío requerido por JPA. */
    public Intercambio() {}

    public Intercambio(int numRonda, double importe,
                       Individuo emisor, Individuo receptor, Simulacion simulacion) {
        this.numRonda   = numRonda;
        this.importe    = importe;
        this.emisor     = emisor;
        this.receptor   = receptor;
        this.simulacion = simulacion;
    }

    public long       getId()         { return id; }
    public int        getNumRonda()   { return numRonda; }
    public void       setNumRonda(int n) { numRonda = n; }
    public double     getImporte()    { return importe; }
    public void       setImporte(double i) { importe = i; }
    public Simulacion getSimulacion() { return simulacion; }
    public void       setSimulacion(Simulacion s) { simulacion = s; }
    public Individuo  getEmisor()     { return emisor; }
    public void       setEmisor(Individuo e) { emisor = e; }
    public Individuo  getReceptor()   { return receptor; }
    public void       setReceptor(Individuo r) { receptor = r; }

    @Override
    public String toString() {
        return "Intercambio{id=" + id + ", ronda=" + numRonda
             + ", importe=" + String.format("%.2f", importe)
             + ", emisor=" + (emisor   != null ? emisor.getNombre()   : "?")
             + ", receptor=" + (receptor != null ? receptor.getNombre() : "?") + '}';
    }
}