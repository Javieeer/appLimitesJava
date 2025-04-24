// Paquete del proyecto
package limitesjava;

// Importaciones de JavaFX para la interfaz gráfica
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Importaciones para evaluación matemática con mXparser
import org.mariuszgromada.math.mxparser.Expression;
import org.mariuszgromada.math.mxparser.Argument;

public class LimitesJava extends Application {

    @Override
    public void start(Stage stage) {
        
        double xInicialInferior;
        double xInicialSuperior;
        double yInicialInferior;
        double yInicialSuperior;
        double tickUnitXInicial;
        double tickUnitYInicial;

        // Crear etiqueta y campo de texto para ingresar la función f(x)
        Label EtiquetaFuncion = new Label("Ingrese la función f(x):");
        TextField entradaFuncion = new TextField();
        entradaFuncion.setPromptText("Ej. x^2-3x-4");  // Texto de ejemplo

        // Crear etiqueta y campo de texto para ingresar el valor al que tiende x
        Label etiquetaValorEnX = new Label("Valor de x:");
        TextField entradaValorEnX = new TextField();
        entradaValorEnX.setPromptText("Ej. 2");  // Texto de ejemplo

        // Botón para activar la acción de graficar
        Button botonGraficar = new Button("Calcular límite");

        // Botones para proceso de zoom
        Button botonAcercar = new Button("ZOOM(+)");
        Button botonAlejar = new Button("ZOOM(-)");
        Button botonResetZoom = new Button("ZOOM(Original)");

        // Crear ejes para el gráfico: uno para x y otro para f(x)
        NumberAxis ejeEnX = new NumberAxis();
        NumberAxis ejeEnY = new NumberAxis();
        ejeEnX.setLabel("x");
        ejeEnY.setLabel("f(x)");

        // No se ajusten por si solos
        ejeEnX.setAutoRanging(false);
        ejeEnY.setAutoRanging(false);

        // Valores iniciales
        ejeEnX.setLowerBound(-10);
        ejeEnX.setUpperBound(10);
        ejeEnX.setTickUnit(1);

        ejeEnY.setLowerBound(-10);
        ejeEnY.setUpperBound(10);
        ejeEnY.setTickUnit(1);

        // Guardar valores originales
        xInicialInferior = ejeEnX.getLowerBound();
        xInicialSuperior = ejeEnX.getUpperBound();
        tickUnitXInicial = ejeEnX.getTickUnit();

        yInicialInferior = ejeEnY.getLowerBound();
        yInicialSuperior = ejeEnY.getUpperBound();
        tickUnitYInicial = ejeEnY.getTickUnit();

        // Crear el gráfico de líneas con los ejes
        LineChart<Number, Number> lineChart = new LineChart<>(ejeEnX, ejeEnY);
        lineChart.setTitle("Gráfica de f(x)");

        // Necesario para el manejo de la grilla
        final double[] mouseAnchorX = new double[1];
        final double[] mouseAnchorY = new double[1];

        // Acción para detectar el click dentro de la grafica
        lineChart.setOnMousePressed(event -> {
            mouseAnchorX[0] = event.getX();
            mouseAnchorY[0] = event.getY();
        });

        // Acción para mover la grafica de manera dinamica
        lineChart.setOnMouseDragged(event -> {
            double deltaX = event.getX() - mouseAnchorX[0];
            double deltaY = event.getY() - mouseAnchorY[0];

            double rangeX = ejeEnX.getUpperBound() - ejeEnX.getLowerBound();
            double rangeY = ejeEnY.getUpperBound() - ejeEnY.getLowerBound();

            double shiftX = deltaX / lineChart.getWidth() * rangeX;
            double shiftY = -deltaY / lineChart.getHeight() * rangeY;

            ejeEnX.setLowerBound(ejeEnX.getLowerBound() - shiftX);
            ejeEnX.setUpperBound(ejeEnX.getUpperBound() - shiftX);
            ejeEnY.setLowerBound(ejeEnY.getLowerBound() - shiftY);
            ejeEnY.setUpperBound(ejeEnY.getUpperBound() - shiftY);

            mouseAnchorX[0] = event.getX();
            mouseAnchorY[0] = event.getY();
        });

        // Se crea un container para los botones de acercar y alejar
        HBox containerBotones = new HBox(10, botonAcercar, botonAlejar);
        containerBotones.setStyle("-fx-aligment: center;");

        // Se crea un container para todo el menú izquierdo
        VBox containerDatos = new VBox(10, EtiquetaFuncion, entradaFuncion, etiquetaValorEnX, entradaValorEnX, botonGraficar, containerBotones, botonResetZoom);
        containerDatos.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Acción cuando se presiona el botón "Calcular límite"
        botonGraficar.setOnAction(e -> {
            try {
                // Actualiza titulo para el valor al que debe tender X
                lineChart.setTitle("Gráfica de f(x) cuando x tiende a: " + entradaValorEnX.getText());
                
                // Lee la función ingresada y el valor en X para procesarlos
                String funcion = entradaFuncion.getText().toLowerCase();
                double valorEnX = Double.parseDouble(entradaValorEnX.getText());

                // Definir la variable x que se utilizará en la expresión
                Argument x = new Argument("x = 0");
                Expression expr = new Expression(funcion, x);

                // Limpiar datos previos y crear la serie para la gráfica
                lineChart.getData().clear();
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("f(x)");

                // Graficar la función en el rango de x
                for (double i = valorEnX - 50; i <= valorEnX + 50; i += 0.1) {
                    x.setArgumentValue(i);
                    double y = expr.calculate();
                    series.getData().add(new XYChart.Data<>(i, y));   
                }

                // Evaluación aproximada del límite usando valores cercanos por izquierda y derecha
                double delta = 0.0001;
                x.setArgumentValue(valorEnX - delta);
                double izquierda = expr.calculate();
                x.setArgumentValue(valorEnX + delta);
                double derecha = expr.calculate();

                String mensaje;
                // Comparar los límites laterales con un umbral de diferencia 
                if (Math.abs(izquierda - derecha) < 1e-2) { 
                    // Si los valores laterales son cercanos, se calcula el límite como promedio
                    double limite = (izquierda + derecha) / 2;
                    mensaje = "El valor del límite cuando x → " + valorEnX + " es: " + String.format("%.6f", limite);
                } else {
                    mensaje = "El límite es indefinido o no existe.\nDesde la izquierda: " + izquierda + "\nDesde la derecha: " + derecha;
                }

                // Mostrar el resultado en la gráfica como una nueva serie
                XYChart.Series<Number, Number> serieLimite = new XYChart.Series<>();
                serieLimite.setName("Aproximación al límite");
                serieLimite.getData().add(new XYChart.Data<>(valorEnX, (izquierda + derecha) / 2));
                lineChart.getData().addAll(series, serieLimite);
                lineChart.setCreateSymbols(true);

                // Ajustar el estilo de la línea de la gráfica
                series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 1;");
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    Node node = data.getNode();
                    if (node != null) node.setStyle("-fx-background-color: transparent;");
                }

                // Mostrar un mensaje con el resultado del cálculo
                Alert alertaResultado = new Alert(Alert.AlertType.INFORMATION);
                alertaResultado.setTitle("Resultado");
                alertaResultado.setHeaderText("Cálculo del límite");
                alertaResultado.setContentText(mensaje);
                alertaResultado.show();

            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error al calcular: " + ex.getMessage()).show();
            }
        });

        // Función de zoom Alejar
        botonAlejar.setOnAction(event -> {
            double zoomFactor = 1.5;
            double centerX = (ejeEnX.getUpperBound() + ejeEnX.getLowerBound()) / 2;
            double centerY = (ejeEnY.getUpperBound() + ejeEnY.getLowerBound()) / 2;

            double anchoX = (ejeEnX.getUpperBound() - ejeEnX.getLowerBound()) * zoomFactor;
            double anchoY = (ejeEnY.getUpperBound() - ejeEnY.getLowerBound()) * zoomFactor;

            ejeEnX.setLowerBound(centerX - anchoX / 2);
            ejeEnX.setUpperBound(centerX + anchoX / 2);
            ejeEnX.setTickUnit(anchoX / 10);

            ejeEnY.setLowerBound(centerY - anchoY / 2);
            ejeEnY.setUpperBound(centerY + anchoY / 2);
            ejeEnY.setTickUnit(anchoY / 10);
        });

        // Función de zoom Acercar
        botonAcercar.setOnAction(event -> {
            double zoomFactor = 0.5;
            double centerX = (ejeEnX.getUpperBound() + ejeEnX.getLowerBound()) / 2;
            double centerY = (ejeEnY.getUpperBound() + ejeEnY.getLowerBound()) / 2;

            double anchoX = (ejeEnX.getUpperBound() - ejeEnX.getLowerBound()) * zoomFactor;
            double anchoY = (ejeEnY.getUpperBound() - ejeEnY.getLowerBound()) * zoomFactor;

            ejeEnX.setLowerBound(centerX - anchoX / 2);
            ejeEnX.setUpperBound(centerX + anchoX / 2);
            ejeEnX.setTickUnit(anchoX / 10);

            ejeEnY.setLowerBound(centerY - anchoY / 2);
            ejeEnY.setUpperBound(centerY + anchoY / 2);
            ejeEnY.setTickUnit(anchoY / 10);
        });

        // Función de zoom reset
        botonResetZoom.setOnAction(event -> {
            ejeEnX.setLowerBound(xInicialInferior);
            ejeEnX.setUpperBound(xInicialSuperior);
            ejeEnX.setTickUnit(tickUnitXInicial);

            ejeEnY.setLowerBound(yInicialInferior);
            ejeEnY.setUpperBound(yInicialSuperior);
            ejeEnY.setTickUnit(tickUnitYInicial);
        });

        // Organizar vista
        BorderPane root = new BorderPane();
        root.setLeft(containerDatos); // Menú a la izquierda
        root.setCenter(lineChart); // Grafica en el centro

        Scene scene = new Scene(root, 800, 600); // Tamaño de la ventana
        stage.setTitle("Gráfico de Función hecho por el mejor CIPAS"); // Titulo de la ventana
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
