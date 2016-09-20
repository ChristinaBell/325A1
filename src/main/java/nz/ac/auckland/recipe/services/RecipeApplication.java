package nz.ac.auckland.recipe.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import nz.ac.auckland.recipe.PersistenceManager;

@ApplicationPath("/services")
public class RecipeApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();
	private Set<Class<?>> classes = new HashSet<Class<?>>();
	
	public RecipeApplication() {
		RecipeResource resource = new RecipeResource();
		singletons.add(PersistenceManager.instance());
		classes.add(RecipeResource.class); 
		classes.add(JavaMarshaller.class);
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
	
	@Override
	   public Set<Class<?>> getClasses()
	   {
	      return classes;
	   }
}
