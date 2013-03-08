package br.com.caelum.vraptor.deserialization.gson;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

import br.com.caelum.vraptor.core.Localization;
import br.com.caelum.vraptor.http.ParameterNameProvider;
import br.com.caelum.vraptor.resource.DefaultResourceClass;
import br.com.caelum.vraptor.resource.DefaultResourceMethod;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.serialization.gson.HierarchicalAdapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class GsonDeserializerTest {

	private GsonDeserialization deserializer;
	private ResourceMethod bark;
	private ParameterNameProvider provider;
	private Localization localization;
	private ResourceMethod jump;
	private DefaultResourceMethod woof;
	private DefaultResourceMethod dropDead;
	private ResourceMethod withDogs;

	@Before
	public void setUp() throws Exception {
		provider = mock(ParameterNameProvider.class);
		localization = mock(Localization.class);

		when(localization.getLocale()).thenReturn(new Locale("pt", "BR"));

		deserializer = new GsonDeserialization(provider, Collections.<JsonDeserializer<?>> emptyList());
		DefaultResourceClass resourceClass = new DefaultResourceClass(DogController.class);

		woof = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("woof"));
		bark = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("bark", Dog.class));
		jump = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("jump", Dog.class,
				Integer.class));
		dropDead = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("dropDead",
				Integer.class, Dog.class));
		withDogs = new DefaultResourceMethod(resourceClass, DogController.class.getDeclaredMethod("withDogs", WithDogs.class));
	}

	static class Dog {
		private String name;
		private Integer age;
	}
	
	static class UglyDog extends Dog {
		public String color;
	}
	
	static class WithDogs {
		Dog dog;
		UglyDog uglyDog;
	}

	static class DogController {

		public void woof() {
		}

		public void bark(Dog dog) {
		}

		public void jump(Dog dog, Integer times) {
		}

		public void dropDead(Integer times, Dog dog) {
		}

		public void withDogs(WithDogs withDogs) {
		}
	}

	private class DogDeserializer implements JsonDeserializer<Dog> {

		public Dog deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
				throws JsonParseException {
			Dog dog = new Dog();
			dog.name = "Renan";
			dog.age = 25;

			return dog;
		}

	}
	
	private class SomeNonHierarchicalAdapter implements JsonDeserializer<Dog> {

		public Dog deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			Dog dog = new Dog();
			dog.name = json.getAsString();
			return dog;
		}
	}

	@HierarchicalAdapter
	private class SomeHierarchicalAdapter implements JsonDeserializer<Dog> {

		public Dog deserialize(JsonElement json, Type typeOfT,
				JsonDeserializationContext context) throws JsonParseException {
			UglyDog uglyDog = new UglyDog();
			uglyDog.color = json.getAsString();
			return uglyDog;
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldNotAcceptMethodsWithoutArguments() throws Exception {
		deserializer.deserialize(new ByteArrayInputStream(new byte[0]), woof);
	}

	@Test
	public void shouldBeAbleToDeserializeADog() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });
		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWithDeserializerAdapter() throws Exception {
		List<JsonDeserializer<?>> deserializers = new ArrayList<JsonDeserializer<?>>();
		deserializers.add(new DogDeserializer());

		deserializer = new GsonDeserialization(provider, deserializers);

		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Renan Reis','age':'0'}}".getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });
		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "dog" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Renan"));
		assertThat(dog.age, is(25));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgument() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(jump.getMethod())).thenReturn(new String[] { "dog", "times" });

		Object[] deserialized = deserializer.deserialize(stream, jump);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogWhenMethodHasMoreThanOneArgumentAndTheXmlIsTheLastOne() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'dog':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(dropDead.getMethod())).thenReturn(new String[] { "times", "dog" });

		Object[] deserialized = deserializer.deserialize(stream, dropDead);

		assertThat(deserialized.length, is(2));
		assertThat(deserialized[1], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[1];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldBeAbleToDeserializeADogNamedDifferently() throws Exception {
		InputStream stream = new ByteArrayInputStream("{'pet':{'name':'Brutus','age':7}}".getBytes());

		when(provider.parameterNamesFor(bark.getMethod())).thenReturn(new String[] { "pet" });

		Object[] deserialized = deserializer.deserialize(stream, bark);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(Dog.class)));
		Dog dog = (Dog) deserialized[0];
		assertThat(dog.name, is("Brutus"));
		assertThat(dog.age, is(7));
	}

	@Test
	public void shouldUseNonHierarchicalAdapterIfExists() {
		InputStream stream = new ByteArrayInputStream(
				"{'withDogs':{'dog':'Simple Dog','uglyDog':{'name':'Ugly Dog','age':100,'color':gray}}}".getBytes());
		
		List<JsonDeserializer<?>> deserializers = new ArrayList<JsonDeserializer<?>>();
		deserializers.add(new SomeNonHierarchicalAdapter());

		deserializer = new GsonDeserialization(provider, deserializers);

		when(provider.parameterNamesFor(withDogs.getMethod())).thenReturn(
				new String[] { "withDogs" });

		Object[] deserialized = deserializer.deserialize(stream, withDogs);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(WithDogs.class)));
		WithDogs withDogs = (WithDogs) deserialized[0];
		assertThat(withDogs.dog.name, is("Simple Dog"));
		assertThat(withDogs.dog.age, is(nullValue()));
		assertThat(withDogs.uglyDog.color, is("gray"));
	}
	
	@Test
	public void shouldUseHierarchicalAdapterIfExists() {
		InputStream stream = new ByteArrayInputStream(
				"{'withDogs':{'dog':'black','uglyDog':'gray'}}".getBytes());
		
		List<JsonDeserializer<?>> deserializers = new ArrayList<JsonDeserializer<?>>();
		deserializers.add(new SomeHierarchicalAdapter());

		deserializer = new GsonDeserialization(provider, deserializers);

		when(provider.parameterNamesFor(withDogs.getMethod())).thenReturn(
				new String[] { "withDogs" });

		Object[] deserialized = deserializer.deserialize(stream, withDogs);

		assertThat(deserialized.length, is(1));
		assertThat(deserialized[0], is(instanceOf(WithDogs.class)));
		WithDogs withDogs = (WithDogs) deserialized[0];
		assertThat(withDogs.dog, is(instanceOf(UglyDog.class)));
		assertThat(withDogs.uglyDog, is(instanceOf(UglyDog.class)));
		assertThat(withDogs.uglyDog.color, is("gray"));
	}
}