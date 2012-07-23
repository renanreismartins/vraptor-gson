package br.com.caelum.vraptor.serialization.gson;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

import br.com.caelum.vraptor.serialization.gson.GsonJSONSerializationTest.MyCollection;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class CollectionSerializer implements JsonSerializer<MyCollection> {

	private Gson gson;

	public CollectionSerializer(Gson gson) {
		this.gson = gson;
	}

	@Override
	public JsonElement serialize(MyCollection coll, Type type, JsonSerializationContext ctx) {
		String src = "testing";
		List<String> x = Arrays.asList(src);
		return gson.toJsonTree(x);
	}
}