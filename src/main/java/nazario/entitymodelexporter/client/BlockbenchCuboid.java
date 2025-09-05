package nazario.entitymodelexporter.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.model.ModelCuboidData;
import net.minecraft.client.model.ModelTransform;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.UUID;

public record BlockbenchCuboid(@Nullable String name, Vector3f from, Vector3f to, Vector3f origin, Vector2f uvOffset, Vector3f pivotPoint) {
    public JsonElement toJson() {
        JsonObject parentObject = new JsonObject();

        parentObject.addProperty("name", name == null ? "cube" : name);
        parentObject.addProperty("box_uv", true);
        parentObject.addProperty("rescale", false);
        parentObject.addProperty("locked", false);
        parentObject.addProperty("light_emission", 0);
        parentObject.addProperty("render_order", "default");
        parentObject.addProperty("allow_mirror_modeling", true);
        parentObject.add("from", vectorToJson(from));
        parentObject.add("to", vectorToJson(to));
        parentObject.addProperty("autouv", 1);
        parentObject.addProperty("color", 0);
        parentObject.add("origin", vectorToJson(origin));
        parentObject.add("uv_offset", vectorToJson(uvOffset));
        //TODO Faces
        parentObject.addProperty("type", "cube");
        parentObject.addProperty("uuid", UUID.randomUUID().toString());

        return parentObject;
    }

    public static JsonElement vectorToJson(Vector3f vector3f) {
        JsonArray array = new JsonArray();
        array.add(vector3f.x());
        array.add(vector3f.y());
        array.add(vector3f.z());
        return array;
    }
    public static JsonElement vectorToJson(Vector2f vector2f) {
        JsonArray array = new JsonArray();
        array.add(vector2f.x());
        array.add(vector2f.y());
        return array;
    }

    public static BlockbenchCuboid convert(String name, ModelCuboidData cube, ModelTransform rotationData) {
        Vector3f origin = new Vector3f(0, 0, 0);
        Vector3f from = new Vector3f(cube.offset.x, -cube.offset.y, cube.offset.z);
        Vector3f to = new Vector3f(cube.dimensions.x + cube.offset.x, cube.dimensions.y + Math.abs(cube.offset.y), cube.dimensions.z + cube.offset.z);
        Vector2f uvOffset = new Vector2f(cube.textureUV.getX(), cube.textureUV.getY());
        Vector3f pivotPoint = new Vector3f(rotationData.pivotX, rotationData.pivotY, rotationData.pivotZ);

        return new BlockbenchCuboid(name, from, to, origin, uvOffset, pivotPoint);
    }
}
