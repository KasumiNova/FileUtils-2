package github.kasuminova.fileutils2.configuration;

import cn.hutool.core.io.IORuntimeException;
import cn.hutool.json.*;
import github.kasuminova.fileutils2.utils.FileUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ConfigManager {
    public static void loadBatchCopyConfigFromFile(String path, String name, BatchCopyConfig oldConfig) throws IOException {
        String fullPath = path + name + ".json";
        if (new File(fullPath).exists()) {
            JSONParser parser = JSONParser.of(new JSONTokener(Files.newInputStream(Paths.get(fullPath)), new JSONConfig()));
            JSONObject jsonObject = new JSONObject();
            parser.parseTo(jsonObject, null);

            oldConfig.setResourceDirectory(jsonObject.get("resourceDirectory", String.class))
                    .setTargetDirectory(jsonObject.get("targetDirectory", String[].class));
        } else {
            saveConfigToFile(path, name, oldConfig);
        }
    }

    public static void saveConfigToFile(String path, String name, BatchCopyConfig batchCopyConfig) throws IORuntimeException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("resourceDirectory", batchCopyConfig.getResourceDirectory())
                  .set("targetDirectory", batchCopyConfig.getTargetDirectory());

        FileUtil.createJsonFile(jsonObject.toStringPretty(), path, name);
    }
}
