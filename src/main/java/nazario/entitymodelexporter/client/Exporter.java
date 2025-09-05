package nazario.entitymodelexporter.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.TexturedModelData;
import org.joml.Vector3f;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Exporter {
    public static void export(String name, TexturedModelData texturedModelData) {
        List<BlockbenchCuboid> blockbenchCuboids = exportRecursive("root", texturedModelData.data.getRoot());

        exportBlockbenchModel("minecraft", name, blockbenchCuboids, texturedModelData.dimensions.width, texturedModelData.dimensions.height);
    }

    public static List<BlockbenchCuboid> exportRecursive(String name, ModelPartData modelPartData) {
        List<BlockbenchCuboid> blockbenchCuboids = new ArrayList<>();

        modelPartData.children.forEach((childName, cube) -> {
            blockbenchCuboids.addAll(exportRecursive(childName, cube));
        });

        modelPartData.cuboidData.forEach(modelCuboidData -> {
            blockbenchCuboids.add(BlockbenchCuboid.convert(name, modelCuboidData));
        });

        return blockbenchCuboids;
    }

    public static void exportBlockbenchModel(String namespace, String modelName, List<BlockbenchCuboid> elements, int textureWidth, int textureHeight) {
        String fileHandle = "/export/" + namespace + "/" + modelName + ".bbmodel";

        Path path = new File(MinecraftClient.getInstance().runDirectory, fileHandle).toPath();

        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        JsonObject metaDataObject = new JsonObject();
        metaDataObject.addProperty("format_version", "4.10");
        metaDataObject.addProperty("model_format", "modded_entity");
        metaDataObject.addProperty("box_uv", true);

        JsonObject parentObject = new JsonObject();

        parentObject.add("meta", metaDataObject);
        parentObject.addProperty("name", modelName.toLowerCase());
        parentObject.add("visible_box", BlockbenchCuboid.vectorToJson(new Vector3f(1, 1, 0)));
        //parentObject.addProperty("model_identifier", "");
        //parentObject.addProperty("modded_entity_entity_class", "");
        //parentObject.addProperty("modded_entity_version", "1.17_yarn");
        //parentObject.addProperty("modded_entity_flip_y", true);
        JsonObject resolutionObject = new JsonObject();
        resolutionObject.addProperty("width", textureWidth);
        resolutionObject.addProperty("height", textureHeight);
        parentObject.add("resolution", resolutionObject);

        JsonArray elementsArray = new JsonArray();
        elements.forEach(cube -> elementsArray.add(cube.toJson()));
        parentObject.add("elements", elementsArray);

        parentObject.add("textures", new JsonArray());

        try{
            Files.createDirectories(path.getParent());

            try(BufferedWriter writer = Files.newBufferedWriter(path)) {
                gson.toJson(parentObject, writer);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
