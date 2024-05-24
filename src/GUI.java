import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.json.JSONObject;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.HashMap;

public class GUI extends Application {

    @Override
    public void start(Stage stage) {

        GridPane root = new GridPane();
        root.setPadding(new Insets(10, 10, 10, 10));
        root.setVgap(10);
        root.setHgap(10);

        Label cityLabel = new Label("City:");
        TextField cityInput = new TextField();
        Label stateLabel = new Label("State:");
        TextField stateInput = new TextField();
        Button fetchButton = new Button("Get Weather");

        Label resultLabel = new Label();

        Image weatherIMG = new Image(getClass().getResourceAsStream("/weather.png"));
        ImageView imageView = new ImageView(weatherIMG);
        imageView.setFitWidth(200); 
        imageView.setPreserveRatio(true);  // Maintain the aspect ratio of file

        root.add(cityLabel, 0, 0);
        root.add(cityInput, 1, 0);
        root.add(stateLabel, 0, 1);
        root.add(stateInput, 1, 1);
        root.add(fetchButton, 1, 2);
        root.add(resultLabel, 0, 3, 2, 1);
        root.add(imageView, 3, 0, 1, 3);

        fetchButton.setOnAction(event -> {
            String city = cityInput.getText();
            String state = stateInput.getText();

            if (!city.isEmpty() && !state.isEmpty()) {
                JSONObject coordinates = WeatherAutomation.fetchCoordinates(city, state);
                if (coordinates != null) {
                    double lat = coordinates.getDouble("lat");
                    double lon = coordinates.getDouble("lon");
                    JSONObject jsonData = WeatherAutomation.fetchWeatherData(lat, lon);
                    if (jsonData != null) {
                        HashMap<String, Object> weatherData = WeatherAutomation.parseWeatherData(jsonData);
                        StringBuilder weatherInfo = new StringBuilder();
                        weatherInfo.append("Weather in ").append(weatherData.get("city")).append(":\n");
                        weatherInfo.append("Temperature: ").append(weatherData.get("temperature")).append("°F\n");
                        weatherInfo.append("Feels like: ").append(weatherData.get("feels_like")).append("°F\n");
                        weatherInfo.append("Min Temperature: ").append(weatherData.get("temp_min")).append("°F\n");
                        weatherInfo.append("Max Temperature: ").append(weatherData.get("temp_max")).append("°F\n");
                        weatherInfo.append("Pressure: ").append(weatherData.get("pressure")).append(" hPa\n");
                        weatherInfo.append("Humidity: ").append(weatherData.get("humidity")).append("%\n");
                        weatherInfo.append("Weather: ").append(weatherData.get("main")).append("\n");
                        weatherInfo.append("Description: ").append(weatherData.get("description")).append("\n");
                        weatherInfo.append("Wind Speed: ").append(weatherData.get("wind_speed")).append(" m/s\n");
                        weatherInfo.append("Wind Direction: ").append(weatherData.get("wind_deg")).append(" degrees\n");
                        resultLabel.setText(weatherInfo.toString());
                    } else {
                        resultLabel.setText("Could not find weather info!");
                    }
                } else {
                    resultLabel.setText("Could not find coordinates!");
                }
            } else {
                resultLabel.setText("Please enter both city and state.");
            }
        });

        // Set the scene and show the stage
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("Weather Application");
        stage.setScene(scene);
        stage.show();
    }
}
