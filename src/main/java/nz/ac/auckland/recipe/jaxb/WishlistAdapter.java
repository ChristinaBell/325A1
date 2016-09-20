//package nz.ac.auckland.recipe.jaxb;
//
//import javax.xml.bind.annotation.adapters.XmlAdapter;
//
//import nz.ac.auckland.recipe.domain.Wishlist;
//
//import org.joda.time.DateTime;
//
///**
// * JAXB XML adapter to convert between Joda DateTime instances and Strings.
// * DateTime objects are marshalled as Strings, and unmarshalled back into 
// * DateTime instances.
// *
// */
//public class WishlistAdapter extends XmlAdapter<String, Wishlist> {
//
//	@Override
//	public Wishlist unmarshal(String wishlistAsString) throws Exception {
//		if(wishlistAsString == null) {
//			return null;
//		}
//		
//		return new Wishlist(wishlistAsString);
//	}
//
//	@Override
//	public String marshal(Wishlist wishlist) throws Exception {
//		if(wishlist == null) {
//			return null;
//		}
//		
//		return wishlist.toString();
//	}
//}