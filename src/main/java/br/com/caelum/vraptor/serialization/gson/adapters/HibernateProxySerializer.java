package br.com.caelum.vraptor.serialization.gson.adapters;

import java.lang.reflect.Type;

import org.hibernate.proxy.HibernateProxy;

import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.serialization.gson.HierarchicalAdapter;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@Component
@HierarchicalAdapter
public class HibernateProxySerializer implements JsonSerializer<HibernateProxy> {

	public JsonElement serialize(HibernateProxy proxyObj, Type type, JsonSerializationContext ctx) {
		Object deProxied = proxyObj.getHibernateLazyInitializer().getImplementation();
		return new Gson().toJsonTree(deProxied);
	}

}