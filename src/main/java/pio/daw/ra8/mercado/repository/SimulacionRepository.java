package pio.daw.ra8.mercado.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import pio.daw.ra8.mercado.model.Individuo;
import pio.daw.ra8.mercado.model.Intercambio;
import pio.daw.ra8.mercado.model.Simulacion;

import java.util.List;

/**
 * Todas las consultas JPQL del proyecto.
 * JPQL opera sobre entidades y atributos Java, no sobre tablas SQL.
 */
public class SimulacionRepository {

    private final EntityManager em;

    public SimulacionRepository(EntityManager em) {
        this.em = em;
    }

    /** Lista todos los individuos ordenados por saldo descendente. */
    public List<Individuo> rankingPorSaldo(Simulacion sim) {
        return em.createQuery(
            "SELECT i FROM Individuo i WHERE i.simulacion = :sim ORDER BY i.saldoActual DESC",
            Individuo.class
        ).setParameter("sim", sim).getResultList();
    }

    /** El individuo con mayor saldo. */
    public Individuo masRico(Simulacion sim) {
        List<Individuo> r = em.createQuery(
            "SELECT i FROM Individuo i WHERE i.simulacion = :sim ORDER BY i.saldoActual DESC",
            Individuo.class
        ).setParameter("sim", sim).setMaxResults(1).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    /** El individuo con menor saldo. */
    public Individuo masPobre(Simulacion sim) {
        List<Individuo> r = em.createQuery(
            "SELECT i FROM Individuo i WHERE i.simulacion = :sim ORDER BY i.saldoActual ASC",
            Individuo.class
        ).setParameter("sim", sim).setMaxResults(1).getResultList();
        return r.isEmpty() ? null : r.get(0);
    }

    /** Saldo medio (función de agregación AVG). */
    public double saldoMedio(Simulacion sim) {
        Double r = em.createQuery(
            "SELECT AVG(i.saldoActual) FROM Individuo i WHERE i.simulacion = :sim", Double.class
        ).setParameter("sim", sim).getSingleResult();
        return r != null ? r : 0.0;
    }

    /** Saldo máximo (función de agregación MAX). */
    public double saldoMaximo(Simulacion sim) {
        Double r = em.createQuery(
            "SELECT MAX(i.saldoActual) FROM Individuo i WHERE i.simulacion = :sim", Double.class
        ).setParameter("sim", sim).getSingleResult();
        return r != null ? r : 0.0;
    }

    /** Saldo mínimo (función de agregación MIN). */
    public double saldoMinimo(Simulacion sim) {
        Double r = em.createQuery(
            "SELECT MIN(i.saldoActual) FROM Individuo i WHERE i.simulacion = :sim", Double.class
        ).setParameter("sim", sim).getSingleResult();
        return r != null ? r : 0.0;
    }

    /** Cuántos individuos tienen más del 50% del saldo inicial. */
    public long contarConMasDeMitadSaldoInicial(Simulacion sim) {
        double umbral = sim.getSaldoInicial() * 0.5;
        Long r = em.createQuery(
            "SELECT COUNT(i) FROM Individuo i WHERE i.simulacion = :sim AND i.saldoActual > :umbral",
            Long.class
        ).setParameter("sim", sim).setParameter("umbral", umbral).getSingleResult();
        return r != null ? r : 0L;
    }

    /** Top 10 intercambios de mayor importe (opcional). */
    public List<Intercambio> top10IntercambiosMayores(Simulacion sim) {
        return em.createQuery(
            "SELECT ic FROM Intercambio ic WHERE ic.simulacion = :sim ORDER BY ic.importe DESC",
            Intercambio.class
        ).setParameter("sim", sim).setMaxResults(10).getResultList();
    }

    /** Solo los valores de saldoActual — útil para la gráfica. */
    public List<Double> todosSaldos(Simulacion sim) {
        return em.createQuery(
            "SELECT i.saldoActual FROM Individuo i WHERE i.simulacion = :sim", Double.class
        ).setParameter("sim", sim).getResultList();
    }
}