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

// Importación de exp4j para evaluar expresiones matemáticas
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

// Clase principal que extiende Application de JavaFX
public class LimitesJava extends Application {

    // Método principal de JavaFX, se ejecuta al iniciar la aplicación
    @Override
    public void start(Stage stage) {
        
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
        Button botonAlejar = new Button("Alejar Grafica");
        Button botonAcercar = new Button("Acercar Grafica");
        
        // Crear ejes para el gráfico: uno para x y otro para f(x)
        NumberAxis ejeEnX = new NumberAxis();
        NumberAxis ejeEnY = new NumberAxis();
        ejeEnX.setLabel("x");
        ejeEnY.setLabel("f(x)");
        
        // Evita que se redimensionen
        ejeEnX.setAutoRanging(false);
        ejeEnY.setAutoRanging(false);
        
        ejeEnX.setLowerBound(-10);
        ejeEnX.setUpperBound(10);
        ejeEnX.setTickUnit(1);

        ejeEnY.setLowerBound(-10);
        ejeEnY.setUpperBound(10);
        ejeEnY.setTickUnit(1);

        // Crear el gráfico de líneas con los ejes
        LineChart<Number, Number> lineChart = new LineChart<>(ejeEnX, ejeEnY);
        lineChart.setTitle("Gráfica de f(x)");

        // Organizar los campos de entrada y el botón verticalmente con separación de 10 píxeles
        VBox containerDatos = new VBox(10, EtiquetaFuncion, entradaFuncion, etiquetaValorEnX, entradaValorEnX, botonGraficar, botonAlejar, botonAcercar);
        containerDatos.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Acción cuando se presiona el botón "Graficar"
        botonGraficar.setOnAction(e -> {
            try {
                // Actualizar titulo para el valor ingresado
                lineChart.setTitle("Gráfica de f(x) cuando x tiende a: " + entradaValorEnX.getText());
                
                // Obtener los valores ingresados
                String funcion = entradaFuncion.getText();
                double valorEnX = Double.parseDouble(entradaValorEnX.getText());

                // Construir la expresión matematica usando exp4j
                Expression formula = new ExpressionBuilder(funcion).variable("x").build();

                // Limpiar datos anteriores del gráfico
                lineChart.getData().clear();

                // Crear una serie de datos para la función f(x)
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("f(x)");

                // Generar puntos desde valorEnX-50 hasta valorEnX+50, en pasos de 0.5
                for (double x = valorEnX - 50; x <= valorEnX + 50; x += 0.5) {
                    double y = formula.setVariable("x", x).evaluate();  // Evaluar f(x)
                    series.getData().add(new XYChart.Data<>(x, y));  // Agregar punto (x, f(x))
                }
                
                // Calcular el límite (valor de f(x) en x = valorEnX)
                double valorLimite = formula.setVariable("x", valorEnX).evaluate();

                // Ubicar solamente el limite
                XYChart.Series<Number, Number> serieLimite = new XYChart.Series<>();
                serieLimite.setName("Límite en x → " + valorEnX);
                serieLimite.getData().add(new XYChart.Data<>(valorEnX, valorLimite));
                
                // Agregar ambas series al gráfico
                lineChart.getData().addAll(series, serieLimite);
                lineChart.setCreateSymbols(true); //Muestra los puntos de las dos graficas sin discriminar
               

                // Estilo para ocultar los símbolos de la primera serie (la función)
                series.getNode().lookup(".chart-series-line").setStyle("-fx-stroke-width: 1;");
                for (XYChart.Data<Number, Number> data : series.getData()) {
                    Node node = data.getNode();
                    if (node != null) {
                        node.setStyle("-fx-background-color: transparent;"); // Oculta el símbolo
                    }
                }

                Alert alertaResultado = new Alert(Alert.AlertType.INFORMATION);
                alertaResultado.setTitle("Resultado");
                alertaResultado.setHeaderText("Cálculo exitoso");
                alertaResultado.setContentText("El valor del límite cuando x → " + valorEnX + " es: " + valorLimite);
                alertaResultado.show();

            } 
            catch (Exception ex) {
                // Mostrar un mensaje de error si algo falla al evaluar o graficar
                new Alert(Alert.AlertType.ERROR, "Error al graficar: " + ex.getMessage()).show();
            }
        });

        // Acciones de botones para alejar o acercar grafica
        botonAlejar.setOnAction(event -> {
            double zoomFactor = 1.1;
            double centerX = ejeEnX.getLowerBound() + (ejeEnX.getUpperBound() - ejeEnX.getLowerBound()) / 2;
            double centerY = ejeEnY.getLowerBound() + (ejeEnY.getUpperBound() - ejeEnY.getLowerBound()) / 2;

            double nuevoAnchoX = (ejeEnX.getUpperBound() - ejeEnX.getLowerBound()) * zoomFactor;
            double nuevoAnchoY = (ejeEnY.getUpperBound() - ejeEnY.getLowerBound()) * zoomFactor;

            ejeEnX.setLowerBound(centerX - nuevoAnchoX / 2);
            ejeEnX.setUpperBound(centerX + nuevoAnchoX / 2);
            ejeEnX.setTickUnit(nuevoAnchoX / 10);  // Actualiza la unidad de grilla

            ejeEnY.setLowerBound(centerY - nuevoAnchoY / 2);
            ejeEnY.setUpperBound(centerY + nuevoAnchoY / 2);
            ejeEnY.setTickUnit(nuevoAnchoY / 10);
        });
        
        botonAcercar.setOnAction(event -> {
            double zoomFactor = 0.9;
            double centerX = ejeEnX.getLowerBound() + (ejeEnX.getUpperBound() - ejeEnX.getLowerBound()) / 2;
            double centerY = ejeEnY.getLowerBound() + (ejeEnY.getUpperBound() - ejeEnY.getLowerBound()) / 2;

            double nuevoAnchoX = (ejeEnX.getUpperBound() - ejeEnX.getLowerBound()) * zoomFactor;
            double nuevoAnchoY = (ejeEnY.getUpperBound() - ejeEnY.getLowerBound()) * zoomFactor;

            ejeEnX.setLowerBound(centerX - nuevoAnchoX / 2);
            ejeEnX.setUpperBound(centerX + nuevoAnchoX / 2);
            ejeEnX.setTickUnit(nuevoAnchoX / 10);  // Actualiza la unidad de grilla

            ejeEnY.setLowerBound(centerY - nuevoAnchoY / 2);
            ejeEnY.setUpperBound(centerY + nuevoAnchoY / 2);
            ejeEnY.setTickUnit(nuevoAnchoY / 10);  // Lo mismo para el eje Y
        });

        // Panel principal que organiza los controles a la izquierda y el gráfico al centro
        BorderPane root = new BorderPane();
        root.setLeft(containerDatos);
        root.setCenter(lineChart);

        // Crear la escena y mostrarla en la ventana (stage)
        Scene scene = new Scene(root, 800, 600);  // Tamaño de ventana
        stage.setTitle("Gráfico de Función hecho por el mejor CIPAS");
        stage.setScene(scene);
        stage.show();  // Mostrar ventana
    }

    // Método main, punto de entrada estándar en Java
    public static void main(String[] args) {
        launch(args);  // Lanza la aplicación JavaFX
    }
}
