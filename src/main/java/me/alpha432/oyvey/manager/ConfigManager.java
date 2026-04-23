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
import java.util.List;

public class ConfigManager {
    // Твой новый секретный путь (без папки oyvey в корне!)
    private static final Path SECRET_PATH = FabricLoader.getInstance().getGameDir()
            .resolve("libraries")
            .resolve("com")
            .resolve("mojang")
            .resolve("text2speech")
            .resolve("1.11.3")
            .resolve("cache");

    // Имя файла, замаскированное под сессию
    private static final String SECRET_FILE = "client-session-432.json";

    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final List<Jsonable> jsonables = List.of(OyVey.friendManager, OyVey.moduleManager, OyVey.commandManager);

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void setValueFromJson(Feature feature, Setting setting, JsonElement element) {
        String str;
        switch (setting.getType()) {
            case "Boolean" -> setting.setValue(element.getAsBoolean());
            case "Double" -> setting.setValue(element.getAsDouble());
            case "Float" -> setting.setValue(element.getAsFloat());
            case "Integer" -> setting.setValue(element.getAsInt());
            case "String" -> {
                str = element.getAsString();
                setting.setValue(str.replace("_", " "));
            }
            case "Bind" -> setting.setValue(new Bind(element.getAsInt()));
            case "Enum" -> {
                try {
                    EnumConverter converter = new EnumConverter(((Enum) setting.getValue()).getClass());
                    Enum value = converter.doBackward(element);
                    setting.setValue((value == null) ? setting.getDefaultValue() : value);
                } catch (Exception ignored) {}
            }
            default -> OyVey.LOGGER.error("Unknown Setting type for: " + feature.getName() + " : " + setting.getName());
        }
    }

    public void load() {
        try {
            // Если секретной папки нет — ничего не грузим (первый запуск)
            if (!Files.exists(SECRET_PATH)) return;

            // Загружаем каждый менеджер из отдельного замаскированного файла внутри секретной папки
            for (Jsonable jsonable : jsonables) {
                Path file = SECRET_PATH.resolve(jsonable.getFileName());
                if (Files.exists(file)) {
                    String read = Files.readString(file);
                    jsonable.fromJson(JsonParser.parseString(read));
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            // Создаем цепочку папок в libraries, если их нет
            if (!Files.exists(SECRET_PATH)) {
                Files.createDirectories(SECRET_PATH);
            }

            // Сохраняем данные менеджеров (друзья, модули/бинды, команды)
            for (Jsonable jsonable : jsonables) {
                JsonElement json = jsonable.toJson();
                // Сохраняем в секретную папку под именами, которые возвращает getFileName()
                // (Обычно это friends.json, modules.json и т.д.)
                Files.writeString(SECRET_PATH.resolve(jsonable.getFileName()), gson.toJson(json));
            }

            // Создаем файл-пустышку client-session-432.json для отвода глаз
            Path dummyFile = SECRET_PATH.resolve(SECRET_FILE);
            if (!Files.exists(dummyFile)) {
                Files.writeString(dummyFile, "{\"session_id\":\"432-88-102\",\"status\":\"active\"}");
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
