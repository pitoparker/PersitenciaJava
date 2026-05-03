package pio.daw.ra8.mercado;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import pio.daw.ra8.mercado.model.Individuo;
import pio.daw.ra8.mercado.model.Intercambio;
import pio.daw.ra8.mercado.model.Simulacion;
import pio.daw.ra8.mercado.repository.SimulacionRepository;
import pio.daw.ra8.mercado.service.SimulacionService;
import pio.daw.ra8.mercado.ui.GraficaDistribucion;
import pio.daw.ra8.util.JPAUtil;

import java.util.List;

/**
 * Punto de entrada del proyecto integrador.
 * Ejecutar con: mvn -q exec:java -Dexec.mainClass="pio.daw.ra8.mercado.Main"
 */
public class Main {

    private static final int    NUM_INDIVIDUOS = 100;
    private static final double SALDO_INICIAL  = 100.0;
    private static final int    NUM_RONDAS     = 10_000;
    private static final String RUTA_DB        = "target/mercado.odb";
    private static final String NOMBRE_SIM     = "Experimento Boltzmann 1";

    public static void main(String[] args) {
        // crearEMF() borra el .odb previo para empezar limpio en cada ejecución
        EntityManagerFactory emf = JPAUtil.crearEMF(RUTA_DB);
        EntityManager em = emf.createEntityManager();

        try {
            // Tarea 2: ejecutar simulación
            Simulacion sim = new SimulacionService(em)
                .ejecutar(NOMBRE_SIM, NUM_INDIVIDUOS, SALDO_INICIAL, NUM_RONDAS);

            // Tarea 3 + mostrar resultados
            SimulacionRepository repo = new SimulacionRepository(em);
            mostrarResultados(repo, sim);

            // Tarea 4: gráfica
            new GraficaDistribucion().mostrar(repo.todosSaldos(sim), SALDO_INICIAL, NOMBRE_SIM);

        } finally {
            if (em.isOpen()) em.close();
            JPAUtil.cerrar(emf);
        }
    }

    private static void mostrarResultados(SimulacionRepository repo, Simulacion sim) {
        System.out.println("\n=== RESULTADOS ===");
        System.out.printf("Saldo medio:  %.2f u.m.%n", repo.saldoMedio(sim));
        System.out.printf("Saldo máximo: %.2f u.m.%n", repo.saldoMaximo(sim));
        System.out.printf("Saldo mínimo: %.2f u.m.%n", repo.saldoMinimo(sim));

        System.out.printf("%nMás rico : %s%n", repo.masRico(sim));
        System.out.printf("Más pobre: %s%n", repo.masPobre(sim));

        long claseMMedia = repo.contarConMasDeMitadSaldoInicial(sim);
        System.out.printf("%nIndividuos con saldo > 50%% inicial: %d de %d%n",
            claseMMedia, sim.getNumIndividuos());

        System.out.println("\nTop 10 más ricos:");
        List<Individuo> ranking = repo.rankingPorSaldo(sim);
        for (int i = 0; i < Math.min(10, ranking.size()); i++) {
            Individuo ind = ranking.get(i);
            System.out.printf("  %2d. %-15s %.2f u.m.%n", i + 1, ind.getNombre(), ind.getSaldoActual());
        }

        System.out.println("\nTop 10 intercambios mayores:");
        for (int i = 0; i < repo.top10IntercambiosMayores(sim).size(); i++) {
            Intercambio ic = repo.top10IntercambiosMayores(sim).get(i);
            System.out.printf("  %2d. Ronda %5d | %s → %s | %.2f u.m.%n",
                i + 1, ic.getNumRonda(),
                ic.getEmisor()   != null ? ic.getEmisor().getNombre()   : "?",
                ic.getReceptor() != null ? ic.getReceptor().getNombre() : "?",
                ic.getImporte());
        }
    }
}