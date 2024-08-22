package net.pedroksl.advanced_ae.datagen;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.pedroksl.advanced_ae.AdvancedAE;

@SuppressWarnings("unused")
@EventBusSubscriber(modid = AdvancedAE.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class AAEDataGen {

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        var gen = event.getGenerator();
        var out = gen.getPackOutput();
        var fileHelper = event.getExistingFileHelper();
        var lookup = event.getLookupProvider();

        gen.addProvider(event.includeClient(), new AAELanguageProvider(out));
        gen.addProvider(event.includeClient(), new AAEModelProvider(out, fileHelper));
        gen.addProvider(event.includeServer(), new AAERecipeProvider(out, lookup));
        gen.addProvider(event.includeServer(), new AAELootTableProvider(out, lookup));

        var blockTags = new AAETagProvider.AAEBlockTagProvider(out, lookup, fileHelper);
        var itemTags = new AAETagProvider.AAEItemTagProvider(out, lookup, blockTags.contentsGetter(), fileHelper);
        gen.addProvider(event.includeServer(), blockTags);
        gen.addProvider(event.includeServer(), itemTags);
    }
}
