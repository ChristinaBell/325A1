
package nz.ac.auckland.recipe.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import nz.ac.auckland.recipe.domain.Review;

import org.joda.time.DateTime;

/**
 * JAXB XML adapter to convert between Joda DateTime instances and Strings.
 * DateTime objects are marshalled as Strings, and unmarshalled back into 
 * DateTime instances.
 *
 */
public class ReviewAdapter extends XmlAdapter<String, Review> {

	@Override
	public Review unmarshal(String reviewAsString) throws Exception {
		if(reviewAsString == null) {
			return null;
		}
		
		return new Review(reviewAsString);
	}

	@Override
	public String marshal(Review review) throws Exception {
		if(review == null) {
			return null;
		}
		
		return review.toString();
	}
}