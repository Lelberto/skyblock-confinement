package net.lelberto.skyblockconfinement.events;

import net.lelberto.skyblockconfinement.Plugin;
import net.lelberto.skyblockconfinement.game.Game;
import net.lelberto.skyblockconfinement.game.SkyBlockPlayer;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public class BossEvent extends Event {

    private Boss boss;

    public BossEvent(int initTime, int time) {
        super("Boss Event", initTime, time);
        this.boss = null;
    }

    @Override
    public void runInit() {
        switch (new Random().nextInt(2)) {
            case 0:
                this.boss = new CapdeputeBoss();
                break;
            case 1:
                this.boss = new TonExBoss();
                break;
            default: break;
        }
    }

    @Override
    public void run() {
        Bukkit.getOnlinePlayers().forEach(this.boss.bar::addPlayer);
        this.boss.spawn(Plugin.getConfiguration().bossEventSpawn);
    }

    @Override
    public void runStop() {
        if (this.boss.isAlive()) {
            this.boss.killIfEventEnd();
        }
        Bukkit.getOnlinePlayers().forEach(this.boss.bar::removePlayer);
    }

    public Boss getBoss() {
        return boss;
    }



    private static abstract class Boss {

        protected final String name;
        protected final int exp;
        protected final int xpMin;
        protected final int xpMax;
        protected final BossBar bar;
        protected final Set<UUID> attackers;

        private Boss(String name, int exp, int xpMin, int xpMax) {
            this.name = name;
            this.exp = exp;
            this.xpMin = xpMin;
            this.xpMax = xpMax;
            this.bar = Bukkit.createBossBar(this.name, BarColor.RED, BarStyle.SOLID, BarFlag.DARKEN_SKY, BarFlag.CREATE_FOG);
            this.attackers = new HashSet<>();
            this.bar.setProgress(1.0);
        }

        public abstract void spawn(Location location);

        public abstract void killIfEventEnd();

        public void processDamage(Player damager) {
            this.attackers.add(damager.getUniqueId());
        }

        public void processKill(Location bossLocation) {
            this.bar.setProgress(0.0);
            Game.getCurrentEvent().time = 0;
            for (int i = 0; i < this.exp; i++) {
                bossLocation.getWorld().spawnEntity(bossLocation, EntityType.EXPERIENCE_ORB);
            }
            this.attackers.forEach(uuid -> SkyBlockPlayer.getPlayer(uuid).addXp(new Random().nextInt(this.xpMax - this.xpMin + 1) + this.xpMin));
        }

        public abstract boolean isAlive();
    }



    private static class CapdeputeBoss extends Boss {

        public static final String NAME = "§c§lCapdepute";

        private Zombie boss;

        private CapdeputeBoss() {
            super(CapdeputeBoss.NAME, 100, 30, 50);
            this.boss = null;
        }

        @Override
        public void spawn(Location location) {
            Zombie boss = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            boss.setBaby(false);
            boss.setCustomName(this.name);
            boss.setCustomNameVisible(true);
            boss.setRemoveWhenFarAway(false);
            boss.setCanPickupItems(false);
            boss.getEquipment().setItemInMainHand(this.createWeapon());
            boss.getEquipment().setItemInMainHandDropChance(1.0F);
            boss.getEquipment().setHelmet(this.createHelmet());
            boss.getEquipment().setHelmetDropChance(0.0F);
            boss.getEquipment().setChestplate(this.createChestplate());
            boss.getEquipment().setChestplateDropChance(0.0F);
            boss.getEquipment().setLeggings(this.createLeggings());
            boss.getEquipment().setLeggingsDropChance(0.0F);
            boss.getEquipment().setBoots(this.createBoots());
            boss.getEquipment().setBootsDropChance(0.0F);
            boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
            boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 2));
            boss.setMetadata("boss", new FixedMetadataValue(Plugin.getPlugin(), true));
            this.boss = boss;

            Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
                if (this.boss.isDead()) {
                    task.cancel();
                    return;
                }
                if (Math.random() < 0.5) {
                    this.boss.getLocation().getWorld().spawnParticle(Particle.FLAME, this.boss.getLocation(), 100, 5.0, 3.0, 5.0);
                    Bukkit.getOnlinePlayers().stream().filter(player -> player.getLocation().distance(this.boss.getLocation()) <= 10).forEach(player -> {
                        player.setVelocity(this.boss.getLocation().getDirection().normalize().add(new Vector(0.0, 0.5, 0.0)).multiply(2.0));
                    });
                } else if (Math.random() < 0.2) {
                    this.spawnSbire(this.boss.getLocation());
                }
            }, 0L, 5L * 20L);
        }

        private void spawnSbire(Location location) {
            Zombie sbire = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            sbire.setBaby(true);
            sbire.setCustomName("§cCapdepute's sbire");
            sbire.setCustomNameVisible(true);
            sbire.setRemoveWhenFarAway(false);
            sbire.setCanPickupItems(false);
            sbire.getEquipment().setItemInMainHand(this.createSbireWeapon());
            sbire.getEquipment().setItemInMainHandDropChance(0.0F);
            sbire.getEquipment().setHelmet(this.createSbireHelmet());
            sbire.getEquipment().setHelmetDropChance(0.0F);
            sbire.getEquipment().setChestplate(this.createSbireChestplate());
            sbire.getEquipment().setChestplateDropChance(0.0F);
            sbire.getEquipment().setLeggings(this.createSbireLeggings());
            sbire.getEquipment().setLeggingsDropChance(0.0F);
            sbire.getEquipment().setBoots(this.createSbireBoots());
            sbire.getEquipment().setBootsDropChance(0.0F);
            sbire.setMetadata("boss_sbire", new FixedMetadataValue(Plugin.getPlugin(), true));
        }

        private ItemStack createWeapon() {
            ItemStack item = new ItemStack(Material.DIAMOND_AXE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setDisplayName("§5Capdep'Axe");
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 4, true);
            itemMeta.addEnchant(Enchantment.KNOCKBACK, 3, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createHelmet() {
            ItemStack item = new ItemStack(Material.GOLDEN_HELMET);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
            itemMeta.addEnchant(Enchantment.THORNS, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createChestplate() {
            ItemStack item = new ItemStack(Material.DIAMOND_CHESTPLATE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
            itemMeta.addEnchant(Enchantment.THORNS, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createLeggings() {
            ItemStack item = new ItemStack(Material.DIAMOND_LEGGINGS);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
            itemMeta.addEnchant(Enchantment.THORNS, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createBoots() {
            ItemStack item = new ItemStack(Material.DIAMOND_BOOTS);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 6, true);
            itemMeta.addEnchant(Enchantment.THORNS, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createSbireWeapon() {
            ItemStack item = new ItemStack(Material.GOLDEN_AXE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createSbireHelmet() {
            ItemStack item = new ItemStack(Material.GOLDEN_HELMET);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createSbireChestplate() {
            ItemStack item = new ItemStack(Material.GOLDEN_CHESTPLATE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createSbireLeggings() {
            ItemStack item = new ItemStack(Material.GOLDEN_LEGGINGS);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createSbireBoots() {
            ItemStack item = new ItemStack(Material.GOLDEN_BOOTS);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        @Override
        public void killIfEventEnd() {
            this.boss.remove();
        }

        @Override
        public void processDamage(Player damager) {
            super.processDamage(damager);
            this.bar.setProgress(this.boss.getHealth() / this.boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        }

        @Override
        public void processKill(Location bossLocation) {
            super.processKill(bossLocation);
            ItemStack gold = new ItemStack(Material.GOLD_INGOT);
            gold.setAmount(20);
            ItemStack diamond = new ItemStack(Material.DIAMOND);
            diamond.setAmount(10);
            bossLocation.getWorld().dropItem(bossLocation, gold);
            bossLocation.getWorld().dropItem(bossLocation, diamond);
        }

        @Override
        public boolean isAlive() {
            return !this.boss.isDead();
        }
    }



    private static class TonExBoss extends Boss {

        public static final String NAME = "§c§lTon Ex";

        private LivingEntity boss;
        private boolean secondPhase;

        private TonExBoss() {
            super(TonExBoss.NAME, 100, 30, 50);
            this.boss = null;
            this.secondPhase = false;
        }

        @Override
        public void spawn(Location location) {
            Zombie boss = (Zombie) location.getWorld().spawnEntity(location, EntityType.ZOMBIE);
            boss.setBaby(false);
            boss.setCustomName(this.name);
            boss.setCustomNameVisible(true);
            boss.setRemoveWhenFarAway(false);
            boss.setCanPickupItems(false);
            boss.getEquipment().setItemInMainHand(this.createWeapon());
            boss.getEquipment().setItemInMainHandDropChance(0.0F);
            boss.getEquipment().setHelmet(this.createHelmet());
            boss.getEquipment().setHelmetDropChance(0.0F);
            boss.getEquipment().setChestplate(this.createChestplate());
            boss.getEquipment().setChestplateDropChance(0.0F);
            boss.getEquipment().setLeggings(this.createLeggings());
            boss.getEquipment().setLeggingsDropChance(0.0F);
            boss.getEquipment().setBoots(this.createBoots());
            boss.getEquipment().setBootsDropChance(0.0F);
            boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 2));
            boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 3));
            boss.setMetadata("boss", new FixedMetadataValue(Plugin.getPlugin(), true));
            this.boss = boss;

            Bukkit.getScheduler().runTaskTimer(Plugin.getPlugin(), (task) -> {
                if (this.boss.isDead()) {
                    task.cancel();
                }
                this.boss.getLocation().getWorld().spawnParticle(Particle.HEART, this.boss.getLocation(), 5, 0.2, 0.2, 0.2);
            }, 0L, 10L);
        }

        private void spawnSecondPhase() {
            WitherSkeleton boss = (WitherSkeleton) this.boss.getLocation().getWorld().spawnEntity(this.boss.getLocation(), EntityType.WITHER_SKELETON);
            boss.setCustomName(this.boss.getCustomName());
            boss.setCustomNameVisible(true);
            boss.setRemoveWhenFarAway(true);
            boss.setCanPickupItems(false);
            boss.getEquipment().setItemInMainHand(this.createWeapon());
            boss.getEquipment().setItemInMainHandDropChance(0.0F);
            boss.getEquipment().setHelmet(this.createHelmet());
            boss.getEquipment().setHelmetDropChance(0.0F);
            boss.getEquipment().setChestplate(this.createChestplate());
            boss.getEquipment().setChestplateDropChance(0.0F);
            boss.getEquipment().setLeggings(this.createLeggings());
            boss.getEquipment().setLeggingsDropChance(0.0F);
            boss.getEquipment().setBoots(this.createBoots());
            boss.getEquipment().setBootsDropChance(0.0F);
            boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 9999999, 3));
            boss.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 9999999, 3));
            boss.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 9999999, 2));
            boss.setMetadata("boss", new FixedMetadataValue(Plugin.getPlugin(), true));
            boss.setHealth(this.boss.getHealth());
            this.boss.remove();
            this.boss = boss;

            this.boss.getLocation().getWorld().spawnParticle(Particle.SMOKE_LARGE, this.boss.getLocation(), 100, 2.0, 2.0, 2.0);
            Bukkit.getOnlinePlayers().stream().filter(player -> player.getLocation().getWorld().equals(this.boss.getLocation().getWorld()) && player.getLocation().distance(this.boss.getLocation()) <= 10).forEach(player -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 5 * 20, 0, false, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 0, false, false, false));
            });

            this.boss.setAI(false);
            this.boss.setInvulnerable(true);
            Bukkit.getScheduler().runTaskLater(Plugin.getPlugin(), (task) -> {
                this.boss.setAI(true);
                this.boss.setInvulnerable(false);
                Bukkit.getOnlinePlayers().stream().filter(player -> player.getLocation().getWorld().equals(this.boss.getLocation().getWorld()) && player.getLocation().distance(this.boss.getLocation()) <= 20).forEach(player -> {
                    Random rand = new Random();
                    player.setVelocity(new Vector(rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble(), 2.0, rand.nextBoolean() ? rand.nextDouble() : -rand.nextDouble()));
                    this.boss.getLocation().getWorld().spawnParticle(Particle.FLAME, this.boss.getLocation(), 500, 10.0, 10.0, 10.0);
                });
            }, 5L * 20L);
        }

        private ItemStack createWeapon() {
            ItemStack item = new ItemStack(Material.WITHER_ROSE);
            ItemMeta itemMeta = item.getItemMeta();
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.DAMAGE_ALL, 12, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createHelmet() {
            ItemStack item = new ItemStack(Material.PLAYER_HEAD);
            SkullMeta itemMeta = (SkullMeta) item.getItemMeta();
            itemMeta.setOwningPlayer(Bukkit.getOfflinePlayer(UUID.fromString("d8d12263-8c31-4848-97bc-c104b6545e4e")));
            itemMeta.setUnbreakable(true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createChestplate() {
            ItemStack item = new ItemStack(Material.LEATHER_CHESTPLATE);
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) item.getItemMeta();
            itemMeta.setColor(Color.FUCHSIA);
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            itemMeta.addEnchant(Enchantment.THORNS, 4, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createLeggings() {
            ItemStack item = new ItemStack(Material.LEATHER_LEGGINGS);
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) item.getItemMeta();
            itemMeta.setColor(Color.FUCHSIA);
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            itemMeta.addEnchant(Enchantment.THORNS, 4, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        private ItemStack createBoots() {
            ItemStack item = new ItemStack(Material.LEATHER_BOOTS);
            LeatherArmorMeta itemMeta = (LeatherArmorMeta) item.getItemMeta();
            itemMeta.setColor(Color.FUCHSIA);
            itemMeta.setUnbreakable(true);
            itemMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 10, true);
            itemMeta.addEnchant(Enchantment.THORNS, 4, true);
            item.setItemMeta(itemMeta);
            return item;
        }

        @Override
        public void killIfEventEnd() {
            this.boss.remove();
        }

        @Override
        public void processDamage(Player damager) {
            super.processDamage(damager);
            this.bar.setProgress(this.boss.getHealth() / this.boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            if (!this.secondPhase && this.boss.getHealth() <= this.boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() / 3.0) {
                this.secondPhase = true;
                this.spawnSecondPhase();
            }
        }

        @Override
        public void processKill(Location bossLocation) {
            super.processKill(bossLocation);
            ItemStack item = new ItemStack(Material.POPPY);
            item.setAmount(5);
            bossLocation.getWorld().dropItem(bossLocation, item);
        }

        @Override
        public boolean isAlive() {
            return !this.boss.isDead();
        }
    }



    public static class Listener implements org.bukkit.event.Listener {

        @EventHandler
        public void onDamage(EntityDamageByEntityEvent e) {
            Entity entity = e.getEntity();
            Entity damager = e.getDamager();
            if (entity.hasMetadata("boss")) {
                if (damager instanceof Player) {
                    ((BossEvent) Game.getCurrentEvent()).getBoss().processDamage((Player) damager);
                } else if (damager instanceof Arrow && ((Arrow) damager).getShooter() instanceof Player) {
                    ((BossEvent) Game.getCurrentEvent()).getBoss().processDamage((Player) ((Arrow) damager).getShooter());
                }
            }
        }

        @EventHandler
        public void onDeath(EntityDeathEvent e) {
            Entity entity = e.getEntity();
            if (entity.hasMetadata("boss")) {
                ((BossEvent) Game.getCurrentEvent()).getBoss().processKill(entity.getLocation());
            }
        }

        @EventHandler
        public void onJoin(PlayerJoinEvent e) {
            if (Game.getCurrentEvent() instanceof BossEvent && Game.getCurrentEvent().status == Status.STARTED) {
                ((BossEvent) Game.getCurrentEvent()).boss.bar.addPlayer(e.getPlayer());
            }
        }
    }
}
