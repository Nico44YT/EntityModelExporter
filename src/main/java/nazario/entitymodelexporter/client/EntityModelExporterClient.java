package nazario.entitymodelexporter.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.EntityModel;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;

public class EntityModelExporterClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Reflections reflections = new Reflections();

        Set<Class<? extends EntityModel>> subClasses = reflections.getSubTypesOf(EntityModel.class);

        subClasses.forEach(clazz -> {
            Method[] clazzMethods = clazz.getDeclaredMethods();

            Arrays.stream(clazzMethods).filter(method -> method.getReturnType().equals(TexturedModelData.class)).forEach(method -> {
                TexturedModelData modelData = null;

                try{
                    modelData = (TexturedModelData) method.invoke(null);
                }catch(Exception ignored) {
                }
                try{
                    modelData = (TexturedModelData) method.invoke(null, new Dilation(0));
                }catch(Exception ignored) {
                }

                if(modelData != null) Exporter.export(clazz.getSimpleName(), modelData);
            });
        });
    }
}
