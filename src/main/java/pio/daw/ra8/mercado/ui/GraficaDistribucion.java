package pio.daw.ra8.mercado.ui;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.Color;
import java.util.List;

/**
 * Histograma de distribución de riqueza usando JFreeChart.
 * Eje X: franjas de saldo. Eje Y: número de individuos en cada franja.
 * El resultado esperado es una curva exponencial decreciente (Boltzmann).
 */
public class GraficaDistribucion {

    private static final int NUM_FRANJAS = 10;

    public void mostrar(List<Double> saldos, double saldoInicial, String titulo) {
        if (saldos == null || saldos.isEmpty()) {
            System.out.println("[Grafica] Sin datos.");
            return;
        }

        // Rango real de saldos
        double minSaldo   = saldos.stream().mapToDouble(Double::doubleValue).min().orElse(0);
        double maxSaldo   = saldos.stream().mapToDouble(Double::doubleValue).max().orElse(saldoInicial * 2);
        double anchFranja = (maxSaldo - minSaldo) / NUM_FRANJAS;

        // Contar individuos en cada franja
        int[] conteo = new int[NUM_FRANJAS];
        for (double saldo : saldos) {
            int idx = (int) ((saldo - minSaldo) / anchFranja);
            if (idx >= NUM_FRANJAS) idx = NUM_FRANJAS - 1;
            conteo[idx]++;
        }

        // Dataset para JFreeChart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < NUM_FRANJAS; i++) {
            String etiqueta = String.format("%.0f-%.0f",
                minSaldo + i * anchFranja, minSaldo + (i + 1) * anchFranja);
            dataset.addValue(conteo[i], "Individuos", etiqueta);
        }

        // Crear gráfico
        JFreeChart grafica = ChartFactory.createBarChart(
            titulo, "Rango de saldo (u.m.)", "Número de individuos",
            dataset, PlotOrientation.VERTICAL, false, true, false
        );

        // Estilo
        grafica.setBackgroundPaint(Color.WHITE);
        CategoryPlot plot = grafica.getCategoryPlot();
        plot.setBackgroundPaint(new Color(240, 240, 250));
        CategoryAxis ejeX = plot.getDomainAxis();
        ejeX.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        ((BarRenderer) plot.getRenderer()).setSeriesPaint(0, new Color(70, 130, 180));

        // Mostrar ventana
        ChartFrame frame = new ChartFrame("Distribución de Riqueza", grafica);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(ChartFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }
}