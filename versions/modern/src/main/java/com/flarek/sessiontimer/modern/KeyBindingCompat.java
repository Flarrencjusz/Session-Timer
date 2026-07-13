package com.flarek.sessiontimer.modern;

import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.MappingResolver;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Creates the editor key across the keybinding category ABI change in Minecraft 1.21.11.
 *
 * <p>Fabric API's {@code KeyBindingHelper} registers bindings but deliberately uses Minecraft's
 * {@link KeyBinding} type. Minecraft 1.21.8-1.21.10 expects a category translation key string,
 * while 1.21.11+ expects a {@code KeyBinding.Category}. Keeping one JAR for both ABIs therefore
 * requires constructor lookup at this single boundary.
 */
final class KeyBindingCompat {
    private static final String KEY_TRANSLATION = "key.sessiontimer.open_editor";
    private static final String LEGACY_CATEGORY = "key.category.sessiontimer";
    private static final Identifier CATEGORY_ID = Identifier.of("sessiontimer", "controls");
    private static final String CATEGORY_INTERMEDIARY_NAME = "net.minecraft.class_304$class_11900";

    private KeyBindingCompat() {
    }

    static KeyBinding createOpenEditorKey() {
        Class<?> categoryType = findModernCategoryType();
        return categoryType == null ? createLegacyBinding() : createModernBinding(categoryType);
    }

    private static Class<?> findModernCategoryType() {
        MappingResolver mappings = FabricLoader.getInstance().getMappingResolver();
        String runtimeName = mappings.mapClassName("intermediary", CATEGORY_INTERMEDIARY_NAME);
        try {
            return Class.forName(runtimeName);
        } catch (ClassNotFoundException notPresentBefore12111) {
            return null;
        }
    }

    private static KeyBinding createLegacyBinding() {
        try {
            return KeyBinding.class
                    .getConstructor(String.class, InputUtil.Type.class, int.class, String.class)
                    .newInstance(KEY_TRANSLATION, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, LEGACY_CATEGORY);
        } catch (ReflectiveOperationException exception) {
            throw creationFailure(exception);
        }
    }

    private static KeyBinding createModernBinding(Class<?> categoryType) {
        try {
            Object category = categoryType.getConstructor(Identifier.class).newInstance(CATEGORY_ID);
            return (KeyBinding) KeyBinding.class
                    .getConstructor(String.class, InputUtil.Type.class, int.class, categoryType)
                    .newInstance(KEY_TRANSLATION, InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_O, category);
        } catch (ReflectiveOperationException exception) {
            throw creationFailure(exception);
        }
    }

    private static IllegalStateException creationFailure(ReflectiveOperationException cause) {
        return new IllegalStateException("Could not create the Session Timer key binding", cause);
    }
}
