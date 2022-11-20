package github.kasuminova.fileutils2.configuration;

public class BatchCopyConfig {
    public static final String DEFAULT_RESOURCE_DIRECTORY = "Files";
    public static final String[] DEFAULT_TARGET_DIRECTORY = {};

    private String resourceDirectory = DEFAULT_RESOURCE_DIRECTORY;

    private String[] targetDirectory = DEFAULT_TARGET_DIRECTORY;

    public String getResourceDirectory() {
        return resourceDirectory;
    }

    public BatchCopyConfig setResourceDirectory(String resourceDirectory) {
        this.resourceDirectory = resourceDirectory == null ? DEFAULT_RESOURCE_DIRECTORY : resourceDirectory;
        return this;
    }

    public String[] getTargetDirectory() {
        return targetDirectory;
    }

    public BatchCopyConfig setTargetDirectory(String[] targetDirectory) {
        this.targetDirectory = targetDirectory == null ? DEFAULT_TARGET_DIRECTORY : targetDirectory;
        return this;
    }
}
