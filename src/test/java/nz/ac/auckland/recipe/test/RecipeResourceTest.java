package nz.ac.auckland.recipe.test;

import java.util.HashSet;
import java.util.List;

import nz.ac.auckland.recipe.PersistenceManager;
import nz.ac.auckland.recipe.dto.Recipe;
import nz.ac.auckland.recipe.domain.Baker;
import nz.ac.auckland.recipe.domain.Category;
import static org.junit.Assert.*;

import javax.persistence.EntityManager;
//import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.junit.After;
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
//	private EntityManagerFactory entityManagerFactory;
	
	
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
		Recipe testRecipe = new Recipe("Chocolate Cake", "This is a recipe for a chocolate cake", author, Category.CAKES  ); 

		Response response = _client
				.target(WEB_SERVICE_URI).request()
				.post(Entity.xml(testRecipe));
		if (response.getStatus() != 201) {
			fail("Failed to create new Recipe");
		}

		String location = response.getLocation().toString();
		response.close();
		_client.close();

		// Query the Web service for the new Recipe.
		Recipe recipeFromService = _client.target(location).request()
				.accept("application/xml").get(Recipe.class);

		// The original local Recipe object (zoran) should have a value equal
		// to that of the Recipe object representing Zoran that is later
		// queried from the Web service. The only exception is the value
		// returned by getId(), because the Web service assigns this when it
		// creates a Recipe.
		assertEquals(testRecipe.getName(), recipeFromService.getName());
		assertEquals(testRecipe.getContent(), recipeFromService.getContent());
		assertEquals(testRecipe.getAuthor(), recipeFromService.getAuthor());		
		

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@Before
//	public void setUp() throws Exception {
//		// Create an EntityManagerFactory from which an EntityManager can be 
//		// requested. The argument to createEntityManagerFactory() is the name
//		// of a persistence unit, named in META-INF/persistence.xml. The 
//		// factory configures itself based on reading the xml file. 
//		// persistence.xml must contain a persistence unit named, in this case,
//		// "nz.ac.auckland.hello".
////		entityManagerFactory = Persistence.createEntityManagerFactory( "nz.ac.auckland.recipe" );
//	}
//
//	/**
//	 * Runs after each test to destroy the EntityManagerFactory.
//	 * 
//	 */
//	@After
//	public void tearDown() throws Exception {
//		// Close the EntityManagerFactory once all tests have executed.
////		entityManagerFactory.close();
//	}
//	
////	/**
////	 * Runs after all tests have executed to print the contents of the 
////	 * database.
////	 */
////	@AfterClass
////	public static void dumpDatabase() throws Exception {
////		Set<String> tableNames = new HashSet<String>();
////		tableNames.add("MESSAGE");
////		DatabaseUtility.openDatabase();
////		DatabaseUtility.dumpDatabase(tableNames);
////		DatabaseUtility.closeDatabase();
////	}
//	
//	
//	/**
//	 * Illustrates use of the JPA EntityManager. This test uses a transaction 
//	 * to store a Message object in the database. It then uses another
//	 * transaction to query the database for Messages. It prints the Message,
//	 * changes its state and updates the database. 
//	 */
//	@Test
//	public void testBasicUsage() {
//		// Acquire an EntityManager, representing a session with the database.
//		// Using the entityManager, create a transaction.
////		EntityManager entityManager = entityManagerFactory.createEntityManager();
//		EntityManager entityManager = PersistenceManager.instance().createEntityManager();
//		entityManager.getTransaction().begin();
//		
//		Baker author = new Baker("Christina"); 
//		Recipe testRecipe = new Recipe("Chocolate Cake", "This is a recipe for a chocolate cake", author, Category.CAKES  ); 
//		
//		// Request the the EntityManager stores the Message.
//		entityManager.persist(testRecipe);
//		
//		// Commit the transaction. This causes the JPA provider to execute the
//		// SQL statement: 
//		//   insert into MESSAGE (ID, TEXT) values (1, 'Hello, World!')
//		entityManager.getTransaction().commit();
//
//		// Now let's pull the Message from the database. Start a new 
//		// transaction. 
//		entityManager.getTransaction().begin();
//		
//		// Query the database for stored Messages. The query is expressed using
//		// JPQL (Java Persistence Query Language) which looks similar to SQL. 
//		// Rather than being written in terms of tables and columns, JPQL 
//		// queries are written in terms of classes and properties. This JPQL
//		// query generates the SQL query: select * from MESSAGE.
//        List<Recipe> recipes = entityManager.createQuery("select m from Message m", Recipe.class).getResultList();
//		for (Recipe r : recipes ) {
//			_logger.info("Message: " + r);
//		}
//		// They query should return one Message object.
//		assertEquals(1, recipes.size());
//		
//		// The text of the returned Message should be what was originally 
//		// persisted.
//		Recipe retrievedMessage = recipes.get(0);
//		
//		assertEquals(testRecipe.getName(), "Chocolate Cake");
//		assertEquals(testRecipe.getContent(), "This is a recipe for a chocolate cake");
//		assertEquals(testRecipe.getAuthor(), author);	
//		assertEquals(testRecipe.getCategory(), Category.CAKES);
//		
//		// When this transaction commits, the following SQL is executed:
//		//   update MESSAGE set TEXT = 'Take me to your leader!' where ID = 1
//        entityManager.getTransaction().commit();
//        entityManager.close();
//	}
}
