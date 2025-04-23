package limitesjava;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class LimitesJava extends Application {

    @Override
    public void start(Stage stage) {
        // Crear los controles para ingresar la función y el valor de x
        Label funcionLabel = new Label("Función f(x):");
        TextField funcionField = new TextField();
        funcionField.setPromptText("Ej. x^2");

        Label valorXLabel = new Label("Valor de x:");
        TextField valorXField = new TextField();
        valorXField.setPromptText("Ej. 2");

        Button graficarBtn = new Button("Graficar límite");

        // Ejes del gráfico
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("x");
        yAxis.setLabel("f(x)");

        // Crear el gráfico
        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Gráfica de f(x)");

        // Layout
        VBox inputLayout = new VBox(10, funcionLabel, funcionField, valorXLabel, valorXField, graficarBtn);
        inputLayout.setStyle("-fx-padding: 20px; -fx-alignment: center;");

        // Acción del botón "Graficar"
        graficarBtn.setOnAction(e -> {
            try {
                // Obtener la función y el valor de x
                String funcion = funcionField.getText();
                double x0 = Double.parseDouble(valorXField.getText());

                // Crear la expresión
                Expression expr = new ExpressionBuilder(funcion).variable("x").build();

                // Limpiar los datos previos del gráfico
                lineChart.getData().clear();

                // Serie de datos para graficar
                XYChart.Series<Number, Number> series = new XYChart.Series<>();
                series.setName("f(x)");

                // Generación de puntos para graficar
                for (double x = x0 - 1; x <= x0 + 1; x += 0.1) {
                    double y = expr.setVariable("x", x).evaluate();
                    series.getData().add(new XYChart.Data<>(x, y));
                }

                // Agregar la serie al gráfico
                lineChart.getData().add(series);
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Error al graficar: " + ex.getMessage()).show();
            }
        });

        // Panel principal con los controles y el gráfico
        BorderPane root = new BorderPane();
        root.setLeft(inputLayout);
        root.setCenter(lineChart);

        // Crear y mostrar la escena
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Gráfico de Función");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
