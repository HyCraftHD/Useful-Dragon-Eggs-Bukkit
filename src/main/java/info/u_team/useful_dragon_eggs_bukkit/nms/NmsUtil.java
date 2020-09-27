package info.u_team.useful_dragon_eggs_bukkit.nms;

import java.lang.reflect.Method;

import org.bukkit.*;
import org.bukkit.entity.Entity;

public class NmsUtil {
	
	public static final String NMS_VERSION = Bukkit.getServer().getClass().getPackage().getName().substring(23);
	
	private static final Class<?> CRAFT_WORLD_CLASS;
	private static final Class<?> CRAFT_ENTITY_CLASS;
	
	private static final Class<?> NMS_WORLD_SERVER_CLASS;
	private static final Class<?> NMS_ENTITY_CLASS;
	private static final Class<?> NMS_BLOCK_POSITION_CLASS;
	
	private static final Method CRAFT_WORLD_METHOD_GET_HANDLE;
	private static final Method CRAFT_ENTITY_METHOD_GET_HANDLE;
	
	private static final Method NMS_ENTITY_METHOD_GET_CHUNK_COORDINATES;
	private static final Method NMS_BLOCK_POSITION_METHOD_B;
	private static final Method NMS_WORLD_SERVER_METHOD_ARE_CHUNKS_LOADED_IN_BETWEEN;
	
	static {
		CRAFT_WORLD_CLASS = getNmsClass("org.bukkit.craftbukkit", "CraftWorld");
		CRAFT_ENTITY_CLASS = getNmsClass("org.bukkit.craftbukkit", "entity.CraftEntity");
		
		NMS_WORLD_SERVER_CLASS = getNmsClass("net.minecraft.server", "WorldServer");
		NMS_ENTITY_CLASS = getNmsClass("net.minecraft.server", "Entity");
		NMS_BLOCK_POSITION_CLASS = getNmsClass("net.minecraft.server", "BlockPosition");
		
		CRAFT_WORLD_METHOD_GET_HANDLE = getNmsMethod(CRAFT_WORLD_CLASS, "getHandle");
		CRAFT_ENTITY_METHOD_GET_HANDLE = getNmsMethod(CRAFT_ENTITY_CLASS, "getHandle");
		
		NMS_ENTITY_METHOD_GET_CHUNK_COORDINATES = getNmsMethod(NMS_ENTITY_CLASS, "getChunkCoordinates");
		NMS_BLOCK_POSITION_METHOD_B = getNmsMethod(NMS_BLOCK_POSITION_CLASS, "b", int.class, int.class, int.class);
		NMS_WORLD_SERVER_METHOD_ARE_CHUNKS_LOADED_IN_BETWEEN = getNmsMethod(NMS_WORLD_SERVER_CLASS, "areChunksLoadedBetween", NMS_BLOCK_POSITION_CLASS, NMS_BLOCK_POSITION_CLASS);
	}
	
	public static void doDragonEggLogic(World world, Entity entity, Runnable callback) {
		
		// Class.forName("org.bukkit.craftbukkit." + NMS_VERSION + ".CraftWorld").getMethod("getHandle");
		
		// final WorldServer world = ((CraftWorld) event.getEntity().getWorld()).getHandle();
		
		final Object worldNms = invokeNmsMethod(CRAFT_WORLD_METHOD_GET_HANDLE, world);
		final Object entityNms = invokeNmsMethod(CRAFT_ENTITY_METHOD_GET_HANDLE, entity);
		
		final Object posNms = invokeNmsMethod(NMS_ENTITY_METHOD_GET_CHUNK_COORDINATES, entityNms);
		
		final Object leftPosNms = invokeNmsMethod(NMS_BLOCK_POSITION_METHOD_B, posNms, -32, -32, -32);
		final Object rightPosNms = invokeNmsMethod(NMS_BLOCK_POSITION_METHOD_B, posNms, 32, 32, 32);
		
		final boolean chunksLoaded = invokeNmsMethod(NMS_WORLD_SERVER_METHOD_ARE_CHUNKS_LOADED_IN_BETWEEN, worldNms, leftPosNms, rightPosNms);
		
		if (chunksLoaded) {
			return;
		}
		
		callback.run();
	}
	
	private static Class<?> getNmsClass(String basePackage, String subPackage) {
		try {
			return Class.forName(getNmsPackage(basePackage, subPackage));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	private static String getNmsPackage(String basePackage, String subPackage) {
		return basePackage + "." + NMS_VERSION + "." + subPackage;
	}
	
	private static Method getNmsMethod(Class<?> clazz, String method, Class<?>... args) {
		try {
			return clazz.getMethod(method, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Object> T invokeNmsMethod(Method method, Object instance, Object... args) {
		try {
			return (T) method.invoke(instance, args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}
}
