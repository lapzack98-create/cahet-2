package example.zackcheats;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ZackcheatsMenuScreen extends Screen {
    private final List<ModuleEntry> modules = new ArrayList<>();
    private String selectedCategory = "All";
    private String searchText = "";
    private int scroll = 0;

    private static final int BG = 0xFF120607;
    private static final int PANEL = 0xFF1A080B;
    private static final int CARD = 0xFF210A0E;
    private static final int BORDER = 0xFF5A1824;
    private static final int RED = 0xFFFF3557;
    private static final int WHITE = 0xFFF7F0F1;
    private static final int MUTED = 0xFFB98F98;

    public ZackcheatsMenuScreen() {
        super(Text.literal("Zackcheats"));
        modules.add(new ModuleEntry("Velocity Meter", "Shows your movement speed in the UI.", "Movement", true));
        modules.add(new ModuleEntry("Sprint Status", "Shows whether sprint is currently active.", "Movement", true));
        modules.add(new ModuleEntry("Coordinates HUD", "Displays your own position when in a world.", "Visual", true));
        modules.add(new ModuleEntry("Clock", "Displays your local in-game day time.", "Utility", false));
        modules.add(new ModuleEntry("Item Durability", "Shows durability for your held item.", "Visual", false));
        modules.add(new ModuleEntry("Low Fire Overlay", "Reduces the first-person fire overlay.", "Visual", false));
        modules.add(new ModuleEntry("Screenshot Reminder", "A demo toggle for a future reminder feature.", "Info", false));
        modules.add(new ModuleEntry("Menu Sounds", "A demo toggle for menu sound feedback.", "Utility", false));
        modules.add(new ModuleEntry("Status Panel", "Displays a compact client status panel.", "Info", false));
        modules.add(new ModuleEntry("HUD Scale", "A demo setting placeholder.", "Utility", false));
        modules.add(new ModuleEntry("Safe Walk (Demo)", "UI placeholder only; no movement automation.", "Movement", false));
    }

    @Override
    protected void init() {
        super.init();
        // The menu uses custom-drawn search and category controls so the layout stays close
        // to the supplied reference image at different window sizes.
        addDrawableChild(ButtonWidget.builder(Text.literal("×"), button -> close())
                .dimensions(width - 42, 8, 30, 24).build());
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, BG);
        int sidebarW = Math.max(170, Math.min(220, width / 5));
        context.fill(0, 0, sidebarW, height, PANEL);
        context.fill(sidebarW - 1, 0, sidebarW, height, BORDER);

        context.drawText(textRenderer, "♛ Zackcheats", 24, 20, RED, false);
        context.drawText(textRenderer, "R E L E A S E", 25, 39, MUTED, false);
        context.fill(22, 62, sidebarW - 18, 63, BORDER);
        context.drawText(textRenderer, "MODULES", 24, 78, MUTED, false);

        String[] sideItems = {"Combat", "Mace", "Misc", "Movement", "Spear", "Visual"};
        for (int i = 0; i < sideItems.length; i++) {
            int y = 104 + i * 39;
            boolean selected = sideItems[i].equals("Movement");
            if (selected) {
                context.fill(14, y - 5, sidebarW - 12, y + 25, 0xFF4B111D);
                context.fill(14, y - 5, 17, y + 25, RED);
                context.drawBorder(14, y - 5, sidebarW - 26, 30, BORDER);
            }
            context.drawText(textRenderer, sideItems[i], 36, y + 4, selected ? WHITE : 0xFFE1CDD1, false);
            context.drawText(textRenderer, sideItems[i].equals("Movement") ? "11" : "", sidebarW - 34, y + 4, MUTED, false);
        }

        int contentX = sidebarW + 22;
        context.drawText(textRenderer, Text.literal("Movement").styled(s -> s.withColor(RED).withItalic(true)), contentX, 18, RED, false);
        int titleWidth = textRenderer.getWidth("Movement");
        context.drawText(textRenderer, "Modules", contentX + titleWidth + 8, 18, WHITE, false);

        long enabledCount = modules.stream().filter(m -> m.enabled).count();
        context.drawText(textRenderer, modules.size() + " modules · " + enabledCount + " enabled", contentX, 43, MUTED, false);

        int searchW = Math.min(290, width / 3);
        int searchX = width - searchW - 54;
        context.fill(searchX, 12, searchX + searchW, 48, CARD);
        context.drawBorder(searchX, 12, searchW, 36, BORDER);
        context.drawText(textRenderer, "⌕ " + (searchText.isEmpty() ? "Search modules" : searchText), searchX + 13, 25, MUTED, false);

        String[] filters = {"All", "Gamemodes", "Network", "Utility", "Info"};
        int fx = contentX;
        for (String filter : filters) {
            int fw = textRenderer.getWidth(filter) + 28;
            boolean active = selectedCategory.equals(filter);
            context.fill(fx, 58, fx + fw, 86, active ? 0xFF5A1422 : PANEL);
            context.drawBorder(fx, 58, fw, 28, active ? RED : BORDER);
            context.drawCenteredTextWithShadow(textRenderer, filter, fx + fw / 2, 67, WHITE);
            fx += fw + 8;
        }

        int listTop = 98;
        int cardH = 66;
        int gap = 12;
        int availableW = width - contentX - 24;
        List<ModuleEntry> visible = getVisibleModules();
        int maxScroll = Math.max(0, visible.size() * (cardH + gap) - (height - listTop - 12));
        scroll = Math.max(0, Math.min(scroll, maxScroll));

        context.enableScissor(contentX, listTop, width - 16, height - 8);
        for (int i = 0; i < visible.size(); i++) {
            ModuleEntry module = visible.get(i);
            int y = listTop + i * (cardH + gap) - scroll;
            if (y + cardH < listTop || y > height) continue;
            context.fill(contentX, y, contentX + availableW, y + cardH, CARD);
            context.fill(contentX, y, contentX + 3, y + cardH, module.enabled ? RED : BORDER);
            context.drawBorder(contentX, y, availableW, cardH, BORDER);
            context.drawText(textRenderer, module.name, contentX + 18, y + 15, WHITE, false);
            context.drawText(textRenderer, module.description, contentX + 18, y + 36, MUTED, false);
            context.drawText(textRenderer, "⌄", contentX + availableW - 76, y + 24, MUTED, false);
            int tx = contentX + availableW - 54;
            int ty = y + 22;
            context.fill(tx, ty, tx + 38, ty + 22, module.enabled ? RED : 0xFF4B3C40);
            context.fill(module.enabled ? tx + 19 : tx + 2, ty + 2, module.enabled ? tx + 36 : tx + 19, ty + 20, WHITE);
        }
        context.disableScissor();

        context.drawText(textRenderer, "Right Shift: open/close menu  •  Click a card to toggle  •  Demo client-side options", contentX, height - 18, MUTED, false);
        super.render(context, mouseX, mouseY, delta);
    }

    private List<ModuleEntry> getVisibleModules() {
        String q = searchText.toLowerCase(Locale.ROOT);
        return modules.stream().filter(m ->
                (selectedCategory.equals("All") || selectedCategory.equals(m.category))
                && (m.name.toLowerCase(Locale.ROOT).contains(q) || m.description.toLowerCase(Locale.ROOT).contains(q))
        ).toList();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int sidebarW = Math.max(170, Math.min(220, width / 5));
        int contentX = sidebarW + 22;
        int listTop = 98;
        int cardH = 66;
        int gap = 12;
        int availableW = width - contentX - 24;
        List<ModuleEntry> visible = getVisibleModules();

        if (mouseY >= 58 && mouseY <= 86 && mouseX >= contentX) {
            String[] filters = {"All", "Gamemodes", "Network", "Utility", "Info"};
            int fx = contentX;
            for (String filter : filters) {
                int fw = textRenderer.getWidth(filter) + 28;
                if (mouseX >= fx && mouseX <= fx + fw) {
                    selectedCategory = filter;
                    scroll = 0;
                    return true;
                }
                fx += fw + 8;
            }
        }

        if (mouseY >= listTop && mouseX >= contentX && mouseX <= contentX + availableW) {
            int row = (int) ((mouseY - listTop + scroll) / (cardH + gap));
            if (row >= 0 && row < visible.size()) {
                visible.get(row).enabled = !visible.get(row).enabled;
                return true;
            }
        }

        // Click the search field to cycle through simple demo search presets.
        int searchW = Math.min(290, width / 3);
        int searchX = width - searchW - 54;
        if (mouseY >= 12 && mouseY <= 48 && mouseX >= searchX && mouseX <= searchX + searchW) {
            String[] presets = {"", "hud", "status", "item", "safe"};
            int next = 0;
            for (int i = 0; i < presets.length; i++) if (presets[i].equals(searchText)) next = (i + 1) % presets.length;
            searchText = presets[next];
            scroll = 0;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        scroll = Math.max(0, scroll - (int) (verticalAmount * 42));
        return true;
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
