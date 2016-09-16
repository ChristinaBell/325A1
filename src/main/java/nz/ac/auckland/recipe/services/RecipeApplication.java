package nz.ac.auckland.recipe.services;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("/services")
public class RecipeApplication extends Application {

	private Set<Object> singletons = new HashSet<Object>();

	public RecipeApplication() {
		singletons.add(new RecipeApplication());
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
