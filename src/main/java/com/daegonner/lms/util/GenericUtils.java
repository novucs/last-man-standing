package com.daegonner.lms.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public final class GenericUtils {

    private GenericUtils() {
    }

    public static Map<String, Object> serializeItem(ItemStack item) {
        Map<String, Object> target = new HashMap<>();
        if (item.getType() != Material.AIR) {
            target.put("material", item.getType().name().toLowerCase());
        }

        if (item.getDurability() != 0) {
            target.put("data", item.getDurability());
        }

        if (item.getAmount() != 1) {
            target.put("amount", item.getAmount());
        }

        if (item.getItemMeta().getDisplayName() != null) {
            target.put("name", item.getItemMeta().getDisplayName());
        }

        if (item.getItemMeta().getLore() != null) {
            target.put("lore", item.getItemMeta().getLore());
        }

        if (!item.getEnchantments().isEmpty()) {
            for (Map.Entry<Enchantment, Integer> entry : item.getItemMeta().getEnchants().entrySet()) {
                target.put("enchantments." + entry.getKey().getName().toLowerCase(), entry.getValue());
            }
        }

        return target;
    }

    public static ItemStack parseItem(Map<?, ?> data) {
        ItemFactoryBuilder builder = new ItemFactoryBuilder();
        GenericUtils.getEnum(Material.class, data, "material").ifPresent(builder::material);
        GenericUtils.getInt(data, "data").ifPresent(b -> builder.data(b.byteValue()));
        GenericUtils.getInt(data, "amount").ifPresent(builder::min);
        GenericUtils.getString(data, "name").ifPresent(builder::name);
        GenericUtils.getList(data, "lore").ifPresent(l -> builder.lore(GenericUtils.castList(String.class, l)));
        GenericUtils.getMap(data, "enchantments").ifPresent(m -> builder.enchantments(GenericUtils.parseEnchantments(m)));
        return builder.build().forceCreate();
    }

    public static Map<Enchantment, Integer> parseEnchantments(Map<?, ?> toParse) {
        Map<Enchantment, Integer> target = new HashMap<>();
        for (Map.Entry<?, ?> entry : toParse.entrySet()) {
            Object key = entry.getKey();
            Object value = entry.getValue();
            Enchantment enchantment = null;
            int level = 0;

            if (key instanceof String) {
                enchantment = Enchantment.getByName((String) key);
            }

            if (value instanceof Integer) {
                level = (Integer) value;
            }

            if (level > 0 && enchantment != null) {
                target.put(enchantment, level);
            }
        }
        return target;
    }

    public static <K extends Enum<K>, V> Map<K, V> castEnumMap(Class<K> keyType, Class<? extends V> valueType, Map<?, ?> toCast) {
        Map<K, V> target = new EnumMap<>(keyType);
        for (Map.Entry<?, ?> entry : toCast.entrySet()) {
            Optional<K> key = parseEnum(keyType, entry.getKey());
            Object value = entry.getValue();
            if (key.isPresent() && valueType.isInstance(value)) {
                target.put(key.get(), valueType.cast(value));
            }
        }
        return target;
    }

    public static <E> List<E> castList(Class<? extends E> type, List<?> toCast) throws ClassCastException {
        return toCast.stream().map(type::cast).collect(Collectors.toList());
    }

    public static Optional<List> getList(Map<?, ?> input, Object key) {
        return getValue(List.class, input, key);
    }

    public static Optional<Material> getMaterial(Map<?, ?> input, Object key) {
        return getEnum(Material.class, input, key);
    }

    public static <T extends Enum<T>> Optional<T> getEnum(Class<T> type, Map<?, ?> input, Object key) {
        Optional<String> name = getString(input, key);
        if (name.isPresent()) {
            return parseEnum(type, name.get());
        }
        return Optional.empty();
    }

    public static Optional<Boolean> getBoolean(Map<?, ?> input, Object key) {
        return getValue(Boolean.class, input, key);
    }

    public static Optional<Integer> getInt(Map<?, ?> input, Object key) {
        return getValue(Integer.class, input, key);
    }

    public static Optional<String> getString(Map<?, ?> input, Object key) {
        return getValue(String.class, input, key);
    }

    public static Optional<Map> getMap(Map<?, ?> input, Object key) {
        return getValue(Map.class, input, key);
    }

    public static <T> Optional<T> getValue(Class<T> clazz, Map<?, ?> input, Object key) {
        Object target = input.get(key);
        if (target == null || !clazz.isInstance(target)) {
            return Optional.empty();
        }
        return Optional.of((T) target);
    }

    public static <T extends Enum<T>> Optional<T> parseEnum(Class<T> type, String name) {
        name = name.toUpperCase().replaceAll("\\s+", "_").replaceAll("\\W", "");
        try {
            return Optional.of(Enum.valueOf(type, name));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    public static <T extends Enum<T>> Optional<T> parseEnum(Class<T> type, Object toParse) {
        if (toParse instanceof String) {
            return parseEnum(type, (String) toParse);
        }

        try {
            return Optional.of(type.cast(toParse));
        } catch (ClassCastException ex) {
            return Optional.empty();
        }
    }
}
