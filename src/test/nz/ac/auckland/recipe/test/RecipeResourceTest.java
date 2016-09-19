package nz.ac.auckland.recipe.test;

import static org.junit.Assert.fail;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple JUnit test to test the behaviour of the Recipe Web service.
 * 
 * The test basically uses the Web service to create new recipes, to query a
 * recipe, to query a range of recipes, to update a recipe and to delete a
 * recipe.
 * 
 * The test is implemented using the JAX-RS client API.
 * 
 * @author Ian Warren
 *
 */
public class RecipeResourceTest {
	
	private static String WEB_SERVICE_URI = "http://localhost:10000/services/recipes";

	private Logger _logger = LoggerFactory.getLogger(RecipeResourceTest.class);

	@Test
	public void testRecipeResource() {
		// Use ClientBuilder to create a new client that can be used to create
		// connections to the Web service.
		Client client = ClientBuilder.newClient();
		
		try {
			// **************
			// *** CREATE ***
			// **************
			_logger.info("Creating Recipes ...");

			// An array of XML strings, describing recipes.
			String[] xmlPayloads = {
					"<recipe>" 
							+ "<id>1111</id>"
							+ "<author>cbel296</author>"
							+ "<content>Chocolate Layer Cake Recipe</content>"
							+ "<creation-time-stamp>17/01/2016</creation-time-stamp>"
							+ "<reviews></reviews>"
							+ "<category>cakes</category>"
							+ "</recipe>",

					"<recipe>" 
							+ "<id>2222</id>"
							+ "<author>cbel296</author>"
							+ "<content>Chocolate Chip Cookies Recipe</content>"
							+ "<creation-time-stamp>18/01/2016</creation-time-stamp>"
							+ "<reviews></reviews>"
							+ "<category>biscuits</category>"
							+ "</recipe>",

					"<recipe>" 
							+ "<id>3333</id>"
							+ "<author>cbel296</author>"
							+ "<content>Banana Cake Recipe</content>"
							+ "<creation-time-stamp>19/01/2016</creation-time-stamp>"
							+ "<reviews></reviews>"
							+ "<category>cakes</category>"
							+ "</recipe>",

					"<recipe>" 
							+ "<id>4444</id>"
							+ "<author>abc123</author>"
							+ "<content>Cheese Scones Recipe</content>"
							+ "<creation-time-stamp>20/01/2016</creation-time-stamp>"
							+ "<reviews></reviews>"
							+ "<category>savory</category>"
							+ "</recipe>" };

			// Send HTTP POST messages, each with a message body containing the
			// XML payload, to the Web service.
			String location = null;

			for (String payload : xmlPayloads) {
				Response response = client.target(WEB_SERVICE_URI).request()
						.post(Entity.xml(payload));

				// Expect a HTTP 201 "Created" response from the Web service.
				int status = response.getStatus();
				if (status != 201) {
					_logger.error("Failed to create Recipe; Web service responded with: "
							+ status);
					fail();
				}

				// Extract location header from the HTTP response message. This
				// should specify the URI for the newly created recipe.
				location = response.getLocation().toString();
				_logger.info("URI for new Recipe: " + location);

				// Close the response, freeing resources associated with it. 
				response.close();
			}

			// ****************
			// *** RETRIEVE ***
			// ****************
			_logger.info("Retrieving Recipes ...");

			// Query the Web service for the last created recipe. Send a HTTP
			// GET request, and expect an XML String in the response.
			String recipeAsXML = client.target(location).request()
					.get(String.class);
			_logger.info("Retrieved Recipe:\n" + recipeAsXML);

			// Query the Web service for Recipes with IDs in the range 1..3.
			// Send a HTTP GET request, and expect an XML String describing the
			// recipes in the response.
			String recipesAsXML = client
					.target(WEB_SERVICE_URI + "?start=1&size=3").request()
					.get(String.class);
			_logger.info("Retrieved Recipes:\n" + recipesAsXML);

			// **************
			// *** Update ***
			// **************
			_logger.info("Updating recipe ...");

			// Create a XML representation of the first recipe, changing Al
			// Capone's gender.
			String updateRecipe = "<recipe>" + "<first-name>Al</first-name>"
					+ "<last-name>Capone</last-name>"
					+ "<gender>Female</gender>"
					+ "<date-of-birth>17/01/1899</date-of-birth>"
					+ "</recipe>";

			// Send a HTTP PUT request to the Web service.
			Response response = client.target(WEB_SERVICE_URI + "/1").request()
					.put(Entity.xml(updateRecipe));

			// Expect a HTTP 204 "No content" response from the Web service.
			int status = response.getStatus();
			if (status != 204) {
				_logger.error("Failed to update Recipe; Web service responded with: "
						+ status);
				fail();
			}
			response.close();

			// Finally, re-query the Recipe. The date-of-birth should have been
			// updated.
			_logger.info("Querying the updated Recipe ...");
			recipeAsXML = client.target(WEB_SERVICE_URI + "/1").request()
					.get(String.class);
			_logger.info("Retrieved Recipe:\n" + recipeAsXML);

			// **************
			// *** DELETE ***
			// **************
			_logger.info("Deleting recipe ...");
			// Send a HTTP DELETE request to the Web service.
			response = client.target(WEB_SERVICE_URI + "/1").request().delete();

			// Expect a HTTP 204 "No content" response from the Web service.
			status = response.getStatus();
			if (status != 204) {
				_logger.error("Failed to delete Recipe; Web service responded with: "
						+ status);
				fail();
			}

			// Finally, re-query the Web service for the deleted Recipe. The
			// response should be 404.
			response = client.target(WEB_SERVICE_URI + "/1").request().get();
			status = response.getStatus();
			if (status != 404) {
				_logger.error("Expecting a status code of 404 for querying a non-existent Recipe; Web service responded with: "
						+ status);
				fail();
			}

		} finally {
			// Release any connection resources.
			client.close();
		}
	}
}
