package com.whty.flow.persistence;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.whty.flow.EasyFlow;
import com.whty.flow.StatefulContext;
import com.whty.flow.container.FlowContainer;
import com.whty.flow.persistence.storage.RAMStorage;

public class StorageUtil {

	private static Gson gson = new GsonBuilder().create();

	public static String save(String persistenceId, StatefulContext ctx,
			StorageMode mode) {
		Map<String, Object> data = ctx.persist();
		// TODO persist
		switch (mode) {
		case RAM:
			RAMStorage.put(persistenceId, new Gson().toJson(data));
			break;
		case REDIS:
			// TODO redis
			break;
		case DB:
			// TODO throw exception
			break;
		default:
			break;
		}
		return gson.toJson(data);
	}

	@SuppressWarnings("unchecked")
	public static <T extends StatefulContext> T get(String persistenceId,
			T ctx, StorageMode mode) {
		String reString = "{}";
		switch (mode) {
		case RAM:
			reString = RAMStorage.get(persistenceId) == null ? "{}"
					: RAMStorage.get(persistenceId);
			break;
		case REDIS:
			// TODO redis
			break;
		case DB:
			// TODO throw exception
			break;
		default:
			break;
		}
		HashMap<String, Object> dataMap = gson
				.fromJson(reString, HashMap.class);
		EasyFlow<T> flow = (EasyFlow<T>) FlowContainer.getFlow(String
				.valueOf(dataMap.get("flow")));
		//flow not find
		if (flow == null) {
			return null;
		} else {
			EasyFlow<T> parentFlow = null;
			if (dataMap.get("parent_flow") != null) {
				parentFlow = (EasyFlow<T>) FlowContainer.getFlow(String
						.valueOf(dataMap.get("parent_flow")));
				ctx.load(dataMap, flow, parentFlow);
			} else {
				ctx.load(dataMap, flow, null);
			}
			return ctx;
		}

	}
}
