package servletPackage;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Date;
import java.util.Scanner;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Servlet implementation class Servlet
 */
public class Servlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public Servlet() {
		super();

		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String city = request.getParameter("city");
//		doGet(request, response);
		String apiKey = "b8046c75965ed87c839c6eecb2cca84f";
		String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + apiKey;

		// API integration
		URL url = null;
		try {
			url = new URI(apiUrl).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			// Handle exceptions here
			e.printStackTrace();
		}

		if (url != null) {
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			// Reading the content
			InputStream inputStream = connection.getInputStream();
			InputStreamReader reader = new InputStreamReader(inputStream);

			// storing the data in sting
			StringBuilder responseContent = new StringBuilder();

			// creating scanner to read from reader object
			Scanner scanner = new Scanner(reader);
			while (scanner.hasNext()) {
				responseContent.append(scanner.nextLine());

			}
			scanner.close();

			// parsing the data into JSON
			Gson gson = new Gson();
			JsonObject jsonObject = gson.fromJson(responseContent.toString(), JsonObject.class);

			// Get all major elements

			//Date & Time
            long dateTimestamp = jsonObject.get("dt").getAsLong() * 1000;
            Date date = new Date(dateTimestamp);

			// Temperature
			double temperatureKelvin = jsonObject.getAsJsonObject("main").get("temp").getAsDouble();
			int tempCelsius = (int) (temperatureKelvin - 273.15);

			// Humidity
			int humidity = jsonObject.getAsJsonObject("main").get("humidity").getAsInt();

			// windSpeed
			double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

			// weather condition
			String weatherCondition = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject().get("main")
					.getAsString();
			// Set the data as request attributes (for sending to the jsp page)
			request.setAttribute("date", date);
			request.setAttribute("city", city);
			request.setAttribute("temperature", tempCelsius);
			request.setAttribute("weatherCondition", weatherCondition);
			request.setAttribute("humidity", humidity);
			request.setAttribute("windSpeed", windSpeed);
			request.setAttribute("weatherData", responseContent.toString());

			connection.disconnect();
			
			
			

		} else {
			System.out.println("the URL is invalid");
		}
		request.getRequestDispatcher("index.jsp").forward(request, response);

	}

}
