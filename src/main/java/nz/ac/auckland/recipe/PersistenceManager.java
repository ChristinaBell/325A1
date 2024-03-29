package nz.ac.auckland.recipe;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * Singleton class to manages an EntityManagerFactory. When a
 * PersistenceManager is instantiated, it creates an EntityManagerFactory. When
 * a Web service application component (e.g. an interceptor or resource object)
 * requires a persistence context (database session), it should call the 
 * PersistentManager's createEntityManager() method to acquire one. The 
 * requester is responsible for closing the session. 
 * 
 * @author Christina Bell - Adapted from Ian Warren's helloworld orm example
 *
 */
public class PersistenceManager {
	private static PersistenceManager _instance = null;
	
	private EntityManagerFactory _entityManagerFactory;
	
	protected PersistenceManager() {
		_entityManagerFactory = Persistence.createEntityManagerFactory("nz.ac.auckland.recipe");
	}
	
	public EntityManager createEntityManager() {
		return _entityManagerFactory.createEntityManager();
	}
	
	public static PersistenceManager instance() {
		if(_instance == null) {
			_instance = new PersistenceManager();
		}
		return _instance;
	}

}
