package nz.ac.auckland.recipe.test;

import nz.ac.auckland.recipe.dto.DtoRecipe;
import nz.ac.auckland.recipe.domain.Baker;
import nz.ac.auckland.recipe.domain.Category;
import static org.junit.Assert.*;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
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
	
	private static String WEB_SERVICE_URI = "http://localhost:10000/services/recipeBlogs";

	private Logger _logger = LoggerFactory.getLogger(RecipeResourceTest.class);
	private static Client _client;

	@BeforeClass
	public static void setUpClient() {
		_client = ClientBuilder.newClient();
	}
	
	/**
	 * Runs before each unit test to restore Web service database. This ensures
	 * that each test is independent; each test runs on a Web service that has
	 * been initialised with a common set of Parolees.
	 */
//	@Before
//	public void reloadServerData() {
//		Response response = _client
//				.target(WEB_SERVICE_URI).request()
//				.put(null);
//		response.close();
//
//		// Pause briefly before running any tests. Test addParoleeMovement(),
//		// for example, involves creating a timestamped value (a movement) and
//		// having the Web service compare it with data just generated with 
//		// timestamps. Joda's Datetime class has only millisecond precision, 
//		// so pause so that test-generated timestamps are actually later than 
//		// timestamped values held by the Web service.
//		try {
//			Thread.sleep(10);
//		} catch (InterruptedException e) {
//		}
//	}
	
	/**
	 * One-time finalisation method that destroys the Web service client.
	 */
	@AfterClass
	public static void destroyClient() {
		_client.close();
	}
	
	@Test
	public void addRecipe() {
		
		Baker author = new Baker("Christina"); 
		DtoRecipe testRecipe = new DtoRecipe("Chocolate Cake", "This is a recipe for a chocolate cake", author, Category.CAKES  ); 

		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.post(Entity.xml(testRecipe));
		if (response.getStatus() != 201) {
			fail("Failed to create new Recipe");
		}

		String location = response.getLocation().toString();
		response.close();

		// Query the Web service for the new Recipe.
		DtoRecipe recipeFromService = _client.target(location).request()
				.accept("application/xml").get(DtoRecipe.class);

		// The original local Recipe object (zoran) should have a value equal
		// to that of the Recipe object representing Zoran that is later
		// queried from the Web service. The only exception is the value
		// returned by getId(), because the Web service assigns this when it
		// creates a Recipe.
		assertEquals(testRecipe.getName(), recipeFromService.getName());
		assertEquals(testRecipe.getContent(), recipeFromService.getContent());
		assertEquals(testRecipe.getAuthor(), recipeFromService.getAuthor());		
		

	}
}
