package br.com.caelum.vraptor.serialization.gson;

import java.lang.reflect.Type;

import org.hibernate.proxy.HibernateProxy;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class HibernateProxySerializer implements JsonSerializer<HibernateProxy> {

	private Gson gson;

	public HibernateProxySerializer(Gson gson) {
		this.gson = gson;
	}

	@Override
	public JsonElement serialize(HibernateProxy proxyObj, Type type, JsonSerializationContext ctx) {
		Object deProxied = proxyObj.getHibernateLazyInitializer().getImplementation();
		return gson.toJsonTree(deProxied);
	}
}