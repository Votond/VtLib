package me.votond.vtlib.util.dsl.item

import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import me.votond.vtlib.base.VtPlugin
import me.votond.vtlib.util.legacy
import me.votond.vtlib.util.setPersistentValueDirectly
import me.votond.vtlib.util.toNamespacedKey
import org.bukkit.*
import org.bukkit.attribute.AttributeModifier
import org.bukkit.block.BlockState
import org.bukkit.block.banner.Pattern
import org.bukkit.block.data.BlockData
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.*
import org.bukkit.potion.PotionEffect
import java.util.*
import kotlin.random.Random

data class ItemContext(
    var plugin: VtPlugin,
    var material: Material,
    var amount: Int? = null,
    var displayName: String? = null,
    var lore: MutableList<String> = mutableListOf(),

    var enchantments: MutableSet<Pair<Enchantment, Int>> = mutableSetOf(),
    var hasEnchantmentGlint: Boolean? = null,
    var isUnbreakable: Boolean? = null,
    var durabilityDamage: Int? = null,

    var persistentValues: MutableSet<Pair<String, Any>> = mutableSetOf(),
    var customModelData: Int? = null,
    var itemFlags: MutableSet<ItemFlag> = mutableSetOf(),
    var attributeModifiers: MutableSet<ItemAttributeContext> = mutableSetOf(),

    // SubMetas specific properties
    var bannerPatterns: MutableList<Pattern> = mutableListOf(),
    var compassTargetLocation: Location? = null,
    var crossbowChargedProjectiles: MutableList<ItemStack> = mutableListOf(),
    // For Material.ENCHANTED_BOOK
    var storedEnchantments: MutableSet<Pair<Enchantment, Int>> = mutableSetOf(),
    var knowledgeBookRecipes: MutableList<NamespacedKey> = mutableListOf(),
    var leatherArmorColor: Color? = null,
    // This field is more preferred than headOwner
    var headBase64Value: String? = null,
    // Use runTask(true) for bypass main thread block when getting item with this property set
    @Deprecated("This head texture set method is laggy. Use headBase64Value instead.")
    var headOwner: OfflinePlayer? = null,
    var suspiciousStewEffects: MutableList<PotionEffect> = mutableListOf(),
    var fireworkStarEffect: FireworkEffect? = null,

    var blockData: BlockData? = null,
    var blockState: BlockState? = null,

    var fireworkProperties: FireworkPropertiesContext? = null,
    var bookProperties: BookPropertiesContext? = null,
    var mapProperties: MapPropertiesContext? = null,
    var potionProperties: PotionPropertiesContext? = null,
    var tropicalFishProperties: TropicalFishPropertiesContext? = null,
) {
    @Suppress("DEPRECATION")
    fun buildItemStack(): ItemStack {
        val item = ItemStack(material)
        val itemMeta = item.itemMeta!!

        amount?.let { item.amount = it }
        displayName?.let { itemMeta.setDisplayName("&r&f$it".legacy(plugin)) }
        if (lore.isNotEmpty())
            itemMeta.lore = lore.map { "&r&f$it".legacy(plugin) }

        enchantments.forEach { itemMeta.addEnchant(it.first, it.second, true) }
        hasEnchantmentGlint?.let {
            if (it) {
                itemMeta.addEnchant(Enchantment.LURE, 1, true)
                itemFlags.add(ItemFlag.HIDE_ENCHANTS)
            }
        }
        isUnbreakable?.let { itemMeta.isUnbreakable = it }
        if (itemMeta is Damageable)
            durabilityDamage?.let { itemMeta.damage = it }

        persistentValues.forEach {
            itemMeta.setPersistentValueDirectly(it.first.toNamespacedKey(plugin), it.second)
        }
        customModelData?.let { itemMeta.setCustomModelData(it) }
        if (itemFlags.isNotEmpty())
            itemMeta.addItemFlags(*itemFlags.toTypedArray())
        attributeModifiers.forEach {
            itemMeta.addAttributeModifier(
                it.attribute!!,
                AttributeModifier(UUID.randomUUID(), Random.nextInt().toString(), it.amount!!, it.operation!!, it.slot)
            )
        }

        if (itemMeta is BannerMeta)
            itemMeta.patterns = bannerPatterns
        compassTargetLocation?.let {
            if (itemMeta is CompassMeta) {
                itemMeta.lodestone = it
                itemMeta.isLodestoneTracked = true
            }
        }
        if (itemMeta is CrossbowMeta)
            itemMeta.setChargedProjectiles(crossbowChargedProjectiles)
        if (itemMeta is EnchantmentStorageMeta)
            storedEnchantments.forEach { itemMeta.addStoredEnchant(it.first, it.second, true) }
        if (itemMeta is KnowledgeBookMeta)
            itemMeta.recipes = knowledgeBookRecipes
        if (itemMeta is LeatherArmorMeta)
            leatherArmorColor?.let { itemMeta.setColor(it) }
        if (itemMeta is SkullMeta) {
            headBase64Value?.let {
                val profile = GameProfile(UUID.randomUUID(), "")
                profile.properties.put("textures", Property("textures", it))

                try {
                    val profileField = itemMeta::class.java.getDeclaredField("profile")
                    profileField.setAccessible(true)
                    profileField.set(itemMeta, profile)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } ?: headOwner?.let { itemMeta.owningPlayer = it }
        }
        if (itemMeta is SuspiciousStewMeta)
            suspiciousStewEffects.forEach {
                itemMeta.addCustomEffect(it, true)
            }
        if (itemMeta is FireworkEffectMeta)
            fireworkStarEffect?.let { itemMeta.effect = it }

        if (itemMeta is BlockDataMeta)
            blockData?.let { itemMeta.setBlockData(it) }
        if (itemMeta is BlockStateMeta)
            blockState?.let { itemMeta.blockState = it }

        if (itemMeta is FireworkMeta) {
            fireworkProperties?.let {
                itemMeta.addEffects(it.fireworkRocketEffects)
                it.fireworkRocketPower?.let { itemMeta.power = it }
            }
        }
        if (itemMeta is BookMeta) {
            bookProperties?.let {
                it.bookTitle?.let { itemMeta.title = it }
                it.bookAuthor?.let { itemMeta.author = it }
                it.bookGeneration?.let { itemMeta.generation = it }
                itemMeta.pages = it.bookPages
            }
        }
        if (itemMeta is MapMeta) {
            mapProperties?.let {
                it.mapColor?.let { itemMeta.color = it }
                it.mapLocationName?.let { itemMeta.locationName = it }
                it.mapView?.let { itemMeta.mapView = it }
                it.isMapScaling?.let { itemMeta.isScaling = it }
            }
        }

        if (itemMeta is PotionMeta) {
            potionProperties?.let {
                it.potionEffects.forEach {
                    itemMeta.addCustomEffect(it, true)
                }
                it.basePotionData?.let { itemMeta.basePotionData = it }
                it.potionColor?.let { itemMeta.color = it }
            }
        }

        if (itemMeta is TropicalFishBucketMeta) {
            tropicalFishProperties?.let {
                it.tropicalFishPatternColor?.let { itemMeta.patternColor = it }
                it.tropicalFishPattern?.let { itemMeta.pattern = it }
                it.tropicalFishBodyColor?.let { itemMeta.bodyColor = it }
                if (!itemMeta.hasVariant())
                    throw Exception("Tropical fish with declared properties does not exist.")
            }
        }

        item.itemMeta = itemMeta
        return item
    }
}