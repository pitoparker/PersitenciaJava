package pio.daw.ra8.mercado.service;

import jakarta.persistence.EntityManager;
import pio.daw.ra8.mercado.model.Individuo;
import pio.daw.ra8.mercado.model.Intercambio;
import pio.daw.ra8.mercado.model.Simulacion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Contiene toda la lógica del experimento de mercado libre.
 *
 * Puntos clave:
 *  - Se crean N individuos con el mismo saldo inicial.
 *  - En cada ronda: dos individuos al azar intercambian un importe aleatorio.
 *  - Cada 100 rondas se hace commit + em.clear() para liberar memoria.
 *  - Tras em.clear() los objetos quedan "detached" → hay que re-adjuntarlos con merge().
 */
public class SimulacionService {

    /** Cada cuántas rondas se hace commit a la base de datos. */
    private static final int TAMANIO_LOTE = 100;

    private final EntityManager em;
    private final Random random = new Random();

    public SimulacionService(EntityManager em) {
        this.em = em;
    }

    /**
     * Ejecuta la simulación completa y devuelve el objeto Simulacion persistido.
     *
     * @param nombre        nombre descriptivo
     * @param numIndividuos número de agentes económicos
     * @param saldoInicial  saldo inicial de cada individuo (u.m.)
     * @param numRondas     número de rondas del experimento
     */
    public Simulacion ejecutar(String nombre, int numIndividuos,
                               double saldoInicial, int numRondas) {

        System.out.println("=== Iniciando: " + nombre + " ===");

        // 1. Crear y persistir la Simulacion
        Simulacion sim = new Simulacion(nombre, numRondas, numIndividuos, saldoInicial);
        em.getTransaction().begin();
        em.persist(sim);

        // 2. Crear y persistir los Individuos
        List<Individuo> poblacion = new ArrayList<>(numIndividuos);
        for (int i = 0; i < numIndividuos; i++) {
            Individuo ind = new Individuo("Individuo_" + (i + 1), saldoInicial, sim);
            em.persist(ind);
            poblacion.add(ind);
        }
        em.getTransaction().commit();
        System.out.println("  Población persistida.");

        // 3. Bucle de rondas con commits periódicos
        em.getTransaction().begin();

        for (int ronda = 0; ronda < numRondas; ronda++) {

            // Seleccionar dos individuos distintos al azar
            int idxA = random.nextInt(numIndividuos);
            int idxB;
            do { idxB = random.nextInt(numIndividuos); } while (idxB == idxA);

            Individuo a = poblacion.get(idxA);
            Individuo b = poblacion.get(idxB);

            double saldoMin = Math.min(a.getSaldoActual(), b.getSaldoActual());

            Intercambio intercambio;
            if (saldoMin < 1.0) {
                // Ambos casi sin saldo: intercambio de 0
                intercambio = new Intercambio(ronda + 1, 0.0, a, b, sim);
            } else {
                // Importe aleatorio entre 1 y saldoMin
                double importe = 1 + random.nextDouble() * (saldoMin - 1);

                // Dirección aleatoria: ¿quién da y quién recibe?
                Individuo emisor, receptor;
                if (random.nextBoolean()) { emisor = a; receptor = b; }
                else                      { emisor = b; receptor = a; }

                // Actualizar saldos en memoria
                emisor.setSaldoActual(emisor.getSaldoActual() - importe);
                receptor.setSaldoActual(receptor.getSaldoActual() + importe);

                intercambio = new Intercambio(ronda + 1, importe, emisor, receptor, sim);
            }
            em.persist(intercambio);

            // Commit cada TAMANIO_LOTE rondas para liberar memoria
            if ((ronda + 1) % TAMANIO_LOTE == 0) {
                em.getTransaction().commit();
                em.clear(); // vacía el caché de primer nivel

                // Tras clear() los objetos son "detached": merge() los re-adjunta
                for (int i = 0; i < poblacion.size(); i++) {
                    poblacion.set(i, em.merge(poblacion.get(i)));
                }

                em.getTransaction().begin();

                if ((ronda + 1) % 1000 == 0)
                    System.out.printf("  Ronda %d / %d%n", ronda + 1, numRondas);
            }
        }

        // Commit final para el último lote (puede ser incompleto)
        if (em.getTransaction().isActive()) {
            em.getTransaction().commit();
        }

        System.out.println("=== Simulación finalizada. ===");
        return sim;
    }
}