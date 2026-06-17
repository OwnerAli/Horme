package com.xenith.action;

import com.xenithlibrary.action.AbstractAction;
import com.xenithlibrary.action.ActionContext;
import com.xenithlibrary.config.DomainConfig;
import org.bukkit.entity.Player;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;

import java.util.LinkedHashMap;
import java.util.Map;

public class HealAction extends AbstractAction {

    private double healAmount;

    public HealAction(double healAmount) {
        this.healAmount = healAmount;
    }

    @Override
    public void execute(ActionContext context) {
        if (!rolledSuccessfully()) return;
        Player player = context.getPlayer();
        if (player == null) return;

        double currentHealth = player.getHealth();
        AttributeInstance maxHealthAttribute = player.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        double maxHealth = (maxHealthAttribute != null) ? maxHealthAttribute.getValue() : 20.0; // Default to 20 if attribute not found

        double newHealth = Math.min(currentHealth + healAmount, maxHealth);
        player.setHealth(newHealth);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> data =
            new LinkedHashMap<>(super.serialize());

        data.put("healAmount", healAmount);
        return data;
    }

    @Override
    public void applyEdit(String field, String value) {
        switch (field) {
            case "healAmount" -> {
                try {
                    this.healAmount = Double.parseDouble(value);
                } catch (NumberFormatException e) {
                    // Log error or handle invalid input
                }
            }
            default -> super.applyEdit(field, value);
        }
    }

    public static AbstractAction fromConfig(DomainConfig config) {
        double healAmount = config.getDouble("healAmount", 1.0);

        HealAction action = new HealAction(healAmount);
        action.setChance(config.getDouble("chance", 100.0));

        return action;
    }
}