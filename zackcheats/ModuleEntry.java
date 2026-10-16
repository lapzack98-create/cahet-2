package example.zackcheats;

public final class ModuleEntry {
    public final String name;
    public final String description;
    public final String category;
    public boolean enabled;

    public ModuleEntry(String name, String description, String category, boolean enabled) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.enabled = enabled;
    }
}
