package cobblemonquestsextended.cobblemon_quests_extended.registry;

import cobblemonquestsextended.cobblemon_quests_extended.CobblemonQuests;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Central registry for all quest action types.
 * This registry allows the mod and add-ons to register custom actions
 * that can be used in quest tasks.
 *
 * <p>The registry is thread-safe and supports dynamic registration at runtime,
 * enabling other mods to add their own action types.</p>
 *
 * <p>Example usage for add-ons:</p>
 * <pre>{@code
 * ActionRegistry.register("my_custom_action", ActionDefinition.of(
 *     "my_custom_action",
 *     true,
 *     ActionCategory.OTHER
 * ));
 * }</pre>
 */
public final class ActionRegistry {

    private static final Map<String, ActionDefinition> ACTIONS = new ConcurrentHashMap<>();

    // Static initializer to register all built-in actions
    static {
        registerBuiltInActions();
    }

    private ActionRegistry() {
        // Prevent instantiation
    }

    /**
     * Registers a new action definition.
     *
     * @param actionId   the unique identifier for the action
     * @param definition the action definition
     * @return true if registration was successful, false if an action with that ID already exists
     */
    public static boolean register(String actionId, ActionDefinition definition) {
        if (actionId == null || definition == null) {
            CobblemonQuests.LOGGER.warn("Attempted to register null action or definition");
            return false;
        }

        if (ACTIONS.containsKey(actionId)) {
            CobblemonQuests.LOGGER.warn("Action '{}' is already registered, skipping duplicate registration", actionId);
            return false;
        }

        ACTIONS.put(actionId, definition);
        CobblemonQuests.LOGGER.debug("Registered action: {}", actionId);
        return true;
    }

    /**
     * Registers an action, replacing any existing registration with the same ID.
     * Use with caution - this can override core actions.
     *
     * @param actionId   the unique identifier for the action
     * @param definition the action definition
     */
    public static void registerOrReplace(String actionId, ActionDefinition definition) {
        if (actionId == null || definition == null) {
            CobblemonQuests.LOGGER.warn("Attempted to register null action or definition");
            return;
        }

        ActionDefinition previous = ACTIONS.put(actionId, definition);
        if (previous != null) {
            CobblemonQuests.LOGGER.info("Replaced action registration for: {}", actionId);
        } else {
            CobblemonQuests.LOGGER.debug("Registered action: {}", actionId);
        }
    }

    /**
     * Gets an action definition by its ID.
     *
     * @param actionId the action identifier
     * @return an Optional containing the action definition, or empty if not found
     */
    public static Optional<ActionDefinition> getAction(String actionId) {
        return Optional.ofNullable(ACTIONS.get(actionId));
    }

    /**
     * Checks if an action with the given ID is registered.
     *
     * @param actionId the action identifier
     * @return true if the action exists in the registry
     */
    public static boolean isRegistered(String actionId) {
        return ACTIONS.containsKey(actionId);
    }

    /**
     * Gets all registered action IDs.
     *
     * @return an unmodifiable list of all action IDs
     */
    public static List<String> getAllActionIds() {
        return new ArrayList<>(ACTIONS.keySet());
    }

    /**
     * Gets all registered action definitions.
     *
     * @return a collection of all action definitions
     */
    public static Collection<ActionDefinition> getAllActions() {
        return ACTIONS.values();
    }

    /**
     * Gets all actions in a specific category.
     *
     * @param category the category to filter by
     * @return a list of action definitions in the specified category
     */
    public static List<ActionDefinition> getActionsByCategory(ActionCategory category) {
        return ACTIONS.values().stream()
                .filter(action -> action.category() == category)
                .collect(Collectors.toList());
    }

    /**
     * Gets all action IDs in a specific category.
     *
     * @param category the category to filter by
     * @return a list of action IDs in the specified category
     */
    public static List<String> getActionIdsByCategory(ActionCategory category) {
        return ACTIONS.values().stream()
                .filter(action -> action.category() == category)
                .map(ActionDefinition::id)
                .collect(Collectors.toList());
    }

    /**
     * Gets the total number of registered actions.
     *
     * @return the count of registered actions
     */
    public static int getActionCount() {
        return ACTIONS.size();
    }

    /**
     * Registers all built-in actions from the base mod.
     * Called automatically during class loading.
     */
    private static void registerBuiltInActions() {
        // CATCH category - obtaining Pokemon
        registerInternal("catch", true, ActionCategory.CATCH);
        registerInternal("obtain", true, ActionCategory.CATCH);
        registerInternal("select_starter", true, ActionCategory.CATCH);
        registerInternal("revive_fossil", true, ActionCategory.CATCH);
        registerInternal("reel", true, ActionCategory.CATCH);
        registerInternal("hatch_egg", true, ActionCategory.CATCH);

        // BATTLE category - combat related
        registerInternal("defeat", true, ActionCategory.BATTLE);
        registerInternal("defeat_player", true, ActionCategory.BATTLE);
        registerInternal("defeat_npc", true, ActionCategory.BATTLE);
        registerInternal("kill", true, ActionCategory.BATTLE);
        registerInternal("faint_pokemon", true, ActionCategory.BATTLE);

        // EVOLUTION category - evolution related
        registerInternal("evolve", true, ActionCategory.EVOLUTION);
        registerInternal("evolve_into", true, ActionCategory.EVOLUTION);
        registerInternal("change_form", true, ActionCategory.EVOLUTION);

        // TRADE category - trading
        registerInternal("trade_away", true, ActionCategory.TRADE);
        registerInternal("trade_for", true, ActionCategory.TRADE);

        // POKEDEX category - dex related
        registerInternal("scan", true, ActionCategory.POKEDEX);
        registerInternal("have_registered", true, ActionCategory.POKEDEX);
        registerInternal("register", true, ActionCategory.POKEDEX);

        // GIMMICK category - mega, tera, z-moves, dynamax, etc.
        registerInternal("mega_evolve", true, ActionCategory.GIMMICK);
        registerInternal("terastallize", true, ActionCategory.GIMMICK);
        registerInternal("use_z_move", true, ActionCategory.GIMMICK);
        registerInternal("dynamax", true, ActionCategory.GIMMICK);
        registerInternal("gigantamax", true, ActionCategory.GIMMICK);
        registerInternal("ultra_burst", true, ActionCategory.GIMMICK);

        // OTHER category - miscellaneous
        registerInternal("level_up", true, ActionCategory.OTHER);
        registerInternal("level_up_to", true, ActionCategory.OTHER);
        registerInternal("release", true, ActionCategory.OTHER);
        registerInternal("throw_ball", true, ActionCategory.OTHER);
        registerInternal("send_out", true, ActionCategory.OTHER);
        registerInternal("give_held_item", true, ActionCategory.OTHER);
        registerInternal("heal", true, ActionCategory.OTHER);
    }

    /**
     * Internal helper to register built-in actions without logging.
     */
    private static void registerInternal(String id, boolean requiresPokemon, ActionCategory category) {
        ACTIONS.put(id, ActionDefinition.of(id, requiresPokemon, category));
    }

    /**
     * Initializes the registry. This method can be called to ensure
     * the static initializer has run, but is not strictly necessary
     * since the class will be loaded when first accessed.
     */
    public static void init() {
        // Static initializer has already run at this point
        CobblemonQuests.LOGGER.info("ActionRegistry initialized with {} actions", ACTIONS.size());
    }
}
