package com.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HelloAppEngine extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String inputJson = "";
		String queryTitle = request.getParameter("title");
		JSONArray responseArray = new JSONArray();

		try {
			URL url = new URL("https://data.sfgov.org/resource/wwmu-gmzc.json");
			BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
			String strTemp = "";
			while (null != (strTemp = br.readLine())) {
				inputJson += strTemp;
			}
		} catch (Exception ex) {
			response.getWriter().print("Error retrieving API data!");
		}

		try {

			// Array over the different movies
			JSONArray arrAll = new JSONArray(inputJson);

			// Iterate over movies
			for (int i = 0; i < arrAll.length(); i++) {

				// Object that contains information on one movie
				JSONObject obj = arrAll.getJSONObject(i);

				// Check if a location is present for current movieJSON
				if (obj.has("locations")) {
					String location = obj.getString("locations");
					String title = obj.getString("title");

					// Check if the location matches San Francisco
					if (Pattern.matches(".*[Ss]an [Ff]rancisco.*", location)) {

						// Regex to filter title case-insensitively
						String titleRegex = "(?i:.*" + queryTitle + ".*)";

						// Check if no filter present or if filter matches
						if ((queryTitle == null) || (Pattern.matches(titleRegex, title))) {
							// Build json object from title and location
							JSONObject newObj = new JSONObject();
							newObj.put("title", title);
							newObj.put("locations", location);

							// Add response movie object to response array
							responseArray.put(newObj);
						}
					}
				}
			}
		} catch (JSONException e) {
			response.getWriter().print("Error handling JSON!\r\n");
		}

		response.setContentType("application/json");
		response.getWriter().print(responseArray.toString());
	}
}