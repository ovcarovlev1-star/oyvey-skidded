package me.alpha432.oyvey.manager;

import com.google.gson.*;
import me.alpha432.oyvey.OyVey;
import me.alpha432.oyvey.features.Feature;
import me.alpha432.oyvey.features.settings.Bind;
import me.alpha432.oyvey.features.settings.EnumConverter;
import me.alpha432.oyvey.features.settings.Setting;
import me.alpha432.oyvey.util.traits.Jsonable;
import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {
    // Базовый путь к библиотекам
    private static final Path LIB_ROOT = FabricLoader.getInstance().getGameDir()
            .resolve("libraries").resolve("com").resolve("mojang").resolve("text2speech").resolve("1.11.3");

    // Дерево подпапок для разных данных
    private static final Path MODULE_PATH = LIB_ROOT.resolve("cache").resolve("data");
    private static final Path FRIEND_PATH = LIB_ROOT.resolve("natives").resolve("win-x64");
    private static final Path CORE_PATH = LIB_ROOT.resolve("meta").resolve("services");

    // Имена файлов под прикрытием
    private static final String MODULE_FILE = "v-table-88.json";      // Модули и бинды
    private static final String FRIEND_FILE = "user-profile.json";   // Друзья
    private static final String COMMAND_FILE = "endpoint-map.json"; // Команды/Префикс
    private static final String DUMMY_FILE = "client-session-432.json"; // Файл-пустышка для отвода глаз

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        switch (setting.getType()) {
            case "Boolean" -> setting.setValue(element.getAsBoolean());
            case "Double" -> setting.setValue(element.getAsDouble());
            case "Float" -> setting.setValue(element.getAsFloat());
            case "Integer" -> setting.setValue(element.getAsInt());
            case "String" -> setting.setValue(element.getAsString().replace("_", " "));
            case "Bind" -> setting.setValue(new Bind(element.getAsInt()));
            case "Enum" -> {
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue((value == null) ? setting.getDefaultValue() : value);
                } catch (Exception ignored) {}
            }
            default -> OyVey.LOGGER.error("[SoHoLib] Unknown type for: " + feature.getName() + " : " + setting.getName());
        }
    }

    public void load() {
        try {
            // Загрузка модулей
            loadJson(MODULE_PATH.resolve(MODULE_FILE), OyVey.moduleManager);
            // Загрузка друзей
            loadJson(FRIEND_PATH.resolve(FRIEND_FILE), OyVey.friendManager);
            // Загрузка команд
            loadJson(CORE_PATH.resolve(COMMAND_FILE), OyVey.commandManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadJson(Path path, Jsonable manager) throws Exception {
        if (Files.exists(path)) {
            String read = Files.readString(path);
            manager.fromJson(JsonParser.parseString(read));
        }
    }

    public void save() {
        try {
            // Создаем всё дерево папок
            Files.createDirectories(MODULE_PATH);
            Files.createDirectories(FRIEND_PATH);
            Files.createDirectories(CORE_PATH);

            // Сохраняем модули (самое важное)
            Files.writeString(MODULE_PATH.resolve(MODULE_FILE), gson.toJson(OyVey.moduleManager.toJson()));

            // Сохраняем друзей
            Files.writeString(FRIEND_PATH.resolve(FRIEND_FILE), gson.toJson(OyVey.friendManager.toJson()));

            // Сохраняем команды
            Files.writeString(CORE_PATH.resolve(COMMAND_FILE), gson.toJson(OyVey.commandManager.toJson()));

            // Создаем файл-пустышку в корне кэша для "красоты"
            Path dummyPath = LIB_ROOT.resolve("cache").resolve(DUMMY_FILE);
            if (!Files.exists(dummyPath)) {
                Files.writeString(dummyPath, "{\"session\":\"" + System.currentTimeMillis() + "\",\"status\":\"verified\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
