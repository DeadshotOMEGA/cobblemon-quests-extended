package cobblemonquestsextended.cobblemon_quests_extended.preview;

import cobblemonquestsextended.cobblemon_quests_extended.domain.CobblemonTaskModel;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.TaskValidator;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.ValidationIssue;
import cobblemonquestsextended.cobblemon_quests_extended.domain.validation.ValidationResult;
import cobblemonquestsextended.cobblemon_quests_extended.tasks.CobblemonTask;
import dev.ftb.mods.ftblibrary.icon.Color4I;
import dev.ftb.mods.ftblibrary.ui.Panel;
import dev.ftb.mods.ftblibrary.ui.Theme;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.List;

import static cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests.MOD_ID;

/**
 * Real-time preview panel showing quest description and validation status.
 *
 * Layout:
 * <pre>
 * +---------------------------------------------+
 * | Quest Preview                               |
 * +---------------------------------------------+
 * | "Catch 5 shiny Pikachu or Raichu using a   |
 * |  Premier Ball in the Plains biome during    |
 * |  daytime."                                  |
 * +---------------------------------------------+
 * | ERRORS                                      |
 * | * [actions] At least one action required    |
 * +---------------------------------------------+
 * | WARNINGS                                    |
 * | * [biomes] Biomes filter unused for action  |
 * +---------------------------------------------+
 * | Active Conditions:                          |
 * | * Species: Pikachu, Raichu                  |
 * | * Shiny: Yes                                |
 * | * Ball: Premier Ball                        |
 * | * Biome: Plains                             |
 * | * Time: Day (0-12000)                       |
 * +---------------------------------------------+
 * </pre>
 */
@Environment(EnvType.CLIENT)
public class LivePreviewPanel extends Panel {

    private final CobblemonTask task;
    private final TaskValidator validator;

    // UI state
    private Component previewText = Component.empty();
    private ValidationResult validationResult = ValidationResult.valid();
    private List<Component> activeConditions = new ArrayList<>();

    // Colors
    private static final Color4I ERROR_COLOR = Color4I.rgb(0xE74C3C);    // Red
    private static final Color4I WARNING_COLOR = Color4I.rgb(0xF39C12);  // Orange
    private static final Color4I INFO_COLOR = Color4I.rgb(0x3498DB);     // Blue
    private static final Color4I SECTION_BG = Color4I.rgb(0x2C2C2C);     // Dark gray
    private static final Color4I BORDER_COLOR = Color4I.rgb(0x555555);   // Gray
    private static final Color4I HEADER_BG = Color4I.rgb(0x3A3A3A);      // Slightly lighter
    private static final Color4I TEXT_COLOR = Color4I.rgb(0xE0E0E0);     // Light gray text
    private static final Color4I MUTED_COLOR = Color4I.rgb(0x808080);    // Muted text

    // Layout constants
    private static final int PADDING = 6;
    private static final int LINE_HEIGHT = 11;
    private static final int SECTION_SPACING = 8;
    private static final int HEADER_HEIGHT = 14;

    public LivePreviewPanel(Panel parent, CobblemonTask task) {
        super(parent);
        this.task = task;
        this.validator = new TaskValidator();
        refresh();
    }

    /**
     * Refreshes the panel with current task state.
     * Call this when task fields change.
     */
    public void refresh() {
        CobblemonTaskModel model = CobblemonTaskModel.fromTask(task);

        // Generate preview text using NaturalLanguageGenerator if available
        this.previewText = generatePreviewText(model);

        // Run validation
        this.validationResult = validator.validate(model);

        // Build active conditions list
        this.activeConditions = buildActiveConditions(model);
    }

    /**
     * Generates preview text from the model.
     * Uses NaturalLanguageGenerator if available, otherwise uses task's getAltTitle().
     */
    private Component generatePreviewText(CobblemonTaskModel model) {
        // Try to use NaturalLanguageGenerator if it exists
        try {
            NaturalLanguageGenerator nlg = new NaturalLanguageGenerator();
            return nlg.generate(model);
        } catch (Exception e) {
            // Fallback to task's alt title
            return task.getAltTitle();
        }
    }

    /**
     * Builds the list of active conditions from the model.
     */
    private List<Component> buildActiveConditions(CobblemonTaskModel model) {
        List<Component> conditions = new ArrayList<>();

        // Actions
        if (!model.getActions().isEmpty()) {
            conditions.add(buildConditionLine("actions", formatOrList(model.getActions())));
        }

        // Species
        if (!model.getPokemons().isEmpty()) {
            List<String> pokemonNames = model.getPokemons().stream()
                .map(this::formatPokemonName)
                .toList();
            conditions.add(buildConditionLine("species", formatOrList(pokemonNames)));
        }

        // Shiny
        if (model.isShiny()) {
            conditions.add(buildConditionLine("shiny", "Yes"));
        }

        // Types
        if (!model.getPokemonTypes().isEmpty()) {
            conditions.add(buildConditionLine("type", formatOrList(capitalizeList(model.getPokemonTypes()))));
        }

        // Ball
        if (!model.getPokeBallsUsed().isEmpty()) {
            List<String> ballNames = model.getPokeBallsUsed().stream()
                .map(this::formatBallName)
                .toList();
            conditions.add(buildConditionLine("ball", formatOrList(ballNames)));
        }

        // Biomes
        if (!model.getBiomes().isEmpty()) {
            List<String> biomeNames = model.getBiomes().stream()
                .map(this::formatBiomeName)
                .toList();
            conditions.add(buildConditionLine("biome", formatOrList(biomeNames)));
        }

        // Dimensions
        if (!model.getDimensions().isEmpty()) {
            List<String> dimNames = model.getDimensions().stream()
                .map(this::formatDimensionName)
                .toList();
            conditions.add(buildConditionLine("dimension", formatOrList(dimNames)));
        }

        // Time
        if (model.getTimeMin() != 0 || model.getTimeMax() != 24000) {
            String timeDesc = formatTimeRange(model.getTimeMin(), model.getTimeMax());
            conditions.add(buildConditionLine("time", timeDesc));
        }

        // Level
        if (model.getMinLevel() > 0 || model.getMaxLevel() > 0) {
            String levelDesc = formatLevelRange(model.getMinLevel(), model.getMaxLevel());
            conditions.add(buildConditionLine("level", levelDesc));
        }

        // Forms
        if (!model.getForms().isEmpty()) {
            conditions.add(buildConditionLine("form", formatOrList(capitalizeList(model.getForms()))));
        }

        // Genders
        if (!model.getGenders().isEmpty()) {
            conditions.add(buildConditionLine("gender", formatOrList(capitalizeList(model.getGenders()))));
        }

        // Regions
        if (!model.getRegions().isEmpty()) {
            conditions.add(buildConditionLine("region", formatOrList(capitalizeList(model.getRegions()))));
        }

        // Natures
        if (!model.getNatures().isEmpty()) {
            conditions.add(buildConditionLine("nature", formatOrList(model.getNatures())));
        }

        // Tera types
        if (!model.getTeraTypes().isEmpty()) {
            conditions.add(buildConditionLine("tera_type", formatOrList(capitalizeList(model.getTeraTypes()))));
        }

        // Mega forms
        if (!model.getMegaForms().isEmpty()) {
            conditions.add(buildConditionLine("mega_form", formatOrList(capitalizeList(model.getMegaForms()))));
        }

        // Z-Crystals
        if (!model.getZCrystals().isEmpty()) {
            conditions.add(buildConditionLine("z_crystal", formatOrList(capitalizeList(model.getZCrystals()))));
        }

        // Dynamax
        if (!model.getDynamaxTypes().isEmpty()) {
            conditions.add(buildConditionLine("dynamax", formatOrList(capitalizeList(model.getDynamaxTypes()))));
        }

        // Dex progress
        if (!"seen".equals(model.getDexProgress())) {
            conditions.add(buildConditionLine("dex_progress", capitalize(model.getDexProgress())));
        }

        return conditions;
    }

    private Component buildConditionLine(String key, String value) {
        return Component.translatable(MOD_ID + ".preview.condition." + key)
            .append(Component.literal(": ").withStyle(ChatFormatting.GRAY))
            .append(Component.literal(value).withStyle(ChatFormatting.WHITE));
    }

    // Formatting helpers

    private String formatPokemonName(String identifier) {
        // "cobblemon:pikachu" -> "Pikachu"
        String[] parts = identifier.split(":");
        if (parts.length > 1) {
            return capitalize(parts[1].replace("_", " "));
        }
        return capitalize(identifier.replace("_", " "));
    }

    private String formatBallName(String identifier) {
        // "cobblemon:poke_ball" -> "Poke Ball"
        String[] parts = identifier.split(":");
        String name = parts.length > 1 ? parts[1] : identifier;
        return capitalize(name.replace("_", " "));
    }

    private String formatBiomeName(String identifier) {
        // "minecraft:plains" -> "Plains"
        String[] parts = identifier.split(":");
        String name = parts.length > 1 ? parts[1] : identifier;
        return capitalize(name.replace("_", " "));
    }

    private String formatDimensionName(String identifier) {
        // "minecraft:overworld" -> "Overworld"
        String[] parts = identifier.split(":");
        String name = parts.length > 1 ? parts[1] : identifier;
        return capitalize(name.replace("_", " "));
    }

    private String formatTimeRange(long min, long max) {
        // Convert ticks to time description
        String minTime = ticksToTimeString(min);
        String maxTime = ticksToTimeString(max);

        // Special cases (using 12000 as day/night boundary to match ConditionFormatter)
        if (min == 0 && max == 12000) {
            return "Day";
        } else if (min == 12000 && max == 24000) {
            return "Night";
        } else if (min == 0 && max == 6000) {
            return "Morning";
        } else if (min == 6000 && max == 12000) {
            return "Noon";
        } else if (min == 12000 && max == 18000) {
            return "Evening";
        }

        return minTime + " - " + maxTime;
    }

    private String ticksToTimeString(long ticks) {
        // Convert Minecraft ticks to rough time
        // 0 = 6:00 AM, 6000 = noon, 12000 = 6:00 PM, 18000 = midnight
        long adjusted = (ticks + 6000) % 24000;
        int hours = (int) (adjusted / 1000);
        String period = hours >= 12 ? "PM" : "AM";
        if (hours > 12) hours -= 12;
        if (hours == 0) hours = 12;
        return hours + ":00 " + period;
    }

    private String formatLevelRange(int min, int max) {
        if (min == max && min > 0) {
            return "Level " + min;
        } else if (min > 0 && max > 0) {
            return "Level " + min + "-" + max;
        } else if (min > 0) {
            return "Level " + min + "+";
        } else if (max > 0) {
            return "Level 1-" + max;
        }
        return "";
    }

    private String formatOrList(List<String> items) {
        if (items.isEmpty()) return "";
        if (items.size() == 1) return items.getFirst();
        if (items.size() == 2) return items.get(0) + " or " + items.get(1);

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (i > 0) {
                if (i == items.size() - 1) {
                    sb.append(", or ");
                } else {
                    sb.append(", ");
                }
            }
            sb.append(items.get(i));
        }
        return sb.toString();
    }

    private List<String> capitalizeList(List<String> items) {
        return items.stream().map(this::capitalize).toList();
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1).toLowerCase().replace("_", " ");
    }

    @Override
    public void addWidgets() {
        // This panel draws everything manually in drawBackground
        // No interactive widgets needed
    }

    @Override
    public void alignWidgets() {
        // No widgets to align
    }

    @Override
    public void drawBackground(GuiGraphics graphics, Theme theme, int x, int y, int w, int h) {
        // Main panel background
        SECTION_BG.draw(graphics, x, y, w, h);
        BORDER_COLOR.draw(graphics, x, y, w, 1);  // Top border
        BORDER_COLOR.draw(graphics, x, y, 1, h);  // Left border
        BORDER_COLOR.draw(graphics, x + w - 1, y, 1, h);  // Right border
        BORDER_COLOR.draw(graphics, x, y + h - 1, w, 1);  // Bottom border

        int yOffset = y + PADDING;

        // Section 1: Quest Preview Header
        yOffset = drawSectionHeader(graphics, theme, x, yOffset, w,
            Component.translatable(MOD_ID + ".preview.title"));

        // Section 1: Preview text
        yOffset = drawWrappedText(graphics, theme, x + PADDING, yOffset, w - PADDING * 2,
            previewText, TEXT_COLOR, 3);
        yOffset += SECTION_SPACING;

        // Section 2: Errors (if any)
        if (validationResult.getErrorCount() > 0) {
            BORDER_COLOR.draw(graphics, x, yOffset, w, 1);
            yOffset += 1;

            yOffset = drawSectionHeader(graphics, theme, x, yOffset, w,
                Component.translatable(MOD_ID + ".preview.errors")
                    .withStyle(ChatFormatting.RED));

            for (ValidationIssue issue : validationResult.getErrors()) {
                yOffset = drawIssue(graphics, theme, x + PADDING, yOffset, w - PADDING * 2,
                    issue, ERROR_COLOR);
            }
            yOffset += SECTION_SPACING / 2;
        }

        // Section 3: Warnings (if any)
        if (validationResult.getWarningCount() > 0) {
            BORDER_COLOR.draw(graphics, x, yOffset, w, 1);
            yOffset += 1;

            yOffset = drawSectionHeader(graphics, theme, x, yOffset, w,
                Component.translatable(MOD_ID + ".preview.warnings")
                    .withStyle(ChatFormatting.GOLD));

            for (ValidationIssue issue : validationResult.getWarnings()) {
                yOffset = drawIssue(graphics, theme, x + PADDING, yOffset, w - PADDING * 2,
                    issue, WARNING_COLOR);
            }
            yOffset += SECTION_SPACING / 2;
        }

        // Section 4: Active Conditions
        BORDER_COLOR.draw(graphics, x, yOffset, w, 1);
        yOffset += 1;

        yOffset = drawSectionHeader(graphics, theme, x, yOffset, w,
            Component.translatable(MOD_ID + ".preview.active_conditions"));

        if (activeConditions.isEmpty()) {
            theme.drawString(graphics,
                Component.translatable(MOD_ID + ".preview.no_conditions")
                    .withStyle(ChatFormatting.ITALIC, ChatFormatting.GRAY),
                x + PADDING + 8, yOffset, MUTED_COLOR, 0);
            yOffset += LINE_HEIGHT;
        } else {
            for (Component condition : activeConditions) {
                MutableComponent bullet = Component.literal("* ").withStyle(ChatFormatting.GRAY);
                theme.drawString(graphics, bullet.append(condition), x + PADDING + 4, yOffset, TEXT_COLOR, 0);
                yOffset += LINE_HEIGHT;
            }
        }
    }

    private int drawSectionHeader(GuiGraphics graphics, Theme theme, int x, int y, int w, Component title) {
        HEADER_BG.draw(graphics, x + 1, y, w - 2, HEADER_HEIGHT);
        theme.drawString(graphics, title, x + PADDING, y + 3, Color4I.WHITE, 0);
        return y + HEADER_HEIGHT;
    }

    private int drawWrappedText(GuiGraphics graphics, Theme theme, int x, int y, int maxWidth,
                                Component text, Color4I color, int maxLines) {
        String str = text.getString();
        List<String> lines = wrapText(str, maxWidth, theme);

        int linesDrawn = 0;
        for (String line : lines) {
            if (linesDrawn >= maxLines) {
                // Draw ellipsis
                theme.drawString(graphics, Component.literal("..."), x, y, MUTED_COLOR, 0);
                y += LINE_HEIGHT;
                break;
            }
            theme.drawString(graphics, Component.literal(line), x, y, color, 0);
            y += LINE_HEIGHT;
            linesDrawn++;
        }
        return y;
    }

    private List<String> wrapText(String text, int maxWidth, Theme theme) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            String testLine = currentLine.isEmpty() ? word : currentLine + " " + word;
            int testWidth = theme.getStringWidth(Component.literal(testLine));

            if (testWidth <= maxWidth) {
                if (!currentLine.isEmpty()) {
                    currentLine.append(" ");
                }
                currentLine.append(word);
            } else {
                if (!currentLine.isEmpty()) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Single word is too long, just add it
                    lines.add(word);
                }
            }
        }

        if (!currentLine.isEmpty()) {
            lines.add(currentLine.toString());
        }

        return lines;
    }

    private int drawIssue(GuiGraphics graphics, Theme theme, int x, int y, int w,
                          ValidationIssue issue, Color4I color) {
        // Color indicator bar
        color.draw(graphics, x, y + 1, 2, LINE_HEIGHT - 2);

        // Field name in brackets
        String fieldText = "[" + issue.field() + "] ";
        theme.drawString(graphics, Component.literal(fieldText).withStyle(ChatFormatting.DARK_GRAY),
            x + 5, y, MUTED_COLOR, 0);

        // Message
        int fieldWidth = theme.getStringWidth(Component.literal(fieldText));
        Component message = Component.translatable(issue.messageKey(), issue.messageArgs());
        theme.drawString(graphics, message.copy().withStyle(s -> s.withColor(color.rgba())),
            x + 5 + fieldWidth, y, color, 0);

        return y + LINE_HEIGHT;
    }

    /**
     * Calculates the required height for this panel based on content.
     */
    public int calculateRequiredHeight() {
        int height = PADDING;

        // Header
        height += HEADER_HEIGHT;

        // Preview text (estimate 3 lines max)
        height += LINE_HEIGHT * 3;
        height += SECTION_SPACING;

        // Errors
        if (validationResult.getErrorCount() > 0) {
            height += 1; // border
            height += HEADER_HEIGHT;
            height += validationResult.getErrorCount() * LINE_HEIGHT;
            height += SECTION_SPACING / 2;
        }

        // Warnings
        if (validationResult.getWarningCount() > 0) {
            height += 1; // border
            height += HEADER_HEIGHT;
            height += validationResult.getWarningCount() * LINE_HEIGHT;
            height += SECTION_SPACING / 2;
        }

        // Conditions
        height += 1; // border
        height += HEADER_HEIGHT;
        height += Math.max(1, activeConditions.size()) * LINE_HEIGHT;
        height += PADDING;

        return height;
    }

    /**
     * Returns the current validation result.
     */
    public ValidationResult getValidationResult() {
        return validationResult;
    }

    /**
     * Returns true if the current configuration has validation errors.
     */
    public boolean hasErrors() {
        return validationResult.getErrorCount() > 0;
    }

    /**
     * Returns true if the current configuration has warnings.
     */
    public boolean hasWarnings() {
        return validationResult.getWarningCount() > 0;
    }
}
