package kokabiel.samurai.api;

import kokabiel.samurai.module.Module;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.metadata.ModMetadata;

import java.util.List;
import java.util.Optional;

public interface IAddon {
    void onInitialize();
    List<Module> modules();
//    List<Command> commands();
    String getName();
    String getId();
    String getDescription();
    String getLicense();
    String getHomepageURL();
    String getIssueTrackerURL();
    String getAuthor();
    default String getVersion() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(getId());

        if(modContainer.isPresent()) {
            ModMetadata metadata = modContainer.get().getMetadata();
            return metadata.getVersion().getFriendlyString();
        }

        return "Unkown";
    }
    default Optional<String> getIcon() {
        Optional<ModContainer> modContainer = FabricLoader.getInstance().getModContainer(getId());

        if(modContainer.isPresent()) {
            ModMetadata metadata = modContainer.get().getMetadata();
            return metadata.getIconPath(64);
        }

        return Optional.of("Unkown");
    }
}
