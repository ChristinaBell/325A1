package nz.ac.auckland.recipe.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Class to marshal/unmarshal Java objects using Java's Serialization facility.
 * 
 * This class introduces a new MIME type: application/example-java. A 
 * JavaMarshaller object unmarshalls data in Java's Serialization format to 
 * create Java objects, and marshalls Java objects into the Serialization 
 * format. Serialized data takes for the form of a byte sequence. 
 * 
 * JavaMarshaller implements the JAX-RS MessageBodyReader and MessageBodyWriter
 * interfaces.
 * 
 * The RestEasy JAX-RS implementation does actually contain a MessageBodyReader
 * and MessageBodyWriter for Java's serialization format. By specifying the 
 * MIME type "application/x-java-serialized-object", the built in support is 
 * used. 
 * 
 * @author Ian Warren
 *
 */
@Produces("application/example-java")
@Consumes("application/example-java")
public class JavaMarshaller<T> implements MessageBodyReader<T>, MessageBodyWriter<T> {

	@Override
	public long getSize(T object, Class<?> cls, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return -1;
	}

	@Override
	public boolean isWriteable(Class<?> cls, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return doesImplementSerializable(cls);
	}

	@Override
	public void writeTo(T object, Class<?> cls, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, Object> httpHeaders,
			OutputStream entityStream) throws IOException, WebApplicationException {
		ObjectOutputStream oos = new ObjectOutputStream(entityStream);
		oos.writeObject(object);
	}

	@Override
	public boolean isReadable(Class<?> cls, Type genericType, Annotation[] annotations,
			MediaType mediaType) {
		return doesImplementSerializable(cls);
	}

	@Override
	@SuppressWarnings("unchecked")
	public T readFrom(Class<T> cls, Type genericType, Annotation[] annotations,
			MediaType mediaType, MultivaluedMap<String, String> httpHeaders,
			InputStream entityStream) throws IOException, WebApplicationException {
		ObjectInputStream ois = new ObjectInputStream(entityStream);

		try {
			return (T)ois.readObject();
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}
	
	private boolean doesImplementSerializable(Class<?> cls) {
		Class<?>[] interfaces = cls.getInterfaces();
		
		for(Class<?> i : interfaces) {
			if(i == java.io.Serializable.class) {
				return true;
			}
		}
		
		return false;
	}
}
