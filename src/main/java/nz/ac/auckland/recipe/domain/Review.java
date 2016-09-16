package nz.ac.auckland.recipe.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable 
public class Review {

	@Column(nullable=false, name="CONTENT")
	private String _content; 

}
